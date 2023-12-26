package dev.zawarudo.holo.core.misc;

import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class that defines miscellaneous behaviour of the bot as response to certain events.
 */
public class MiscListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiscListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
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
}