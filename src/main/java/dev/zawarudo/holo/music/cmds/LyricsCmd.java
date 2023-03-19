package dev.zawarudo.holo.music.cmds;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

@Deactivated
@Command(name = "lyrics",
        description = "Displays the lyrics of a given song.",
        usage = "<song name>",
        category = CommandCategory.MUSIC)
public class LyricsCmd extends AbstractMusicCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a song name to search for.");
            return;
        }

        sendTyping(event);
        Lyrics lyrics;

        try {
            LyricsClient client = new LyricsClient();
            lyrics = client.getLyrics(String.join(" ", args)).get();
        } catch (InterruptedException | ExecutionException ex) {
            sendErrorEmbed(event, "Something went wrong while communicating with the Lyrics API. Please try again in a few minutes!");
            return;
        }

        if (lyrics == null || lyrics.getContent() == null || lyrics.getContent().isEmpty()) {
            sendErrorEmbed(event, "Couldn't find any lyrics for `" + String.join(" ", args) + "`");
            return;
        }

        String text = lyrics.getContent();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Lyrics | " + lyrics.getTitle() + " by " + lyrics.getAuthor().replace("Lyrics", ""));

        StringBuilder fieldContent = new StringBuilder();
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (line.isBlank()) {
                builder.addField("", fieldContent.toString(), false);
                fieldContent = new StringBuilder();
            } else {
                if (fieldContent.length() + line.length() > MessageEmbed.VALUE_MAX_LENGTH) {
                    builder.addField("", fieldContent.toString(), false);
                    fieldContent = new StringBuilder(line + "\n");
                } else {
                    fieldContent.append(line).append("\n");
                }
            }
        }

        sendEmbed(event, builder, true);
    }
}