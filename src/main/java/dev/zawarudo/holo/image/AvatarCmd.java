package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Command(name = "avatar",
        description = "Retrieves the avatar of a specified user. Tip: Use the id of the user if you don't want to ping them.",
        usage = "[<user id>]",
        alias = {"av", "pfp"},
        category = CommandCategory.IMAGE)
public class AvatarCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        User user = getUser(event);
        Member member = getMember(event, user);

        String name = member != null ? member.getEffectiveName() : user.getName();
        String url = user.getEffectiveAvatarUrl() + "?size=512";
        Color embedColor = member != null ? member.getColor() : null;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Avatar of " + name, url);
        builder.setImage(url);
        sendEmbed(event, builder, true, 5, TimeUnit.MINUTES, embedColor);
    }

    /**
     * Returns the given user, either as mention or as id. If the argument is
     * invalid, simply return the author of the message.
     */
    private User getUser(MessageReceivedEvent e) {
        try {
            return e.getJDA().getUserById(Long.parseLong(args[0]
                    .replace("<", "")
                    .replace(">", "")
                    .replace("!", "")
                    .replace("@", "")));
        } catch (Exception ex) {
            return e.getAuthor();
        }
    }

    /**
     * Returns the user as a member of the guild. If the user isn't a member, it returns null
     */
    @Nullable
    private Member getMember(MessageReceivedEvent e, User user) {
        try {
            return e.getGuild().retrieveMember(user).complete();
        } catch (Exception ex) {
            return null;
        }
    }
}