package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// TODO: Add argument to see guild-wide perms or only perms within a specific channel

@Deactivated
@Command(name = "perm",
		description = "Shows my permissions in this channel",
		category = CommandCategory.GENERAL)
public class PermCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();		
		builder.setTitle("My Permissions");
		String s = e.getGuild().getSelfMember().getPermissions().stream()
				.map(Permission::getName)
				.collect(Collectors.joining("\n"));
		builder.setDescription("```" + s + "```");
		sendEmbed(e, builder, true, 1, TimeUnit.MINUTES);
	}
}