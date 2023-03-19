package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.List;
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

        String userAvatar = user.getEffectiveAvatarUrl() + "?size=1024";
        String serverAvatar = member != null ? member.getEffectiveAvatarUrl() + "?size=1024" : null;

        Color embedColor = member != null ? member.getColor() : null;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Avatar of " + name, userAvatar);
        builder.setImage(userAvatar);

        if (serverAvatar != null && !userAvatar.equals(serverAvatar)) {
            List<MessageEmbed> embeds = getEmbedWithMultipleImages(builder, List.of(userAvatar, serverAvatar));
            event.getChannel().sendMessageEmbeds(embeds).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
        } else {
            sendEmbed(event, builder, true, 5, TimeUnit.MINUTES, embedColor);
        }
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
        } catch (NumberFormatException ex) {
            return e.getAuthor();
        }
    }

    /**
     * Returns the user as a member of the guild. If the user isn't a member, it returns null
     */
    @Nullable
    private Member getMember(MessageReceivedEvent event, User user) {
        try {
            return event.getGuild().retrieveMember(user).complete();
        } catch (ErrorResponseException ex) {
            return null;
        }
    }
}