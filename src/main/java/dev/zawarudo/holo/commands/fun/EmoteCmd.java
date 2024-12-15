package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.modules.emotes.EmoteManager;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;
import java.util.Optional;

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

            Webhook webhook = prepareWebhook(event.getChannel().asTextChannel(), caller);
            webhook.sendMessage(emote.getImageUrl()).queue();
        } catch (SQLException | IOException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    private Webhook prepareWebhook(TextChannel channel, @NotNull Member member) throws IOException {
        String avatarUrl = member.getEffectiveAvatarUrl();
        Icon icon = getIcon(avatarUrl);
        return channel.createWebhook("Webhook").setName(member.getEffectiveName()).setAvatar(icon).complete();
    }

    private Icon getIcon(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return Icon.from(connection.getInputStream());
    }
}
