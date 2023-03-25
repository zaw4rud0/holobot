package dev.zawarudo.holo.fun;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.utils.FileUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Command(name = "8ball",
        description = "Ask the Magic 8 Ball a question and get an answer.",
        usage = "<question>",
        thumbnail = "https://media.discordapp.net/attachments/778991087847079972/946790101109841990/magic8ball.png",
        guildOnly = false,
        category = CommandCategory.MISC)
public class Magic8BallCmd extends AbstractCommand {

    private final List<File> responses;

    public Magic8BallCmd() {
        responses = FileUtils.getAllFiles("src/main/resources/image/8ball");
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0) {
            sendErrorEmbed(event, "Incorrect usage of the command. Please ask a question.");
            return;
        }

        int index = new Random().nextInt(responses.size());
        File response = responses.get(index);

        InputStream input;

        try {
            input = Files.newInputStream(Paths.get(response.getPath()));
        } catch (IOException ex) {
            sendErrorEmbed(event, "An error occurred while fetching an answer. Please try again later.");
            if (logger.isErrorEnabled()) {
                logger.error("Something went wrong while getting an answer.", ex);
            }
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Magic 8-Ball");
        builder.setImage("attachment://8ball.png");
        FileUpload upload = FileUpload.fromData(input, "8ball.png");
        event.getMessage().replyFiles(upload).setEmbeds(builder.build()).queue();
    }
}