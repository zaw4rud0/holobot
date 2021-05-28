package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.commands.core.CommandManager;

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
        e.getChannel().sendTyping().queue();
        
        // Given command doesn't exist or <help was called
        if (args.length == 1 && !this.manager.isValidName(args[0])) {
            addErrorReaction(e.getMessage());
            builder.setTitle("Command not found");
            builder.setDescription("Please check for typos and try again!");
            sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
            return;
        }
        
        e.getMessage().delete().queue();
        
        // Help page for given command
        if (args.length == 1 && this.manager.isValidName(args[0])) {
        	Command cmd = this.manager.getCommand(args[0]);
            builder.setTitle("Command Help");
            builder.addField("Name", cmd.getName(), false);
            builder.addField("Description", cmd.getDescription(), false);
            
            if (cmd.getUsage() != null)
            	builder.addField("Usage", "`" + getGuildPrefix(e.getGuild()) + cmd.getUsage() + "`", false);
            
            if (cmd.getExample() != null)
                builder.addField("Example", "`" + getGuildPrefix(e.getGuild()) + cmd.getExample() + "`", false);
            
            if (cmd.getAliases().size() != 0) {
                String aliases = "`" + cmd.getAliases().get(0) + "`";
                for (int i = 1; i < cmd.getAliases().size(); i++)
                    aliases += ", `" + cmd.getAliases().get(i) + "`";
                builder.addField("Aliases", aliases, false);
            }
        }
        
        // Open the full help page
        if (args.length == 0) {
        	builder.setTitle("Help Page");
            builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            builder.setDescription("I currently use `" + this.getGuildPrefix(e.getGuild()) + "` as prefix for all commands\n" + "For more information on each command, use `" + this.getGuildPrefix(e.getGuild()) + "help [command]`");
            
            
            // TODO Rework so it hides owner and admin commands from normal users
            for (int length = CommandCategory.values().length, k = 0; k < length; ++k) {
                CommandCategory category = CommandCategory.values()[k];
                
                List<Command> cmds = this.manager.getCommands(category);
                
                String cmdsString = "";
                
                if (cmds != null) {                	
                    if (!cmds.isEmpty()) {
                        cmdsString += "`" + cmds.get(0).getName() + "`";
                        
                        for (int j = 1; j < cmds.size(); ++j)
                            cmdsString = cmdsString + ", `" + cmds.get(j).getName() + "`";
                        
                        builder.addField(category.getName(), cmdsString, false);
                    }
                }
            }
        }
        
        sendEmbed(e, builder, 2, TimeUnit.MINUTES, true);
	}
}
