package com.xharlock.holo.owner;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Command(name = "restart",
        description = "Restarts the bot",
        alias = {"reboot"},
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class RestartCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        e.getMessage().reply("Restarting... See you again in a few seconds!").queue();

        // Leaves all voice channels
        e.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).forEach(g -> g.getAudioManager().closeAudioConnection());

        // Deletes messages
        Bootstrap.holo.getPokemonSpawnManager().getMessages().values().forEach(m -> m.delete().queue());

        e.getJDA().shutdown();
        Bootstrap.restart();
    }
}