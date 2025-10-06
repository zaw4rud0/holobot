package dev.zawarudo.holo.commands.image;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(name = "upscale",
        description = "This command lets you Upscale a given image with Waifu2x. Please provide an image " +
                "as an attachment or as a link to process it. Alternatively, you can reply to a message " +
                "with an image.",
        category = CommandCategory.IMAGE)
public class UpscaleCmd extends AbstractCommand {

    /** The URL of the Waifu2x API. */
    public static final String API_URL = "https://api.deepai.org/api/waifu2x";

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        Message referenced = event.getMessage().getReferencedMessage();
        Optional<String> url = referenced != null ? getImage(referenced) : getImage(event.getMessage());

        // User didn't provide an image
        if (url.isEmpty()) {
            sendErrorEmbed(event, "You need to provide an image to upscale!");
            return;
        }

        sendTyping(event);

        String imageUrl;
        try {
            imageUrl = process(url.get());
        } catch (IOException ex) {
            sendErrorEmbed(event, "Something went wrong while processing your image! Please make sure it's an image and try again.");
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Upscaled Image");
        embedBuilder.setImage(imageUrl);
        sendEmbed(event, embedBuilder, true, 5, TimeUnit.MINUTES, getEmbedColor());
    }

    /**
     * Sends a given image URL to the Waifu2x API where it is upscaled. The processed image is then returned.
     *
     * @param url The URL of the image to upscale.
     * @return The URL of the upscaled image.
     */
    public static String process(String url) throws IOException {
        String token = Bootstrap.holo.getConfig().getDeepAIKey();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "curl",
                "-F",
                "image=" + url,
                "-H",
                "api-key:" + token,
                API_URL
        );
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = reader.lines().collect(Collectors.joining("\n"));
        JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
        if (obj == null || obj.get("err") != null) {
            throw new IOException("No result!");
        }
        return obj.get("output_url").getAsString();
    }
}