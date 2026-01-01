package dev.zawarudo.holo.commands.fun;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.modules.urbandictionary.UrbanDictionaryEntry;
import dev.zawarudo.holo.modules.urbandictionary.UrbanDictionaryScraper;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.interact.ButtonPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandInfo(name = "urban",
        description = "Searches for a term in the Urban Dictionary",
        usage = "<term>",
        example = "yeet",
        alias = {"ub", "urbandictionary"},
        thumbnail = "https://media.discordapp.net/attachments/804619918120452109/1132687110806192229/72d52e81ce2903194bc1e04ec73c922e.png",
        embedColor = EmbedColor.URBAN,
        category = CommandCategory.MISC)
public class UrbanDictionaryCmd extends AbstractCommand {

    private final UrbanDictionaryScraper scraper = new UrbanDictionaryScraper();

    private final ButtonPaginator<UrbanDictionaryEntry> paginator;

    public UrbanDictionaryCmd(EventWaiter waiter) {
        this.paginator = new ButtonPaginator<>(
                waiter,
                this::createUrbanEmbed,
                "urban",
                5, TimeUnit.MINUTES
        );
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a term to search for.");
            return;
        }

        String searchTerm = String.join(" ", args).trim();
        List<UrbanDictionaryEntry> entries;
        try {
            entries = scraper.fetch(searchTerm);

            if (entries.isEmpty()) {
                sendErrorEmbed(event, "Couldn't find any Urban Dictionary entries with the given search terms.");
                return;
            }

            paginator.start(event.getMessage(), event.getAuthor(), entries);

        } catch (IOException e) {
            sendErrorEmbed(event, "Something went wrong while searching your term. Please try again at a later time.");
        }
    }

    private MessageEmbed createUrbanEmbed(UrbanDictionaryEntry entry, int index, int total) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(getThumbnail());
        embedBuilder.setTitle(entry.term(), entry.link());
        embedBuilder.setColor(getEmbedColor());

        boolean hasContent = false;

        if (entry.hasValidDefinition()) {
            String description = Formatter.truncate(
                    entry.definition(),
                    MessageEmbed.DESCRIPTION_MAX_LENGTH
            );
            embedBuilder.setDescription(description);
            hasContent = true;
        }

        if (entry.hasValidExample()) {
            String example = Formatter.truncate(
                    entry.example(),
                    MessageEmbed.VALUE_MAX_LENGTH
            );
            embedBuilder.addField("Example", example, false);
            hasContent = true;
        }

        if (!hasContent) {
            embedBuilder.setDescription("No definition or example available.");
        }

        return ButtonPaginator.withPageFooter(embedBuilder, index, total);
    }
}