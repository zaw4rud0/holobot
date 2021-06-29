package com.xharlock.holo.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
		setIsNSFW(true);
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
		case "asui": {
			url = getImage("asui_tsuyu");
			name = "Asui Tsuyu";
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
		case "chisaki": {
			url = getImage("miyazaki_chisaki");
			name = "Chisaki Miyazaki";
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
		case "dio": {
			url = getImage("dio_brando");
			name = "Dio Brando";
			break;
		}
		case "echidna": {
			url = getImage("echidna_(re:zero)");
			name = "Echidna (Re:Zero)";
			break;
		}
		case "elma": {
			url = getImage("elma_(maidragon)");
			name = "Elma (Maid Dragon)";
			break;
		}
		case "emilia": {
			url = getImage("emilia_(re:zero)");
			name = "Emilia (Re:Zero)";
			break;
		}
		case "eris": {
			url = getImage("eris_(konosuba)");
			name = "Eris (Konosuba)";
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
		case "giorno": {
			url = getImage("giorno_giovanna");
			name = "Giorno Giovanna";
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
		case "ilulu": {
			url = getImage("iruru");
			name = "Ilulu (Maid Dragon)";
			break;
		}
		case "jolyne": {
			url = getImage("kuujou_jolyne");
			name = "Jolyne Kujo";
			break;
		}
		case "jonathan": {
			url = getImage("jonathan_joestar");
			name = "Jonathan Joestar";
			break;
		}
		case "joseph": {
			int i = new Random().nextInt(2);
			url = i == 0 ? "joseph_joestar_(young)" : "joseph_joestar";
			name = "Joseph Joestar";
			break;
		}
		case "jotaro": {
			url = "kuujou_joutarou";
			name = "Jotaro Kujo";
			break;
		}
		case "josuke": {
			url = "higashikata_josuke";
			name = "Josuke Higashikata";
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
		case "kanna": {
			url = getImage("kanna_kamui");
			name = "Kanna (Maid Dragon)";
			break;
		}
		case "kasumi": {
			url = getImage("yoshizawa_kasumi");
			name = "Yoshizawa Kasumi";
			break;
		}
		case "kitsune": {
			url = getImage("fox_ears");
			name = "Kitsune";
			break;
		}
		case "kobayashi": {
			url = getImage("kobayashi_(maidragon)");
			name = "Kobayashi (Maid Dragon)";
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
		case "lena": {
			url = getImage("vladilena_millize");
			name = "Vladilena Milizé";
			break;
		}
		case "lisa": {
			url = getImage("lisa_lisa");
			name = "Lisa Lisa";
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
		case "megumin": {
			url = getImage("megumin");
			name = "Megumin (Konosuba)";
			break;
		}
		case "mikasa": {
			url = getImage("mikasa_ackerman");
			name = "Mikasa Ackerman (Attack on Titan)";
			break;
		}
		case "milim": {
			url = getImage("milim_nava");
			name = "Milim Nava";
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
		case "momo": {
			url = getImage("yaoyorozu_momo");
			name = "Yaoyorozu Momo";
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
		case "nagatoro": {
			url = getImage("nagatoro_hayase");
			name = "Nagatoro Hayase";
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
		case "natsuki": {
			url = getImage("natsuki_(doki_doki_literature_club)");
			name = "Natsuki";
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
		case "nonko": {
			url = getImage("arahabaki_nonko");
			name = "Nonko Arahabiki";
			break;
		}
		case "quetzalcoatl": {
			url = getImage("quetzalcoatl_(maidragon)");
			name = "Quetzalcoatl (Maid Dragon)";
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
		case "rem_galleu": {
			url = getImage("rem_galleu");
			name = "Rem Galleu";
			break;
		}
		case "rimuru": {
			url = getImage("rimuru_tempest");
			name = "Rimuru Tempest";
			break;
		}
		case "robin": {
			url = getImage("nico_robin");
			name = "Nico Robin (One Piece)";
			break;
		}
		case "sagiri": {
			url = getImage("ameno_sagiri_(yuragisou_no_yuuna-san)");
			name = "Sagiri Ameno";
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
		case "shera": {
			url = getImage("shera_l._greenwood");
			name = "Shera L. Greenwood";
			break;
		}
		case "shinobu": {
			url = getImage("kochou_shinobu");
			name = "Shinobu Kochou (Demon Slayer)";
			break;
		}
		case "shion": {
			url = getImage("shion_(tensei_shitara_slime_datta_ken)");
			name = "Shion";
			break;
		}
		case "shuna": {
			url = getImage("shuna_(tensei_shitara_slime_datta_ken)");
			name = "Shuna";
			break;
		}
		case "speedwagon": {
			url = getImage("robert_e._o._speedwagon");
			name = "Robert E. O. Speedwagon";
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
		case "tohru": {
			url = getImage("tohru_(maidragon)");
			name = "Tohru (Maid Dragon)";
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
		case "uraraka": {
			url = getImage("uraraka_ochako");
			name = "Uraraka Ochako";
			break;
		}
		case "vanilla": {
			url = getImage("vanilla_(nekopara)");
			name = "Vanilla (Nekopara)";
			break;
		}
		case "vivy": {
			url = getImage("vivy");
			name = "Vivy";
			break;
		}
		case "wiz": {
			url = getImage("wiz_(konosuba)");
			name = "Wiz (Konosuba)";
			break;
		}
		case "yaya": {
			url = getImage("fushiguro_yaya");
			name = "Yaya Fushiguro";
			break;
		}
		case "yukina": {
			url = getImage("himeragi_yukina");
			name = "Yukina Himeragi";
			break;
		}
		case "yuri": {
			url = getImage("yuri_(doki_doki_literature_club)");
			name = "Yuri";
			break;
		}
		case "yuu": {
			url = getImage("ishigami_yuu");
			name = "Yuu Ishigami";
			break;
		}
		case "yuuna": {
			url = getImage("yunohana_yuuna");
			name = "Yuuna Yunohana";
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
		
		sendEmbed(e, builder, true);
	}

	private void loadCategories() {
		
		categories.add("2b");
		categories.add("a2");
		categories.add("albedo");
		categories.add("alice");
		categories.add("asui");
		categories.add("asuka");
		categories.add("asuna");
		categories.add("aqua");
		categories.add("catboy");
		categories.add("cerberus");
		categories.add("chika");
		categories.add("chisaki");
		categories.add("chitoge");
		categories.add("chocola");
		categories.add("darkness");
		categories.add("dio");
		categories.add("echidna");
		categories.add("elma");
		categories.add("emilia");
		categories.add("eris");
		categories.add("eru");
		categories.add("erza");
		categories.add("giorno");
		categories.add("hayasaka");
		categories.add("helena");
		categories.add("ilulu");
		categories.add("holo");
		categories.add("jolyne");
		categories.add("jonathan");
		categories.add("joseph");
		categories.add("josuke");
		categories.add("jotaro");
		categories.add("kaguya");
		categories.add("kanao");
		categories.add("kanna");
		categories.add("kasumi");
		categories.add("kitsune");
		categories.add("kobayashi");
		categories.add("kosaki");
		categories.add("kurisu");
		categories.add("kurumi");
		categories.add("kyouko");
		categories.add("lena");
		categories.add("lisa");
		categories.add("lucy");
		categories.add("marika");
		categories.add("megumin");
		categories.add("mikasa");
		categories.add("milim");
		categories.add("misaka");
		categories.add("mitsuri");
		categories.add("modeus");
		categories.add("momo");
		categories.add("monika");
		categories.add("myuri");
		categories.add("nagatoro");
		categories.add("nami");
		categories.add("nanako");
		categories.add("natsuki");
		categories.add("neko");
		categories.add("nezuko");
		categories.add("nonko");
		categories.add("quetzalcoatl");
		categories.add("ram");
		categories.add("raphtalia");
		categories.add("rea");
		categories.add("rebecca");
		categories.add("rei");
		categories.add("reimi");
		categories.add("rem");
		categories.add("rem_galleu");
		categories.add("rimuru");
		categories.add("robin");
		categories.add("sagiri");
		categories.add("sayori");
		categories.add("senko");
		categories.add("shalltear");
		categories.add("shinobu");
		categories.add("shion");
		categories.add("shuna");
		categories.add("speedwagon");
		categories.add("tanjiro");
		categories.add("tanya");
		categories.add("tohru");
		categories.add("touka");
		categories.add("tsugumi");
		categories.add("tsukasa");
		categories.add("uraraka");
		categories.add("vanilla");
		categories.add("vivy");
		categories.add("wiz");
		categories.add("yaya");
		categories.add("yukina");
		categories.add("yuri");
		categories.add("yuu");
		categories.add("yuuna");
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
