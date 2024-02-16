package dev.zawarudo.holo.commands.image;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.utils.TypeTokenUtils;
import dev.zawarudo.holo.utils.Writer;
import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.core.misc.EmbedColor;
import dev.zawarudo.holo.utils.HttpResponse;
import dev.zawarudo.holo.utils.Reader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Class of the Action command. By calling this command, the user can get a random
 * reaction or action gif.
 */
@Command(name = "action",
        description = "Sends an action GIF. For directed actions you can either mention an user or reply to a " +
                "message to direct the action towards them.",
        usage = "[<action> | list]",
        example = "blush",
        embedColor = EmbedColor.LIGHT_GRAY,
        category = CommandCategory.IMAGE)
public class ActionCmd extends AbstractCommand {

    private static final String FILE_PATH = "./src/main/resources/misc/actions.json";

    private final Map<String, Action> actions;
    private static final Random RANDOM = new Random();

    public ActionCmd() {
        actions = new HashMap<>();
        try {
            initializeActions();
        } catch (IOException ex) {
            throw new IllegalStateException("Something went wrong while initializing the actions!", ex);
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        // Show a list of available actions
        if (args.length == 0 || args[0].equals("list")) {
            deleteInvoke(event);
            builder.setTitle("List of Actions");
            builder.setDescription(getActionsAsString());
            sendEmbed(event, builder, true, 1, TimeUnit.MINUTES, getEmbedColor());
        }

        // Create new action
        else if (args[0].equals("create") && isBotOwner(event.getAuthor())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            createNewAction(event);
        }

        else if (args[0].equals("add") && isBotOwner(event.getAuthor())) {
            args = Arrays.copyOfRange(args, 1, args.length);
            addImageToAction(event);
        }

        // Call specific action
        else if (isAction(args[0])) {
            Action action = actions.get(args[0]);
            args = Arrays.copyOfRange(args, 1, args.length);
            displayAction(event, action);
        }

        // Unknown action
        else {
            sendErrorEmbed(event, "Couldn't find this action. Use `" + getPrefix(event) + "action list` to see all available actions.");
        }
    }

    private void createNewAction(MessageReceivedEvent event) {
        // E.g. <action create <name> <is_api> <text>
        if (args.length < 3) {
            sendErrorEmbed(event, "Insufficient argument: Expected `<name> <is_api> <sentence>`");
            return;
        }

        String name = args[0];
        boolean isApi = isBoolean(args[1]) && Boolean.parseBoolean(args[1]);
        int start = isBoolean(args[1]) ? 2 : 1;
        String sentence = String.join(" ", Arrays.copyOfRange(args, start, args.length));

        if (isAction(name)) {
            sendErrorEmbed(event, "This action already exists!");
        } else if (!sentence.contains("{s}")) {
            sendErrorEmbed(event, "Sentence needs a subject (`{s}`)!");
        } else {
            deleteInvoke(event);
            actions.put(name, new Action(name, sentence, isApi));
            try {
                writeActionsToFile();
                event.getChannel().sendMessage(String.format("Successfully created action: `%s`", name)).queue();
            } catch (IOException e) {
                sendErrorEmbed(event, "Something went wrong while storing the updated actions: " + e.getMessage());
                logger.error("Something went wrong while storing the new action.", e);
            }
        }
    }

    private void addImageToAction(MessageReceivedEvent event) {
        // E.g. <action add <name> <url>
        if (args.length < 2) {
            sendErrorEmbed(event, "Insufficient arguments: Expected `<name> <url>`");
            return;
        }

        String name = args[0];
        String url = args[1];

        if (!isAction(name)) {
            sendErrorEmbed(event, "Not a valid action: " + args[0]);
        } else if (!isValidUrl(url)) {
            sendErrorEmbed(event, "Not a valid URL: " + args[1]);
        } else {
            deleteInvoke(event);
            getAction(name).addNewUrl(url);
            try {
                writeActionsToFile();
                event.getChannel().sendMessage(String.format("Added new link to action: `%s`", name)).queue();
            } catch (IOException e) {
                sendErrorEmbed(event, "Something went wrong while storing the updated actions: " + e.getMessage());
                logger.error("Something went wrong while storing the action with the new url.", e);
            }
        }
    }

    /**
     * Displays the action gif or image in an embed and sends it.
     */
    public void displayAction(@NotNull MessageReceivedEvent event, @NotNull Action action) {
        // In case the member is a webhook
        if (event.getMember() == null) {
            return;
        }

        deleteInvoke(event);

        Optional<String> result = fetchActionUrl(action);
        if (result.isEmpty()) {
            sendErrorEmbed(event, "Something went wrong while fetching an image. Please try again later.");
            return;
        }
        String url = result.get();

        String mention = determineMention(event);
        String title = action.getSentence().replace("{s}", event.getMember().getEffectiveName()).replace("{u}", mention);

        sendActionEmbed(event, url, title);
    }

    private Optional<String> fetchActionUrl(Action action) {
        return action.getRandomUrl().flatMap(url -> action.isApi() ? fetchUrlFromApi(url) : Optional.of(url));
    }

    private Optional<String> fetchUrlFromApi(String url) {
        try {
            JsonObject obj = HttpResponse.getJsonObject(url);
            String imageUrl = obj.getAsJsonArray("results").get(0).getAsJsonObject().get("url").getAsString();
            return Optional.ofNullable(imageUrl);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    private String determineMention(MessageReceivedEvent event) {
        Message repliedTo = event.getMessage().getReferencedMessage();
        if (repliedTo != null) {
            return "you";
        } else if (args.length != 0) {
            return event.getMessage().getMentions().getMembers().isEmpty() ?
                    String.join(" ", args) :
                    event.getMessage().getMentions().getMembers().get(0).getEffectiveName();
        } else {
            return "nothing";
        }
    }

    private void sendActionEmbed(MessageReceivedEvent event, String url, String title) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setImage(url).setTitle(title);

        Message repliedTo = event.getMessage().getReferencedMessage();
        if (repliedTo != null) {
            sendReplyEmbed(repliedTo, builder, getEmbedColor());
        } else {
            sendEmbed(event, builder, false, getEmbedColor());
        }
    }

    /**
     * Checks if a given string is an action.
     */
    public boolean isAction(String name) {
        return actions.containsKey(name);
    }

    /**
     * Returns the action associated with the given name.
     */
    public Action getAction(String name) {
        return actions.get(name);
    }

    /**
     * Initializes all actions using a Json file containing information for each action.
     */
    private void initializeActions() throws IOException {
        JsonArray array = Reader.readJsonArray(FILE_PATH);
        Type listType = TypeTokenUtils.getListTypeToken(Action.class);
        List<Action> actionList = new Gson().fromJson(array, listType);
        actionList.forEach(action -> actions.put(action.getName(), action));
    }

    /**
     * Writes the current actions to the file. Used to store updated actions.
     */
    private void writeActionsToFile() throws IOException {
        List<Action> actionList = new ArrayList<>(actions.values());
        Collections.sort(actionList);
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(actionList);
        Writer.writeToFile(jsonString, FILE_PATH);
    }

    private String getActionsAsString() {
        return "```" + actions.keySet().stream().sorted().toList().toString()
                .replace("]", "")
                .replace("[", "")
                .replace(",", "")
                .replace(" ", ", ") + "```";
    }

    /**
     * Class representing an action. Serves as a container for all the information.
     */
    public static class Action implements Comparable<Action> {
        @SerializedName("name")
        private String name;
        @SerializedName("sentence")
        private String sentence;
        @SerializedName("api")
        private boolean api;
        @SerializedName("urls")
        private List<String> urls;

        public Action(String name, String sentence, boolean api) {
            this.name = name;
            this.sentence = sentence;
            this.api = api;
            urls = new ArrayList<>();
        }

        /**
         * Gets the name of the action.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the sentence associated with the action.
         */
        public String getSentence() {
            return sentence;
        }

        /**
         * Checks whether the images or gifs are fetched from an API.
         */
        public boolean isApi() {
            return api;
        }

        /**
         * Gets a random URL to a gif or image of this action.
         */
        public Optional<String> getRandomUrl() {
            if (urls.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(urls.get(RANDOM.nextInt(urls.size())));
        }

        /**
         * Adds a new image or GIF to this action. Make sure to store the updated action as well.
         */
        public void addNewUrl(String url) {
            urls.add(url);
        }

        @Override
        public int compareTo(@NotNull Action o) {
            return this.name.compareTo(o.name);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Action action && name.equals(action.name);
        }
    }
}