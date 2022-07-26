package services.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class CountThreadListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BGListener.class);
    private static String listenTo = "820098162013503498"; //Avanis

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ThreadChannel thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("996746797236105236");
        //here you would look up who you listened for before the restart, if you have a way of storing that
      
        try {
            if (message.getAuthor().getId().equals(listenTo)) {
                thread.sendMessage("" + (Integer.parseInt(message.getContentRaw()) + 1)).queue();
                break;
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("996746797236105236")) {
            if (event.getAuthor().getId().equals(listenTo)) {
                try {
                    event.getThreadChannel().sendMessage("" + (Integer.parseInt(event.getMessage().getContentRaw()) + 1)).queue();
                } catch (Exception ignored) {}
            }
        } else if (event.getAuthor().getId().equals("155419933998579713") && event.getMessage().getContentRaw().contains("<watch")) {
          // no clue how you handle persistant stuff, but here would be the part where you (or me because you left) update who holo listens to 
          
          //updateConfig("CountThreadListenTo", event.getMessage().getContentRaw().replace("rdwatch ", ""));

            //event.getChannel().sendMessage("I am watching you <@" + (listenTo = getConfig().get("CountThreadListenTo")) + "> <:bustinGood:747783377171644417>").queue(
             //       (msg) -> msg.delete().queueAfter(60, TimeUnit.SECONDS)
            //);
        }

    }
}
