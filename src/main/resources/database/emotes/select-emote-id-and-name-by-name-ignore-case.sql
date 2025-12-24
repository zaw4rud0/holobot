SELECT emote_id, emote_name
FROM Emotes
WHERE LOWER(emote_name) = LOWER(?);