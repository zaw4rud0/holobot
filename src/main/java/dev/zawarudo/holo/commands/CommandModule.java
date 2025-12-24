package dev.zawarudo.holo.commands;

public interface CommandModule {

    ModuleId id();

    String name();

    String description();

    void register(CommandManager registry);

    enum ModuleId {
        NONE,
        MUSIC,
        POKEMON
    }
}