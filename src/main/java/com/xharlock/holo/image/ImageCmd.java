package com.xharlock.holo.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.Formatter;

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
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length == 0 || args[0].toLowerCase().equals("list")) {
			builder.setTitle("Image Tags");
			builder.setDescription(getCategoriesString());
			builder.addField("Usage", "`" + getPrefix(e) + "image <tag>`", false);
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}

		if (!categories.contains(args[0].toLowerCase())) {
			builder.setTitle("Tag not found");
			builder.setDescription("Use `" + getPrefix(e) + "image` to see all available tags");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		String url = "";
		String name = "ERROR";
		
		String category = args[0].toLowerCase();

		switch (category) {
		
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
		case "albedo": {
			url = getImage("albedo_(overlord)");
			name = "Albedo (Overlord)";
			break;
		}
		case "alice": {
			url = getImage("alice_zuberg");
			name = "Alice Zuberg (Sword Art Online)";
			break;
		}
		case "asuka": {
			url = getImage("souryuu_asuka_langley");
			name = "Asuka Langley Sohryu";
			break;
		}
		case "asuna": {
			url = getImage("asuna_(sao)");
			name = "Asuna Yuuki (Sword Art Online)";
			break;
		}
		case "aqua": {
			url = getImage("aqua_(konosuba)");
			name = "Aqua (Konosuba)";
			break;
		}
		case "catboy": {
			url = getImage("cat_boy");
			name = "Catboy";
			break;
		}
		case "cerberus": {
			url = getImage("cerberus_(helltaker)");
			name = "Cerberus (Helltaker)";
			break;
		}
		case "chika": {
			url = getImage("fujiwara_chika");
			name = "Chika Fujiwara";
			break;
		}
		case "chitoge": {
			url = getImage("kirisaki_chitoge");
			name = "Chitoge Kirisaki";
			break;
		}
		case "chocola": {
			url = getImage("chocola_(nekopara)");
			name = "Chocola (Nekopara)";
			break;
		}
		case "darkness": {
			url = getImage("darkness_(konosuba)");
			name = "Lalatina \"Darkness\" Dustiness";
			break;
		}
		case "echidna": {
			url = getImage("echidna_(re:zero)");
			name = "Echidna (Re:Zero)";
			break;
		}
		case "emilia": {
			url = getImage("emilia_(re:zero)");
			name = "Emilia (Re:Zero)";
			break;
		}
		case "eru": {
			url = getImage("chitanda_eru");
			name = "Eru Chitanda (Hyouka)";
			break;
		}
		case "erza": {
			url = getImage("erza_scarlet");
			name = "Erza Scarlet (Fairy Tail)";
			break;
		}
		case "hayasaka": {
			url = getImage("hayasaka_ai");
			name = "Ai Hayasaka";
			break;
		}
		case "helena": {
			url = getImage("helena_(azur_lane)");
			name = "Helena (Azur Lane)";
			break;
		}
		case "holo": {
			url = getImage("holo");
			name = "Holo (Spice & Wolf)";
			break;
		}
		case "kaguya": {
			url = getImage("shinomiya_kaguya");
			name = "Kaguya Shinomiya";
			break;
		}
		case "kanao": {
			url = getImage("tsuyuri_kanao");
			name = "Kanao Tsuyuri (Demon Slayer)";
			break;
		}
		case "kitsune": {
			url = getImage("fox_ears");
			name = "Kitsune";
			break;
		}
		case "kosaki": {
			url = getImage("onodera_kosaki");
			name = "Kosaki Onodera";
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
		case "lucy": {
			url = getImage("lucy_heartfilia");
			name = "Lucy Heartfilia (Fairy Tail)";
			break;
		}
		case "marika": {
			url = getImage("tachibana_marika");
			name = "Marika Tachibana";
			break;
		}
		case "mikasa": {
			url = getImage("mikasa_ackerman");
			name = "Mikasa Ackerman (Attack on Titan)";
			break;
		}
		case "misaka": {
			url = getImage("misaka_mikoto");
			name = "Misaka Mikoto";
			break;
		}
		case "mitsuri": {
			url = getImage("kanroji_mitsuri");
			name = "Mitsuri Kanroji (Demon Slayer)";
			break;
		}
		case "modeus": {
			url = getImage("modeus_(helltaker)");
			name = "Modeus (Helltaker)";
			break;
		}
		case "monika": {
			url = getImage("monika_(doki_doki_literature_club)");
			name = "Monika";
			break;
		}
		case "myuri": {
			url = getImage("myuri_(spice_and_wolf)");
			name = "Myuri (Spice & Wolf)";
			break;
		}
		case "nami": {
			url = getImage("nami_(one_piece)");
			name = "Nami (One Piece)";
			break;
		}
		case "nanako": {
			url = getImage("yukishiro_nanako");
			name = "Nanako Yukishiro (Senryu Girl)";
			break;
		}
		case "neko": {
			url = getImage("cat_girl");
			name = "Neko";
			break;
		}
		case "nezuko": {
			url = getImage("kamado_nezuko");
			name = "Nezuko Kamado";
			break;
		}		
		case "ram": {
			url = getImage("ram_(re:zero)");
			name = "Ram (Re:Zero)";
			break;
		}		
		case "raphtalia": {
			url = getImage("raphtalia");
			name = "Raphtalia (Rising of the Shield Hero)";
			break;
		}
		case "rea": {
			url = getImage("sanka_rea");
			name = "Rea Sanka (Sankarea)";
			break;
		}
		case "rebecca": {
			url = getImage("rebecca_bluegarden");
			name = "Rebecca Bluegarden (Eden's Zero)";
			break;
		}
		case "rei": {
			url = getImage("ayanami_rei");
			name = "Rei Ayanami";
			break;
		}
		case "reimi": {
			url = getImage("sugimoto_reimi");
			name = "Reimi Sugimoto";
			break;
		}
		case "rem": {
			url = getImage("rem_(re:zero)");
			name = "Rem (Re:Zero)";
			break;
		}
		case "robin": {
			url = getImage("nico_robin");
			name = "Nico Robin (One Piece)";
			break;
		}
		case "sayori": {
			url = getImage("sayori_(doki_doki_literature_club)");
			name = "Sayori";
			break;
		}
		case "senko": {
			url = getImage("senko_(sewayaki_kitsune_no_senko-san)");
			name = "Senko";
			break;
		}
		case "shalltear": {
			url = getImage("shalltear_bloodfallen");
			name = "Shalltear Bloodfallen";
			break;
		}
		case "shinobu": {
			url = getImage("kochou_shinobu");
			name = "Shinobu Kochou (Demon Slayer)";
			break;
		}		
		case "taiga": {
			url = getImage("aisaka_taiga");
			name = "Taiga Aisaka (Toradora!)";
			break;
		}
		case "tanjiro": {
			url = getImage("kamado_tanjirou");
			name = "Tanjiro Kamado";
			break;
		}
		case "tanya": {
			url = getImage("tanya_degurechaff");
			name = "Tanya von Degurechaff";
			break;
		}
		case "touka": {
			url = getImage("kirishima_touka");
			name = "Touka Kirishima (Tokyo Ghoul)";
			break;
		}
		case "tsugumi": {
			url = getImage("tsugumi_seishirou");
			name = "Tsugumi Seishirou";
			break;
		}
		case "tsukasa": {
			url = getImage("yuzaki_tsukasa");
			name = "Tsukasa Yuzaki (Tonikawa)";
			break;
		}
		case "vanilla": {
			url = getImage("vanilla_(nekopara)");
			name = "Vanilla (Nekopara)";
			break;
		}
		case "yukina": {
			url = getImage("himeragi_yukina");
			name = "Yukina Himeragi";
			break;
		}
		case "yuu": {
			url = getImage("ishigami_yuu");
			name = "Yuu Ishigami";
			break;
		}
		case "zerotwo": {
			url = getImage("zero_two_(darling_in_the_franxx)");
			name = "Zero Two";
			break;
		}		
		default:
			url = getImage(category);
			name = Formatter.firstLetterUp(category);
			break;
		}
		
		builder.setTitle(name);
		
		if (url != null)
			builder.setImage(url);
		else
			builder.setDescription("Something went wrong while retrieving the image! Please try again in a few minutes");
		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
	}

	private void loadCategories() {
		categories.add("2b");
		categories.add("a2");
		categories.add("albedo");
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
		categories.add("misaka");
		categories.add("mitsuri");
		categories.add("modeus");
		categories.add("monika");
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
		categories.add("sayori");
		categories.add("senko");
		categories.add("shalltear");
		categories.add("shinobu");
		categories.add("tanjiro");
		categories.add("tanya");
		categories.add("touka");
		categories.add("tsugumi");
		categories.add("tsukasa");
		categories.add("vanilla");
		categories.add("yukina");
		categories.add("yuu");
		categories.add("zerotwo");
	}

	private String getImage(String tag) {
		JsonObject object = null;
		try {
			object = GelbooruAPI.getJsonArray(GelbooruAPI.Rating.SAFE, GelbooruAPI.Sort.RANDOM, 1, tag).get(0).getAsJsonObject();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return object.has("large_file_url") ? object.get("large_file_url").getAsString() : object.get("file_url").getAsString();
	}

	private String getCategoriesString() {
		return categories.toString().replace("]", "`").replace("[", "`").replace(",", "`").replace(" ", ", `");
	}

}
