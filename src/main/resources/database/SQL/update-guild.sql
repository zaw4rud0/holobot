UPDATE DiscordGuilds
SET guild_name = ?,
    owner_id   = ?
WHERE guild_id = ?;