package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
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

        Optional<User> userOptional = fetchMentionedUser(event);
        if (userOptional.isEmpty()) {
            sendErrorEmbed(event, "I couldn't find the given user! Please make sure you provided the correct user id or mentioned them!");
            return;
        }
        User user = userOptional.get();

        Optional<Member> member = getAsGuildMember(user, event.getGuild());

        String name = member.map(Member::getEffectiveName).orElseGet(user::getName);

        String userAvatar = user.getEffectiveAvatarUrl() + "?size=1024";
        String serverAvatar = member.map(value -> value.getEffectiveAvatarUrl() + "?size=1024").orElse(null);

        Color embedColor = member.map(m -> m.getColors().getPrimary()).orElse(null);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Avatar of " + name, userAvatar);
        builder.setImage(userAvatar);

        if (serverAvatar != null && !userAvatar.equals(serverAvatar)) {
            List<MessageEmbed> embeds = getEmbedWithMultipleImages(builder, userAvatar, serverAvatar);
            event.getChannel().sendMessageEmbeds(embeds).queue(msg -> msg.delete().queueAfter(5, TimeUnit.MINUTES));
        } else {
            sendEmbed(event, builder, true, 5, TimeUnit.MINUTES, embedColor);
        }
    }
}