package com.xharlock.holo.commands.cmds;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoBotCmd extends Command {

	public InfoBotCmd(String name) {
		super(name);
		setDescription("Use this command to display informations about me.");
		setUsage(name);
		setAliases(List.of("source", "bot", "sauce"));
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		String cpu_percentage = "";
		int cores = os.getAvailableProcessors();;
		
		if (os.getSystemLoadAverage() == -1) {
			cpu_percentage = "N/A";
		} else {
			double avg = os.getSystemLoadAverage();
			double cpu_usage = avg * 100.0 / (double) cores;
			cpu_percentage = "" + (cpu_usage * 100 / 100.0) + "%";
		}
		
		long heap = memory.getHeapMemoryUsage().getUsed();
		long max = memory.getHeapMemoryUsage().getMax();
		double heap_percentage = (double)((heap * 10000) / max) / 100.0;
		
		String system_info = "**CPU:** `" + cpu_percentage + " on " + cores + " core(s)`\n"
						+ "**Memory:** `" + heap / 1024 / 1024 + "MB / " + max / 1024 / 1024 + "MB (" + heap_percentage + "%)`\n"
						+ "**Uptime:** `" + Formatter.formatTime(System.currentTimeMillis() - Bootstrap.startup_time) + "`";

		String description = "Use `" + getPrefix(e) + "help` to see all commands";
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(e.getJDA().getSelfUser().getName() + " | Informations");
		builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		builder.setDescription(description);
		builder.addField("Creator", "<@" + Bootstrap.holo.getConfig().getOwnerId() + ">", false);
		builder.addField("Version", "`" + Bootstrap.holo.getConfig().getVersion() + "`", false);
		builder.addField("System Informations", system_info, false);
		builder.addField("Source", "No link yet", false);
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}
