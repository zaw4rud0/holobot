package dev.zawarudo.holo.modules.booru.danbooru;

import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.modules.booru.BooruPost;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class DanbooruPost extends BooruPost {

    @SerializedName("created_at")
    private String createdAt;

    // Tag data
    @SerializedName("tag_string")
    private String tagString;
    @SerializedName("tag_string_general")
    private String tagStringGeneral;
    @SerializedName("tag_string_artist")
    private String tagStringArtist;
    @SerializedName("tag_string_meta")
    private String tagStringMeta;
    @SerializedName("tag_string_character")
    private String tagStringCharacter;
    @SerializedName("tag_string_copyright")
    private String tagStringCopyright;

    // File data
    @SerializedName("source")
    private String source;
    @SerializedName("image_width")
    private int imageWidth;
    @SerializedName("image_height")
    private int imageHeight;
    @SerializedName("file_ext")
    private String fileExtension;
    @SerializedName("large_file_url")
    private String largeFileUrl;
    @SerializedName("preview_file_url")
    private String previewFileUrl;
    @SerializedName("has_large")
    private boolean hasLarge;

    public DanbooruPost(int id, int score, String rating, String url) {
        super(id, score, rating, url);
    }

    /**
     * Returns the date this post was created.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns all the tags of this post.
     */
    @Nullable
    @Override
    public String[] getTags() {
        if (tagString == null || tagString.isEmpty()) {
            return null;
        }
        return tagString.split(" ");
    }

    /**
     * Returns the general tags of this post.
     */
    @Nullable
    public String[] getGeneralTags() {
        if (tagStringGeneral == null || tagStringGeneral.isEmpty()) {
            return null;
        }
        return tagStringGeneral.split(" ");
    }

    /**
     * Returns the artist tags of this post.
     */
    @Nullable
    public String[] getArtistTags() {
        if (tagStringArtist == null || tagStringArtist.isEmpty()) {
            return null;
        }
        return tagStringArtist.split(" ");
    }

    /**
     * Returns the meta tags of this post.
     */
    @Nullable
    public String[] getMetaTags() {
        if (tagStringMeta == null || tagStringMeta.isEmpty()) {
            return null;
        }
        return tagStringMeta.split(" ");
    }

    /**
     * Returns the character tags of this post.
     */
    @Nullable
    public String[] getCharacterTags() {
        if (tagStringCharacter == null || tagStringCharacter.isEmpty()) {
            return null;
        }
        return tagStringCharacter.split(" ");
    }

    /**
     * Returns the copyright tags of this post.
     */
    @Nullable
    public String[] getCopyrightTags() {
        if (tagStringCopyright == null || tagStringCopyright.isEmpty()) {
            return null;
        }
        return tagStringCopyright.split(" ");
    }

    /**
     * Returns the source of this post.
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the dimension of the image.
     */
    public Dimension getImageDimension() {
        return new Dimension(imageWidth, imageHeight);
    }

    /**
     * Returns the file extension of the image.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Returns the url of the large image.
     */
    public String getLargeFileUrl() {
        return largeFileUrl;
    }

    /**
     * Returns the url of the preview image.
     */
    public String getPreviewFileUrl() {
        return previewFileUrl;
    }

    /**
     * Checks if the post has a large image.
     */
    public boolean hasLargeImage() {
        return hasLarge;
    }
}
