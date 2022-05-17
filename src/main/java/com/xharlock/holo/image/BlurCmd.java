package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.utils.ImageOperations;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Deactivated
@Command(name = "blur", description = "Blurs an image", adminOnly = true, category = CommandCategory.IMAGE)
public class BlurCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        BufferedImage raw;
        int intensity = Integer.parseInt(args[0]);

        e.getChannel().sendTyping().queue();

        try {
            InputStream in = e.getMessage().getAttachments().get(0).retrieveInputStream().get();
            raw = ImageIO.read(in);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (raw == null) {
            e.getChannel().sendMessage("No image found").queue();
            return;
        }

        int height = raw.getHeight();
        int width = raw.getWidth();

        BufferedImage result = ImageOperations.resize(raw, width / (5 * intensity), height / (5 * intensity));
        result = ImageOperations.resize(result, width, height);

        try {
            ImageIO.write(result, "png", new File("C:/Users/adria/Desktop/output.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        e.getMessage().reply("Image blurred!").queue();
    }
}