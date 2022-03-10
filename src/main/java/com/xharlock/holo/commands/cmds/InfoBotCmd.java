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
		deleteInvoke(e);
		
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		String cpuPercentage = "";
		int cores = os.getAvailableProcessors();
		
		if (os.getSystemLoadAverage() == -1) {
			cpuPercentage = "N/A";
		} else {
			double avg = os.getSystemLoadAverage();
			double cpuUsage = avg * 100.0 / (double) cores;
			cpuPercentage = "" + (cpuUsage * 100 / 100.0) + "%";
		}
		
		long heap = memory.getHeapMemoryUsage().getUsed();
		long max = memory.getHeapMemoryUsage().getMax();
		double heapPercentage = (double)((heap * 10000) / max) / 100.0;
		
		String systemInfo = "**CPU:** `" + cpuPercentage + " on " + cores + " core(s)`\n"
						+ "**Memory:** `" + heap / 1024 / 1024 + "MB / " + max / 1024 / 1024 + "MB (" + heapPercentage + "%)`\n"
						+ "**Uptime:** `" + Formatter.formatTime(System.currentTimeMillis() - Bootstrap.startupTime) + "`";

		String description = "Use `" + getPrefix(e) + "help` to see all commands";
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(e.getJDA().getSelfUser().getName() + " | Information");
		builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		builder.setDescription(description);
		builder.addField("Creator", "<@" + Bootstrap.holo.getConfig().getOwnerId() + ">", false);
		builder.addField("Version", "`" + Bootstrap.holo.getConfig().getVersion() + "`", false);
		builder.addField("System Informations", systemInfo, false);
		builder.addField("Source", "No link yet", false);
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}