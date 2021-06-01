package com.xharlock.holo.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.commands.core.CommandManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCmd extends Command {

	private CommandManager manager;

	public HelpCmd(String name, CommandManager manager) {
		super(name);
		setDescription("Use this comamnd to display a list of all commands or to show more informations about a specific command.");
		setUsage("help [command]");
		setExample("help ping");
		setCommandCategory(CommandCategory.GENERAL);
		this.manager = manager;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();

		// Given command doesn't exist
		if (args.length == 1 && !this.manager.isValidName(args[0])) {
			addErrorReaction(e.getMessage());
			builder.setTitle("Command not found");
			builder.setDescription("Please check for typos and try again!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		if (e.isFromGuild())
			e.getMessage().delete().queue();

		// Help page for given command
		if (args.length == 1 && this.manager.isValidName(args[0])) {
			Command cmd = this.manager.getCommand(args[0]);
			builder.setTitle("Command Help");
			builder.addField("Name", cmd.getName(), false);
			builder.addField("Description", cmd.getDescription(), false);

			if (cmd.getUsage() != null)
				builder.addField("Usage", "`" + getPrefix(e) + cmd.getUsage() + "`", false);

			if (cmd.getExample() != null)
				builder.addField("Example", "`" + getPrefix(e) + cmd.getExample() + "`", false);

			if (cmd.getAliases().size() != 0) {
				String aliases = "`" + cmd.getAliases().get(0) + "`";
				for (int i = 1; i < cmd.getAliases().size(); i++)
					aliases += ", `" + cmd.getAliases().get(i) + "`";
				builder.addField("Aliases", aliases, false);
			}
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}

		// Open the full help page
		if (args.length == 0) {
			builder.setTitle("Help Page");
			builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
			builder.setDescription("I currently use `" + getPrefix(e) + "` as prefix for all commands\n"
					+ "For more information on a certain command, use `" + getPrefix(e) + "help <command>`");

			for (CommandCategory category : CommandCategory.values()) {

				// Only show admin commands to guild admins (guild owner included) and bot-owner
				if (category.equals(CommandCategory.ADMIN) && !isGuildAdmin(e) && !isBotOwner(e))
					continue;

				// Only show bot-owner commands to the bot-owner
				if (category.equals(CommandCategory.OWNER) && !isBotOwner(e))
					continue;

				List<Command> cmds = this.manager.getCommands(category);

				// Command category is empty, nothing to display
				if (cmds.isEmpty())
					continue;

				String cmdsString = "`" + cmds.get(0).getName() + "`";
				for (int i = 1; i < cmds.size(); i++) {
					cmdsString += ", `" + cmds.get(i).getName() + "`";
				}
				builder.addField(category.getName(), cmdsString, false);
			}
			sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
		}
	}
}
