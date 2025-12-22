UPDATE DiscordGuildConfigs
SET prefix = ?,
    nsfw = ?
WHERE guild_id = ?