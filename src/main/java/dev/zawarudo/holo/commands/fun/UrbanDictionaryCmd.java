package dev.zawarudo.holo.commands.fun;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.core.misc.Emote;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.utils.Formatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private final EventWaiter waiter;

    public UrbanDictionaryCmd(EventWaiter waiter) {
        this.waiter = waiter;
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

            sendUrbanDictionaryEntries(event, entries);
        } catch (FileNotFoundException e) {
            sendErrorEmbed(event, "Couldn't find any Urban Dictionary entries with the given search terms.");
        } catch (IOException e) {
            sendErrorEmbed(event, "Something went wrong while searching your term. Please try again at a later time.");
        }
    }

    private void sendUrbanDictionaryEntries(MessageReceivedEvent event, List<UrbanEntry> entries) {
        List<Button> buttons = new ArrayList<>(List.of(
                Button.primary("prev", Emote.ARROW_LEFT.getAsEmoji()),
                Button.danger("exit", Emote.TRASH_BIN.getAsEmoji()),
                Button.primary("next", Emote.ARROW_RIGHT.getAsEmoji())
        ));
        setButtonStates(buttons, 0, entries.size() - 1);
        EmbedBuilder builder = createUrbanEmbed(entries.getFirst(), 0, entries.size());
        Message msg = event.getMessage().replyEmbeds(builder.build()).addComponents(ActionRow.of(buttons)).complete();
        awaitUserSelection(msg, event.getAuthor(), 0, entries, buttons);
    }

    private void awaitUserSelection(Message msg, User caller, int index, List<UrbanEntry> entries, List<Button> buttons) {
        waiter.waitForEvent(
                ButtonInteractionEvent.class,
                evt -> isReactionValid(evt, msg, caller),
                evt -> handleReaction(evt, index, entries, buttons, caller),
                5, TimeUnit.MINUTES,
                () -> msg.delete().queue()
        );
    }

    private boolean isReactionValid(ButtonInteractionEvent buttonEvent, Message msg, User caller) {
        // Ignore interactions on other messages
        if (!buttonEvent.getMessage().equals(msg)) {
            return false;
        }
        if (!buttonEvent.getUser().equals(caller)) {
            buttonEvent.reply("This command was not called by you!").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private void handleReaction(ButtonInteractionEvent buttonEvent, int index, List<UrbanEntry> entries, List<Button> buttons, User caller) {
        String id = buttonEvent.getButton().getCustomId();

        // Immediate delete
        if ("exit".equals(id)) {
            buttonEvent.deferEdit().queue(
                    s -> buttonEvent.getMessage().delete().queue(),
                    e -> {}
            );
            return;
        }

        // Prev / next logic
        if ("prev".equals(id)) {
            index = Math.max(0, index - 1);
        } else {
            index = Math.min(entries.size() - 1, index + 1);
        }

        setButtonStates(buttons, index, entries.size() - 1);
        EmbedBuilder builder = createUrbanEmbed(entries.get(index), index, entries.size());

        buttonEvent.deferEdit().queue(s -> {}, e -> {});
        Message msg = buttonEvent.getMessage().editMessageEmbeds(builder.build()).setComponents(ActionRow.of(buttons)).complete();

        // Keep waiting for prev/next
        awaitUserSelection(msg, caller, index, entries, buttons);
    }

    private void setButtonStates(List<Button> buttons, int index, int maxIndex) {
        for (int i = 0; i < buttons.size(); i++) {
            Button btn = buttons.get(i);
            if ("prev".equals(btn.getCustomId())) {
                buttons.set(i, btn.withDisabled(index == 0));
            } else if ("next".equals(btn.getCustomId())) {
                buttons.set(i, btn.withDisabled(index == maxIndex));
            }
        }
    }

    private EmbedBuilder createUrbanEmbed(UrbanEntry entry, int index, int total) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(getThumbnail());
        embedBuilder.setTitle(entry.term(), entry.link());
        embedBuilder.setFooter(String.format("Page %d / %d", index + 1, total));
        embedBuilder.setColor(getEmbedColor());
        if (entry.hasValidDefinition()) {
            String description = Formatter.truncate(entry.definition, MessageEmbed.DESCRIPTION_MAX_LENGTH);
            embedBuilder.setDescription(description);
        }
        if (entry.hasValidExample()) {
            String example = Formatter.truncate(entry.example(), MessageEmbed.VALUE_MAX_LENGTH);
            embedBuilder.addField("Example", example, false);
        }
        return embedBuilder;
    }

    private String getSearchTerm(Message message) {
        String content = message.getContentRaw();
        int firstSpaceIndex = content.indexOf(" ");
        return content.substring(firstSpaceIndex + 1);
    }

    private List<UrbanEntry> fetchDefinitions(@NotNull String searchTerm) throws IOException {
        List<UrbanEntry> list = new ArrayList<>();

        String url = String.format("%s/define.php?term=%s", BASE_URL, Formatter.encodeUrl(searchTerm));

        Document doc = Jsoup.parse(URI.create(url).toURL().openStream(), "UTF-8", url);
        Elements elements = doc.select("div.definition");

        int max = Math.max(10, elements.size());
        int counter = 0;

        for (Element element : elements) {
            if (counter == max) {
                break;
            }

            String title = extractTitle(element);
            String meaning = extractMeaning(element);
            String example = extractExample(element);
            String link = extractLink(element);

            UrbanEntry entry = new UrbanEntry(title, meaning, example, link);
            list.add(entry);

            counter++;
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
        return (titleElement != null) ? BASE_URL + titleElement.attr("href").replace(" ", "%20") : null;
    }

    /**
     * Turns HTML links into Markdown links, so they can be displayed properly in a Discord embed.
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

    private record UrbanEntry(String term, String definition, String example, String link) {
        public boolean hasValidDefinition() {
            return definition != null && !definition.trim().isEmpty();
        }

        public boolean hasValidExample() {
            return example != null && !example.trim().isEmpty();
        }
    }
}