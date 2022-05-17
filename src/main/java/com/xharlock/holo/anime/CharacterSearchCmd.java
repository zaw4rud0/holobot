package com.xharlock.holo.anime;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deactivated
@Command(name = "character",
        description = "Search for an anime character.",
        usage = "<character name>",
        alias = {"charactersearch", "char"},
        category = CommandCategory.ANIME)
public class CharacterSearchCmd extends AbstractCommand {

    private final EventWaiter waiter;

    public CharacterSearchCmd(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    public void onCommand(MessageReceivedEvent e) {
        e.getMessage().reply("Not implemented yet. " + waiter).queue();
    }
}