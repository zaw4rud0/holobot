package dev.zawarudo.holo.fun;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

@Command(name = "uwu",
        description = "Uwuifies a text of your choice. You can also reply to a message to uwuify it.",
        usage = "<text>",
        category = CommandCategory.MISC)
public class UwuCmd extends AbstractCommand {

    private static final int CHAR_LIMIT = 2000;

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide text or reply to a message!");
            return;
        }

        // Check for pings
        if (hasPings(event.getMessage())) {
            sendErrorEmbed(event, "No pings!");
            return;
        }

        // No emotes
        if (!event.getMessage().getMentions().getCustomEmojis().isEmpty()) {
            sendErrorEmbed(event, "I can't use custom emojis!");
            return;
        }

        if (event.getMessage().getReferencedMessage() != null) {
            String uwu = uwuify(event.getMessage().getReferencedMessage().getContentRaw().split(" "));
            sendText(event, uwu);
        } else {
            String uwu = uwuify(args);
            sendText(event, uwu);
        }
    }

    private void sendText(MessageReceivedEvent event, String text) {
        int index = 0;
        while (index < text.length()) {
            String chunk = text.substring(index, Math.min(index + CHAR_LIMIT, text.length()));
            event.getChannel().sendMessage(chunk).queue();
            index += CHAR_LIMIT;
        }
    }

    private String uwuify(String... words) {
        return Arrays.stream(words)
                .map(this::replaceWord)
                .collect(Collectors.joining(" "));
    }

    private String replaceWord(String word) {
        return word
                .replaceAll("(?i)you", "uwu")
                .replaceAll("(?i)[rl]", "w")
                .replaceAll("(?i)it", "iwt")
                .replaceAll("(?i)(?<=[ai])t", "w$0")
                .replaceAll("(?i)is", "iws");
    }
}