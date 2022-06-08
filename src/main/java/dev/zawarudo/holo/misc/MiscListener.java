package dev.zawarudo.holo.misc;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Locale;

public class MiscListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String content = e.getMessage().getContentRaw().toLowerCase(Locale.UK);

        // Add ❤ as reaction
        if (content.contains(":heart:") || content.contains("<3") || content.contains("❤")) {
            addReaction(e.getMessage(), Emoji.HEART);
        }

        // Add 💀 as reaction
        if (Arrays.stream(content.split(" ")).anyMatch(s -> s.contains("forgor"))) {
            addReaction(e.getMessage(), Emoji.SKULL);
        }

        // Add 🍔 as reaction
        if (content.contains("burgir") || content.contains("burger") || content.contains("burgar")) {
            addReaction(e.getMessage(), Emoji.BURGER);
        }

        // React to pings
        User self = e.getJDA().getSelfUser();
        Message msg = e.getMessage().getReferencedMessage();
        if (msg != null && msg.getAuthor().equals(self)) {
            // Ignore self-replies
            return;
        }
        if (e.getMessage().getMentions().isMentioned(self)) {
            addReaction(e.getMessage(), Emote.PINGED);
        }
    }

    private void addReaction(Message msg, Emoji emoji) {
        msg.addReaction(emoji.getAsDisplay()).queue(s -> {}, err -> {});
    }

    private void addReaction(Message msg, Emote emote) {
        msg.addReaction(emote.getAsReaction()).queue(s -> {}, err -> {});
    }
}