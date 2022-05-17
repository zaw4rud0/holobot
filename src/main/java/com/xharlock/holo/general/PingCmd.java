package com.xharlock.holo.general;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "ping",
		description = "Shows the ping of the bot",
		alias = {"pong"},
		category = CommandCategory.GENERAL)
public class PingCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();		
		builder.setTitle("Pong!");
		builder.setDescription("Ping: `...` ms\nHeartbeat: `...` ms");
		long start = System.currentTimeMillis();
		Message message = sendEmbedAndGetMessage(e, builder, true);
		long ms = System.currentTimeMillis() - start;
		builder.setDescription("Ping: `" + ms + "` ms\nHeartbeat: `" + e.getJDA().getGatewayPing() + "` ms");		
		message.editMessageEmbeds(builder.build()).queue();
		message.delete().queueAfter(1, TimeUnit.MINUTES);
	}
}