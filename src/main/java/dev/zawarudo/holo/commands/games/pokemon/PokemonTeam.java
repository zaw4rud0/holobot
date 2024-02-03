package dev.zawarudo.holo.commands.games.pokemon;

import dev.zawarudo.holo.modules.pokeapi.model.Pokemon;
import dev.zawarudo.holo.utils.CollageUtils;
import dev.zawarudo.holo.utils.FontUtils;
import dev.zawarudo.holo.utils.Formatter;
import dev.zawarudo.holo.utils.ImageOperations;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokemonTeam {

    private final List<Pokemon> team;

    /**
     * Constructs an empty team.
     */
    public PokemonTeam() {
        team = new ArrayList<>();
    }

    /**
     * Constructs a team with the given Pokémon.
     *
     * @param team The Pokémon to add to the team.
     */
    public PokemonTeam(Pokemon... team) {
        if (team.length > 6) {
            throw new IllegalArgumentException("A team can only have 6 Pokémon!");
        }
        this.team = List.of(team);
    }

    /**
     * Generates a collage of the Pokémon team.
     *
     * @return A {@link BufferedImage} of the collage.
     * @throws IOException If an I/O error occurs.
     */
    public BufferedImage generateTeamImage() throws IOException {
        List<BufferedImage> images = new ArrayList<>();

        for (Pokemon pokemon : team) {
            // No Pokémon in this slot, draw gray image
            if (pokemon == null) {
                images.add(drawGray());
                continue;
            }

            BufferedImage artwork = ImageIO.read(new URL(pokemon.getSprites().getOther().getArtwork().getFrontDefault()));

            List<String> types = pokemon.getTypes();
            PokemonType type1 = PokemonUtils.getType(types.get(0));
            PokemonType type2 = types.size() == 2 ? PokemonUtils.getType(types.get(1)) : null;
            String string = String.format("#%03d %s", pokemon.getPokedexId(), Formatter.capitalize(pokemon.getName()));

            BufferedImage img = draw(artwork, type1.getColor(), type2 != null ? type2.getColor() : null, string);
            images.add(img);
        }
        return CollageUtils.createCollage(2, 3, images);
    }

    /**
     * Method to draw a Pokémon and its name onto a background
     *
     * @param img    = Picture of the Pokémon
     * @param color1 = Color of the first type
     * @param color2 = Color of the second type
     * @param name   = Name of the Pokémon and its Pok�dex id
     * @return A BufferedImage displaying the Pok�mon with background and name
     */
    private static BufferedImage draw(BufferedImage img, Color color1, Color color2, String name) {
        int width = 500;
        int height = 500;
        BufferedImage temp;
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color oldColor = g2.getColor();

        // Draw first type
        g2.setPaint(color1);
        g2.fillRect(0, 0, width, height);

        // Draw second type (if available)
        if (color2 != null) {
            g2.setPaint(color2);
            g2.fillPolygon(new int[]{width, 0, width}, new int[]{0, height, height}, 3);
        }

        g2.setColor(oldColor);
        temp = ImageOperations.resize(img, 420, 420);
        g2.drawImage(temp, null, width / 2 - temp.getWidth() / 2, 20);
        drawName(g2, name, new Rectangle(width, height), FontUtils.loadFontFromFile(30));
        g2.dispose();
        return res;
    }

    /**
     * Helper method to draw the name of the Pok�mon in the right position
     *
     * @param g2   = Graphics2D
     * @param text = Name and id of the Pok�mon
     * @param rect = A rectangle of the size of the Pok�mon image
     * @param font = Font of the text
     */
    private static void drawName(Graphics2D g2, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.height - 20;
        g2.setColor(Color.BLACK);
        g2.setFont(font);
        g2.drawString(text, x, y);
    }

    /**
     * Method to draw a simple gray square for when there is no Pokémon in a slot.
     */
    private static BufferedImage drawGray() {
        int width = 500;
        int height = 500;
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        g2.setPaint(Color.DARK_GRAY);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return res;
    }
}