package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Command(name = "shutdown",
        description = "Shuts down the bot.",
        alias = {"kill"},
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class ShutdownCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        e.getMessage().reply("Shutting down... Goodbye!").queue();

        // Leaves all voice channels
        e.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).forEach(g -> g.getAudioManager().closeAudioConnection());

        // Deletes messages
        Bootstrap.holo.getPokemonSpawnManager().getMessages().values().forEach(m -> m.delete().queue());

        e.getJDA().shutdown();
    }
}