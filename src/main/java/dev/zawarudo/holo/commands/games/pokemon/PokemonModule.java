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
    public String description() {
        return "Commands related to Pokémon, such as catching Pokémon, managing teams, and viewing Pokédex entries.";
    }

    @Override
    public void register(CommandManager registry) {
        ModuleId moduleId = id();

        registry.addCommand(new CatchCmd(pokemonSpawnManager), moduleId);
        registry.addCommand(new PokedexCmd(), moduleId);
        registry.addCommand(new PokemonTeamCmd(), moduleId);
        registry.addCommand(new SpawnCmd(pokemonSpawnManager), moduleId);
    }
}
