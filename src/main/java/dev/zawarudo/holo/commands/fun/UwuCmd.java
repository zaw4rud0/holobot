package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Command(name = "uwu",
        description = "Uwuifies a text of your choice. You can also reply to a message to uwuify it.",
        usage = "<text>",
        category = CommandCategory.MISC)
public class UwuCmd extends AbstractCommand {

    private static final Random rand = new Random();

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        if (args.length == 0 && event.getMessage().getReferencedMessage() == null) {
            sendErrorEmbed(event, "Please provide text or reply to a message!");
            return;
        }

        Message reply = event.getMessage().getReferencedMessage();
        Message msg = reply != null ? reply : event.getMessage();

        // Check for pings
        if (hasPings(msg)) {
            sendErrorEmbed(event, "No pings!");
            return;
        }

        // No emotes
        if (!msg.getMentions().getCustomEmojis().isEmpty()) {
            sendErrorEmbed(event, "I can't use custom emojis!");
            return;
        }

        if (event.getMessage().getReferencedMessage() != null) {
            String uwu = uwuify(event.getMessage().getReferencedMessage().getContentRaw());
            sendText(event, uwu);
        } else {
            String uwu = uwuify(String.join(" ", args));
            sendText(event, uwu);
        }
    }

    private void sendText(MessageReceivedEvent event, String text) {
        int index = 0;
        while (index < text.length()) {
            String chunk = text.substring(index, Math.min(index + Message.MAX_CONTENT_LENGTH, text.length()));
            event.getChannel().sendMessage(chunk).queue();
            index += Message.MAX_CONTENT_LENGTH;
        }
    }

    private static String uwuify(String stringToUwuify) {
        String result = stringToUwuify
                .toLowerCase()
                .replaceAll("[rl]", "w")
                .replaceAll("n([aeiou])", "ny$1")
                .replace("ove", "uve")
                .replace("uck", "uwq")
                .replaceFirst("i", "i-i")
                .replaceFirst("(?s)(.*)" + "i-i-i", "$1" + "i-i");
        if (rand.nextInt(10) <= 2) {
            result += " >-<";
        }
        if (rand.nextInt(10) <= 1) {
            result += " UwU";
        }
        return result;
    }
}