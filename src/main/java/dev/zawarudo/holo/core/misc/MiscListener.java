package dev.zawarudo.holo.core.misc;

import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Class that defines miscellaneous behaviour of the bot as response to certain events.
 */
public class MiscListener extends ListenerAdapter {

    private final List<Long> existingEmotes;

    private static final Logger LOGGER = LoggerFactory.getLogger(MiscListener.class);

    public MiscListener() {
        try {
            existingEmotes = DBOperations.getEmoteIds();
        } catch (SQLException ex) {
            throw new IllegalStateException("Something went wrong while fetching the emote ids from the database.");
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        // Ignore default emojis
        if (event.getEmoji().getType() == Emoji.Type.UNICODE) {
            return;
        }
        storeNewEmotesInDatabase(event.getEmoji().asCustom());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        List<CustomEmoji> messageEmotes = event.getMessage().getMentions().getCustomEmojis();
        if (!messageEmotes.isEmpty()) {
            storeNewEmotesInDatabase(messageEmotes.toArray(new CustomEmoji[0]));
        }

        String content = event.getMessage().getContentRaw().toLowerCase(Locale.UK);

        // Add ❤ as reaction
        if (content.contains(":heart:") || content.contains("<3") || content.contains("❤")) {
            LOGGER.info("REACTION: Reacting to heart");
            addReaction(event.getMessage(), Emote.HEART);
        }

        // Add 💀 as reaction
        if (Arrays.stream(content.split(" ")).anyMatch(s -> s.contains("forgor"))) {
            LOGGER.info("REACTION: Reacting to forgor");
            addReaction(event.getMessage(), Emote.SKULL);
        }
    }

    private void addReaction(@NotNull Message msg, @NotNull Emote emote) {
        msg.addReaction(emote.getAsEmoji()).queue(s -> {}, err -> {});
    }

    private void storeNewEmotesInDatabase(CustomEmoji... emotes) {
        List<CustomEmoji> newEmotes = Arrays.stream(emotes).filter(e -> !existingEmotes.contains(e.getIdLong())).toList();
        try {
            DBOperations.insertEmotes(newEmotes);
        } catch (SQLException ex) {
            LOGGER.error("Something went wrong while storing new emotes.", ex);
        }

        existingEmotes.addAll(Arrays.stream(emotes).map(ISnowflake::getIdLong).toList());
    }
}