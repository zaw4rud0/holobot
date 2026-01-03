package dev.zawarudo.holo.core.command;

import org.jetbrains.annotations.NotNull;

public interface ExecutableCommand {

    void execute(@NotNull CommandContext ctx);
}
