package dev.zawarudo.holo.fun;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Command(name = "urban",
        description = "Searches for a term in the Urban Dictionary",
        usage = "<term>",
        example = "yeet",
        alias = {"ub", "urbandictionary"},
        thumbnail = "https://media.discordapp.net/attachments/804619918120452109/1132687110806192229/72d52e81ce2903194bc1e04ec73c922e.png",
        embedColor = EmbedColor.URBAN,
        category = CommandCategory.MISC)
public class UrbanDictionaryCmd extends AbstractCommand {

    private static final String BASE_URL = "https://www.urbandictionary.com";

    record UrbanEntry(String term, String definition, String example, String link) {
        public boolean hasValidDefinition() {
            return definition != null && !definition.trim().isEmpty();
        }

        public boolean hasValidExample() {
            return example != null && !example.trim().isEmpty();
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (args.length == 0) {
            sendErrorEmbed(event, "Please provide a term to search for.");
            return;
        }

        String searchTerm = getSearchTerm(event.getMessage());
        List<UrbanEntry> entries;
        try {
            entries = fetchDefinitions(searchTerm);
            if (entries.isEmpty()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            sendErrorEmbed(event, "Couldn't find any Urban Dictionary entries with the given search terms.");
            return;
        } catch (IOException e) {
            sendErrorEmbed(event, "Something went wrong while searching your term. Please try again at a later time.");
            return;
        }

        EmbedBuilder builder = createUrbanEmbed(entries.get(0));
        replyEmbed(event, event.getMessage(), builder, false, getEmbedColor());
    }

    private String getSearchTerm(Message message) {
        String content = message.getContentRaw();
        int firstSpaceIndex = content.indexOf(" ");
        return content.substring(firstSpaceIndex + 1);
    }

    private List<UrbanEntry> fetchDefinitions(@NotNull String searchTerm) throws IOException {
        List<UrbanEntry> list = new ArrayList<>();

        String url = String.format("%s/define.php?term=%s", BASE_URL, Formatter.encodeUrl(searchTerm));

        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        Elements elements = doc.select("div.definition");

        for (Element element : elements) {
            String title = extractTitle(element);
            String meaning = extractMeaning(element);
            String example = extractExample(element);
            String link = extractLink(element);

            UrbanEntry entry = new UrbanEntry(title, meaning, example, link);
            list.add(entry);
        }

        return list;
    }

    private String extractTitle(Element element) {
        Element titleElement = element.selectFirst("a.word");
        return (titleElement != null) ? titleElement.wholeText() : null;
    }

    private String extractMeaning(Element element) {
        Element meaningElement = element.selectFirst("div.meaning");
        if (meaningElement != null) {
            sanitizeLinks(meaningElement);
            return meaningElement.wholeText();
        }
        return null;
    }

    private String extractExample(Element element) {
        Element exampleElement = element.selectFirst("div.example");
        if (exampleElement != null) {
            sanitizeLinks(exampleElement);
            return exampleElement.wholeText();
        }
        return null;
    }

    private String extractLink(Element element) {
        Element titleElement = element.selectFirst("a.word");
        return (titleElement != null) ? BASE_URL + titleElement.attr("href") : null;
    }

    /**
     * Turns HTML links into markdown links.
     */
    private void sanitizeLinks(@NotNull Element element) {
        for (Element child : element.children()) {
            if (child.tagName().equals("a")) {
                String href = BASE_URL + child.attr("href");
                String linkText = child.text();
                String markdownLink = "[" + linkText + "](" + href + ")";
                child.replaceWith(Jsoup.parse(markdownLink));
            }
        }
    }

    private EmbedBuilder createUrbanEmbed(UrbanEntry entry) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setThumbnail(getThumbnail());
        embedBuilder.setTitle(entry.term(), entry.link());

        if (entry.hasValidDefinition()) {
            embedBuilder.setDescription(entry.definition());
        }

        if (entry.hasValidExample()) {
            embedBuilder.addField("Example", entry.example(), false);
        }

        return embedBuilder;
    }
}