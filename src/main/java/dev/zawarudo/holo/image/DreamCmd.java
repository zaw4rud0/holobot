package dev.zawarudo.holo.image;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Deactivated
@Command(name = "dream",
        description = "Processes an image with the DeepDream algorithm. Please provide an image as an attachment " +
                      "or a link to process it. Alternatively, you can reply to a message with an image.",
        embedColor = EmbedColor.DEFAULT,
        ownerOnly = true,
        category = CommandCategory.IMAGE)
public class DreamCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        deleteInvoke(e);
        EmbedBuilder eb = new EmbedBuilder();


        // TODO: Check if anything has been provided


        Message referenced = e.getMessage().getReferencedMessage();
        String url = referenced != null ? getImage(referenced) : getImage(e.getMessage());

        // User didn't provide an image
        if (url == null) {
            eb.setTitle("Error");
            eb.setDescription("You need to provide an image to apply the DeepDream processing on it!");
            sendEmbed(e, eb, 30, TimeUnit.SECONDS, true, Color.RED);
            return;
        }

        int iterations = 50;

        sendTyping(e);

        for (int i = 0; i < iterations; i++) {
            try {
                url = process(url);
            } catch (IOException ex) {
                eb.setTitle("Error");
                eb.setDescription("Something went wrong while processing your image! Please try again later.");
                sendEmbed(e, eb, 30, TimeUnit.SECONDS, true, Color.RED);
                return;
            }
        }
        eb.setTitle("Deep Dream").setImage(url);
        sendEmbed(e, eb, true, getEmbedColor());
    }

    /**
     * The URL of the DeepDream API.
     */
    public static final String apiUrl = "https://api.deepai.org/api/deepdream";

    /**
     * Sends a given image URL to the DeepDream API where it is processed. The URL of the processed image is then returned.
     *
     * @param url The URL of the image to process.
     * @return The URL of the processed image.
     */
    public static String process(String url) throws IOException {
        String token = Bootstrap.holo.getConfig().getKeyDeepAI();
        Process pr = Runtime.getRuntime().exec("curl -F image=" + url + " -H api-key:" + token + " " + apiUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String result = reader.lines().collect(Collectors.joining("\n"));
        JsonObject obj = JsonParser.parseString(result).getAsJsonObject();
        if (obj == null || obj.get("err") != null) {
            throw new IOException("No result!");
        }
        return obj.get("output_url").getAsString();
    }
}