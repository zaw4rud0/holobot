package com.xharlock.holo.music.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.misc.Emojis;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class SkipCmd extends MusicCommand {

	private EventWaiter waiter;

	public SkipCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to request a skip. About half of the members "
				+ "in the voice channel (bot excluded) that are actively listening (i.e. not deafened) "
				+ "have to react with an upvote in order to skip the track.");
		setUsage(name);
		this.waiter = waiter;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		// Checks if there are tracks to skip
		if (musicManager.audioPlayer.getPlayingTrack() == null) {
			builder.setTitle("Error");
			builder.setDescription("I'm not playing any tracks at the moment!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		// Bot owner can always skip
		if (isBotOwner(e)) {
			musicManager.setVoting(false);
			musicManager.getCounter().set(0);
			skip(e);
			return;
		}

		// Check vc conditions (user and bot in same vc, etc.)
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
		List<Member> listeners = new ArrayList<Member>();
		listeners.addAll(e.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
				.filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).collect(Collectors.toList()));

		int requiredVotes = (int) Math.floor(listeners.size() / 2.0);
		
		// User can skip without voting
		if (requiredVotes == 0) {
			musicManager.setVoting(false);
			musicManager.getCounter().set(0);
			skip(e);
			return;
		}

		builder.setTitle(e.getMember().getEffectiveName() + " requested a skip");
		builder.setDescription("Upvote to skip current track\n`" + requiredVotes + "` upvotes are required");
		builder.setColor(Bootstrap.holo.getConfig().getDefaultColor());

		e.getChannel().sendMessageEmbeds(builder.build()).queue(msg -> {

			msg.addReaction(Emojis.UPVOTE.getAsUnicode()).queue();

			waiter.waitForEvent(MessageReactionAddEvent.class, evt -> {
				// So reactions on other messages and bot reactions are ignored
				if (evt.getMessageIdLong() != msg.getIdLong() && !evt.getUser().isBot()) {
					return false;
				}

				if (listeners.contains(evt.getMember())	&& evt.getReactionEmote().getEmoji().equals(Emojis.UPVOTE.getAsBrowser())) {
					if (musicManager.getCounter().incrementAndGet() == requiredVotes) {
						return true;
					}
				}
				return false;

			}, evt -> {
				msg.delete().queue();
				musicManager.setVoting(false);
				musicManager.getCounter().set(0);
				skip(e);
			}, 1L, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
				musicManager.setVoting(false);
				musicManager.getCounter().set(0);
			});
		});
	}

	private void skip(MessageReceivedEvent e) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		EmbedBuilder builder = new EmbedBuilder();
		musicManager.scheduler.playNext();
		builder.setTitle("Skipped Track");
		builder.setDescription(musicManager.audioPlayer.getPlayingTrack() == null ? "Nothing to play next!"
				: "Now playing: `" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "`");
		sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
	}
}