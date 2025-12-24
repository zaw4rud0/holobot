SELECT emote_id, emote_name, is_animated
FROM Emotes
WHERE LOWER(emote_name) = LOWER(?);