package dev.zawarudo.holo.commands.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.CommandManager;
import dev.zawarudo.holo.commands.CommandModule;

public class MusicModule implements CommandModule {

    private final EventWaiter eventWaiter;

    public MusicModule(EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
    }

    @Override
    public ModuleId id() {
        return ModuleId.MUSIC;
    }

    @Override
    public String name() {
        return "music";
    }

    @Override
    public String description() {
        return "Commands related to music and the music player.";
    }

    @Override
    public void register(CommandManager registry) {
        registry.addCommand(new ClearCmd(eventWaiter));
        registry.addCommand(new CloneCmd());
        registry.addCommand(new JoinCmd());
        registry.addCommand(new LeaveCmd());
        registry.addCommand(new LoopCmd());
        registry.addCommand(new NowPlayingCmd());
        registry.addCommand(new PlayCmd());
        registry.addCommand(new QueueCmd());
        registry.addCommand(new ShuffleCmd());
        registry.addCommand(new SkipCmd(eventWaiter));
        registry.addCommand(new StopCmd());
    }
}