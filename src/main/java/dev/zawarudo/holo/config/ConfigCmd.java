package dev.zawarudo.holo.config;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Command that lets the bot owner, the guild owner and the guild admins to
 * change the configurations of the bot for the guild in question.
 */
@Deactivated
@Command(name = "config",
		description = "See and change the configuration of the bot for this guild.",
		usage = "TODO",
		ownerOnly = true,
		category = CommandCategory.GENERAL)
public class ConfigCmd extends AbstractCommand {

	private final GuildConfigManager manager;

	public ConfigCmd(GuildConfigManager manager) {
		this.manager = manager;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();

		GuildConfig config = manager.getGuildConfig(e.getGuild());

		// Show the current configuration
		if (args.length == 0) {
			builder.setTitle("Bot Config of " + e.getGuild().getName());
			builder.setThumbnail(e.getGuild().getIconUrl());
			builder.setDescription("My current configuration for this server is the following:");
			builder.addField("Prefix", "```" + config.getPrefix() + "```", true);
			builder.addField("NSFW", "```" + config.isNSFWEnabled() + "```", true);
			builder.addBlankField(true);
			builder.addField("Pokémon Channel", "```SoonTM```", false);
			builder.addField("Music Channel", "```SoonTM```", false);
			sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
			return;
		}

		// Show information about a single module
		if (args.length == 1) {
			switch (args[0].toLowerCase()) {
				case "prefix" -> {
					builder.setTitle("Prefix Configuration");
					builder.setDescription("The prefix that will be used to invoke commands in this guild. To change " +
										"it, use `" + config.getPrefix() + "config prefix <new prefix>`.");
					sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
					return;
				}
				case "nsfw" -> {
					builder.setTitle("NSFW Configuration");
					builder.setDescription("This configuration determines if commands that might be considered " +
										"NSFW (not safe for work) are enabled. If enabled, NSFW commands will only " +
										"send messages in channels that are marked as NSFW. To change it, use `" +
										config.getPrefix() + "config nsfw <true/false>`.");
					sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
					return;
				}
			}
			return;
		}

		switch (args[0].toLowerCase()) {
			case "prefix" -> {
				String newPrefix = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				config.setPrefix(newPrefix);

				builder.setTitle("Prefix Changed");
				builder.setDescription("My new prefix is now `" + newPrefix + "`.");
				sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
			}
			case "nsfw" -> {
				boolean nsfw = Boolean.parseBoolean(args[1]);
				config.setAllowNSFW(nsfw);

				builder.setTitle("NSFW Config Changed");
				builder.setDescription("NSFW commands are now " + (nsfw ? "enabled" : "disabled") + ".");
				sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
			}
			case "pokemon" -> {

				// Check that a proper channel was provided



			}
			case "music" -> {

				// Check that a proper channel was provided

			}
			default -> {
				builder.setTitle("Invalid Config Option");
				builder.setDescription("TODO");
				sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
			}
		}
	}
}