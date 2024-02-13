CREATE TABLE Blacklisted
(
    id      INTEGER,
    user_id INTEGER,
    reason  TEXT,
    date    TEXT,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE BlockedImages
(
    id      INTEGER,
    url     TEXT,
    user_id INTEGER,
    date    TEXT,
    reason  TEXT,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE Countdowns
(
    id           INTEGER,
    name         TEXT,
    time_created INTEGER,
    date_time    INTEGER,
    user_id      INTEGER,
    guild_id     INTEGER,
    PRIMARY KEY (id AUTOINCREMENT),
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
    guild_id   INTEGER,
    guild_name TEXT,
    owner_id   INTEGER,
    FOREIGN KEY (owner_id) REFERENCES DiscordUsers (user_id),
    PRIMARY KEY (guild_id)
);

CREATE TABLE DiscordNicknames
(
    id       INTEGER,
    nickname TEXT,
    user_id  INTEGER,
    guild_id INTEGER,
    FOREIGN KEY (guild_id) REFERENCES DiscordGuilds,
    FOREIGN KEY (user_id) REFERENCES DiscordUsers,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE DiscordUsers
(
    user_id       INTEGER,
    username      TEXT,
    discriminator TEXT,
    is_bot        TEXT,
    PRIMARY KEY (user_id)
);

CREATE TABLE Emotes
(
    emote_id    INTEGER,
    emote_name  TEXT,
    created_at  TEXT,
    image_url   TEXT,
    is_animated TEXT,
    PRIMARY KEY (emote_id)
);

CREATE TABLE Waifu
(
    id    TEXT,
    tag   TEXT,
    title TEXT,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE XkcdComics
(
    id    INTEGER,
    title TEXT,
    alt   TEXT,
    img   TEXT,
    day   INTEGER,
    month INTEGER,
    year  INTEGER,
    PRIMARY KEY (id)
)