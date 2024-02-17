package dev.zawarudo.holo.core.misc;

import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        List<CustomEmoji> messageEmotes = e.getMessage().getMentions().getCustomEmojis();
        if (!messageEmotes.isEmpty()) {
            storeNewEmotesInDatabase(messageEmotes);
        }

        String content = e.getMessage().getContentRaw().toLowerCase(Locale.UK);

        // Add ❤ as reaction
        if (content.contains(":heart:") || content.contains("<3") || content.contains("❤")) {
            logInfo("REACTION: Reacting to heart");
            addReaction(e.getMessage(), Emote.HEART);
        }

        // Add 💀 as reaction
        if (Arrays.stream(content.split(" ")).anyMatch(s -> s.contains("forgor"))) {
            logInfo("REACTION: Reacting to forgor");
            addReaction(e.getMessage(), Emote.SKULL);
        }

        // React to pings
        User self = e.getJDA().getSelfUser();
        Message msg = e.getMessage().getReferencedMessage();
        if (msg != null && msg.getAuthor().equals(self)) {
            // Ignore pings from self-replies
            return;
        }
        if (e.getMessage().getAuthor().getIdLong() == Bootstrap.holo.getConfig().getOwnerId()) {
            // Ignore pings from the bot owner
            return;
        }
        if (e.getMessage().getMentions().isMentioned(self)) {
            logInfo("REACTION: Reacting to ping");
            addReaction(e.getMessage(), Emote.PINGED);
        }
    }

    private void addReaction(@NotNull Message msg, @NotNull Emote emote) {
        msg.addReaction(emote.getAsEmoji()).queue(s -> {}, err -> {});
    }

    private void logInfo(String msg, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(msg, args);
        }
    }

    private void storeNewEmotesInDatabase(List<CustomEmoji> emotes) {
        List<CustomEmoji> newEmotes = emotes.stream().filter(e -> !existingEmotes.contains(e.getIdLong())).toList();
        try {
            DBOperations.insertEmotes(newEmotes);
        } catch (SQLException ex) {
            LOGGER.error("Something went wrong while storing new emotes.", ex);
        }
    }
}