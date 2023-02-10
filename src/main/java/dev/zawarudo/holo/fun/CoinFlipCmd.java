package dev.zawarudo.holo.fun;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Command(name = "coinflip",
        description = "Flips a coin. You can provide an additional argument as the number of times I should flip a coin with the limit being at 1,000,000 coin flips.",
        usage = "[<times>]",
        category = CommandCategory.MISC)
public class CoinFlipCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        int times, heads = 0, tails = 0;

        if (args.length == 0) {
            times = 1;
        } else if (isInteger(args[0])) {
            times = Integer.parseInt(args[0]);
            if (times < 1) {
                sendErrorMessage(event);
                return;
            } else if (times > 1000000) {
                event.getMessage().reply("Hold on! My limit is 1'000'000 coin flips at once.").queue();
                return;
            }
        } else {
            sendErrorMessage(event);
            return;
        }

        for (int i = 0; i < times; i++) {
            if (flipCoin() == 0) {
                heads++;
            } else {
                tails++;
            }
        }
        event.getMessage().reply(formatMessage(times, heads, tails)).queue();
    }

    private void sendErrorMessage(MessageReceivedEvent event) {
        event.getMessage().reply("Invalid argument! Please provide a positive non-zero integer as argument.").queue();
    }

    private int flipCoin() {
        return (int) (Math.random() * 2);
    }

    private String formatMessage(int times, int heads, int tails) {
        DecimalFormat df = new DecimalFormat("#,###,###");
        return String.format("I flipped a coin **%s** times. I got **%s** heads and **%s** tails.",
                df.format(times), df.format(heads), df.format(tails));
    }
}