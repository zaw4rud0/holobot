package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// TODO: Refactor class, split each part into separate methods

@Deactivated
@CommandInfo(name = "roleinfo",
        description = "Shows information about a role.",
        usage = "<role name, id or mention>",
        category = CommandCategory.GENERAL)
public class RoleInfoCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        deleteInvoke(e);

        EmbedBuilder builder = new EmbedBuilder();

        if (args.length == 0) {
            builder.setTitle("Error");
            builder.setDescription("You must provide a role name or id. Alternatively, you can also ping a role.");
            sendEmbed(e, builder, true,30, TimeUnit.SECONDS);
            return;
        }

        Role role = null;

        if (!e.getMessage().getMentions().getRoles().isEmpty()) {
            role = e.getMessage().getMentions().getRoles().getFirst();
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
            sendEmbed(e, builder, true, 30, TimeUnit.SECONDS);
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
        Color roleColor = role.getColors().getPrimary();
        String color = "None";
        if (roleColor != null) {
            color = String.format("#%02x%02x%02x", roleColor.getRed(), roleColor.getGreen(), roleColor.getBlue());
        }
        builder.addField("Color", "`" + color + "`", true);
        builder.addField("Hoisted", String.valueOf(role.isHoisted()), true);
        builder.addField("Position", String.valueOf(role.getPosition()), true);

        // Permissions
        String perms = "None";
        if (!role.getPermissions().isEmpty()) {
            perms = role.getPermissions().stream().map(Permission::getName).collect(Collectors.joining(", "));
        }
        builder.addField("Permissions", Formatter.asCodeBlock(perms), false);

        sendEmbed(e, builder, true, 2, TimeUnit.MINUTES, roleColor);
    }
}