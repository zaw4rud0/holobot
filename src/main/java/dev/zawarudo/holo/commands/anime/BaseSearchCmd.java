package dev.zawarudo.holo.commands.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.misc.Emote;
import dev.zawarudo.holo.modules.jikan.model.AbstractMedium;
import dev.zawarudo.holo.modules.jikan.model.Nameable;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.HoloUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseSearchCmd<T extends AbstractMedium<T>> extends AbstractCommand {
    protected final EventWaiter waiter;
    protected final List<Emote> selection = HoloUtils.getNumbers();

    protected BaseSearchCmd(EventWaiter waiter) {
        this.waiter = waiter;
    }

    protected abstract List<T> performSearch(MessageReceivedEvent event, String search);

    protected abstract EmbedBuilder createSearchResultEmbed(List<T> results);

    protected abstract void setEmbedDetails(EmbedBuilder builder, T selected);

    protected void showSearchResults(MessageReceivedEvent event, List<T> result) {
        EmbedBuilder builder = createSearchResultEmbed(result);
        Message msg = event.getChannel().sendMessageEmbeds(builder.build()).complete();
        User caller = event.getAuthor();

        HoloUtils.addReactions(msg, result.size());
        AtomicInteger selected = new AtomicInteger(-1);

        waitForUserReaction(event, msg, caller, result, selected);
    }

    protected void waitForUserReaction(MessageReceivedEvent event, Message msg, User caller, List<T> result, AtomicInteger selected) {
        waiter.waitForEvent(
                MessageReactionAddEvent.class,
                evt -> isReactionValid(evt, msg, caller, result, selected),
                evt -> handleUserReaction(event, msg, result, selected),
                5,
                TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private boolean isReactionValid(MessageReactionAddEvent evt, Message msg, User caller, List<T> result, AtomicInteger selected) {
        if (evt.getMessageIdLong() != msg.getIdLong()) {
            return false;
        }
        if (evt.retrieveUser().complete().isBot() || !caller.equals(evt.retrieveUser().complete())) {
            return false;
        }
        for (int i = 0; i < result.size(); i++) {
            if (evt.getReaction().getEmoji().equals(selection.get(i).getAsEmoji())) {
                selected.set(i);
                return true;
            }
        }
        return false;
    }

    private void handleUserReaction(MessageReceivedEvent event, Message msg, List<T> result, AtomicInteger selected) {
        msg.delete().queue();
        sendSelection(event, result.get(selected.get()));
    }

    protected void sendSelection(MessageReceivedEvent event, T selected) {
        EmbedBuilder builder = createEmbedBuilder(selected);
        setEmbedDetails(builder, selected);
        sendEmbed(event, builder, true, getEmbedColor());
    }

    protected EmbedBuilder createEmbedBuilder(T selected) {
        EmbedBuilder builder = new EmbedBuilder();
        String type = selected.getType() == null ? "null" : selected.getType();

        String title = Formatter.truncateString(selected.getTitle(), MessageEmbed.TITLE_MAX_LENGTH - (type.length() + 3));
        builder.setTitle(String.format("%s [%s]", title, type));

        builder.setThumbnail(selected.getImages().getJpg().getLargeImage());
        if (selected.getSynopsis().isPresent()) {
            String synopsis = Formatter.truncateString(selected.getSynopsis().get(), MessageEmbed.DESCRIPTION_MAX_LENGTH);
            builder.setDescription(synopsis);
        }
        return builder;
    }

    protected String formatList(List<Nameable> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> strings = list.stream().map(Nameable::toString).toList();
        return String.join(", ", strings);
    }

    protected String formatScore(double score) {
        return score == 0.0 ? "N/A" : String.valueOf(score);
    }

    protected String formatRank(int rank) {
        return rank == 0 ? "N/A" : String.valueOf(rank);
    }
}