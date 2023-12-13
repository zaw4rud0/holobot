package dev.zawarudo.holo.commands.image.nsfw;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "block",
        description = "Requests to block an image. Simply reply to a message containing " +
                "the image you want to block. Note that this command is intended for NSFW " +
                "(not safe for work) images and that you will be blacklisted if you abuse it.",
        category = CommandCategory.IMAGE)
public class BlockCmd extends AbstractCommand {

    private List<String> blocked;
    private List<String> blockRequests;

    public BlockCmd() {
        // Get the blocked images from the DB
        try {
            blocked = DBOperations.getBlockedImages();
            //blockRequests = getBlockRequests();
            blockRequests = new ArrayList<>();
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Something went wrong while initializing the blocked user list!", e);
            }
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        // TODO Code part to call the block request list so that it can be reviewed by the owner

        EmbedBuilder builder = new EmbedBuilder();

        if (e.getMessage().getReferencedMessage() == null) {
            builder.setTitle("Incorrect Usage");
            builder.setDescription("Please reply to a message containing the image to block it");
            sendEmbed(e, builder, false, 15, TimeUnit.SECONDS);
            return;
        }

        // Bot owner can directly block the image
        if (isBotOwner(e.getAuthor())) {
            block(e);
        }
    }

    /**
     * Effectively block the image, can only be done by the owner
     */
    public void block(MessageReceivedEvent event) {
        deleteInvoke(event);

        if (event.getMessage().getReferencedMessage() == null) {
            // TODO: Add error message
            return;
        }

        String url = getImage(event.getMessage().getReferencedMessage());

        if (url == null) {
            sendErrorEmbed(event, "Image not found! Please make sure the message you replied to contains an image.");
            return;
        }

        try {
            DBOperations.insertBlockedImage(url, event.getAuthor().getIdLong(), event.getMessage().getTimeCreated().toString(), "None given");
        } catch (SQLException ex) {
            sendErrorEmbed(event, "Something went wrong.");
            if (logger.isErrorEnabled()) {
                logger.error("Something went wrong while blocking an image.", ex);
            }
            return;
        }

        blocked.add(url);

        if (event.isFromGuild()) {
            event.getMessage().getReferencedMessage().delete().queue();
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Image has been bonked");
        builder.setDescription("The image has been added to the blocklist and won't appear ever again");
        sendEmbed(event, builder, false, 15, TimeUnit.SECONDS);
    }

    public List<String> getBlockedImages() {
        return new ArrayList<>(blocked);
    }

    public boolean isBlocked(String imageUrl) {
        return blocked.contains(imageUrl);
    }

    public List<String> getBlockRequests() {
        return new ArrayList<>(blockRequests);
    }

    public boolean isBlockRequested(String imageUrl) {
        return blockRequests.contains(imageUrl);
    }
}