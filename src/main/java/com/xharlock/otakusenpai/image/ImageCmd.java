package com.xharlock.otakusenpai.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.utils.Formatter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ImageCmd extends Command {

	private List<String> categories;

	public ImageCmd(String name) {
		super(name);
		setDescription("Use this command to get an image of a given tag.");
		setAliases(List.of("img"));
		setUsage(name + " [tag]");
		setCommandCategory(CommandCategory.IMAGE);

		categories = new ArrayList<>();
		loadCategories();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0 || args[0].toLowerCase().equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getGuildPrefix(e.getGuild()) + "image <tag>`", false);
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}

		if (!categories.contains(args[0].toLowerCase())) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getGuildPrefix(e.getGuild()) + "image <tag>` to see all available tags");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		String url = "";
		String name = "ERROR";
		
		String category = args[0].toLowerCase();

		switch (category) {
		case "echidna": {
			url = getImage("echidna_(re:zero)");
			name = "Echidna (Re:Zero)";
			break;
		}
		case "tanjiro": {
			url = getImage("kamado_tanjirou");
			name = "Tanjiro Kamado (Demon Slayer)";
			break;
		}
		case "catboy": {
			url = getImage("cat_boy");
			name = "Catboy";
			break;
		}
		case "emilia": {
			url = getImage("emilia_(re:zero)");
			name = "Emilia (Re:Zero)";
			break;
		}
		case "helena": {
			url = getImage("helena_(azur_lane)");
			name = "Helena (Azur Lane)";
			break;
		}
		case "kaguya": {
			url = getImage("shinomiya_kaguya");
			name = "Kaguya Shinomiya (Kaguya-sama: Love Is War)";
			break;
		}
		case "kosaki": {
			url = getImage("onodera_kosaki");
			name = "Kosaki Onodera (Nisekoi: False Love)";
			break;
		}
		case "kurisu": {
			url = getImage("makise_kurisu");
			name = "Kurisu Makise (Steins;Gate)";
			break;
		}
		case "kurumi": {
			url = getImage("tokisaki_kurumi");
			name = "Kurumi Tokisaki (Date A Live)";
			break;
		}
		case "kyouko": {
			url = getImage("hori_kyouko");
			name = "Kyouko Hori (Horimiya)";
			break;
		}
		case "marika": {
			url = getImage("tachibana_marika");
			name = "Marika Tachibana (Nisekoi: False Love)";
			break;
		}
		case "mikasa": {
			url = getImage("mikasa_ackerman");
			name = "Mikasa Ackerman (Attack on Titan)";
			break;
		}
		case "modeus": {
			url = getImage("modeus_(helltaker)");
			name = "Modeus (Helltaker)";
			break;
		}
		case "nanako": {
			url = getImage("yukishiro_nanako");
			name = "Nanako Yukishiro (Senryu Girl)";
			break;
		}
		case "nezuko": {
			url = getImage("kamado_nezuko");
			name = "Nezuko Kamado (Demon Slayer)";
			break;
		}
		case "tsugumi": {
			url = getImage("tsugumi_seishirou");
			name = "Tsugumi Seishirou (Nisekoi: False Love)";
			break;
		}
		case "cerberus": {
			url = getImage("cerberus_(helltaker)");
			name = "Cerberus (Helltaker)";
			break;
		}
		case "yukina": {
			url = getImage("himeragi_yukina");
			name = "Yukina Himeragi (Strike The Blood)";
			break;
		}
		case "kitsune": {
			url = getImage("fox_ears");
			name = "Kitsune";
			break;
		}
		case "zenitsu": {
			url = getImage("agatsuma_zenitsu");
			name = "Zenitsu Agatsuma (Demon Slayer)";
			break;
		}
		case "zerotwo": {
			url = getImage("zero_two_(darling_in_the_franxx)");
			name = "Zero Two (Darling In The FranXX)";
			break;
		}
		case "2b": {
			url = getImage("yorha_no._2_type_b");
			name = "2B (NieR: Automata)";
			break;
		}
		case "a2": {
			url = getImage("yorha_type_a_no._2");
			name = "A2 (NieR: Automata)";
			break;
		}
		case "eru": {
			url = getImage("chitanda_eru");
			name = "Eru Chitanda (Hyouka)";
			break;
		}
		case "ram": {
			url = getImage("ram_(re:zero)");
			name = "Ram (Re:Zero)";
			break;
		}
		case "rea": {
			url = getImage("sanka_rea");
			name = "Rea Sanka (Sankarea)";
			break;
		}
		case "rei": {
			url = getImage("ayanami_rei");
			name = "Rei Ayanami (Neon Genesis Evangelion)";
			break;
		}
		case "rem": {
			url = getImage("rem_(re:zero)");
			name = "Rem (Re:Zero)";
			break;
		}
		case "yuu": {
			url = getImage("ishigami_yuu");
			name = "Yuu Ishigami (Kaguya-sama: Love Is War)";
			break;
		}
		case "aqua": {
			url = getImage("aqua_(konosuba)");
			name = "Aqua (Konosuba)";
			break;
		}
		case "erza": {
			url = getImage("erza_scarlet");
			name = "Erza Scarlet (Fairy Tail)";
			break;
		}
		case "holo": {
			url = getImage("holo");
			name = "Holo (Spice & Wolf)";
			break;
		}
		case "lucy": {
			url = getImage("lucy_heartfilia");
			name = "Lucy Heartfilia (Fairy Tail)";
			break;
		}
		case "nami": {
			url = getImage("nami_(one_piece)");
			name = "Nami (One Piece)";
			break;
		}
		case "neko": {
			url = getImage("cat_girl");
			name = "Neko";
			break;
		}
		case "alice": {
			url = getImage("alice_zuberg");
			name = "Alice Zuberg (Sword Art Online)";
			break;
		}
		case "asuka": {
			url = getImage("souryuu_asuka_langley");
			name = "Asuka Langley Sohryu (Neon Genesis Evangelion)";
			break;
		}
		case "asuna": {
			url = getImage("asuna_(sao)");
			name = "Asuna Yuuki (Sword Art Online)";
			break;
		}
		case "chika": {
			url = getImage("fujiwara_chika");
			name = "Chika Fujiwara (Kaguya-sama: Love Is War)";
			break;
		}
		case "kanao": {
			url = getImage("tsuyuri_kanao");
			name = "Kanao Tsuyuri (Demon Slayer)";
			break;
		}
		case "myuri": {
			url = getImage("myuri_(spice_and_wolf)");
			name = "Myuri (Spice & Wolf)";
			break;
		}
		case "reimi": {
			url = getImage("sugimoto_reimi");
			name = "Reimi Sugimoto (JoJo's Bizarre Adventure)";
			break;
		}
		case "robin": {
			url = getImage("nico_robin");
			name = "Nico Robin (One Piece)";
			break;
		}
		case "senko": {
			url = getImage("senko_(sewayaki_kitsune_no_senko-san)");
			name = "Senko (The Helpful Fox Senko-san)";
			break;
		}
		case "taiga": {
			url = getImage("aisaka_taiga");
			name = "Taiga Aisaka (Toradora!)";
			break;
		}
		case "touka": {
			url = getImage("kirishima_touka");
			name = "Touka Kirishima (Tokyo Ghoul)";
			break;
		}
		case "vanilla": {
			url = getImage("vanilla_(nekopara)");
			name = "Vanilla (Nekopara)";
			break;
		}
		case "hayasaka": {
			url = getImage("hayasaka_ai");
			name = "Ai Hayasaka (Kaguya-sama: Love Is War)";
			break;
		}
		case "chitoge": {
			url = getImage("kirisaki_chitoge");
			name = "Chitoge Kirisaki (Nisekoi: False Love)";
			break;
		}
		case "chocola": {
			url = getImage("chocola_(nekopara)");
			name = "Chocola (Nekopara)";
			break;
		}
		case "raphtalia": {
			url = getImage("raphtalia");
			name = "Raphtalia (Rising of the Shield Hero)";
			break;
		}
		case "mitsuri": {
			url = getImage("kanroji_mitsuri");
			name = "Mitsuri Kanroji (Demon Slayer)";
			break;
		}
		case "rebecca": {
			url = getImage("rebecca_bluegarden");
			name = "Rebecca Bluegarden (Eden's Zero)";
			break;
		}
		case "darkness": {
			url = getImage("darkness_(konosuba)");
			name = "Lalatina \"Darkness\" Dustiness Ford (Konosuba)";
			break;
		}
		case "inosuke": {
			url = getImage("hashibira_inosuke");
			name = "Inosuke Hashibira (Demon Slayer)";
			break;
		}
		case "shinobu": {
			url = getImage("kochou_shinobu");
			name = "Shinobu Kochou (Demon Slayer)";
			break;
		}
		default:
			url = getImage(category);
			break;
		}
		
		name = Formatter.firstLetterUp(category);

		builder.setTitle(name);
		
		if (url != null)
			builder.setImage(url);
		else
			builder.setDescription("Something went wrong while retrieving the image! Please try again in a few minutes");
		
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}

	private void loadCategories() {
		categories.add("2b");
		categories.add("a2");
		categories.add("alice");
		categories.add("asuka");
		categories.add("asuna");
		categories.add("aqua");
		categories.add("catboy");
		categories.add("cerberus");
		categories.add("chika");
		categories.add("chitoge");
		categories.add("chocola");
		categories.add("darkness");
		categories.add("echidna");
		categories.add("emilia");
		categories.add("eru");
		categories.add("erza");
		categories.add("hayasaka");
		categories.add("helena");
		categories.add("holo");
		categories.add("inosuke");
		categories.add("kaguya");
		categories.add("kanao");
		categories.add("kitsune");
		categories.add("kosaki");
		categories.add("kurisu");
		categories.add("kurumi");
		categories.add("kyouko");
		categories.add("lucy");
		categories.add("marika");
		categories.add("mikasa");
		categories.add("mitsuri");
		categories.add("modeus");
		categories.add("myuri");
		categories.add("nami");
		categories.add("nanako");
		categories.add("neko");
		categories.add("nezuko");
		categories.add("ram");
		categories.add("raphtalia");
		categories.add("rea");
		categories.add("rebecca");
		categories.add("rei");
		categories.add("reimi");
		categories.add("rem");
		categories.add("robin");
		categories.add("senko");
		categories.add("shinobu");
		categories.add("tanjiro");
		categories.add("touka");
		categories.add("tsugumi");
		categories.add("vanilla");
		categories.add("yukina");
		categories.add("yuu");
		categories.add("zenitsu");
		categories.add("zerotwo");
	}

	private String getImage(String tag) {
		
		JsonArray array = null;

		try {
			array = GelbooruAPIWrapper.getJsonArray(GelbooruAPIWrapper.Rating.SAFE, GelbooruAPIWrapper.Sort.SCORE_DESC, 500, tag);
		} catch (IOException e) {
			return null;
		}

		Random rand = new Random();
		JsonObject object = (JsonObject) array.get(rand.nextInt(array.size()));

		String url = "";
		
		if (object.has("large_file_url"))
			url = object.get("large_file_url").getAsString();
		else
			url = object.get("file_url").getAsString();
		
		return url;
	}

	private String getCategoriesString() {
		return categories.toString().replace("]", "`").replace("[", "`").replace(",", "`").replace(" ", ", `");
	}

}
