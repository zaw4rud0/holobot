package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.modules.emotes.EmoteManager;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;

@Command(name = "emote",
        description = "Sends a specified emote in the channel.",
        category = CommandCategory.IMAGE)
public class EmoteCmd extends AbstractCommand {

    private final EmoteManager emoteManager;

    public EmoteCmd(EmoteManager emoteManager) {
        this.emoteManager = emoteManager;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        String emoteName = args[0];

        try {
            Optional<CustomEmoji> emojiOptional = emoteManager.getEmoteByName(emoteName);

            if (emojiOptional.isEmpty()) {
                event.getMessage().reply(String.format("Emote not found: %s", args[0])).queue();
                return;
            }


        } catch (SQLException e) {
            event.getChannel().sendMessage(e.getMessage());
        }
    }
}
