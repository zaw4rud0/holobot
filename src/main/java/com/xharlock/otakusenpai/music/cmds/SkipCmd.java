package com.xharlock.otakusenpai.music.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.music.core.GuildMusicManager;
import com.xharlock.otakusenpai.music.core.MusicCommand;
import com.xharlock.otakusenpai.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class SkipCmd extends MusicCommand {

	private EventWaiter waiter;
	
	// Use this to check if there is already a voting going on -> Prevents multiple skips at once
	private HashMap<Guild, Boolean> voting;
	
	// Each guild has their own count
	private HashMap<Guild, AtomicInteger> voteCount;

	public SkipCmd(String name, EventWaiter waiter) {
		super(name);
		setDescription("Use this command to request a skip. About half of the members "
				+ "in the voice channel (bot excluded) that are actively listening (i.e. not deafened) "
				+ "have to react with an upvote in order to skip the track.");
		setUsage(name);
		this.waiter = waiter;
		this.voting = new HashMap<>();
		this.voteCount = new HashMap<>();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();

		// Checks if there is already a voting for the guild
		if (voting.containsKey(e.getGuild()) && voting.get(e.getGuild())) {
			builder.setTitle("Already voting!");
			builder.setDescription("There is already a voting going on to skip the current track!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		// Checks if there are tracks to skip
		if (musicManager.audioPlayer.getPlayingTrack() == null) {
			builder.setTitle("Error");
			builder.setDescription("I'm not playing any tracks at the moment!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// Check vc conditions (user and bot in same vc, etc.)
		if (!isUserInSameChannel(e)) {
			builder.setTitle("Not in same voice channel!");
			builder.setDescription("You need to be in the same voice channel as me!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		// Skip voting started in this guild
		if (this.voting.containsKey(e.getGuild()))
			this.voting.replace(e.getGuild(), true);
		else
			this.voting.put(e.getGuild(), true);

		List<Member> listeners = new ArrayList<Member>();
		// Add all members that are not deafened to the list of active listeners
		listeners.addAll(e.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
				.filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).collect(Collectors.toList()));

		int requiredVotes = (int) Math.floor(listeners.size() / 2.0);

		if (requiredVotes == 0) {
			skip(e);
			return;
		}

		builder.setTitle(e.getMember().getEffectiveName() + " requested a skip");
		builder.setDescription("Upvote to skip current track\n`" + requiredVotes + "` upvotes are required");

		e.getChannel().sendMessage(builder.build()).queue(msg -> {

			msg.addReaction(Emojis.UPVOTE.getAsReaction()).queue(v -> {
			}, err -> {
			});

			waiter.waitForEvent(GuildMessageReactionAddEvent.class, evt -> {

				// Checks that reaction is added to this message and not any other
				// Reactions by bots are ignored
				// Only reactions from active listeners may count
				if (evt.getMessageId().equals(msg.getId()) && !evt.retrieveUser().complete().isBot()) {
					if (listeners.contains(evt.getMember()) && evt.getReactionEmote().getEmoji().equals("⬆"))
						return true;
				}
				
				return false;
			}, evt -> {
				msg.delete().queue();
				skip(e);
				if (voting.containsKey(e.getGuild()))
					voting.replace(e.getGuild(), false);
				else
					voting.put(e.getGuild(), false);
			}, 1L, TimeUnit.MINUTES, () -> {
				msg.delete().queue();
				if (voting.containsKey(e.getGuild()))
					voting.replace(e.getGuild(), false);
				else
					voting.put(e.getGuild(), false);
			});
		});

	}

	private void skip(MessageReceivedEvent e) {
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		EmbedBuilder builder = new EmbedBuilder();
		musicManager.scheduler.playNext();		
		builder.setTitle("Skipped Track");
		builder.setDescription("Now playing: `" + musicManager.audioPlayer.getPlayingTrack().getInfo().title + "`");
		sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
	}

}
