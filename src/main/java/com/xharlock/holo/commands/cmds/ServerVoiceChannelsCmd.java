package com.xharlock.holo.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerVoiceChannelsCmd extends Command {

	public ServerVoiceChannelsCmd(String name) {
		super(name);
		setDescription("Use this command to view all voice channels of the server");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		List<VoiceChannel> vcs = e.getGuild().getVoiceChannels();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Voice Channels of " + e.getGuild().getName());

		if (vcs.isEmpty()) {
			builder.setDescription("This server doesn't have any voice channels.");
		} else {
			StringBuilder sb = new StringBuilder();
			for (VoiceChannel vc : vcs) {
				sb.append(vc.getAsMention() + "\n");
			}
			builder.setDescription(sb.toString());
		}
		sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
	}
}