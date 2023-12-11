package dev.zawarudo.holo.misc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a submission from a user to the bot owner. This can either
 * be a bug report or a suggestion.
 */
public class Submission {

    public final String type;
    public final String date;
    public final Guild guild;
    public final Channel channel;
    public final User author;
    public final String message;

    public Submission(String type, MessageReceivedEvent event, String message) {
        this.type = type;
        this.date = event.getMessage().getTimeCreated().toString();
        this.guild = event.getGuild();
        this.channel = event.getChannel();
        this.author = event.getAuthor();
        this.message = message;
    }
}