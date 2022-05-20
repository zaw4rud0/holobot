
# Version 2.2.9

## ✔️ What's new

- 

## ⚙️ Changes

-

## 🐜 Bugfixes

-

---

# Version 2.2.8

## ✔️ What's new

- New actions: `punch`
- New `coinflip` command!
- New `roleinfo` command! You can get information about a role by using its name, id or by mentioning it.

## ⚙️ Changes

- `catch` and `spawn` commands have been disabled for the time being.
- `xkcd` command has been improved, you can now search for xkcd comics by using their title as well

---

# Version 2.2.7

There is nothing new, just a bunch of hotfixes

## ⚙️ Changes

- Fixed a bug of <animesearch that resulted in a Nullpointerexception
- Fixed <check not working for replies that don't have embeds

## 🐜 Bugfixes

- <akinator command is somewhat borked for the time being, just until I set up a proper system for the command. More specifically, akinator sometimes gets unresponsive and won't proceed to the next question. Simply wait a bit if that happens. The question counter also shows wrong numbers, especially when undoing an answer.

---

# Version 2.2.6

## ✔️ What's new

- Magic 8-Ball Command: Simply call it using `<8ball`
- Wild Pokémon you can catch by typing their name correctly (either in English or German)! The system is still under construction but as soon as it's finished they will spawn in specific channels you can set. Gotta catch 'em all.
- Action commands! Use `<action list` (or `<action` for short) to see all available actions. For example, `<blush` sends a gif of an anime character blushing. More actions and reactions will be added in the future.
- `<mock` command! uSe ThIs CoMmAnD tO tRaNsFoRm A gIvEn TeXt InTo A mOcKiNg TeXt.

## ⚙️ Changes

- Many commands now have unique embed colors and an improved help page.
- Anime and Manga search commands have been updated to support the new API version. Searches may show little to no results since the API now queries their own DB instead of scraping MyAnimeList.
- The infrastructure of Holo has been improved once more, most command misusages and errors should return a (somewhat) more helpful message.
- Holo now automatically leaves a voice channel as soon as everyone left.

## 🐜 Bugfixes

- Fixed a bug that occasionally broke Anime and Manga search commands. In addition, with the new API they should be much quicker and more stable.
- Fixed a bug that made Akinator not respond or appear in the first place.