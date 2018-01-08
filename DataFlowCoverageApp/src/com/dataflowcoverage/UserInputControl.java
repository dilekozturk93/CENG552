package com.dataflowcoverage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * Dilek Öztürk
 */
public class UserInputControl {
	
	public static void main(String[] args) {
		String edgeInput = new String("1 2\n" + "2 3\n" + "3 4\n" + "3 7\n" + "4 5\n" + "4 6\n" + "5 6\n" + "6 3\n");
		System.out.println("a");
		String defInput = new String("value 1\n" + "b 1 2");
		String useInput = new String("value 3,7 3,4 4,5 5 4,6\n" + "b 3 4,6");
		
		Set<String> edgeSet = edgeSet(edgeInput);
		System.out.println("edgeSet " + edgeSet.toString());
		
		Set<String> vertexSet = vertexSet(edgeInput);
		System.out.println("vertexSet " + vertexSet.toString());
		
		Map<String,Set<String>> map = DUmap(defInput, vertexSet, edgeSet);
		System.out.println(map);
		

	}

	public static boolean inputControl(String input) {

		Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s\\n,]");
		Matcher matcher = pattern.matcher(input);

		Pattern pattern2 = Pattern.compile(",[\\s]+|[\\s]+,|[\\s]+,[\\s]+");
		Matcher matcher2 = pattern2.matcher(input);

		while (input != null) {
			if (!matcher.find() && !matcher2.find()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static boolean edgeInputControl(String edgeInput) {

		boolean control = true;
		
		if (inputControl(edgeInput)) {

			String[] edgeArray = edgeInput.split("[\\n]+");

			Set<String[]> edgeArraySet = new HashSet<String[]>();
			for (String s : edgeArray) {
				 edgeArraySet.add(s.trim().split("[[\\s]+,]"));
			}
			
			for(String[] eas: edgeArraySet) {
				control = (eas.length == 2) && control;
			}
			return control;
		}
		return false;
	}

	public static Map<String, Set<String>> DUmap(String DUinput, Set<String> vertexSet, Set<String> edgeSet) {

		Map<String, Set<String>> DU = new HashMap<String, Set<String>>();

		if (inputControl(DUinput)) {

			String[] lineArray = DUinput.split("[\\n]+");

			for (String DUline : lineArray) {

				String[] du = DUline.trim().split("[\\s]+");

				if(du.length == 1)
					return DU;

				Set<String> DUset = new HashSet<String>();
				for (int i = 1; i < du.length; i++) {

					if (du[i].contains(",")) {

						if (edgeSet.contains(du[i])) {
							DUset.add(du[i]);
						} 
					} else{
						if (vertexSet.contains(du[i])) {
							DUset.add(du[i]);
						}	
					}

				}
				DU.put(du[0], DUset);
				du = null;
			}
		}
		return DU;

	}

	public static String initialOrFinalNode(String nodeInput) {

		String node = new String();

		if (inputControl(nodeInput)) {
			String[] nodeArray = nodeInput.split("[\\s\\n,]");
				node = nodeArray[0];
		}
		return node;
	}

	public static Set<String> edgeSet(String edgeInput) {

		Set<String> edgeSet = new HashSet<String>();

		if (edgeInputControl(edgeInput)) {
			String[] edgeArray = edgeInput.split("[\\n]+");

			for (String e : edgeArray) {
				String[] edgeSample = e.trim().split("[\\s,]");
				String edge = edgeSample[0] + "," + edgeSample[1];
				edgeSet.add(edge);
				edge = null;
			}
		}
		return edgeSet;
	}

	public static Set<String> vertexSet(String edgeInput) {

		Set<String> vertexSet = new HashSet<String>();

		if (edgeInputControl(edgeInput)) {

			String[] edgeArray = edgeInput.split("[\\n\\s,]");

			for (String v : edgeArray) {
				vertexSet.add(v);
			}
		}
		return vertexSet;
	}

}
