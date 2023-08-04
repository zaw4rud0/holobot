package dev.zawarudo.holo.core;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CountThreadListener extends ListenerAdapter {
    private static int lastSent, interruptCount = 60;
    private static boolean spamPingProtection = false;
    private static boolean stopPing = false;
    private static ThreadChannel thread;
    public static String listenTo = "820098162013503498";

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!event.getJDA().getSelfUser().getId().equals("781601968736960543")) {
            return;
        }

        thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        checkRecentMessages();
        event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@858053151452823563> I should have started").queue();
    }

    @Override
    public void onSessionRecreate(@NotNull SessionRecreateEvent event) {
        checkRecentMessages();
        event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@858053151452823563> did I resume?").queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("996746797236105236")) {
            if (event.getAuthor().getId().equals(listenTo)) {
                try {
                    int nextNumber = (Integer.parseInt(event.getMessage().getContentRaw()) + 1);

                    if (nextNumber > lastSent) {
                        event.getChannel().sendMessage("" + nextNumber).queue();
                        lastSent = nextNumber;
                    }
                } catch (Exception ignored) {
                    event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@858053151452823563> look, an exception. <@223932775474921472> :DinkDonk:").queue();
                    event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage(ignored.toString().substring(0, 1990)).queue();
                }
            }

            if (interruptCount < 60) {
                interruptCount++;
            } else {
                spamPingProtection = false;
            }
        } else if (event.getChannel().getId().equals("819966095070330950")) {
            if (interruptCount <= 50 && stopPing) {
                event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@858053151452823563> I am counting down " + interruptCount).queue();
                stopPing = true;
            }

            if (!spamPingProtection && --interruptCount <= 0) {
                String botId = thread.getHistory().retrievePast(1).complete().get(0).getAuthor().getId();

                if (botId.equals(listenTo)) {
                    checkRecentMessages();
                }
                event.getJDA().getGuildById("747752542741725244").getTextChannelById("768600365602963496").sendMessage("<@858053151452823563> I am counting down (later) " + interruptCount).queue();
                spamPingProtection = true;
            }
        }
    }

    public static void checkRecentMessages() {
        for (Message message : thread.getHistory().retrievePast(1).complete()) {
            try {
                if (message.getAuthor().getId().equals(listenTo)) {
                    lastSent = Integer.parseInt(message.getContentRaw()) + 1;
                    thread.sendMessage("" + lastSent).queue();
                }
            } catch (Exception ignored) {}
        }
    }
}
