package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.TimeUnit;

@Command(name = "info",
		description = "Shows information about me",
		alias = {"source", "bot", "sauce"},
		category = CommandCategory.GENERAL)
public class InfoBotCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		deleteInvoke(e);
		
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		String cpuPercentage;
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
		builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl().concat("?size=512"));
		builder.setDescription(description);
		builder.addField("Creator", "<@" + Bootstrap.holo.getConfig().getOwnerId() + ">", false);
		builder.addField("Bot Version", "`" + Bootstrap.holo.getConfig().getVersion() + "`", false);
		builder.addField("JDA Version", "`" + JDAInfo.VERSION.replace("_" + JDAInfo.COMMIT_HASH, "") + "`", false);
		builder.addField("System Information", systemInfo, false);
		builder.addField("Database Size", "`" + new File(Database.PATH_DB).length() / 1024 / 1024 + "MB`", false);
		builder.addField("Source", "[GitHub](https://github.com/xHarlock/HoloBot)", false);
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}
}