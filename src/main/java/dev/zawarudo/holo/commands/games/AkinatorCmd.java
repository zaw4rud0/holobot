package dev.zawarudo.holo.commands.games;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.modules.akinator.AkinatorSession;
import dev.zawarudo.holo.modules.akinator.AkinatorSessionManager;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jspecify.annotations.NonNull;

@Command(
        name = "akinator",
        description = "Play Akinator using buttons.",
        category = CommandCategory.GAMES,
        embedColor = EmbedColor.AKINATOR
)
public class AkinatorCmd extends AbstractCommand {

    private final EventWaiter waiter;
    private final AkinatorSessionManager sessions;

    public AkinatorCmd(EventWaiter waiter, AkinatorSessionManager sessions) {
        this.waiter = waiter;
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NonNull MessageReceivedEvent event) {
        long userId = event.getAuthor().getIdLong();

        if (sessions.hasActiveSession(userId)) {
            event.getMessage().reply("You already have an active Akinator game running.").queue();
            return;
        }

        long guildId = event.getGuild().getIdLong();

        AkinatorSession session = new AkinatorSession(
                userId,
                event.getChannel().getIdLong(),
                guildId,
                waiter,
                sessions
        );

        if (!sessions.registerSession(session)) {
            event.getMessage().reply("You already have an active Akinator game running.").queue();
            return;
        }

        session.start(event.getMessage());
    }
}
