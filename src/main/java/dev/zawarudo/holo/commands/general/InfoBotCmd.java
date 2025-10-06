package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.database.Database;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

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
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		String cpuPercentage;
		int cores = os.getAvailableProcessors();
		
		if (os.getSystemLoadAverage() == -1) {
			cpuPercentage = "N/A";
		} else {
			double avg = os.getSystemLoadAverage();
			double cpuUsage = avg / cores * 100;
			cpuPercentage = (cpuUsage * 100 / 100.0) + "%";
		}
		
		long usedMemoryBytes = memory.getHeapMemoryUsage().getUsed();
		long maxMemoryBytes = memory.getHeapMemoryUsage().getMax();

		double usedMemoryMB = usedMemoryBytes / (1024.0 * 1024);
		double maxMemoryMB = maxMemoryBytes / (1024.0 * 1024);

		double usedMemoryPercentage = (usedMemoryBytes / (double) maxMemoryBytes) * 100;
		
		String systemInfo = "**CPU:** `" + cpuPercentage + " on " + cores + " core(s)`\n"
						+ "**Memory:** `" + String.format("%.2f MB / %.2f MB (%.2f%%)", usedMemoryMB, maxMemoryMB, usedMemoryPercentage) + "`\n"
						+ "**Uptime:** `" + Formatter.formatTime(System.currentTimeMillis() - Bootstrap.getStartupTime()) + "`";

		String description = "Use `" + getPrefix(e) + "help` to see all commands";

		EmbedBuilder builder = getInfoEmbed(e, description, systemInfo);
		sendEmbed(e, builder, true, 1, TimeUnit.MINUTES);
	}

	@NotNull
	private static EmbedBuilder getInfoEmbed(@NotNull MessageReceivedEvent e, String description, String systemInfo) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(e.getJDA().getSelfUser().getName() + " | Information");
		builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl().concat("?size=512"));
		builder.setDescription(description);
		builder.addField("Creator", "<@" + Bootstrap.holo.getConfig().getOwnerId() + ">", false);
		builder.addField("Bot Version", "`" + Bootstrap.holo.getConfig().getVersion() + "`", false);
		builder.addField("JDA Version", "`" + JDAInfo.VERSION.replace("_" + JDAInfo.COMMIT_HASH, "") + "`", false);
		builder.addField("System Information", systemInfo, false);
		builder.addField("Database Size", "`" + new File(Database.getDbPath()).length() / 1024 / 1024 + "MB`", false);
		builder.addField("Source", "[GitHub](https://github.com/xHarlock/HoloBot)", false);
		return builder;
	}
}