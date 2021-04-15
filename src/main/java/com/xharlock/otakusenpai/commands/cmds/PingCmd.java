package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Bootstrap;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCmd extends Command {

	public PingCmd(String name) {
		super(name);
		setDescription("Use this command to see my latency.");
		setUsage(name);
		setAliases(List.of("pong"));
		setCmdCooldown(10);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Pong!");
		builder.setDescription("Ping: `...` ms\nHeartbeat: `...` ms");
		if (e.isFromGuild()) {
			builder.setColor(getGuildColor(e.getGuild()));
			builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", e.getMember().getEffectiveName()),
					e.getAuthor().getEffectiveAvatarUrl());
		} else {
			builder.setColor(Bootstrap.otakuSenpai.getConfig().getColor());
		}
		long start = System.currentTimeMillis();
		Message message = e.getChannel().sendMessage(builder.build()).complete();
		long ms = System.currentTimeMillis() - start;
		builder.setDescription("Ping: `" + ms + "` ms\nHeartbeat: `" + e.getJDA().getGatewayPing() + "` ms");		
		message.editMessage(builder.build()).queue();
		message.delete().queueAfter(1, TimeUnit.MINUTES);
	}
}
