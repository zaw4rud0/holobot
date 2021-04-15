package com.xharlock.otakusenpai.image;

import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AvatarCmd extends Command {

	public AvatarCmd(String name) {
		super(name);
		setDescription("Use this command to get your own avatar or the avatar of a given user inside the guild. Tip: Use the id of the user if you don't want to ping them.");
		setUsage(name + " [user]");
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		EmbedBuilder builder = new EmbedBuilder();
		
        if (args.length > 1) {
            builder.setTitle(Messages.TITLE_INCORRECT_USAGE.getText());
            builder.setDescription("Please provide only one argument");
            sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
            return;
        }
        
        e.getMessage().delete().queue();
        
        User user = e.getAuthor();
        
        try {
            user = e.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
        }        
        catch (Exception ex) {}
        
        Member member = e.getGuild().retrieveMember(user).complete();
        
        String url = user.getEffectiveAvatarUrl() + "?size=512";        
        builder.setTitle(member.getEffectiveName() + "'s Avatar", url);
        builder.setImage(url);
        
        sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);		
	}

}
