package com.xharlock.holo.music.cmds;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "clear",
		description = """
					Requests to clear the queue. About half of the members in the voice channel (bot excluded) that are actively listening (i.e. not deafened) have to react with an upvote in order to clear the queue.
					""",
		category = CommandCategory.MUSIC)
public class ClearCmd extends AbstractMusicCommand {

	private final EventWaiter waiter;

	public ClearCmd(EventWaiter waiter) {
		this.waiter = waiter;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		// Checks if queue is empty
		if (musicManager.scheduler.queue.isEmpty()) {
			builder.setTitle("Error");
			builder.setDescription("My queue is already empty");
			sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
			return;
		}

		// Owner can always clear
		if (e.getAuthor().getIdLong() == Bootstrap.holo.getConfig().getOwnerId()) {
			musicManager.setVoting(false);
			musicManager.getCounter().set(0);
			clear(e);
			return;
		}
		
		// Checks vc conditions (user and bot in same vc, etc.)
		if (!isUserInSameAudioChannel(e)) {
			builder.setTitle("Not in same voice channel!");
			builder.setDescription("You need to be in the same voice channel as me!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		// Checks if there is already a voting for the guild
		if (musicManager.isVoting()) {
			builder.setTitle("Already voting!");
			builder.setDescription("There is already a voting going on!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		musicManager.setVoting(true);

		// Add all members in the voice channel that are not deafened to the list of
		// active listeners
		List<Member> listeners = e.getGuild().getSelfMember().getVoiceState().getChannel()
				.getMembers().stream().filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).toList();

		int requiredVotes = (int) Math.floor(listeners.size() / 2.0);

		// User can clear without voting
		if (requiredVotes == 0) {
			musicManager.setVoting(false);
			musicManager.getCounter().set(0);
			clear(e);
			return;
		}

		builder.setTitle(e.getMember().getEffectiveName() + " requested to clear the queue");
		builder.setDescription("Upvote to clear the queue\n`" + requiredVotes + "` upvotes are required");

		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {
			msg.addReaction(Emoji.UPVOTE.getAsDisplay()).queue(v -> {}, err -> {});

			waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {

				// So reactions on other messages and bot reactions are ignored
				if (evt.getMessageIdLong() != msg.getIdLong() && !evt.getUser().isBot()) {
					return false;
				}

				if (listeners.contains(evt.getMember())	&& evt.getReactionEmote().getEmoji().equals(Emoji.UPVOTE.getAsDisplay())) {
					if (musicManager.getCounter().incrementAndGet() == requiredVotes) {
						return true;
					}
				}
				return false;

			}, evt -> {
				msg.delete().queue();
				musicManager.setVoting(false);
				musicManager.getCounter().set(0);
				clear(e);
			}, 1L, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
				musicManager.setVoting(false);
				musicManager.getCounter().set(0);
			});
		});
	}

	private void clear(MessageReceivedEvent e) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		EmbedBuilder builder = new EmbedBuilder();
		musicManager.scheduler.queue.clear();
		builder.setTitle("Queue Cleared");
		builder.setDescription("My queue is now empty");
		sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
	}
}