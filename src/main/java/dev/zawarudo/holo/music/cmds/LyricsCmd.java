package dev.zawarudo.holo.music.cmds;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// TODO: Newest dependency version doesn't work. Look for a workaround.

@Deactivated
@Command(name = "lyrics",
        description = "Displays the lyrics of a given song.",
        usage = "<song name>",
        category = CommandCategory.MUSIC)
public class LyricsCmd extends AbstractMusicCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        sendTyping(event);

        EmbedBuilder builder = new EmbedBuilder();

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a song name to search for.");
            return;
        }

        Lyrics lyrics;

        try {
            LyricsClient client = new LyricsClient();
            lyrics = client.getLyrics(String.join(" ", args)).get();
        } catch (InterruptedException | ExecutionException ex) {
            sendErrorEmbed(event, "Something went wrong while communicating with the Lyrics API. Please try again in a few minutes!");
            return;
        }

        if (lyrics == null || lyrics.getContent() == null || lyrics.getContent().isEmpty()) {
            builder.setTitle("Error");
            builder.setDescription("Couldn't find any lyrics for `" + String.join(" ", args) + "`");
            sendEmbed(event, builder, true, 15, TimeUnit.SECONDS);
            return;
        }

        String text = lyrics.getContent();
        builder.setTitle("Lyrics | " + lyrics.getTitle() + " by " + lyrics.getAuthor().replace("Lyrics", ""));

        Scanner scanner = new Scanner(text);
        String block = "";

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                builder.addField("", block, false);
                block = "";
            } else {
                // A field can't contain more than 1024 characters
                if (block.length() + line.length() > 1024) {
                    builder.addField("", block, false);
                    builder.addField("", line, false);
                    block = "";
                } else {
                    block += line + "\n";
                }
            }
        }

        builder.addField("", block, false);
        sendEmbed(event, builder, true);
        scanner.close();
    }
}