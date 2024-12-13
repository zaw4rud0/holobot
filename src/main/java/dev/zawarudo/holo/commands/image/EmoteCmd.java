package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

@Command(name = "emote",
        description = "Sends the image of an emote",
        category = CommandCategory.IMAGE)
public class EmoteCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        try {
            Member caller = event.getMember();
            if (caller == null) return;

            // TODO: Get requested emote from the database

            Webhook webhook = prepareWebhook(event.getChannel().asTextChannel(), caller);
            webhook.sendMessage("Hello").queue();
        } catch (IOException ex) {
            ex.printStackTrace();
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