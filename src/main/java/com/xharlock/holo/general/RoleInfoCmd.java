package com.xharlock.holo.general;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(name = "roleinfo",
        description = "Shows information about a role.",
        usage = "<role name, id or mention>",
        category = CommandCategory.GENERAL)
public class RoleInfoCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        deleteInvoke(e);

        EmbedBuilder builder = new EmbedBuilder();
        Role role = null;

        if (args.length == 0) {
            builder.setTitle("Error");
            builder.setDescription("You must provide a role name or id. Alternatively, you can also ping a role.");
            sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
            return;
        } else if (e.getMessage().getMentionedRoles().size() > 0) {
            role = e.getMessage().getMentionedRoles().get(0);
        } else if (isLong(args[0])) {
            role = e.getGuild().getRoleById(args[0]);
        } else {
            String name = String.join(" ", args).toLowerCase(Locale.UK);
            for (Role r : e.getGuild().getRoles()) {
                if (r.getName().toLowerCase(Locale.UK).equals(name)) {
                    role = r;
                }
            }
        }

        if (role == null) {
            builder.setTitle("Error");
            builder.setDescription("Could not find a role with that name or id.");
            sendEmbed(e, builder, 30, TimeUnit.SECONDS, true);
            return;
        }

        builder.setTitle("Role Information");
        if (role.getIcon() != null) {
            builder.setThumbnail(role.getIcon().getIconUrl());
        }
        builder.setDescription(role.getAsMention() + " (" + role.getId() + ")");

        // Creation date
        var localDateTime = LocalDateTime.ofInstant(role.getTimeCreated().toInstant(), ZoneId.of("Europe/Zurich"));
        String s = localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));
        builder.addField("Creation Date", "`" + s + "`", false);

        // Member count
        Role copy = role; // Copy of the role to avoid concurrent modification
        long count = e.getGuild().getMembers().stream().filter(m -> m.getRoles().contains(copy)).count();
        builder.addField("Members", "`" + count + "`", false);

        // Other info
        Color roleColor = role.getColor();
        String color = "None";
        if (roleColor != null) {
            color = String.format("#%02x%02x%02x", roleColor.getRed(), roleColor.getGreen(), roleColor.getBlue());
        }
        builder.addField("Color", "`" + color + "`", true);
        builder.addField("Hoisted", String.valueOf(role.isHoisted()), true);
        builder.addField("Position", String.valueOf(role.getPosition()), true);

        // Permissions
        String perms = "None";
        if (role.getPermissions().size() > 0) {
            perms = role.getPermissions().stream().map(Permission::getName).collect(Collectors.joining(", "));
        }
        builder.addField("Permissions", "```" + perms + "```", false);

        sendEmbed(e, builder, 2, TimeUnit.MINUTES, true, role.getColor());
    }
}