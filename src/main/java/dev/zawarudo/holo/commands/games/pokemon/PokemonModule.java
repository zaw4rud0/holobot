package dev.zawarudo.holo.commands.games.pokemon;

import dev.zawarudo.holo.commands.CommandManager;
import dev.zawarudo.holo.commands.CommandModule;

public class PokemonModule implements CommandModule {

    private final PokemonSpawnManager pokemonSpawnManager;

    public PokemonModule(PokemonSpawnManager pokemonSpawnManager) {
        this.pokemonSpawnManager = pokemonSpawnManager;
    }

    @Override
    public ModuleId id() {
        return ModuleId.POKEMON;
    }

    @Override
    public String name() {
        return "pokemon";
    }

    @Override
    public String description() {
        return "Commands related to Pokémon, such catching Pokémon, managing teams, and viewing Pokédex entries.";
    }

    @Override
    public void register(CommandManager registry) {
        registry.addCommand(new CatchCmd(pokemonSpawnManager));
        registry.addCommand(new PokedexCmd());
        registry.addCommand(new PokemonTeamCmd());
        registry.addCommand(new SpawnCmd(pokemonSpawnManager));
    }
}
