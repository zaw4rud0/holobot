package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Deactivated
@Command(name = "http",
        description = "Shows a silly cat image with the specified HTTP error code.",
        usage = "<http code>",
        example = "404",
        category = CommandCategory.IMAGE,
        guildOnly = false
)
public class HttpCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        String input = String.join("", args);
        String url = String.format("https://http.cat/%s", input);
        event.getMessage().reply(url).queue();
    }
}