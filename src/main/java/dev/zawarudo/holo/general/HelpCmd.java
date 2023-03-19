package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.core.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "help",
        description = "Shows a list of commands or their respective usage",
        usage = "[command]",
        example = "ping",
        guildOnly = false,
        category = CommandCategory.GENERAL)
public class HelpCmd extends AbstractCommand {

    private final CommandManager manager;

    /**
     * Creates a new instance of the help command.
     *
     * @param manager The command manager that will be used to retrieve commands
     *                and their respective information.
     */
    public HelpCmd(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        deleteInvoke(e);

        // Send the full help page
        if (args.length == 0) {
            sendHelpPage(e);
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        // Given command doesn't exist
        if (!manager.isValidName(args[0])) {
            builder.setTitle("Command not found");
            builder.setDescription("Please check for typos and try again!");
            sendEmbed(e, builder, true, 15, TimeUnit.SECONDS);
            return;
        }

        // Help page for given command
        sendHelpPageForCommand(e, manager.getCommand(args[0]));
    }

    /**
     * Sends the full help page of the bot.
     */
    private void sendHelpPage(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Help Page");
        builder.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl().concat("?size=512"));
        builder.setDescription("I currently use `" + getPrefix(event) + "` as prefix for all commands\n"
                + "For more information on a certain command, use ```" + getPrefix(event) + "help <command>```");

        for (CommandCategory category : CommandCategory.values()) {
            List<AbstractCommand> cmds = getCommandsForCategory(category, event);

            // Hide guild only commands from DMs
            if (!event.isFromGuild()) {
                cmds.removeIf(AbstractCommand::isGuildOnly);
            }
            if (cmds.isEmpty()) {
                continue;
            }

            List<String> names = cmds.stream().map(AbstractCommand::getName).toList();
            String text = String.format("```%s```", String.join(", ", names));
            builder.addField(category.getName(), text, false);
        }
        sendEmbed(event, builder, true, 2, TimeUnit.MINUTES);
    }

    /**
     * Gets the commands of a given category. Returns an empty list if the user is
     * missing the required permissions.
     */
    private List<AbstractCommand> getCommandsForCategory(CommandCategory category, MessageReceivedEvent event) {
        if (category == CommandCategory.OWNER && !isBotOwner(event.getAuthor())) {
            return new ArrayList<>();
        }
        if (category == CommandCategory.ADMIN &&
                !(isGuildAdmin(event) || isBotOwner(event.getAuthor()))) {
            return new ArrayList<>();
        }
        return manager.getCommands(category);
    }

    /**
     * Sends the help page for a given command.
     */
    private void sendHelpPageForCommand(MessageReceivedEvent event, AbstractCommand cmd) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Command Help");
        builder.addField("Name", cmd.getName(), false);
        builder.addField("Description", cmd.getDescription(), false);

        if (cmd.hasUsage()) {
            String s = String.format("```%s%s %s```", getPrefix(event), cmd.getName(), cmd.getUsage());
            builder.addField("Usage", s, false);
        }
        if (cmd.hasExample()) {
            String s = String.format("```%s%s %s```", getPrefix(event), cmd.getName(), cmd.getExample());
            builder.addField("Example", s, false);
        }
        if (cmd.hasThumbnail()) {
            builder.setThumbnail(cmd.getThumbnail());
        }
        if (cmd.hasAlias()) {
            String aliases = String.join(", ", cmd.getAlias());
            builder.addField("Alias", String.format("```%s```", aliases), false);
        }
        sendEmbed(event, builder, true, 1, TimeUnit.MINUTES, cmd.getEmbedColor());
    }
}