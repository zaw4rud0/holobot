package dev.zawarudo.holo.commands.games.akinator;

import java.util.Random;

/**
 * Class that holds all the different sprites of Akinator and provides methods to work with them. Each
 * sprite has a different facial expression, gesture or both.
 */
public final class AkinatorSprite {
    public static final String ICON = "https://media.discordapp.net/attachments/824916413139124254/824917438062919740/akinator_default.png";
    public static final String START = "https://media.discordapp.net/attachments/824916413139124254/824927512827527178/akinator_start.png";
    public static final String THINKING_1 = "https://media.discordapp.net/attachments/824916413139124254/824917445125865472/akinator_thinking_1.png";
    public static final String THINKING_2 = "https://media.discordapp.net/attachments/824916413139124254/824917445629575168/akinator_thinking_2.png";
    public static final String THINKING_3 = "https://media.discordapp.net/attachments/824916413139124254/824917447999750144/akinator_thinking_3.png";
    public static final String THINKING_4 = "https://media.discordapp.net/attachments/824916413139124254/824927627483545610/akinator_thinking_4.png";
    public static final String THINKING_5 = "https://media.discordapp.net/attachments/824916413139124254/824927631589376010/akinator_thinking_5.png";
    public static final String THINKING_6 = "https://media.discordapp.net/attachments/824916413139124254/824927633992581130/akinator_thinking_6.png";
    public static final String SHOCKED = "https://media.discordapp.net/attachments/824916413139124254/824927453982097418/akinator_shocked.png";
    public static final String DEFEAT = "https://media.discordapp.net/attachments/824916413139124254/824917439242043412/akinator_defeat.png";
    public static final String GUESSING = "https://media.discordapp.net/attachments/824916413139124254/824917441557299220/akinator_guessing.png";
    public static final String VICTORY = "https://media.discordapp.net/attachments/824916413139124254/824917454647721984/akinator_victory.png";
    public static final String CANCEL = "https://media.discordapp.net/attachments/824916413139124254/824927268140089415/akinator_cancel.png";

    private AkinatorSprite() {
    }

    /**
     * Returns a random sprite of Akinator of him thinking.
     *
     * @return The sprite url as a String.
     */
    public static String getRandomThinkingSprite() {
        Random rand = new Random();
        int index = rand.nextInt(8);

        return switch (index) {
            case 0 -> THINKING_1;
            case 1 -> THINKING_2;
            case 2 -> THINKING_3;
            case 3 -> THINKING_4;
            case 4 -> THINKING_5;
            case 5 -> THINKING_6;
            case 6 -> SHOCKED;
            default -> START;
        };
    }
}