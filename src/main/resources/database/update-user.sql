UPDATE DiscordUsers
SET username      = ?,
    discriminator = ?,
    is_bot        = ?
WHERE user_id = ?;