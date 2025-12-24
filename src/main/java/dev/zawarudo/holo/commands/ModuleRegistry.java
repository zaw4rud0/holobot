package dev.zawarudo.holo.commands;

import java.util.*;

public final class ModuleRegistry {

    private final Map<CommandModule.ModuleId, CommandModule> modules = new EnumMap<>(CommandModule.ModuleId.class);

    public void register(CommandModule module) {
        modules.put(module.id(), module);
    }

    public Collection<CommandModule> all() {
        return modules.values();
    }

    public Optional<CommandModule> find(CommandModule.ModuleId id) {
        return Optional.ofNullable(modules.get(id));
    }
}
