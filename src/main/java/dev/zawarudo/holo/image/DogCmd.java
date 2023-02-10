package dev.zawarudo.holo.image;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.apis.DogAPI;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.exceptions.APIException;
import dev.zawarudo.holo.exceptions.InvalidRequestException;
import dev.zawarudo.holo.misc.EmbedColor;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.Reader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Command(name = "dog",
        description = "Fetches an image of a dog.",
        usage = "[breeds | <breed> | random]",
        category = CommandCategory.IMAGE)
public class DogCmd extends AbstractCommand {

    private static final String PATH = "./src/main/resources/misc/dog-breeds.json";
    private final String[] breeds;
    private final Map<String, String> formattedNames;

    public DogCmd() {
        formattedNames = new HashMap<>();

        try {
            JsonObject obj = Reader.readJsonObject(PATH);
            breeds = new Gson().fromJson(obj.getAsJsonArray("breeds"), String[].class);

            obj.getAsJsonArray("breeds-formatted").forEach(breed -> {
                String name = breed.getAsJsonObject().get("name").getAsString();
                String formatted = breed.getAsJsonObject().get("formattedName").getAsString();
                formattedNames.put(name, formatted);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);

        if (args.length == 0 || args[0].equalsIgnoreCase("random")) {
            sendRandomDogEmbed(event);
        } else if (args[0].equalsIgnoreCase("breeds") || args[0].equalsIgnoreCase("list")) {
            sendBreedListEmbed(event);
        } else if (Arrays.stream(breeds).anyMatch(b -> b.equalsIgnoreCase(args[0]))) {
            sendDogImageEmbed(event, args[0]);
        } else {
            sendUnknownBreedEmbed(event);
        }
    }

    /**
     * Sends an embed with a random dog image.
     */
    private void sendRandomDogEmbed(MessageReceivedEvent event) {
        String url;
        try {
            url = DogAPI.getRandomImage();
        } catch (APIException e) {
            e.printStackTrace();
            sendAPIErrorEmbed(event);
            return;
        }
        String breed = url.split("/")[4];

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Here is your random dog!");
        builder.setDescription("It's a **" + getFormattedName(breed) + "**!");
        builder.setImage(url);
        sendEmbed(event, builder, true,2, TimeUnit.MINUTES, getEmbedColor());
    }

    /**
     * Sends an embed with a list of available breeds.
     */
    private void sendBreedListEmbed(MessageReceivedEvent event) {
        String s = String.format("```%s```", String.join(", ", breeds));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Available dog breeds");
        builder.setDescription(s);
        sendEmbed(event, builder, true, 2, TimeUnit.MINUTES, getEmbedColor());
    }

    /**
     * Sends an embed with a dog image of the specified breed.
     */
    private void sendDogImageEmbed(MessageReceivedEvent event, String breed) {
        String url = null;
        try {
            url = DogAPI.getRandomBreedImage(breed);
        } catch (APIException | InvalidRequestException e) {
            sendAPIErrorEmbed(event);
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Here is your " + getFormattedName(breed) + "!");
        builder.setImage(url);
        sendEmbed(event, builder, true, 2, TimeUnit.MINUTES, getEmbedColor());
    }

    /**
     * Sends an embed stating that the given breed is unknown.
     */
    private void sendUnknownBreedEmbed(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Unknown breed");
        builder.setDescription("The breed you specified is unknown. Use " + getPrefix(event) + "`dog breeds` to see a list of available breeds.");
        sendEmbed(event, builder, true, 1, TimeUnit.MINUTES, getEmbedColor());
    }

    /**
     * Sends an embed stating that there was an error with the API.
     */
    private void sendAPIErrorEmbed(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("API Error");
        builder.setDescription("An error occurred while fetching the image. Try again later!");
        sendEmbed(event, builder, true, 30, TimeUnit.SECONDS, EmbedColor.ERROR.getColor());
    }

    /**
     * Gets the formatted name of a breed.
     *
     * @param name The standard name as given by the API.
     * @return The formatted name to be displayed in the embed.
     */
    private String getFormattedName(String name) {
        return formattedNames.getOrDefault(name, Formatter.capitalize(name));
    }
}