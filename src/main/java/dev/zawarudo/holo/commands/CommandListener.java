package dev.zawarudo.holo.commands;

import dev.zawarudo.holo.commands.fun.EmoteCmd;
import dev.zawarudo.holo.commands.image.ActionCmd;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.zawarudo.holo.utils.LoggingUtils.withMdc;

/**
 * Class that listens to messages and checks if a bot command has been called. If that's
 * the case, it executes the command with the given arguments.
 */
public class CommandListener extends ListenerAdapter {

    private final CommandManager cmdManager;
    private final PermissionManager permManager;

    private final ExecutorService executorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private static final Logger AUDIT = LoggerFactory.getLogger("audit");

    public CommandListener(CommandManager cmdManager, PermissionManager permManager) {
        this.cmdManager = cmdManager;
        this.permManager = permManager;

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore webhooks and bots
        if (event.isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        final String prefix = getPrefix(event);
        final String rawMsg = event.getMessage().getContentRaw();

        // Ignore messages without the bot prefix
        if (!rawMsg.startsWith(prefix) || rawMsg.equals(prefix)) {
            return;
        }

        String withoutPrefix = rawMsg.replaceFirst("(?i)" + Pattern.quote(prefix), "");
        List<String> split = parseArguments(withoutPrefix);
        if (split.isEmpty()) {
            return;
        }
        String invoke = split.get(0).toLowerCase(Locale.UK);

        // Build MDC context for logging
        Map<String, String> mdc = new HashMap<>();
        mdc.put("guildId", event.isFromGuild() ? event.getGuild().getId() : "DM");
        mdc.put("channelId", event.getChannel().getId());
        mdc.put("userId", event.getAuthor().getId());
        mdc.put("command", invoke);

        // Action cmd has been called
        ActionCmd actionCmd = (ActionCmd) cmdManager.getCommand("action");
        if (actionCmd.isAction(invoke)) {
            MDC.setContextMap(mdc);
            try {
                AUDIT.info("Action command invoked");
                LOGGER.info("Action command invoked");
            } finally {
                MDC.clear();
            }
            actionCmd.args = split.subList(1, split.size()).toArray(new String[0]);
            actionCmd.displayAction(event, actionCmd.getAction(invoke));
            return;
        }

        // No valid command
        if (!cmdManager.isValidName(invoke)) {
            checkEmoteInvoke(event, invoke);
            return;
        }

        AbstractCommand cmd = cmdManager.getCommand(invoke);

        // Check if user can do anything
        if (!permManager.hasUserPermission(event, cmd) || !permManager.hasChannelPermission(event, cmd)) {
            return;
        }

        // Log the call with MDC
        MDC.setContextMap(mdc);
        try {
            AUDIT.info("Command invoked");
            LOGGER.info("Command invoked");
        } finally {
            MDC.clear();
        }

        cmd.args = (split.size() > 1) ? split.subList(1, split.size()).toArray(new String[0]) : new String[0];

        executorService.submit(withMdc(mdc, () -> {
            try {
                cmd.onCommand(event);
            } catch (InsufficientPermissionException ex) {
                handlePermissionError(event, ex);
            } catch (Exception ex) {
                LOGGER.error("An error occurred while executing a command.", ex);
            }
        }));
    }

    private String getPrefix(MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getPrefix();
        } else {
            return Bootstrap.holo.getConfig().getDefaultPrefix();
        }
    }

    /**
     * Parses the given input string into a list of arguments, recognizing spaces as delimiters. Arguments
     * enclosed in double quotes are treated as single arguments.
     */
    private List<String> parseArguments(String input) {
        List<String> arguments = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                arguments.add(matcher.group(1));
            } else {
                arguments.add(matcher.group());
            }
        }
        return arguments;
    }

    private void handlePermissionError(MessageReceivedEvent event, InsufficientPermissionException ex) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Missing Permission");
        builder.setDescription("Cannot perform action due to a lack of permission. Please update my permissions " +
                "so I can run the called command.");
        builder.addField("Permission", String.format("```%s```", ex.getPermission().getName()), false);

        boolean hasWritePermission = PermissionUtil.checkPermission(
                event.getGuildChannel().getPermissionContainer(),
                event.getGuild().getSelfMember(),
                Permission.MESSAGE_SEND
        );

        if (hasWritePermission) {
            event.getChannel().sendMessage(event.getAuthor().getAsMention()).addEmbeds(builder.build()).queue();
            return;
        }

        String serverId = event.getGuild().getId();
        String channelId = event.getChannel().getId();
        String messageId = event.getMessageId();

        String channelLink = String.format("https://discord.com/channels/%s/%s", serverId, channelId);
        builder.addField("Channel", channelLink, false);
        String messageLink = String.format("%s/%s", channelLink, messageId);
        builder.addField("Message", messageLink, false);

        event.getAuthor().openPrivateChannel().queue(dm -> dm.sendMessageEmbeds(builder.build()).queue(s -> {},
                err -> LOGGER.warn("Can't send a private message because I have been blocked by {} (ID: {}).",
                event.getAuthor().getName(), event.getAuthor().getId())));
    }

    private void checkEmoteInvoke(MessageReceivedEvent event, String invoke) {
        try {
            // Only guild channels support webhooks
            if (!event.isFromGuild()) {
                return;
            }

            // Check MANAGE_WEBHOOKS permission
            if (!PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MANAGE_WEBHOOKS)) {
                return;
            }

            Optional<CustomEmoji> emoteOptional = Bootstrap.holo.getEmoteManager().getEmoteByName(invoke);
            if (emoteOptional.isPresent()) {
                CustomEmoji emote = emoteOptional.get();
                LOGGER.info("{} has called emote ({})", event.getAuthor().getName(), invoke);
                EmoteCmd cmd = (EmoteCmd) cmdManager.getCommand("emote");
                cmd.sendEmoteMessage(event, emote);
            }
        } catch (SQLException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }
}