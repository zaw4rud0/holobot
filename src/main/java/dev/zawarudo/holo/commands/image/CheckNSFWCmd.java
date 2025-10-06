package dev.zawarudo.holo.commands.image;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.ImageOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(name = "check",
        description = "Checks an image for NSFW elements and returns the likelihood of " +
                "such elements being present. The image can be provided as a URL or as " +
                "an attachment. Replying to a message with an image also works. To get " +
                "more information on the evaluation, use `advanced` (or `adv` for short) " +
                "as an additional argument.",
        usage = "[advanced | adv] [<image url>]",
        category = CommandCategory.IMAGE)
public class CheckNSFWCmd extends AbstractCommand {

    /** The url of the NSFW Detector API. */
    public static final String API_URL = "https://api.deepai.org/api/nsfw-detector";

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        Message reply = event.getMessage().getReferencedMessage();
        Optional<String> imageUrl = reply != null ? getImage(reply) : getImage(event.getMessage());

        if (imageUrl.isEmpty()) {
            sendErrorEmbed(event, "Use `" + getPrefix(event) + "help check` to see the correct usage of this command.");
            return;
        }

        sendTyping(event);
        JsonObject obj;

        try {
            obj = getEvaluation(imageUrl.get());
        } catch (IOException ex) {
            sendErrorEmbed(event, "Something went wrong while communicating with the API");
            return;
        }

        // Advanced Check
        if (args.length >= 1 && (args[0].equals("advanced") || args[0].equals("adv"))) {
            try {
                double score = obj.getAsJsonObject("output").get("nsfw_score").getAsDouble();
                String scoreString = String.format("%.2f", score * 100.0);

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Advanced NSFW Check");
                builder.setDescription("Your image is `" + scoreString + "` likely to contain NSFW elements");
                builder.setImage("attachment://check.png");

                // Get url as a BufferedImage to draw the boxes into it
                BufferedImage img = ImageIO.read(URI.create(imageUrl.get()).toURL());

                int boxes = obj.getAsJsonObject("output").getAsJsonArray("detections").size();

                // Draw nsfw boxes
                for (int i = 0; i < boxes; i++) {
                    JsonObject detection = obj.getAsJsonObject("output").getAsJsonArray("detections").get(i).getAsJsonObject();
                    JsonArray boundingBox = detection.getAsJsonArray("bounding_box");

                    // Values of the box
                    int x = boundingBox.get(0).getAsInt();
                    int y = boundingBox.get(1).getAsInt();
                    int width = boundingBox.get(2).getAsInt();
                    int height = boundingBox.get(3).getAsInt();

                    int sizeClass = Math.max(img.getWidth(), img.getHeight()) / 1000;
                    // For tiny images
                    if (sizeClass == 0) {
                        sizeClass = 1;
                    }

                    // Draw the box into the image
                    drawBox(img, x, y, width, height, i + 1, sizeClass);

                    // Display box information
                    String reason = detection.get("name").getAsString();
                    double confidence = detection.get("confidence").getAsDouble();
                    String s = String.format("**Reason:** %s\n**Confidence:** %s", reason, confidence);
                    builder.addField("Box " + (i + 1), s, false);
                }

                InputStream input = ImageOperations.toInputStream(img);
                if (reply != null) {
                    reply.replyFiles(FileUpload.fromData(input, "check.png")).setEmbeds(builder.build()).queue();
                } else {
                    event.getChannel().sendFiles(FileUpload.fromData(input, "check.png")).setEmbeds(builder.build()).queue();
                }
                input.close();
            } catch (IOException ex) {
                sendErrorEmbed(event, "Something went wrong while processing and evaluating your image. Please try again in a few minutes!");
            }
        }

        // Normal Check
        else {
            double score = obj.getAsJsonObject("output").get("nsfw_score").getAsDouble();
            String scoreString = String.format("%.2f", score * 100.0);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("NSFW Check");
            builder.setDescription("Your image is `" + scoreString + "` likely to contain NSFW elements");

            if (reply != null) {
                reply.replyEmbeds(builder.build()).queue();
            } else {
                sendEmbed(event, builder, true, 2, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * Draw a box into the image with the given properties.
     */
    private static void drawBox(BufferedImage img, int x, int y, int width, int height, int boxNumber, int sizeClass) {
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(5 * sizeClass));
        g2d.drawRect(x, y, width, height);
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 25 * sizeClass));
        g2d.drawString(String.valueOf(boxNumber), x + 12 * sizeClass, y + 30 * sizeClass);
        g2d.dispose();
    }

    /**
     * Makes an API request to evaluate the image.
     */
    @NotNull
    public static JsonObject getEvaluation(String imageUrl) throws IOException {
        String token = Bootstrap.holo.getConfig().getDeepAIKey();
        Process pr = Runtime.getRuntime().exec("curl -F image=" + imageUrl + " -H api-key:" + token + " " + API_URL);
        String result = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines().collect(Collectors.joining("\n"));
        return JsonParser.parseString(result).getAsJsonObject();
    }
}