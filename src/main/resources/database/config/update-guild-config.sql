UPDATE DiscordGuildConfigs
SET prefix           = ?,
    nsfw             = ?,
    disabled_modules = ?
WHERE guild_id = ?;