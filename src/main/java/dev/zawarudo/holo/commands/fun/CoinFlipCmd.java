package dev.zawarudo.holo.commands.fun;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Random;

@Command(name = "coinflip",
        description = "Flips a coin. You can provide an additional argument as the number of times I should flip a coin with the limit being 1'000'000 coin flips at once.",
        usage = "[<times>]",
        category = CommandCategory.MISC)
public class CoinFlipCmd extends AbstractCommand {

    private static final int MAX_COIN_FLIPS = 1_000_000;
    private static final Random RANDOM = new Random();

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        int times = getCoinFlipCount();

        if (times == -1) {
            sendErrorMessage(event);
            return;
        }
        if (times > MAX_COIN_FLIPS) {
            event.getMessage().reply(String.format("Hold on! My limit is %d coin flips at once.", MAX_COIN_FLIPS)).queue();
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
            int result = RANDOM.nextInt(2);
            if (result == 0) {
                heads++;
            }
        }
        return heads;
    }

    private String formatMessage(int times, int heads, int tails) {
        String text = "I flipped a coin **%s** times. I got **%s** heads and **%s** tails.";
        DecimalFormat df = new DecimalFormat("###,###,###");
        return String.format(text, df.format(times), df.format(heads), df.format(tails)).replace(",", "'");
    }

    private void sendErrorMessage(MessageReceivedEvent event) {
        event.getMessage().reply("Invalid argument! Please provide a positive non-zero integer as argument.").queue();
    }
}