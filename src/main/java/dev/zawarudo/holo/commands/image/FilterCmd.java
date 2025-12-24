package dev.zawarudo.holo.commands.image;

import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import dev.zawarudo.holo.modules.image.FilterRegistry;
import dev.zawarudo.holo.modules.image.ImageFilter;
import dev.zawarudo.holo.utils.ImageOperations;
import dev.zawarudo.holo.utils.annotations.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(
        name = "filter",
        description = "Applies a dramatic grayscale filter while preserving red tones",
        alias = {"acheron"},
        category = CommandCategory.IMAGE,
        ownerOnly = true
)
public class FilterCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        if (shouldShowList()) {
            replyWithFilterListEmbed(event);
            return;
        }

        Optional<String> imageUrl = resolveImageUrl(event);
        if (imageUrl.isEmpty()) {
            sendMissingImageError(event);
            return;
        }

        ImageFilter filter = resolveFilter();
        if (filter == null) {
            sendUnknownFilterError(event);
            return;
        }

        String[] filterArgs = resolveFilterArgs();
        applyFilterAndReply(event, imageUrl.get(), filter, filterArgs);
    }

    private boolean shouldShowList() {
        return args.length == 0 || "list".equalsIgnoreCase(args[0]);
    }

    private void replyWithFilterListEmbed(@NotNull MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Available filters")
                .setDescription(formatFiltersAsCodeBlock());

        event.getMessage().replyEmbeds(eb.build()).queue();
    }

    private Optional<String> resolveImageUrl(@NotNull MessageReceivedEvent event) {
        Message referenced = event.getMessage().getReferencedMessage();
        return referenced != null ? getImage(referenced) : getImage(event.getMessage());
    }

    private void sendMissingImageError(@NotNull MessageReceivedEvent event) {
        sendErrorEmbed(event, "You need to provide an image!");
    }

    private ImageFilter resolveFilter() {
        String filterName = args[0];
        return FilterRegistry.get(filterName).orElse(null);
    }

    private void sendUnknownFilterError(@NotNull MessageReceivedEvent event) {
        String filterName = args[0];
        sendErrorEmbed(event, "Unknown filter: `" + filterName);
    }

    private String[] resolveFilterArgs() {
        return (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
    }

    private void applyFilterAndReply(MessageReceivedEvent event, String url, ImageFilter filter, String[] filterArgs) {
        try {
            BufferedImage img = readImage(url);
            if (img == null) {
                sendUnreadableImageError(event, url);
                return;
            }

            BufferedImage result = filter.apply(img, filterArgs);
            replyWithImage(event, result, String.format("filter-%s.png", filter.name()));
        } catch (IllegalArgumentException ex) {
            sendErrorEmbed(event, ex.getMessage());
        } catch (IOException ex) {
            sendErrorEmbed(event, "Something went wrong while applying the filter.");
            logger.error("Error applying filter {} on image: {}", filter.name(), url, ex);
        }
    }

    private BufferedImage readImage(String url) throws IOException {
        return ImageIO.read(URI.create(url).toURL());
    }

    private void sendUnreadableImageError(@NotNull MessageReceivedEvent event, @NotNull String url) {
        sendErrorEmbed(event, "I couldn't read the image. Please try a different format.");
        logger.error("Image is null: {}", url);
    }

    private void replyWithImage(MessageReceivedEvent event, BufferedImage img, String filename) throws IOException {
        try (InputStream input = ImageOperations.toInputStream(img)) {
            event.getMessage().replyFiles(FileUpload.fromData(input, filename)).queue();
        }
    }

    private String formatFiltersAsCodeBlock() {
        List<ImageFilter> filters = FilterRegistry.list();

        String joined = filters.stream()
                .map(ImageFilter::name)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining("\n"));

        return "```\n" + joined + "\n```";
    }
}