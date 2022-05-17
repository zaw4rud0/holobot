package com.xharlock.holo.general;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "help",
		description = "Shows a list of commands or their respective usage",
		usage = "[command]",
		example = "ping",
		category = CommandCategory.GENERAL)
public class HelpCmd extends AbstractCommand {

	private final CommandManager manager;

	public HelpCmd(CommandManager manager) {
		this.manager = manager;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();

		// Given command doesn't exist
		if (args.length >= 1 && !manager.isValidName(args[0])) {
			builder.setTitle("Command not found");
			builder.setDescription("Please check for typos and try again!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		deleteInvoke(e);

		/*
		* TODO: Properly format and show all the fields of a command
		*/
		// Help page for given command
		if (args.length >= 1 && manager.isValidName(args[0])) {
			AbstractCommand cmd = manager.getCommand(args[0]);
			builder.setTitle("Command Help");
			builder.addField("Name", cmd.getName(), false);
			builder.addField("Description", cmd.getDescription(), false);
			
			if (cmd.getUsage() != null) {
				builder.addField("Usage", "`" + getPrefix(e) + cmd.getName() + " " + cmd.getUsage() + "`", false);
			}
			if (cmd.getExample() != null) {
				builder.addField("Example", "`" + getPrefix(e) + cmd.getName() + " " + cmd.getExample() + "`", false);
			}
			if (cmd.getThumbnail() != null) {
				builder.setThumbnail(cmd.getThumbnail());
			}
			if (cmd.getAlias().length != 0) {
				StringBuilder aliases = new StringBuilder("`" + cmd.getAlias()[0] + "`");
				for (int i = 1; i < cmd.getAlias().length; i++) {
					aliases.append(", `").append(cmd.getAlias()[i]).append("`");
				}
				builder.addField("Alias", aliases.toString(), false);
			}
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, cmd.getEmbedColor());
			return;
		}

		// Open the full help page
		if (args.length == 0) {
			builder.setTitle("Help Page");
			builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl().concat("?size=512"));
			builder.setDescription("I currently use `" + getPrefix(e) + "` as prefix for all commands\n"
					+ "For more information on a certain command, use `" + getPrefix(e) + "help <command>`");

			for (CommandCategory category : CommandCategory.values()) {
				// Only show admin commands to guild admins (guild owner included) and bot-owner
				if (category.equals(CommandCategory.ADMIN) && !isGuildAdmin(e) && !isBotOwner(e)) {
					continue;
				}

				// Only show bot-owner commands to bot-owner
				if (category.equals(CommandCategory.OWNER) && !isBotOwner(e)) {
					continue;
				}

				List<AbstractCommand> cmds = manager.getCommands(category);

				// Command category is empty, nothing to display
				if (cmds.isEmpty()) {
					continue;
				}

				StringBuilder cmdsString = new StringBuilder("`" + cmds.get(0).getName() + "`");
				for (int i = 1; i < cmds.size(); i++) {
					cmdsString.append(", `").append(cmds.get(i).getName()).append("`");
				}
				builder.addField(category.getName(), cmdsString.toString(), false);
			}
			sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
		}
	}
}