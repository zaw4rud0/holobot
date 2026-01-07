package dev.zawarudo.holo.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class ImageResolver {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator();

    public Optional<String> resolveImageUrl(@NotNull Message msg) {
        // Attachment image
        for (Message.Attachment a : msg.getAttachments()) {
            if (a.isImage()) return Optional.of(a.getUrl());
        }

        // Embed image
        for (MessageEmbed e : msg.getEmbeds()) {
            MessageEmbed.ImageInfo img = e.getImage();
            if (img != null && img.getUrl() != null) return Optional.of(img.getUrl());
        }

        // URL in message text
        String raw = msg.getContentRaw();
        if (raw.isBlank()) return Optional.empty();

        String[] parts = raw.split("\\s+");
        String last = parts[parts.length - 1];
        return URL_VALIDATOR.isValid(last) ? Optional.of(last) : Optional.empty();
    }
}