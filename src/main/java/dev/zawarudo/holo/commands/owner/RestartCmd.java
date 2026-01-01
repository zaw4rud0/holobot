package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "restart",
        description = "Restarts the bot",
        alias = {"reboot"},
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class RestartCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        e.getMessage().reply("Restarting... See you again in a few seconds!").queue();

        // Leaves all voice channels
        e.getJDA().getGuilds().stream().filter(g -> {
            if (g.getSelfMember().getVoiceState() == null) {
                return false;
            }
            return g.getSelfMember().getVoiceState().inAudioChannel();
        }).forEach(g -> g.getAudioManager().closeAudioConnection());

        // Deletes messages
        //Bootstrap.holo.getPokemonSpawnManager().getMessages().values().forEach(m -> m.delete().queue());

        e.getJDA().shutdown();
        Bootstrap.restart();
    }
}