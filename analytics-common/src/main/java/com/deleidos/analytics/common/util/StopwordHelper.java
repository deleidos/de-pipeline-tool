package com.deleidos.analytics.common.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple stopword helper class. Could be replaced by something fancier like lucene, but this is fine for now.
 * 
 * I'm using the Default English stopwords list from here: http://www.ranks.nl/stopwords
 * 
 * I commented out some of the words that I felt we should include. I also added a few things- see comments below.
 * 
 * @author vernona
 */
public class StopwordHelper {
	private static Set<String> stopwords = new HashSet<String>();

	static {
		// Default English stopwords list:
		stopwords.add("a");
		stopwords.add("about");
		//stopwords.add("above");
		stopwords.add("after");
		//stopwords.add("again");
		//stopwords.add("against");
		stopwords.add("all");
		stopwords.add("am");
		stopwords.add("an");
		stopwords.add("and");
		stopwords.add("any");
		stopwords.add("are");
		stopwords.add("aren't");
		stopwords.add("as");
		stopwords.add("at");
		stopwords.add("be");
		stopwords.add("because");
		stopwords.add("been");
		stopwords.add("before");
		stopwords.add("being");
		//stopwords.add("below");
		stopwords.add("between");
		stopwords.add("both");
		stopwords.add("but");
		stopwords.add("by");
		stopwords.add("can't");
		stopwords.add("cannot");
		stopwords.add("could");
		stopwords.add("couldn't");
		stopwords.add("did");
		stopwords.add("didn't");
		stopwords.add("do");
		stopwords.add("does");
		stopwords.add("doesn't");
		stopwords.add("doing");
		stopwords.add("don't");
		//stopwords.add("down");
		stopwords.add("during");
		stopwords.add("each");
		stopwords.add("few");
		stopwords.add("for");
		stopwords.add("from");
		stopwords.add("further");
		stopwords.add("had");
		stopwords.add("hadn't");
		stopwords.add("has");
		stopwords.add("hasn't");
		stopwords.add("have");
		stopwords.add("haven't");
		stopwords.add("having");
		stopwords.add("he");
		stopwords.add("he'd");
		stopwords.add("he'll");
		stopwords.add("he's");
		stopwords.add("her");
		stopwords.add("here");
		stopwords.add("here's");
		stopwords.add("hers");
		stopwords.add("herself");
		stopwords.add("him");
		stopwords.add("himself");
		stopwords.add("his");
		stopwords.add("how");
		stopwords.add("how's");
		stopwords.add("i");
		stopwords.add("i'd");
		stopwords.add("i'll");
		stopwords.add("i'm");
		stopwords.add("i've");
		stopwords.add("if");
		stopwords.add("in");
		stopwords.add("into");
		stopwords.add("is");
		stopwords.add("isn't");
		stopwords.add("it");
		stopwords.add("it's");
		stopwords.add("its");
		stopwords.add("itself");
		stopwords.add("let's");
		stopwords.add("me");
		stopwords.add("more");
		stopwords.add("most");
		stopwords.add("mustn't");
		stopwords.add("my");
		stopwords.add("myself");
		stopwords.add("no");
		stopwords.add("nor");
		stopwords.add("not");
		stopwords.add("of");
		stopwords.add("off");
		stopwords.add("on");
		stopwords.add("once");
		stopwords.add("only");
		stopwords.add("or");
		stopwords.add("other");
		stopwords.add("ought");
		stopwords.add("our");
		stopwords.add("ours");
		stopwords.add("ourselves");
		stopwords.add("out");
		stopwords.add("over");
		stopwords.add("own");
		stopwords.add("same");
		stopwords.add("shan't");
		stopwords.add("she");
		stopwords.add("she'd");
		stopwords.add("she'll");
		stopwords.add("she's");
		stopwords.add("should");
		stopwords.add("shouldn't");
		stopwords.add("so");
		stopwords.add("some");
		stopwords.add("such");
		stopwords.add("than");
		stopwords.add("that");
		stopwords.add("that's");
		stopwords.add("the");
		stopwords.add("their");
		stopwords.add("theirs");
		stopwords.add("them");
		stopwords.add("themselves");
		stopwords.add("then");
		stopwords.add("there");
		stopwords.add("there's");
		stopwords.add("these");
		stopwords.add("they");
		stopwords.add("they'd");
		stopwords.add("they'll");
		stopwords.add("they're");
		stopwords.add("they've");
		stopwords.add("this");
		stopwords.add("those");
		stopwords.add("through");
		stopwords.add("to");
		stopwords.add("too");
		//stopwords.add("under");
		stopwords.add("until");
		//stopwords.add("up");
		stopwords.add("very");
		stopwords.add("was");
		stopwords.add("wasn't");
		stopwords.add("we");
		stopwords.add("we'd");
		stopwords.add("we'll");
		stopwords.add("we're");
		stopwords.add("we've");
		stopwords.add("were");
		stopwords.add("weren't");
		stopwords.add("what");
		stopwords.add("what's");
		stopwords.add("when");
		stopwords.add("when's");
		stopwords.add("where");
		stopwords.add("where's");
		stopwords.add("which");
		stopwords.add("while");
		stopwords.add("who");
		stopwords.add("who's");
		stopwords.add("whom");
		stopwords.add("why");
		stopwords.add("why's");
		stopwords.add("with");
		stopwords.add("won't");
		stopwords.add("would");
		stopwords.add("wouldn't");
		stopwords.add("you");
		stopwords.add("you'd");
		stopwords.add("you'll");
		stopwords.add("you're");
		stopwords.add("you've");
		stopwords.add("your");
		stopwords.add("yours");
		stopwords.add("yourself");
		stopwords.add("yourselves");
		
		// Custom additions:
		stopwords.add("rt"); // retweet
		stopwords.add("can");
		stopwords.add("com");
		stopwords.add("come");
		stopwords.add("www");
		stopwords.add("t.co");
		stopwords.add("bit.ly");
		stopwords.add("http");
		stopwords.add("https");
	}

	/**
	 * Determine if a word is a stopword. Assumes the word is already trimmed and lower case to avoid unnecessary
	 * repetition of those operations.
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isStopword(String word) {
		return stopwords.contains(word);
	}
}
