package dev.zawarudo.holo.commands.image.nsfw;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Deactivated
@Command(name = "holo",
        description = "Sends a random image of Holo from Spice & Wolf.",
        alias = {"bestgirl", "waifu", "wisewolf"},
        category = CommandCategory.IMAGE)
public class HoloCmd extends AbstractCommand {

    private static final String API_URL = "https://nekos.life/api/v2/img/holo";

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        BlockCmd blockCmd = (BlockCmd) Bootstrap.holo.getCommandManager().getCommand("block");

        deleteInvoke(event);

        EmbedBuilder builder = new EmbedBuilder();
        String url;

        try {
            do {
                url = HttpResponse.getJsonObject(API_URL).get("url").getAsString();
            } while (blockCmd.isBlocked(url) || blockCmd.isBlockRequested(url));
        } catch (IOException ex) {
            sendErrorEmbed(event, "Something went wrong while fetching an image. Please try again in a few minutes!");
            return;
        }

        builder.setTitle("Holo");
        builder.setImage(url);
        sendEmbed(event, builder, true);
    }
}