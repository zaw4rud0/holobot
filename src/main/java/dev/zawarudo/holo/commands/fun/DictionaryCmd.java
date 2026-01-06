package dev.zawarudo.holo.commands.fun;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.modules.MerriamWebsterClient;
import dev.zawarudo.holo.utils.Emote;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.utils.exceptions.APIException;
import dev.zawarudo.holo.utils.exceptions.InvalidRequestException;
import dev.zawarudo.holo.utils.exceptions.NotFoundException;
import dev.zawarudo.holo.utils.interact.ButtonPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CommandInfo(
        name = "dictionary",
        description = "Looks up a word in the Merriam-Webster Dictionary.",
        usage = "<word>",
        example = "syzygy",
        alias = {"dict", "define"},
        thumbnail = "https://dictionaryapi.com/images/MWLogo.png",
        embedColor = EmbedColor.DICTIONARY,
        category = CommandCategory.MISC
)
public class DictionaryCmd extends AbstractCommand {

    private final ButtonPaginator<MerriamWebsterClient.Entry> paginator;
    private final MerriamWebsterClient client;

    public DictionaryCmd(EventWaiter waiter, MerriamWebsterClient client) {
        this.paginator = new ButtonPaginator<>(
                waiter,
                this::createEmbed,
                "dict",
                5, TimeUnit.MINUTES
        );

        this.client = client;
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        sendTyping(event);

        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a word to look up.");
            return;
        }

        String term = String.join(" ", args).trim();

        MerriamWebsterClient.LookupResult result;
        try {
            result = client.lookupDictionary(term);
        } catch (APIException | InvalidRequestException | NotFoundException ex) {
            sendErrorEmbed(event, "An error occurred while looking up that word. Please try again later.");
            logger.error("Dictionary lookup failed: {}", term, ex);
            return;
        }

        if (result.hasEntries()) {
            paginator.start(event.getMessage(), event.getAuthor(), result.entries());
            return;
        }

        if (result.hasSuggestions()) {
            String suggestions = result.suggestions().stream()
                    .limit(10)
                    .map(s -> "* " + s)
                    .collect(Collectors.joining("\n"));

            EmbedBuilder b = new EmbedBuilder();
            b.setThumbnail("https://dictionaryapi.com/images/MWLogo.png");
            b.setTitle("Not found");
            b.setDescription("Did you mean:\n" + suggestions);
            sendReplyEmbed(event.getMessage(), b, getEmbedColor());
            return;
        }

        sendErrorEmbed(event, "No results found for **" + term + "**.");
    }

    private MessageEmbed createEmbed(MerriamWebsterClient.Entry entry, int index, int total) {
        EmbedBuilder b = new EmbedBuilder();
        b.setThumbnail("https://dictionaryapi.com/images/MWLogo.png");

        String word = entry.headword() == null ? "Unknown" : entry.headword();
        String fl = entry.functionalLabel();
        String title = (fl == null || fl.isBlank()) ? word : String.format("%s (%s)", word, fl);

        String warning = entry.offensive() ? Emote.WARNING.getAsEmoji().getFormatted() : "";

        b.setTitle(title + " " + warning);

        // Definitions as description
        List<String> defs = entry.shortDefs() == null ? List.of() : entry.shortDefs();
        String description;
        if (defs.isEmpty()) {
            description = "_No definition available._";
        } else if (defs.size() == 1) {
            description = defs.getFirst();
        } else {
            description = defs.stream()
                    .map(d -> "• " + d)
                    .collect(Collectors.joining("\n"));
        }

        b.setDescription(Formatter.truncate(description, MessageEmbed.DESCRIPTION_MAX_LENGTH));

        if (entry.pronunciation() != null && !entry.pronunciation().isBlank()) {
            b.addField("Pronunciation", entry.pronunciation(), true);
        }

        if (entry.plural() != null && !entry.plural().isBlank()) {
            b.addField("Plural", entry.plural(), true);
        }

        // Display etymology if available
        if (entry.etymology() != null && !entry.etymology().isBlank()) {
            String et = Formatter.truncate(entry.etymology(), MessageEmbed.VALUE_MAX_LENGTH);
            b.addField("Etymology", et, false);
        }

        if (entry.usageNotes() != null && !entry.usageNotes().isBlank()) {
            String usage = Formatter.truncate(entry.usageNotes(), MessageEmbed.VALUE_MAX_LENGTH);
            b.addField("Usage note", usage, false);
        }

        List<String> examples = entry.examples();

        if (examples != null && !examples.isEmpty()) {
            String exText = examples.stream()
                    .limit(8)
                    .map(e -> "• " + e)
                    .collect(Collectors.joining("\n"));

            exText = Formatter.truncate(exText, MessageEmbed.VALUE_MAX_LENGTH);
            b.addField("Examples", exText, false);
        }

        b.setFooter(String.format("Page %d / %d", index + 1, total));
        b.setColor(getEmbedColor());
        return b.build();
    }
}