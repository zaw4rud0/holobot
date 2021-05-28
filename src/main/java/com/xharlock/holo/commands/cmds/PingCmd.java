package com.xharlock.holo.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

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
		e.getChannel().sendTyping().queue();
		
		builder.setTitle("Pong!");
		builder.setDescription("Ping: `...` ms\nHeartbeat: `...` ms");
		long start = System.currentTimeMillis();
		Message message = sendEmbedAndGetMessage(e, builder, true);
		long ms = System.currentTimeMillis() - start;
		builder.setDescription("Ping: `" + ms + "` ms\nHeartbeat: `" + e.getJDA().getGatewayPing() + "` ms");		
		message.editMessage(builder.build()).queue();
		message.delete().queueAfter(1, TimeUnit.MINUTES);
	}
}
