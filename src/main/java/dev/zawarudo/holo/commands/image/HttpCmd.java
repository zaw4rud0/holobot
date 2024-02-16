package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(name = "http",
        description = "Shows a silly cat image with the specified HTTP error code. If " +
                "the provided argument is not valid, 404 will be returned.",
        usage = "<code>",
        example = "404",
        category = CommandCategory.IMAGE,
        thumbnail = "https://http.cat/images/404.jpg",
        guildOnly = false
)
public class HttpCmd extends AbstractCommand {

    private static final List<String> CODES = List.of(
            "100", "101", "102", "103",
            "200", "201", "202", "203", "204", "205", "206", "207", "208", "226",
            "300", "301", "302", "303", "304", "305", "307", "308",
            "400", "401", "402", "403", "404", "405", "406", "407", "408", "409",
            "410", "411", "412", "413", "414", "415", "416", "417", "418", "420",
            "421", "422", "423", "424", "425", "426", "428", "429", "431", "444",
            "450", "451", "497", "498", "499",
            "500", "501", "502", "503", "504", "506", "507", "508", "509", "510",
            "511", "521", "522", "523", "525", "530", "599"
    );

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        String input = String.join("", args);
        String url = String.format("https://http.cat/%s", parseInput(input));
        event.getMessage().reply(url).queue();
    }

    private String parseInput(String input) {
        return CODES.contains(input) ? input : "404";
    }
}