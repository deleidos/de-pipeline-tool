package com.deleidos.analytics.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ProfanityFilter {

	private static final String[] profanityArray = 
			{ "4r5e", "5h1t", "5hit", "a55", "anal", "anus", "ar5e", "arrse", "arse", "ass",
			"ass-fucker", "asses", "assfucker", "assfukka", "asshole", "assholes", "asswhole", "a_s_s", "b!tch",
			"b00bs", "b17ch", "b1tch", "ballbag", "balls", "ballsack", "bastard", "beastial", "beastiality",
			"bellend", "bestial", "bestiality", "bi+ch", "biatch", "bitch", "bitcher", "bitchers", "bitches",
			"bitchin", "bitching", "bloody", "blow job", "blowjob", "blowjobs", "boiolas", "bollock", "bollok",
			"boner", "boob", "boobs", "booobs", "boooobs", "booooobs", "booooooobs", "breasts", "buceta", "bugger",
			"bum", "bunny fucker", "butt", "butthole", "buttmuch", "buttplug", "c0ck", "c0cksucker",
			"carpet muncher", "cawk", "chink", "cipa", "cl1t", "clit", "clitoris", "clits", "cnut", "cock",
			"cock-sucker", "cockface", "cockhead", "cockmunch", "cockmuncher", "cocks", "cocksuck", "cocksucked",
			"cocksucker", "cocksucking", "cocksucks", "cocksuka", "cocksukka", "cok", "cokmuncher", "coksucka",
			"coon", "cox", "crap", "cum", "cummer", "cumming", "cums", "cumshot", "cunilingus", "cunillingus",
			"cunnilingus", "cunt", "cuntlick", "cuntlicker", "cuntlicking", "cunts", "cyalis", "cyberfuc",
			"cyberfuck", "cyberfucked", "cyberfucker", "cyberfuckers", "cyberfucking", "d1ck", "damn", "dick",
			"dickhead", "dildo", "dildos", "dink", "dinks", "dirsa", "dlck", "dog-fucker", "doggin", "dogging",
			"donkeyribber", "doosh", "duche", "dyke", "ejaculate", "ejaculated", "ejaculates", "ejaculating",
			"ejaculatings", "ejaculation", "ejakulate", "f u c k", "f u c k e r", "f4nny", "fag", "fagging",
			"faggitt", "faggot", "faggs", "fagot", "fagots", "fags", "fanny", "fannyflaps", "fannyfucker", "fanyy",
			"fatass", "fcuk", "fcuker", "fcuking", "feck", "fecker", "felching", "fellate", "fellatio",
			"fingerfuck", "fingerfucked", "fingerfucker", "fingerfuckers", "fingerfucking", "fingerfucks",
			"fistfuck", "fistfucked", "fistfucker", "fistfuckers", "fistfucking", "fistfuckings", "fistfucks",
			"flange", "fook", "fooker", "fuck", "fucka", "fucked", "fucker", "fuckers", "fuckhead", "fuckheads",
			"fuckin", "fucking", "fuckings", "fuckingshitmotherfucker", "fuckme", "fucks", "fuckwhit", "fuckwit",
			"fudge packer", "fudgepacker", "fuk", "fuker", "fukker", "fukkin", "fuks", "fukwhit", "fukwit", "fux",
			"fux0r", "f_u_c_k", "gangbang", "gangbanged", "gangbangs", "gaylord", "gaysex", "goatse", "God",
			"god-dam", "god-damned", "goddamn", "goddamned", "hardcoresex", "hell", "heshe", "hoar", "hoare",
			"hoer", "homo", "hore", "horniest", "horny", "hotsex", "jackass", "jack-off", "jackoff", "jap",
			"jerk-off", "jism", "jiz", "jizm", "jizz", "kawk", "knob", "knobead", "knobed", "knobend", "knobhead",
			"knobjocky", "knobjokey", "kock", "kondum", "kondums", "kum", "kummer", "kumming", "kums", "kunilingus",
			"l3i+ch", "l3itch", "labia", "lmfao", "lust", "lusting", "m0f0", "m0fo", "m45terbate", "ma5terb8",
			"ma5terbate", "masochist", "master-bate", "masterb8", "masterbat*", "masterbat3", "masterbate",
			"masterbation", "masterbations", "masturbate", "mo-fo", "mof0", "mofo", "mothafuck", "mothafucka",
			"mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin",
			"mothafucking", "mothafuckings", "mothafucks", "mother fucker", "motherfuck", "motherfucked",
			"motherfucker", "motherfuckers", "motherfuckin", "motherfucking", "motherfuckings", "motherfuckka",
			"motherfucks", "muff", "mutha", "muthafecker", "muthafuckker", "muther", "mutherfucker", "n1gga",
			"n1gger", "nazi", "nigg3r", "nigg4h", "nigga", "niggah", "niggas", "niggaz", "nigger", "niggers", "nob",
			"nob jokey", "nobhead", "nobjocky", "nobjokey", "numbnuts", "nutsack", "orgasim", "orgasims", "orgasm",
			"orgasms", "p0rn", "pawn", "pecker", "penis", "penisfucker", "phonesex", "phuck", "phuk", "phuked",
			"phuking", "phukked", "phukking", "phuks", "phuq", "pigfucker", "pimpis", "piss", "pissed", "pisser",
			"pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff", "poop", "porn", "porno",
			"pornography", "pornos", "prick", "pricks", "pron", "pube", "pusse", "pussi", "pussies", "pussy",
			"pussys", "rectum", "retard", "rimjaw", "rimming", "s hit", "s.o.b.", "sadist", "schlong", "screwing",
			"scroat", "scrote", "scrotum", "semen", "sex", "sh!+", "sh!t", "sh1t", "shag", "shagger", "shaggin",
			"shagging", "shemale", "shi+", "shit", "shitdick", "shite", "shited", "shitey", "shitfuck", "shitfull",
			"shithead", "shiting", "shitings", "shits", "shitted", "shitter", "shitters", "shitting", "shittings",
			"shitty", "skank", "slut", "sluts", "smegma", "smut", "snatch", "son-of-a-bitch", "spac", "spunk",
			"s_h_i_t", "t1tt1e5", "t1tties", "teets", "teez", "testical", "testicle", "tit", "titfuck", "tits",
			"titt", "tittie5", "tittiefucker", "titties", "tittyfuck", "tittywank", "titwank", "tosser", "turd",
			"tw4t", "twat", "twathead", "twatty", "twunt", "twunter", "v14gra", "v1gra", "vagina", "viagra",
			"vulva", "w00se", "wang", "wank", "wanker", "wanky", "whoar", "whore", "willies", "willy", "xrated",
			"xxx" };
	
	// Creates a has set of the profane words indicated in the profanityArray[]
	private static final HashSet<String> profanitySet = new HashSet<String>(Arrays.asList(profanityArray));
	
	public static String filter(String rawString) { 
		// Creates an array list of the raw string input
		ArrayList<String> unfilteredAL = new ArrayList<String>(Arrays.asList(rawString.split(" ")));
		StringBuilder filteredStr = new StringBuilder();
		// for every word in the unfiltered array list
		for (int i = 0; i < unfilteredAL.size(); i++) {
			// set the current word
			String current_word = unfilteredAL.get(i);
			// creates an instance of the word without any special characters
			String clean_current_word = current_word.replaceAll("[^a-zA-Z0-9_-]", "");
			// if the set of profane words contain the cleansed word
			if (profanitySet.contains(clean_current_word.toLowerCase())) {
				char[] original_word = current_word.toCharArray();
				char[] filtered_word = new char[current_word.length()];
				// iterates through every character of the current word
				for (int p = 0; p < original_word.length; p++) {
					// leaves the first letter of the word alone
					if (p == 0) {
						filtered_word[p] = current_word.charAt(p);
					}
					// leaves the special characters alone
					else if (String.valueOf(original_word[p]).matches("[^A-Za-z0-9 ]")) {
						filtered_word[p] = original_word[p];
					}
					// replaces everything else with '*'
					else {
						filtered_word[p] = '*';
					}
				}
				// adds the filtered word to the filtered string
				filteredStr.append(filtered_word).append(" ");
			} else {
				// adds the unfiltered and unprofane word to the filtered string
				filteredStr.append(current_word).append(" ");
			}
		}
		return filteredStr.toString().trim();
    }
}
