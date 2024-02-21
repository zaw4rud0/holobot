package dev.zawarudo.holo.core;

import dev.zawarudo.holo.database.DBOperations;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages the configurations of the bot within each guild.
 */
public class GuildConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildConfigManager.class);

    private Map<Long, GuildConfig> guildConfigs;

    public GuildConfigManager() {
        try {
            guildConfigs = DBOperations.selectGuildConfigs();
            Bootstrap.holo.getJDA().getGuilds().stream()
                    .map(Guild::getIdLong)
                    .filter(g -> !guildConfigs.containsKey(g))
                    .forEach(this::addNewGuildConfig);
        } catch (SQLException ex) {
			LOGGER.error("Something went wrong while loading the guild configs from the DB.", ex);
            guildConfigs = new HashMap<>();
        }
    }

    private void addNewGuildConfig(long guildId) {
        GuildConfig config = new GuildConfig(guildId);
        guildConfigs.put(guildId, config);
        try {
            DBOperations.insertGuildConfig(config);
        } catch (SQLException e) {
            throw new IllegalStateException("Error initializing config for guild (" + guildId + ").", e);
        }
    }

    /**
     * Returns the bot configuration for the specified guild.
     *
     * @param guild The guild to get the configuration for.
     * @return The configuration for the specified guild.
     */
    public GuildConfig getGuildConfig(Guild guild) {
        return guildConfigs.get(guild.getIdLong());
    }

    /**
     * Resets the bot configuration to the default settings.
     *
     * @param guild The guild to reset the bot configurations for.
     */
    public void resetConfigurationForGuild(Guild guild) {
        GuildConfig config = new GuildConfig(guild.getIdLong());
        guildConfigs.put(guild.getIdLong(), config);
    }
}