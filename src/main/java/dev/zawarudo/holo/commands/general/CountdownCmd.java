package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Command(name="countdown",
        description = "Shows your countdowns.",
        category = CommandCategory.MISC
)
public class CountdownCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {

    }
}