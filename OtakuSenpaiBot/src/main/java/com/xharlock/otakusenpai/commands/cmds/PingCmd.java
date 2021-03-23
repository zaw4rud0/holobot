package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Main;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
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
		long start = System.currentTimeMillis();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Pong!");
		builder.setDescription("Ping: `...` ms\nHeartbeat: `...` ms");
		if (e.isFromGuild()) {
			builder.setColor(getGuildColor(e.getGuild()));
			String name = e.getGuild().retrieveMember(e.getAuthor()).complete().getEffectiveName();
			builder.setFooter(Messages.CMD_INVOKED_BY.getText().replace("{0}", name),
					e.getAuthor().getEffectiveAvatarUrl());
		} else {
			builder.setColor(Main.otakuSenpai.getConfig().getColor());
		}

		e.getChannel().sendMessage(builder.build()).queue(msg -> {
			long ping = System.currentTimeMillis() - start;
			builder.setDescription("Ping: `" + ping + "` ms\nHeartbeat: `" + e.getJDA().getGatewayPing() + "` ms");
			msg.editMessage(builder.build()).queue();
			msg.delete().queueAfter(1L, TimeUnit.MINUTES);
		});
	}
}
