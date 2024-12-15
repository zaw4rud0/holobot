package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.modules.emotes.EmoteManager;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(name = "emote",
        description = "Sends a specified emote in the channel.",
        category = CommandCategory.IMAGE)
public class EmoteCmd extends AbstractCommand {

    private final EmoteManager emoteManager;

    public EmoteCmd(EmoteManager emoteManager) {
        this.emoteManager = emoteManager;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 1) {
            sendEmote(event);
        } else if (args.length == 2 && args[0].equals("search")) {
            searchEmote(event);
        } else if (args.length == 3 && args[0].equals("rename")) {
            renameEmote(event);
        }
    }

    private void sendEmote(MessageReceivedEvent event) {
        Member caller = event.getMember();
        if (caller == null) return;

        String emoteName = args[0];

        try {
            Optional<CustomEmoji> emojiOptional = emoteManager.getEmoteByName(emoteName);

            if (emojiOptional.isEmpty()) {
                event.getMessage().reply(String.format("Emote not found: %s", args[0])).queue();
                return;
            }

            deleteInvoke(event);
            CustomEmoji emote = emojiOptional.get();

            Webhook webhook = getWebhook(event.getChannel().asTextChannel(), caller);
            webhook.sendMessage(emote.getImageUrl()).queue(m -> webhook.delete().queue());
        } catch (SQLException | IOException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    private void searchEmote(MessageReceivedEvent event) {
        String keyword = args[1];

        try {
            List<CustomEmoji> emotes = emoteManager.searchEmotesByName(keyword);

            if (emotes.isEmpty()) {
                event.getMessage().reply(String.format("No emotes found for searched name: %s", keyword)).queue();
                return;
            }

            // TODO: Find better way to display emote search results
            String resultsMessage = emotes.stream().map(CustomEmoji::getName).collect(Collectors.joining("\n"));
            List<String> chunks = splitMessage(resultsMessage);

            for (String chunk : chunks) {
                event.getMessage().reply(chunk).queue();
            }
        } catch (SQLException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    private void renameEmote(MessageReceivedEvent event) {
        // TODO: Better check
        if (!event.getAuthor().getId().equals("466292292945313799")) {
            return;
        }

        String emote = args[1];
        String newName = args[2];

        try {
            emoteManager.renameEmote(emote, newName);
            event.getMessage().reply(String.format("The emote `%s` has been successfully renamed to `%s`", emote, newName)).queue();
        } catch (SQLException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    private Webhook getWebhook(TextChannel channel, @NotNull Member member) throws IOException {
        String avatarUrl = member.getEffectiveAvatarUrl();
        Icon icon = getIcon(avatarUrl);
        return channel.createWebhook(createWebhookId()).setName(member.getEffectiveName()).setAvatar(icon).complete();
    }

    private String createWebhookId() {
        String selfId = Bootstrap.holo.getJDA().getSelfUser().getId();
        return String.format("weebhook-holo-%s", selfId);
    }

    private Icon getIcon(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return Icon.from(connection.getInputStream());
    }

    @Deprecated(forRemoval = true)
    private List<String> splitMessage(String message) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < message.length()) {
            int end = Math.min(start + Message.MAX_CONTENT_LENGTH, message.length());

            if (end < message.length() && message.charAt(end) != '\n') {
                int lastNewline = message.lastIndexOf('\n', end);
                if (lastNewline > start) {
                    end = lastNewline + 1;
                }
            }
            chunks.add(message.substring(start, end));
            start = end;
        }
        return chunks;
    }
}