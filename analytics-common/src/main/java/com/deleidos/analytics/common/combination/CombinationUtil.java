package com.deleidos.analytics.common.combination;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom combination algorithms.
 * 
 * @author vernona - design
 * @author hamiltone - implementation
 */
public class CombinationUtil {

	/**
	 * Given a list of non-empty lists of varying lengths, return all combinations of exactly one element from each
	 * input list. Order of input and output lists does not matter. All that matters is each output list contains
	 * exactly one element from each input list, and each output list contains a unique set of elements.
	 * 
	 * @param lists
	 * @return
	 */
	public static <T> List<List<T>> getCombinations(List<List<T>> lists) {
		// Calculate the total number of combinations (Assumes no empty lists)
		int numCombos = 0;
		for (List<T> list : lists) {
			if (numCombos == 0) {
				numCombos = list.size();
			}
			else {
				numCombos *= list.size();
			}
		}

		// Create lists for all the combinations
		List<List<T>> combinations = new ArrayList<List<T>>();
		for (int i = 0; i < numCombos; i++) {
			combinations.add(new ArrayList<T>());
		}

		// Iterate through the source lists, and distribute them
		// evenly across the combination lists
		for (List<T> sourceList : lists) {
			for (int i = 0; i < combinations.size(); i++) {
				int sourceListIndex = i % sourceList.size();

				combinations.get(i).add(sourceList.get(sourceListIndex));
			}
		}

		return combinations;
	}

	public static void main(String[] args) throws Exception {

		List<String> la = new ArrayList<String>();
		la.add("a1");
		la.add("a2");
		la.add("a3");
		List<String> lb = new ArrayList<String>();
		lb.add("b1");
		List<String> lc = new ArrayList<String>();
		lc.add("c1");
		lc.add("c2");

		List<List<String>> lists = new ArrayList<List<String>>();
		lists.add(la);
		lists.add(lb);
		lists.add(lc);

		List<List<String>> combinations = CombinationUtil.getCombinations(lists);

		for (List<String> l : combinations) {
			for (String s : l) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}

}
