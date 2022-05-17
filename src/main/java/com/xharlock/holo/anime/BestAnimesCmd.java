package com.xharlock.holo.anime;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import com.xharlock.nanojikan.JikanAPI;
import com.xharlock.nanojikan.exception.APIException;
import com.xharlock.nanojikan.model.Anime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "bestanimes",
        description = "Shows the best animes",
        embedColor = EmbedColor.MAL,
        category = CommandCategory.ANIME)
public class BestAnimesCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        deleteInvoke(e);
        EmbedBuilder eb = new EmbedBuilder();

        sendTyping(e);

        List<Anime> top;

        try {
            top = JikanAPI.getTopAnimes(100);
        } catch (APIException ex) {
            eb.setTitle("Error");
            eb.setDescription("An error occurred while fetching the data. Please try again later.");
            sendEmbed(e, eb, 30, TimeUnit.SECONDS, false, Color.RED);
            return;
        }


        // TODO: Allow user to navigate through the list by using arrow reactions


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            Anime anime = top.get(i);
            sb.append("**" + (i + 1) + ":** ").append(anime.getTitle()).append("\n");
        }

        eb.setTitle("Best Animes");
        eb.setDescription(sb.toString());
        sendEmbed(e, eb, 1, TimeUnit.MINUTES, true, getEmbedColor());
    }
}
