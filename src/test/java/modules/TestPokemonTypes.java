package modules;

import dev.zawarudo.holo.modules.pokemon.PokeApiClient;
import dev.zawarudo.holo.modules.pokemon.model.PokemonType;
import dev.zawarudo.holo.utils.exceptions.APIException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPokemonTypes {

    @Test
    void testTypeNormal() throws APIException {
        PokemonType normal = PokeApiClient.getType("normal");

        assertEquals(1, normal.getId());
        assertEquals("normal", normal.getName());
        assertEquals("Normal", normal.getNameFormatted());
        assertEquals("Normal", normal.getName("en"));

        // Check that exceptions are thrown when expected
        assertThrows(IllegalArgumentException.class, () -> normal.getName("invalid"));

        // Check type effectiveness
        List<String> noDamageFrom = new ArrayList<>(List.of("ghost"));
        List<String> halfDamageFrom = new ArrayList<>();
        List<String> doubleDamageFrom = new ArrayList<>(List.of("fighting"));
        List<String> noDamageTo = new ArrayList<>(List.of("ghost"));
        List<String> halfDamageTo = new ArrayList<>(List.of("rock", "steel"));
        List<String> doubleDamageTo = new ArrayList<>();

        assertEquals(noDamageFrom, normal.getNoDamageFrom());
        assertEquals(halfDamageFrom, normal.getHalfDamageFrom());
        assertEquals(doubleDamageFrom, normal.getDoubleDamageFrom());
        assertEquals(noDamageTo, normal.getNoDamageTo());
        assertEquals(halfDamageTo, normal.getHalfDamageTo());
        assertEquals(doubleDamageTo, normal.getDoubleDamageTo());
    }

    @Test
    void testTypeGround() throws APIException {
        PokemonType ground = PokeApiClient.getType("ground");

        assertEquals(5, ground.getId());
        assertEquals("ground", ground.getName());
        assertEquals("Ground", ground.getNameFormatted());
        assertEquals("Ground", ground.getName("en"));

        // Check type effectiveness
        List<String> noDamageFrom = new ArrayList<>(List.of("electric"));
        List<String> halfDamageFrom = new ArrayList<>(List.of("poison", "rock"));
        List<String> doubleDamageFrom = new ArrayList<>(List.of("water", "grass", "ice"));
        List<String> noDamageTo = new ArrayList<>(List.of("flying"));
        List<String> halfDamageTo = new ArrayList<>(List.of("bug", "grass"));
        List<String> doubleDamageTo = new ArrayList<>(List.of("poison", "rock", "steel", "fire", "electric"));

        assertEquals(noDamageFrom, ground.getNoDamageFrom());
        assertEquals(halfDamageFrom, ground.getHalfDamageFrom());
        assertEquals(doubleDamageFrom, ground.getDoubleDamageFrom());
        assertEquals(noDamageTo, ground.getNoDamageTo());
        assertEquals(halfDamageTo, ground.getHalfDamageTo());
        assertEquals(doubleDamageTo, ground.getDoubleDamageTo());
    }

    @Test
    void testTypeMoves() throws APIException {
        PokemonType poison = PokeApiClient.getType("poison");
        PokemonType fairy = PokeApiClient.getType("fairy");

        List<String> poisonMoves = poison.getMoves();
        assertTrue(poisonMoves.contains("acid"));
        assertTrue(poisonMoves.contains("acid-armor"));
        assertTrue(poisonMoves.contains("acid-spray"));
        assertTrue(poisonMoves.contains("toxic"));

        List<String> fairyMoves = fairy.getMoves();
        assertTrue(fairyMoves.contains("charm"));
        assertTrue(fairyMoves.contains("disarming-voice"));
        assertTrue(fairyMoves.contains("draining-kiss"));
        assertTrue(fairyMoves.contains("dazzling-gleam"));
    }

    @Test
    void testTypeMoves2() throws APIException {
        PokemonType fighting = PokeApiClient.getType("fighting");
        PokemonType water = PokeApiClient.getType("water");

        List<String> fightingMoves = fighting.getMovesFormatted();
        assertTrue(fightingMoves.contains("Arm Thrust"));
        assertTrue(fightingMoves.contains("Bulk Up"));
        assertTrue(fightingMoves.contains("Close Combat"));

        List<String> waterMoves = water.getMovesFormatted();
        assertTrue(waterMoves.contains("Aqua Jet"));
        assertTrue(waterMoves.contains("Surf"));
        assertTrue(waterMoves.contains("Hydro Pump"));
    }
}