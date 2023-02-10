package dev.zawarudo.holo.misc;

import dev.zawarudo.holo.core.Bootstrap;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class that defines miscellaneous behaviour of the bot as response to certain events.
 */
public class MiscListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        String content = e.getMessage().getContentRaw().toLowerCase(Locale.UK);

        // Add ❤ as reaction
        if (content.contains(":heart:") || content.contains("<3") || content.contains("❤")) {
            addReaction(e.getMessage(), Emote.HEART);
        }

        // Add 💀 as reaction
        if (Arrays.stream(content.split(" ")).anyMatch(s -> s.contains("forgor"))) {
            addReaction(e.getMessage(), Emote.SKULL);
        }

        // Add 🍔 as reaction
        if (content.contains("burgir") || content.contains("burger") || content.contains("burgar")) {
            addReaction(e.getMessage(), Emote.BURGER);
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
            addReaction(e.getMessage(), Emote.PINGED);
        }
    }

    private void addReaction(@NotNull Message msg, @NotNull Emote emote) {
        msg.addReaction(emote.getAsEmoji()).queue(s -> {}, err -> {});
    }
}