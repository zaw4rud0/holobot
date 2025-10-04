package dev.zawarudo.holo.core.misc;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

/**
 * Enum to store all the emojis and emotes Holo uses.
 */
public enum Emote {

    // ###### Emoji section ######

    // Misc emotes
    THUMBS_UP("👍"),
    THUMBS_DOWN("👎"),
    HEART("❤"),
    HEARTS("💕"),
    HEARTBREAK("💔"),
    BLUSH("😊"),
    FLUSHED("😳"),
    ZZZ("💤"),
    SKULL("💀"),

    // Food and drinks
    BURGER("🍔"),
    FRIES("🍟"),
    PIZZA("🍕"),
    COFFEE("☕"),
    BEER("🍺"),
    WINE("🍷"),
    APPLE("🍎"),
    BAGUETTE("🥖"),
    COOKIE("🍪"),
    DOUGHNUT("🍩"),
    HOTDOG("🌭"),

    // Objects
    AXE("🪓"),
    BOMB("💣"),
    COIN("💰"),
    CROWN("👑"),
    DOLLAR("💵"),
    DIAMOND("💎"),
    GEAR("⚙"),
    HAMMER("🔨"),
    MAGIC_WAND("🖌"),
    MAGNET("🧲"),
    PICKAXE("⛏"),
    PISTOL("🔫"),
    SAW("🔪"),
    WRENCH("🔧"),
    TRASH_BIN("🗑️"),

    // Arrows and traffic signs
    ARROW_UP("⬆"),
    ARROW_DOWN("⬇"),
    ARROW_LEFT("⬅"),
    ARROW_RIGHT("➡"),

    // Music and sound
    MUTED("🔇"),
    SPEAKER("🔈"),
    SPEAKER_QUIET("🔉"),
    SPEAKER_LOUD("🔊"),
    BELL("🔔"),
    NO_BELL("🔕"),
    LOUDSPEAKER("📢"),
    MEGAPHONE("📣"),
    POSTAL_HORN("📯"),
    NOTE("🎵"),
    NOTES("🎶"),
    MICROPHONE("🎤"),
    HEADPHONES("🎧"),
    RADIO("📻"),

    // Numbers
    ZERO("0️⃣"),
    ONE("1️⃣"),
    TWO("2️⃣"),
    THREE("3️⃣"),
    FOUR("4️⃣"),
    FIVE("5️⃣"),
    SIX("6️⃣"),
    SEVEN("7️⃣"),
    EIGHT("8️⃣"),
    NINE("9️⃣"),
    TEN("🔟"),
    HUNDRED("💯"),

    // Signs and warnings
    CROSS("❌"),
    WARNING("⚠"),
    NO_ENTRY("⛔"),
    PROHIBITED("🚫"),
    NO_BICYCLES("🚳"),
    RADIOACTIVE("☢"),
    BIOHAZARD("☣"),
    UNDERAGE("🔞"),

    // ###### Emote section ######

    // Misc emotes
    PEPE_LOVE("<:pepelove:806641947292860466>"),
    PINGED("<:pinged:975678737205063721>"),
    HOLO_SMILE("<:HoloSmile:975684803523403787>"),

    // Akinator emotes
    TICK("<:tick:824297786102644757>"),
    CROSS2("<:cross:824297739377573910>"),
    UNDO("<:undo:824289332688060416>"),
    CONTINUE("<:continue:824749379842998283>"),

    // Pokémon emotes
    TYPE_NORMAL("<:type_normal:805109990393905192>"),
    TYPE_FIRE("<:type_fire:805109990473596948>"),
    TYPE_FIGHTING("<:type_fighting:805109990112886785>"),
    TYPE_FLYING("<:type_flying:805109990305562634>"),
    TYPE_WATER("<:type_water:805109990536642620>"),
    TYPE_GRASS("<:type_grass:805109990259687496>"),
    TYPE_ELECTRIC("<:type_electric:805109990138314804>"),
    TYPE_POISON("<:type_poison:805109990448168960>"),
    TYPE_DARK("<:type_dark:805109990301368400>"),
    TYPE_FAIRY("<:type_fairy:805109990372278292>"),
    TYPE_PSYCHIC("<:type_psychic:805109990163218523>"),
    TYPE_STEEL("<:type_steel:805109990444105748>"),
    TYPE_ROCK("<:type_rock:805109990447775744>"),
    TYPE_GROUND("<:type_ground:805109990284066817>"),
    TYPE_BUG("<:type_bug:805109990158630953>"),
    TYPE_DRAGON("<:type_dragon:805109989902909481>"),
    TYPE_GHOST("<:type_ghost:805109990754222111>"),
    TYPE_ICE("<:type_ice:805109990394298398>");

    private final String code;

    Emote(String code) {
        this.code = code;
    }

    /**
     * Returns the Emote in {@link EmojiUnion} form.
     */
    public EmojiUnion getAsEmoji() {
        return Emoji.fromFormatted(code);
    }

    public String getAsText() {
        return getAsEmoji().getFormatted();
    }
}