package dev.zawarudo.holo.games.akinator;

import com.github.markozajc.akiwrapper.core.entities.Server;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.misc.EmbedColor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Command(name = "akinator",
        description = "Starts a new Akinator game",
        embedColor = EmbedColor.AKINATOR,
        thumbnail = AkinatorSprite.ICON,
        category = CommandCategory.GAMES,
        ownerOnly = true)
@SuppressWarnings({"PMD", "unused"})
public class AkinatorCmd extends AbstractCommand {

    private final AkinatorManager manager;

    public AkinatorCmd() {
        manager = Bootstrap.holo.getAkinatorManager();
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (manager.hasInstance(event.getAuthor().getIdLong())) {
            sendErrorEmbed(event, "You are already playing this game. Please finish or cancel it to start a new game!");
        }

        try {
            AkinatorInstance instance = manager.createInstance(event);
            instance.start();
        } catch (APIException e) {
            sendErrorEmbed(event, "Something went wrong while starting a new Akinator game. Please try again in a few minutes.");
        }
    }

    private Server.GuessType getGuessType(@NotNull String s) {
        return switch(s.toLowerCase(Locale.UK)) {
            case ("animal") -> Server.GuessType.ANIMAL;
            case ("movie") -> Server.GuessType.MOVIE_TV_SHOW;
            case ("object") -> Server.GuessType.OBJECT;
            case ("character") -> Server.GuessType.CHARACTER;
            default -> throw new IllegalArgumentException("Unknown guess type");
        };
    }
}