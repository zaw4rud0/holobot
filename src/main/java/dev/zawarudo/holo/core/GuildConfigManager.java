package dev.zawarudo.holo.core;

import dev.zawarudo.holo.database.dao.GuildConfigDao;
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

    private final GuildConfigDao guildConfigDao;

    public GuildConfigManager(GuildConfigDao guildConfigDao) {
        this.guildConfigDao = guildConfigDao;

        try {
            guildConfigs = guildConfigDao.findAll();
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
            guildConfigDao.insert(config);
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
     * Inserts a new config when joining a new guild.
     */
    public void ensureConfigExists(Guild guild) throws SQLException {
        long id = guild.getIdLong();
        if (guildConfigs.containsKey(id)) return;

        GuildConfig cfg = new GuildConfig(id);
        guildConfigs.put(id, cfg);
        guildConfigDao.insert(cfg);
    }

    /**
     * Removes the configuration when leaving a guild.
     */
    public void removeConfig(Guild guild) throws SQLException {
        long id = guild.getIdLong();
        guildConfigs.remove(id);
        guildConfigDao.deleteByGuildId(id);
    }

    /**
     * Save configuration after changes.
     */
    public void persist(GuildConfig config) throws SQLException {
        guildConfigDao.update(config);
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