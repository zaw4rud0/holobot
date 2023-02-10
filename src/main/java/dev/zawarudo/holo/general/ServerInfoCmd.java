package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

@Command(name = "serverinfo",
        description = "Shows information about the server",
        category = CommandCategory.GENERAL)
public class ServerInfoCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        Guild guild = event.getGuild();

        // Prepare the fields
        String creationDate = "`" + guild.getTimeCreated().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)) + "`";
        long normalCount = guild.getEmojis().stream().filter(em -> !em.isAnimated()).count();
        long animatedCount = guild.getEmojis().stream().filter(CustomEmoji::isAnimated).count();

        int stickerCount = guild.getStickers().size();
        int maxStickers;

        switch (guild.getBoostTier().getKey()) {
            case 1 -> maxStickers = 15;
            case 2 -> maxStickers = 30;
            case 3 -> maxStickers = 60;
            default -> maxStickers = 0;
        }

        String additionalChecks = "Normal Emotes: `" + normalCount + " / " + guild.getMaxEmojis() + "`\n"
                + "Animated Emotes: `" + animatedCount + " / " + guild.getMaxEmojis() + "`\n"
                + "Stickers: `" + stickerCount + " / " + maxStickers + "`\n"
                + "Channels: `" + guild.getChannels().size() + " / 500`\n"
                + "Roles: `" + guild.getRoles().size() + " / 250`";

        // Set the embed
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(guild.getName() + " (" + guild.getId() + ")");
        if (guild.getIconUrl() != null) {
            builder.setThumbnail(guild.getIconUrl());
        }
        builder.addField("Owner", guild.retrieveOwner().complete().getAsMention(), true);
        builder.addField("Members", String.valueOf(guild.getMemberCount()), true);
        builder.addField("Boost Level", String.valueOf(guild.getBoostTier().getKey()), false);
        builder.addField("Creation Date", creationDate, false);
        builder.addField("Additional Checks", additionalChecks, false);
        if (guild.getSplashUrl() != null) {
            builder.setImage(guild.getSplashUrl().replace(".png", ".webp") + "?size=4096");
        }

        sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);
    }
}