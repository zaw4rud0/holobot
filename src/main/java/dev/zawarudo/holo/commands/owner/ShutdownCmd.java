package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@Command(name = "shutdown",
        description = "Shuts down the bot.",
        alias = {"kill"},
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class ShutdownCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        e.getMessage().reply("Shutting down... Goodbye!").queue();

        // Leaves all voice channels
        e.getJDA().getGuilds().stream().filter(g -> {
            GuildVoiceState self = g.getSelfMember().getVoiceState();
            if (self == null) {
                return false;
            }
            return self.inAudioChannel();
        }).forEach(g -> g.getAudioManager().closeAudioConnection());

        // Deletes messages
        Bootstrap.holo.getPokemonSpawnManager().getMessages().values().forEach(m -> m.delete().queue());

        Bootstrap.shutdown();
    }
}