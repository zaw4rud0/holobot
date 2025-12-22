CREATE TABLE Blacklisted
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    reason  TEXT,
    date    TEXT,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers
);

CREATE TABLE BlockedImages
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    url     TEXT,
    user_id INTEGER,
    date    TEXT,
    reason  TEXT,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers
);

CREATE TABLE Countdowns
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    name         TEXT,
    time_created INTEGER,
    date_time    INTEGER,
    user_id      INTEGER,
    guild_id     INTEGER,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers,
    FOREIGN KEY (guild_id) REFERENCES DiscordGuilds
);

CREATE TABLE DiscordGuildUsers
(
    guild_id INTEGER,
    user_id  INTEGER,
    FOREIGN KEY (guild_id) REFERENCES DiscordGuilds,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers
);

CREATE TABLE DiscordGuilds
(
    guild_id   INTEGER PRIMARY KEY,
    guild_name TEXT
);

CREATE TABLE DiscordGuildConfigs
(
    guild_id INTEGER PRIMARY KEY,
    prefix TEXT,
    nsfw TEXT,
    FOREIGN KEY (guild_id) REFERENCES DiscordGuilds
);

CREATE TABLE DiscordNicknames
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nickname TEXT,
    user_id  INTEGER,
    guild_id INTEGER,
    FOREIGN KEY (guild_id) REFERENCES DiscordGuilds,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers
);

CREATE TABLE DiscordUsers
(
    user_id       INTEGER PRIMARY KEY,
    username      TEXT,
    discriminator TEXT,
    is_bot        TEXT
);

CREATE TABLE Emotes
(
    emote_id    INTEGER PRIMARY KEY,
    emote_name  TEXT,
    created_at  TEXT,
    image_url   TEXT,
    is_animated TEXT
);

CREATE TABLE Waifu
(
    id    TEXT PRIMARY KEY,
    tag   TEXT,
    title TEXT
);

CREATE TABLE XkcdComics
(
    id    INTEGER PRIMARY KEY,
    title TEXT,
    alt   TEXT,
    img   TEXT,
    day   INTEGER,
    month INTEGER,
    year  INTEGER
)