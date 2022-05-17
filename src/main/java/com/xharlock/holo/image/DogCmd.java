package com.xharlock.holo.image;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import com.xharlock.holo.utils.HttpResponse;
import com.xharlock.holo.utils.Reader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/*
 * TODO:
 *  - Add formatted breed names to "./src/main/resources/misc/dog-breeds-names.json.json"
 *  - Store formatted breed names in a map at startup
 *  - Allow owner to add formatted breed names to the breed list and map with a subcommand
 *  - Implement DogAPI
 */

@Command(name = "dog",
        description = "Sends an image of a dog.",
        usage = "[breeds | <breed> | random]",
        embedColor = EmbedColor.NONE,
        category = CommandCategory.IMAGE)
public class DogCmd extends AbstractCommand {

    private static final String PATH = "./src/main/resources/misc/dog-breeds.json";
    private static String[] breeds;

    public DogCmd() {
        try {
            JsonArray array = Reader.readJsonArray(PATH);
            breeds = new Gson().fromJson(array, String[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(MessageReceivedEvent e) {
        deleteInvoke(e);
        EmbedBuilder builder = new EmbedBuilder();

        if (args.length == 0 || args[0].equalsIgnoreCase("random")) {
            String url;
            try {
                url = getRandomDog();
            } catch (IOException ex) {
                builder.setTitle("API Error");
                builder.setDescription("Something went wrong with the API. Try again later.");
                sendEmbed(e, builder, 30, TimeUnit.SECONDS, true, getEmbedColor());
                return;
            }

            String breed = url.split("/")[4];

            builder.setTitle("Here is your random dog!");
            builder.setDescription("It's a **" + format(breed) + "**!");
            builder.setImage(url);
            sendEmbed(e, builder, 2, TimeUnit.MINUTES, true, getEmbedColor());
        } else if (args[0].equalsIgnoreCase("breeds")) {
            builder.setTitle("All available dog breeds");
            builder.setDescription("```" + String.join(", ", breeds) + "```");
            sendEmbed(e, builder, 2, TimeUnit.MINUTES, true, getEmbedColor());
        } else if (Arrays.stream(breeds).anyMatch(breed -> breed.equalsIgnoreCase(args[0]))) {
            String url;
            try {
                url = getDog(args[0]);
            } catch (IOException ex) {
                builder.setTitle("API Error");
                builder.setDescription("Something went wrong with the API. Try again later.");
                sendEmbed(e, builder, 30, TimeUnit.SECONDS, true, getEmbedColor());
                return;
            }
            builder.setTitle("Here is your " + format(args[0]) + "!");
            builder.setImage(url);
            sendEmbed(e, builder, 2, TimeUnit.MINUTES, true, getEmbedColor());
        } else {
            builder.setTitle("Unknown breed");
            builder.setDescription("Use `" + getPrefix(e) + "dog breeds` to see all available breeds.");
            sendEmbed(e, builder, 30, TimeUnit.SECONDS, true, getEmbedColor());
        }
    }

    private String getRandomDog() throws IOException {
        String url = "https://dog.ceo/api/breeds/image/random";
        JsonObject obj = HttpResponse.getJsonObject(url);
        return obj.get("message").getAsString();
    }

    private String getDog(String breed) throws IOException {
        String url = String.format("https://dog.ceo/api/breed/%s/images/random", breed);
        JsonObject obj = HttpResponse.getJsonObject(url);
        return obj.get("message").getAsString();
    }

    /**
     * TODO: Refactor away
     */
    @Deprecated(forRemoval = true)
    private String format(String breed) {
        return switch (breed) {
            case ("akita") -> "Akita";
            case ("beagle") -> "Beagle";
            case ("corgi-cardigan") -> "Cardigan Welsh Corgi";
            case ("pembroke") -> "Pembroke Welsh Corgi";
            case ("eskimo") -> "Eskimo";
            case ("dane-great") -> "Great Dane";

            // Bulldog breeds
            case ("bulldog-boston") -> "Boston Terrier";
            case ("bulldog-english") -> "English Bulldog";
            case ("bulldog-french") -> "French Bulldog";

            // Gundog breeds (Setter and Pointer dogs)
            case ("pointer") -> "Pointer";
            case ("pointer-english") -> "English Pointer";
            case ("pointer-german") -> "Pointing dog";
            case ("pointer-germanlonghair") -> "German Longhaired Pointer";
            case ("setter") -> "Setter";
            case ("setter-english") -> "English Setter";
            case ("setter-irish") -> "Irish Setter";

            // Mastiff breeds
            case ("mastiff") -> "Mastiff";
            case ("mastiff-bull") -> "Bullmastiff";
            case ("mastiff-english") -> "English Mastiff";
            case ("mastiff-tibetan") -> "Tibetan Mastiff";

            // Poodle breeds
            case ("poodle") -> "Poodle";
            case ("poodle-miniature") -> "Miniature Poodle";
            case ("poodle-standard") -> "Standard Poodle";
            case ("poodle-toy") -> "Toy Poodle";

            // Retriever breeds
            case ("retriever") -> "Retriever";
            case ("retriever-chesapeake") -> "Chesapeake Bay Retriever";
            case ("retriever-flatcoated") -> "Flat-coated Retriever";
            case ("retriever-golden") -> "Golden Retriever";
            case ("retriever-curly") -> "Curly-coated Retriever";
            case ("retriever-labrador") -> "Labrador Retriever";

            // Sheepdog breeds
            case ("komondor") -> "Komondor";
            case ("sheepdog-english") -> "English Sheepdog";
            case ("sheepdog-shetland") -> "Shetland Sheepdog";

            // Shepherd breeds
            case ("australian") -> "Australian Shepherd Dog";
            case ("australian-shepherd") -> "Australian Shepherd Dog";
            case ("malinois") -> "Malinois";
            case ("tervuren") -> "Tervuren";
            case ("groenendael") -> "Groenendael";
            case ("laekenois") -> "Laekenois";
            case ("ovcharka-caucasian") -> "Caucasian Shepherd Dog";
            case ("germanshepherd") -> "German Shepherd Dog";

            // Spaniel breeds
            case ("spaniel-blenheim") -> "King Charles Spaniel";
            case ("spaniel-brittany") -> "Brittany Spaniel";
            case ("spaniel-cocker") -> "Cocker Spaniel";
            case ("spaniel-irish") -> "Irish Water Spaniel";
            case ("spaniel-japanese") -> "Japanese Spaniel";
            case ("spaniel-sussex") -> "Sussex Spaniel";
            case ("spaniel-welsh") -> "Welsh Springer Spaniel";

            // Terrier breeds
            case ("airedale") -> "Airedale Terrier";
            case ("terrier") -> "Terrier";
            case ("terrier-american") -> "American Staffordshire Terrier";
            case ("terrier-australian") -> "Australian Terrier";
            case ("terrier-bedlington") -> "Bedlington Terrier";
            case ("terrier-border") -> "Border Terrier";
            case ("terrier-cairn") -> "Cairn Terrier";
            case ("terrier-dandie") -> "Dandie Dinmont Terrier";
            case ("terrier-fox") -> "Fox Terrier";
            case ("terrier-irish") -> "Irish Terrier";
            case ("terrier-kerryblue") -> "Kerry Blue Terrier";
            case ("terrier-lakeland") -> "Lakeland Terrier";
            case ("terrier-norfolk") -> "Norfolk Terrier";
            case ("terrier-norwich") -> "Norwich Terrier";
            case ("terrier-patterdale") -> "Patterdale Terrier";
            case ("terrier-russell") -> "Jack Russell Terrier";
            case ("terrier-scottish") -> "Scottish Terrier";
            case ("terrier-silky") -> "Silky Terrier";
            case ("terrier-tibetan") -> "Tibetan Terrier";
            case ("terrier-toy") -> "Toy Terrier";
            case ("terrier-welsh") -> "Welsh Terrier";
            case ("terrier-westhighland") -> "West Highland White Terrier";
            case ("terrier-wheaten") -> "Wheaten Terrier";

            // Other breeds
            case ("mexicanhairless") -> "Mexican Hairless Dog";
            case ("african") -> "African";
            case ("bouvier") -> "Bouvier des Flandres";
            case ("papillon") -> "Papillon";
            case ("mountain-bernese") -> "Bernese Mountain Dog";
            case ("entlebucher") -> "Entlebucher Mountain Dog";
            case ("mountain-swiss") -> "Swiss Mountain Dog";
            case ("pyrenees") -> "Pyrenean Mountain Dog";
            case ("stbernard") -> "St. Bernard";
            case ("bullterrier-staffordshire") -> "Staffordshire Bull Terrier";
            case ("schnauzer-giant") -> "Giant Schnauzer";
            case ("schnauzer-miniature") -> "Miniature Schnauzer";
            case ("kuvasz") -> "Kuvasz";
            case ("samoyed") -> "Samoyed";
            case ("springer-english") -> "English Springer Spaniel";
            case ("hound-basset") -> "Basset Hound";
            case ("hound-blood") -> "Blood Hound";
            case ("hound-ibizan") -> "Ibizan Hound";
            case ("hound-plott") -> "Plott Hound";
            case ("hound-afghan") -> "Afghan Hound";
            case ("hound-english") -> "English Hound";
            case ("hound-walker") -> "Walker Hound";
            case ("otterhound") -> "Otterhound";
            case ("wolfhound-irish") -> "Irish Wolfhound";
            case ("greyhound-italian") -> "Italian Greyhound";
            case ("coonhound") -> "Coonhound";
            case ("redbone") -> "Redbone Coonhound";
            case ("deerhound-scottish") -> "Scottish Deerhound";
            case ("buhund-norwegian") -> "Norwegian Buhund";
            case ("finnish-lapphund") -> "Finnish Lapphund";
            case ("saluki") -> "Saluki";
            case ("elkhound-norwegian") -> "Norwegian Elkhound";
            case ("cotondetulear") -> "Coton de Tulear";
            case ("newfoundland") -> "Newfoundland";
            case ("ridgeback-rhodesian") -> "Rhodesian Ridgeback";
            case ("chihuahua") -> "Chihuahua";
            case ("husky") -> "Husky";
            case ("collie-border") -> "Border Collie";
            case ("malamute") -> "Alaskan Malamute";
            case ("briard") -> "Briard";
            case ("waterdog-spanish") -> "Spanish Water Dog";
            case ("appenzeller") -> "Appenzeller Sennenhund";
            case ("vizsla") -> "Vizsla";
            case ("cattledog-australian") -> "Australian Cattle Dog";
            case ("maltese") -> "Maltese";
            case ("havanese") -> "Havanese";
            case ("frise-bichon") -> "Bichon Frise";
            case ("brabancon") -> "Griffon Bruxellois";
            case ("affenpinscher") -> "Affenpinscher";
            case ("pinscher-miniature") -> "Miniature Pinscher";
            case ("whippet") -> "Whippet";
            case ("bluetick") -> "Bluetick Coonhound";
            case ("basenj") -> "Basenji";
            case ("labrador") -> "Labrador Retriever";
            case ("dalmatian") -> "Dalmatian";
            case ("labradoodle") -> "Labradoodle";
            case ("schipperke") -> "Schipperke";
            case ("doberman") -> "Dobermann";
            case ("shiba") -> "Shiba Inu";
            case ("rottweiler") -> "Rottweiler";
            case ("pug") -> "Pug";
            case ("boxer") -> "Boxer";
            case ("pomeranian") -> "Pomeranian";
            case ("pekinese") -> "Pekingese";
            case ("chow") -> "Chow Chow";
            case ("kelpie") -> "Kelpie";
            case ("weimaraner") -> "Weimaraner";
            case ("shihtzu") -> "Shih Tzu";
            default -> breed;
        };
    }
}