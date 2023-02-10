package dev.zawarudo.holo.image;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.utils.HttpResponse;
import dev.zawarudo.holo.utils.Reader;
import net.dv8tion.jda.api.EmbedBuilder;
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
        description = "Sends an action GIF",
        usage = "[<action> | list]",
        example = "blush",
        embedColor = EmbedColor.LIGHT_GRAY,
        category = CommandCategory.IMAGE)
public class ActionCmd extends AbstractCommand {

    private final Map<String, Action> actions;

    public ActionCmd() {
        actions = new HashMap<>();
        try {
            initializeActions();
        } catch (IOException ex) {
            throw new RuntimeException("Something went wrong while initializing the actions!", ex);
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        EmbedBuilder builder = new EmbedBuilder();

        // Show a list of available actions
        if (args.length == 0 || args[0].equals("list")) {
            deleteInvoke(e);
            builder.setTitle("List of Actions");
            builder.setDescription(getActionsAsString());
            sendEmbed(e, builder, true, 1, TimeUnit.MINUTES, getEmbedColor());
        }

        // Call specific action
        else if (isAction(args[0])) {
            Action action = actions.get(args[0]);
            args = Arrays.copyOfRange(args, 1, args.length);
            displayAction(e, action);
        }

        // Unknown action
        else {
            builder.setTitle("Error");
            builder.setDescription("Couldn't find this action. Use `" + getPrefix(e) + "action list` to see all available actions.");
            sendEmbed(e, builder, true, 1, TimeUnit.MINUTES, EmbedColor.ERROR.getColor());
        }
    }

    /**
     * Displays the action gif or image in an embed and sends it.
     */
    public void displayAction(@NotNull MessageReceivedEvent e, @NotNull Action action) {
        deleteInvoke(e);
        EmbedBuilder builder = new EmbedBuilder();

        String url;

        if (action.isApi()) {
            try {
                JsonObject obj = HttpResponse.getJsonObject(action.getRandomUrl());
                url = obj.getAsJsonArray("results").get(0).getAsJsonObject().get("url").getAsString();
            } catch (IOException ex) {
                builder.setTitle("Error");
                builder.setDescription("Something went wrong while fetching an image");
                sendEmbed(e, builder, true, 15, TimeUnit.SECONDS, EmbedColor.ERROR.getColor());
                return;
            }
        } else {
            url = action.getRandomUrl();
        }

        String mention = "nothing";

        if (args.length != 0) {
            if (!e.getMessage().getMentions().getMembers().isEmpty()) {
                mention = e.getMessage().getMentions().getMembers().get(0).getEffectiveName();
            } else {
                mention = String.join(" ", args);
            }
        }

        // In case the member is a webhook
        if (e.getMember() == null) {
            return;
        }

        String title = action.getSentence().replace("{s}", e.getMember().getEffectiveName()).replace("{u}", mention);
        builder.setTitle(title);
        builder.setImage(url);
        sendEmbed(e, builder, false, getEmbedColor());
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
        String path = "./src/main/resources/misc/actions.json";
        JsonArray array = Reader.readJsonArray(path);
        Type listType = new TypeToken<List<Action>>() {}.getType();
        List<Action> actionList = new Gson().fromJson(array, listType);
        actionList.forEach(action -> actions.put(action.getName(), action));
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
    private static class Action {
        @SerializedName("name")
        private String name;
        @SerializedName("sentence")
        private String sentence;
        @SerializedName("api")
        private boolean api;
        @SerializedName("urls")
        private List<String> urls;

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
        public String getRandomUrl() {
            return urls.get(new Random().nextInt(urls.size()));
        }
    }
}