package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.utils.ImageOperations;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * TODO:
 *  - Check for arguments
 *  - Check if image was provided and is valid
 */

@Deactivated
@Command(name = "pixelate",
        description = "Pixelates an image",
        alias = {"pixel"},
        ownerOnly = true,
        category = CommandCategory.IMAGE)
public class PixelateCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        int intensity = 1;

        if (args.length == 0) {

          // Provide arguments

        } else if (isInteger(args[0])) {
            intensity = Integer.parseInt(args[0]);

            if (intensity < 1 || intensity > 10) {
                e.getChannel().sendMessage("Intensity must be between 1 and 10").queue();
                return;
            }
        } else {
            return;
        }

        try {
            BufferedImage img = fetchImage(getImage(e.getMessage()));

            if (img == null) {
                e.getChannel().sendMessage("Could not read image").queue();
                return;
            }

            BufferedImage result = pixelate(img, intensity);
            e.getMessage().reply(ImageOperations.toInputStream(result), "result.png").queue();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Something went wrong
        }
    }

    private BufferedImage fetchImage(String url) throws IOException {
        return ImageIO.read(new URL(url).openStream());
    }

    private BufferedImage pixelate(@NotNull BufferedImage img, int intensity) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = ImageOperations.resize(img, width / (intensity * 4), height / (intensity * 4));
        return ImageOperations.resize(result, width, height);
    }
}