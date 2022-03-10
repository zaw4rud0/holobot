package com.xharlock.holo.core;

import com.google.gson.annotations.SerializedName;

import net.dv8tion.jda.api.entities.Message;

public class MessageToDelete {
	@SerializedName("channel_id")
	long channelId;
	@SerializedName("message_id")
	long messageId;
	
	public MessageToDelete(Message msg) {
		channelId = msg.getTextChannel().getIdLong();
		messageId = msg.getIdLong();
	}
}