package dev.zawarudo.holo.fun;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Command(name = "coinflip",
        description = "Flips a coin. You can provide an additional argument as the number of times I should flip a coin with the limit being 1'000'000 coin flips at once.",
        usage = "[<times>]",
        category = CommandCategory.MISC)
public class CoinFlipCmd extends AbstractCommand {

    private static final int MAX_COIN_FLIPS = 1_000_000;

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        int times = getCoinFlipCount();

        if (times == -1) {
            sendErrorMessage(event);
            return;
        }
        if (times > MAX_COIN_FLIPS) {
            event.getMessage().reply("Hold on! My limit is 1'000'000 coin flips at once.").queue();
            return;
        }

        int heads = flipCoins(times);
        int tails = times - heads;

        event.getMessage().reply(formatMessage(times, heads, tails)).queue();
    }

    private int getCoinFlipCount() {
        if (args.length == 0) {
            return 1;
        }
        if (!isInteger(args[0])) {
            return -1;
        }
        int times = Integer.parseInt(args[0]);
        if (times < 1) {
            return -1;
        }
        return times;
    }

    private int flipCoins(int times) {
        int heads = 0;
        for (int i = 0; i < times; i++) {
            int result = (int) (Math.random() * 2);
            if (result == 0) {
                heads++;
            }
        }
        return heads;
    }

    private String formatMessage(int times, int heads, int tails) {
        String text = "I flipped a coin **%s** times. I got **%s** heads and **%s** tails.";
        DecimalFormat df = new DecimalFormat("#'###'###");
        return String.format(text, df.format(times), df.format(heads), df.format(tails));
    }

    private void sendErrorMessage(MessageReceivedEvent event) {
        event.getMessage().reply("Invalid argument! Please provide a positive non-zero integer as argument.").queue();
    }
}