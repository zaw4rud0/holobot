package com.xharlock.otakusenpai.misc;

public enum Messages {
	
	CMD_ADMIN_ONLY("This command is admin-only"),
	CMD_INVOKED_BY("Invoked by {0}"),
	CMD_OWNER_ONLY("This command is owner-only"),
	
	CMD_USER_ON_COOLDOWN("{0}, you are on cooldown!\nPlease wait `{1}` seconds before using this command again."),
	
	NO_NSFW_CHANNEL_PERM("You can't use a NSFW command in a non-NSFW channel, p-pervert!"),	
	NO_PRIVATE_CHAT_PERM("You are not allowed to use this command in a private chat!"),
	
	TITLE_CMD_DISABLED("Command Disabled"),
	TITLE_CMD_NSFW("NSFW Command"),
	TITLE_ON_COOLDOWN("On Cooldown!"),
	TITLE_ERROR("Error"),
	TITLE_HELP_PAGE("{0} | Help Page"),
	TITLE_INCORRECT_USAGE("Incorrect Usage"),
	TITLE_NO_PERM("No Permission"),
	;
	
	private String text;
	
	Messages(String text){
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}

}
