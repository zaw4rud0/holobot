package com.xharlock.otakusenpai.music.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.xharlock.otakusenpai.music.core.*;
import com.xharlock.otakusenpai.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QueueCmd extends MusicCommand {

	public QueueCmd(String name) {
		super(name);
		setDescription("Use this command to see the current queue");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Queue");
        
        if (queue.isEmpty()) {
            builder.setDescription("My queue is empty!");
            sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
            return;
        }
        
        int trackCount = Math.min(queue.size(), 12);
        
        List<AudioTrack> trackList = new ArrayList<AudioTrack>(queue);
        StringBuilder sb = new StringBuilder();        
        
        for (int i = 0; i < trackCount; ++i) {        	
            AudioTrack track = trackList.get(i);
            AudioTrackInfo info = track.getInfo();            
            sb.append(String.format("`#%02d %s by %s [%s]`", i + 1, info.title, info.author, Formatter.formatTrackTime(track.getDuration()))).append("\n");
        }
        
        if (trackList.size() > trackCount) {
            sb.append("And `").append(trackList.size() - trackCount).append("` more...");
        }
        
        long duration = 0L;        
        for (AudioTrack track : trackList) {
        	duration += track.getDuration();
        }
        
        builder.setDescription(sb.toString());        
        builder.addField("Total Duration", Formatter.formatTrackTime(duration), false);
       
        sendEmbed(e, builder, 1L, TimeUnit.MINUTES, true);
	}

}
