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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(MessageReceivedEvent e) {
        EmbedBuilder builder = new EmbedBuilder();

        // Show a list of available actions
        if (args.length == 0 || args[0].equals("list")) {
            deleteInvoke(e);
            builder.setTitle("List of Actions");
            builder.setDescription(getActionsAsString());
            sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, getEmbedColor());
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
            builder.setDescription("Couldn't find this action. Use `" + getPrefix(e) + "action list` to see all available actions");
            sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, getEmbedColor());
        }
    }

    public void displayAction(MessageReceivedEvent e, Action action) {
        deleteInvoke(e);
        EmbedBuilder builder = new EmbedBuilder();

        String url;

        if (action.isApi()) {
            try {
                JsonObject obj = HttpResponse.getJsonObject(action.getRandomUrl());
                url = obj.get("url").getAsString();
            } catch (IOException ex) {
                builder.setTitle("Error");
                builder.setDescription("Something went wrong while fetching an image");
                sendEmbed(e, builder, 15, TimeUnit.SECONDS, true, getEmbedColor());
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

        String title = action.getSentence().replace("{s}", e.getMember().getEffectiveName()).replace("{u}", mention);
        builder.setTitle(title);
        builder.setImage(url);

        sendEmbed(e, builder, false, getEmbedColor());
    }

    /**
     * Checks if a given string is an action
     */
    public boolean isAction(String name) {
        return actions.containsKey(name);
    }

    /**
     * Returns the action associated with the given name
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

        Type listType = new TypeToken<List<Action>>(){}.getType();
        List<Action> apis = new Gson().fromJson(array, listType);
        apis.forEach(action -> actions.put(action.getName(), action));
    }

    private String getActionsAsString() {
        return actions.keySet().stream().sorted().toList().toString()
                .replace("]", "`")
                .replace("[", "`")
                .replace(",", "`")
                .replace(" ", ", `");
    }

    public static class Action {
        @SerializedName("name")
        private String name;
        @SerializedName("sentence")
        private String sentence;
        @SerializedName("directed")
        private boolean directed;
        @SerializedName("api")
        private boolean api;
        @SerializedName("urls")
        private List<String> urls;

        public String getName() {
            return name;
        }

        public String getSentence() {
            return sentence;
        }

        public boolean isDirected() {
            return directed;
        }

        public boolean isApi() {
            return api;
        }

        public String getRandomUrl() {
            return urls.get(new Random().nextInt(urls.size()));
        }

        public void addUrl(String url) {
            urls.add(url);
        }
    }
}