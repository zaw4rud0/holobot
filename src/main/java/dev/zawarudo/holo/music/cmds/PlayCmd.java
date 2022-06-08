package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "play",
		description = "Plays a given YouTube video or playlist. If there is already a track playing, it will be queued.",
		usage = "<url>",
		alias = {"p"},
		category = CommandCategory.MUSIC)
public class PlayCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter(String.format("Invoked by %s", e.getMember().getEffectiveName()),	e.getAuthor().getEffectiveAvatarUrl());
		
		if (!isUserInSameAudioChannel(e)) {
			builder.setTitle("Not in same voice channel!");
			builder.setDescription("You need to be in the same voice channel as me!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}
		
        if (args.length == 0) {
        	builder.setTitle("Wrong Usage");
        	builder.setDescription("Please provide a youtube link!");
        	sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
            return;
        }
        
        String link = args[0].replace("<", "").replace(">", "");
        
        if (!isValidURL(link)) {
            builder.setTitle("Invalid Link");
        	builder.setDescription("Please provide a valid youtube link!");
        	sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
            return;
        }
        
        PlayerManager.getInstance().loadAndPlay(e, builder, link);
	}
}
