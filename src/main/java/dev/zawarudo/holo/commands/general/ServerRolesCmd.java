package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandInfo(name = "serverroles",
		description = "Shows all the roles of the server",
		category = CommandCategory.GENERAL)
public class ServerRolesCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Roles of " + e.getGuild().getName());
		
		List<Role> roles = e.getGuild().getRoles();

		if (roles.isEmpty()) {
			builder.setDescription("This server doesn't have any roles");
			sendEmbed(e, builder, true, 1, TimeUnit.MINUTES);
			return;
		}

		StringBuilder fieldContent = new StringBuilder();
		int counter = 0;

		for (Role role : roles) {
			String roleName = role.getAsMention();
			String roleId = role.getId();
			String roleText = String.format("%s%n(%s)%n", roleName, roleId);

			if (fieldContent.length() + roleText.length() > MessageEmbed.VALUE_MAX_LENGTH) {
				builder.addField(Integer.toString(counter), fieldContent.toString(), true);
				fieldContent = new StringBuilder(roleText);
				counter++;
			} else {
				fieldContent.append(roleText);
			}
		}

		if (!fieldContent.isEmpty()) {
			builder.addField(Integer.toString(counter), fieldContent.toString(), true);
		}

		sendEmbed(e, builder, true, 2, TimeUnit.MINUTES);
	}
}