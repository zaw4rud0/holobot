package dev.zawarudo.holo.core.command;

import org.jetbrains.annotations.NotNull;

public interface ContextCommand {

    void onCommand(@NotNull CommandContext ctx);
}
