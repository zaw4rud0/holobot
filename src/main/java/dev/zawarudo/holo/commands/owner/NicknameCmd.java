package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Command(name = "nickname",
        description = "Changes the nickname of the bot or of a specified user.",
        usage = "<user> <nickname>",
        alias = {"nick"},
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class NicknameCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        deleteInvoke(e);

        // TODO: Check for other cases, such as when no argument was given

        if (args.length >= 1 && args[0].equals("self")) {
            e.getGuild().getMember(e.getJDA().getSelfUser()).modifyNickname(String.join(" ", Arrays.copyOfRange(args, 1, args.length))).queue();
        } else {
            long id = Long.parseLong(args[0]);
            e.getGuild().getMemberById(id).modifyNickname(String.join(" ", Arrays.copyOfRange(args, 1, args.length))).queue();
        }
    }
}