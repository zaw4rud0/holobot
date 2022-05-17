package com.xharlock.holo.general;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.DecimalFormat;

@Command(name = "coinflip",
        description = "Flips a coin. You can provide an additional argument as the number of times I should flip a coin with the limit being at 1,000,000 coin flips.",
        usage = "[<times>]",
        category = CommandCategory.MISC)
public class CoinFlipCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        int times, heads = 0, tails = 0;

        if (args.length == 0) {
            times = 1;
        } else if (isInteger(args[0])) {
            times = Integer.parseInt(args[0]);
            if (times < 1) {
                errorMessage(e);
                return;
            } else if (times > 1000000) {
                e.getMessage().reply("Hold on! My limit is 1,000,000 coin flips at once.").queue();
                return;
            }
        } else {
            errorMessage(e);
            return;
        }

        for (int i = 0; i < times; i++) {
            if (coinFlip() == 0) {
                heads++;
            } else {
                tails++;
            }
        }
        e.getMessage().reply(formatMessage(times, heads, tails)).queue();
    }

    private void errorMessage(MessageReceivedEvent e) {
        e.getMessage().reply("Invalid argument! Please provide a positive non-zero integer as argument.").queue();
    }

    private int coinFlip() {
        return (int) (Math.random() * 2);
    }

    private String formatMessage(int times, int heads, int tails) {
        DecimalFormat df = new DecimalFormat("#,###,###");
        return String.format("I flipped a coin **%s** times. I got **%s** heads and **%s** tails.",
                df.format(times), df.format(heads), df.format(tails));
    }
}