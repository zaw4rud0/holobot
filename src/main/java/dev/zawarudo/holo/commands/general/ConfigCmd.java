package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.GuildConfig;
import dev.zawarudo.holo.core.GuildConfigManager;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Command to change the configurations of the bot for the guild.
 */
@Command(name = "config",
		description = "See and change the configuration of the bot for this guild.",
		ownerOnly = true,
		category = CommandCategory.GENERAL)
public class ConfigCmd extends AbstractCommand {

	private final GuildConfigManager configManager;

	public ConfigCmd(GuildConfigManager configManager) {
		this.configManager = configManager;
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		if (args.length == 0) {
			showCurrentConfig(event);
			return;
		}

		String module = args[0].toLowerCase(Locale.ROOT);

		switch (module) {
			case "prefix" -> {
				if (args.length == 1) showPrefixInfo(event);
				else changePrefix(event);
			}
			case "nsfw" -> {
				if (args.length == 1) showNSFWInfo(event);
				else changeNSFW(event);
			}
			case "reset" -> resetConfiguration(event);
			default -> showUnknownConfigurationEmbed(event);
		}
	}

	private void showCurrentConfig(MessageReceivedEvent event) {
		GuildConfig config = configManager.getGuildConfig(event.getGuild());

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(String.format("Bot Configuration for %s", event.getGuild().getName()))
				.setThumbnail(event.getGuild().getIconUrl())
				.setDescription(String.format("Here you can see all my configurations for this server. To " +
						"see a specific configuration and how to change it, run the following command: ```%sconfig " +
						"<config_name>```", getPrefix(event)))
				.addField("Prefix", "```" + config.getPrefix() + "```", true)
				.addField("NSFW", String.format("```%s```",
						config.isNSFWEnabled() ? "Enabled" : "Disabled"), true);

		sendEmbed(event, builder, true, 5, TimeUnit.MINUTES);
	}

	private void showPrefixInfo(MessageReceivedEvent event) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(String.join("Bot Prefix for %s", event.getGuild().getName()))
				.setDescription(String.format("The prefix is needed to run commands. To change my prefix, run the " +
						"following command:```%sconfig prefix <new_prefix>```", getPrefix(event)))
				.addField("Current Prefix", String.format("```%s```", getPrefix(event)), false);

		sendEmbed(event, builder, true, 5, TimeUnit.MINUTES);
	}

	private void changePrefix(MessageReceivedEvent event) {
		GuildConfig config = configManager.getGuildConfig(event.getGuild());

		String newPrefix = args[1];
		config.setPrefix(newPrefix);

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Prefix Changed")
				.addField("New Prefix", String.format("```%s```", newPrefix), false);

		sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);

		saveChanges(event, config);
	}

	private void showNSFWInfo(MessageReceivedEvent event) {
		GuildConfig config = configManager.getGuildConfig(event.getGuild());

		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle(String.format("NSFW Configuration for %s", event.getGuild().getName()));
		builder.setDescription(String.format("This configuration determines if the usage of commands that " +
						"might be considered NSFW (not safe for work) are allowed. If enabled, NSFW commands " +
						"will be usable in channels that are marked as 18+. If disabled, none of the NSFW " +
						"commands can be used in this server. To change it, use the following command: " +
						"```%sconfig nsfw <true/false>```", getPrefix(event)))
				.addField("Status", String.format("```%s```",
						config.isNSFWEnabled() ? "Enabled" : "Disabled"), false);
		sendEmbed(event, builder, true, 5, TimeUnit.MINUTES);
	}

	private void changeNSFW(MessageReceivedEvent event) {
		GuildConfig config = configManager.getGuildConfig(event.getGuild());

		boolean nsfw = Boolean.parseBoolean(args[1]);
		config.setAllowNSFW(nsfw);

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("NSFW Config Changed")
				.setDescription("NSFW commands are now " + (nsfw ? "enabled" : "disabled") + ".");

		sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);

		saveChanges(event, config);
	}

	private void resetConfiguration(MessageReceivedEvent event) {
		configManager.resetConfigurationForGuild(event.getGuild());
		GuildConfig config = configManager.getGuildConfig(event.getGuild());

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Reset Bot Configuration")
				.setDescription("My configuration for this server has been reset to the default settings.")
				.addField("Prefix", String.format("```%s```", getPrefix(event)), false);

		sendEmbed(event, builder, true, 30, TimeUnit.SECONDS);

		saveChanges(event, config);
	}

	private void showUnknownConfigurationEmbed(MessageReceivedEvent event) {
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Unknown Configuration")
				.setDescription(String.format("I don't know a configuration with the name `%s`. To see a " +
						"list of configurations, use the following command:```%sconfig```", args[0], getPrefix(event)));
		sendEmbed(event, builder, true, 1, TimeUnit.MINUTES);
    }

	/**
	 * Saves the new configuration in the database.
	 */
	private void saveChanges(MessageReceivedEvent event, GuildConfig config) {
		try {
			configManager.persist(config);
		} catch (SQLException ex) {
			sendErrorEmbed(event, "Something went wrong while updating your configuration in the " +
					"database. We will try to fix this ASAP.");
			logger.error("Something went wrong while storing the updated config in the database.", ex);
		}
	}
}