package dev.zawarudo.holo.utils;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.misc.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class containing miscellaneous utility methods.
 */
public class HoloUtils {

    /**
     * Returns a list of {@link Emote}s representing the numbers one to ten.
     */
    public static @NotNull List<Emote> getNumbers() {
        return new ArrayList<>(List.of(
                Emote.ONE, Emote.TWO, Emote.THREE, Emote.FOUR, Emote.FIVE,
                Emote.SIX, Emote.SEVEN, Emote.EIGHT, Emote.NINE, Emote.TEN
        ));
    }

    /**
     * Returns a list of {@link Emote}s representing the numbers one to ten.
     *
     * @param count The number of elements to return.
     */
    public static List<Emote> getNumbers(int count) {
        return getNumbers().subList(0, count);
    }

    /**
     * Adds emotes to a message and listens to the selection of the user.
     *
     * @param waiter The {@link EventWaiter} used to listen for reactions.
     * @param msg    The message to listens to.
     * @param caller The user who called the command.
     * @param size   The number of choices to select from.
     * @return The index of the selected choice, from 0 to size - 1.
     */
    public static int sendAndGetSelection(EventWaiter waiter, Message msg, User caller, int size) {
        List<Emote> numbers = getNumbers();
        for (int i = 0; i < size; i++) {
            msg.addReaction(numbers.get(i).getAsEmoji()).queue();
        }

        AtomicInteger selected = new AtomicInteger(-1);

        waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {
                    // Ignore reactions on other messages
                    if (evt.getMessageIdLong() != msg.getIdLong()) {
                        return false;
                    }

                    // Ignore reactions from bots or users other than the caller
                    if (evt.retrieveUser().complete().isBot() || !caller.equals(evt.retrieveUser().complete())) {
                        return false;
                    }

                    for (int i = 0; i < size; i++) {
                        if (evt.getReaction().getEmoji().equals(numbers.get(i).getAsEmoji())) {
                            selected.set(i);
                            return true;
                        }
                    }
                    return false;
                },
                evt -> msg.delete().queue(),
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue());

        return selected.get();
    }

    public static void addReactions(Message msg, int count) {
        for (Emote emote : HoloUtils.getNumbers(count)) {
            msg.addReaction(emote.getAsEmoji()).queue(s -> {}, e -> {});
        }
    }
}