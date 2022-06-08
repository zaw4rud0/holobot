package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Command(name = "emote",
        description = "Creates an emote and sends it.",
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class EmoteCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        Guild g = e.getJDA().getGuildById(497697605090934794L);

        Icon icon;
        try {
            InputStream input = new URL(e.getMessage().getAttachments().get(0).getUrl()).openStream();
            icon = Icon.from(input);
        } catch (IOException ex) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Error");
            eb.setDescription("Something went wrong while trying to create the emote.");
            sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
            return;
        }

        Emote emote = g.createEmote(args[0], icon).complete();
        e.getChannel().sendMessage(emote.getAsMention()).queue();
    }
}