package dev.zawarudo.holo.core;

import dev.zawarudo.holo.database.dao.GuildConfigDao;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-guild bot configuration.
 */
public class GuildConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildConfigManager.class);

    private final Map<Long, GuildConfig> guildConfigs = new ConcurrentHashMap<>();
    private final GuildConfigDao guildConfigDao;

    /**
     * Creates a new manager and loads all configurations from the database.
     * Ensures that every guild the bot is currently in has a configuration.
     *
     * @param guildConfigDao DAO used for loading and persisting configuration
     */
    public GuildConfigManager(GuildConfigDao guildConfigDao) {
        this.guildConfigDao = guildConfigDao;

        try {
            guildConfigs.putAll(guildConfigDao.findAll());
        } catch (SQLException ex) {
            LOGGER.error("Something went wrong while loading the guild configs from the DB.", ex);
        }

        // Ensure configs exist for all guilds the bot is in
        Bootstrap.holo.getJDA().getGuilds().forEach(this::ensureConfigExists);
    }

    /**
     * Returns the configuration for the given guild.
     *
     * <p>If no configuration exists, a default configuration is created and an insert is attempted.
     * If the insert fails, this method still returns a default in-memory configuration, but it will
     * not be cached/persisted until a later successful insert.</p>
     *
     * @param guild The guild
     * @return The existing configuration or a default configuration
     */
    public GuildConfig getOrCreate(Guild guild) {
        long id = guild.getIdLong();

        GuildConfig cfg = guildConfigs.computeIfAbsent(id, key -> {
            GuildConfig created = new GuildConfig(key);
            try {
                guildConfigDao.insert(created);
                return created;
            } catch (SQLException e) {
                LOGGER.error("Failed to insert config for guild {}", key, e);
                return null; // do not cache if not persisted.
            }
        });

        return (cfg != null) ? cfg : new GuildConfig(id);
    }

    /**
     * Ensures a configuration exists for the given guild.
     *
     * <p>This is a convenience method that delegates to {@link #getOrCreate(Guild)}.</p>
     *
     * @param guild The guild
     */
    public void ensureConfigExists(Guild guild) {
        getOrCreate(guild);
    }

    /**
     * Removes the configuration for the given guild from the cache and the database.
     *
     * @param guild The guild
     * @throws SQLException If the database delete fails
     */
    public void removeConfig(Guild guild) throws SQLException {
        long id = guild.getIdLong();
        guildConfigs.remove(id);
        guildConfigDao.deleteByGuildId(id);
    }

    /**
     * Persists the given configuration to the database.
     *
     * @param config The configuration to persist
     * @throws SQLException If the database update fails
     */
    public void persist(GuildConfig config) throws SQLException {
        guildConfigDao.update(config);
    }

    /**
     * Resets the configuration for the given guild to defaults, updates the cache,
     * and attempts to persist the reset to the database.
     *
     * @param guild The guild
     * @return The newly created default configuration
     */
    public GuildConfig resetConfigurationForGuild(Guild guild) {
        long id = guild.getIdLong();
        GuildConfig config = new GuildConfig(id);
        guildConfigs.put(id, config);

        try {
            guildConfigDao.update(config);
        } catch (SQLException ex) {
            LOGGER.error("Failed to persist reset config for guild {}", id, ex);
        }

        return config;
    }
}