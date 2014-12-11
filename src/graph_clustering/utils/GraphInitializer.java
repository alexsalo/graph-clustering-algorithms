package graph_clustering.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GraphInitializer {
	private static String filename;
	private static ArrayList<String> inputStrings;
	private static HashMap<String, ArrayList<String>> graph;
	private static GraphInitializer initializer;
	
	protected GraphInitializer(String filename){
		GraphInitializer.filename = filename;
	}
	
	public static HashMap<String, ArrayList<String>> initGraph(String filename){
		if (initializer == null)
			initializer = new GraphInitializer(filename);
		readFile();
		initGraph();
		ArrayList<String> g  = new ArrayList<String>();
		int i = 0, j = 0;
		ArrayList<String> names = new ArrayList<String>(graph.keySet());
		Scanner sc = null;
		for (String s : inputStrings) {
			sc = new Scanner(s);
			String s1 = sc.next();
			String s2 = sc.next();
			g.add(Integer.toString(names.indexOf(s1)) + " "
					+ Integer.toString(names.indexOf(s2)));
		}
		sc.close();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("g_test.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (String s : g){
			writer.println(s);
		}
		writer.close();
		return graph;
	}
	
	private static void readFile() {
		Scanner sc;
		try {
			sc = new Scanner(new BufferedReader(new FileReader(filename)));
			inputStrings = new ArrayList<String>();
			while (sc.hasNext())
				inputStrings.add(sc.next() + " " + sc.next());
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void initGraph() {
		graph = new HashMap<String, ArrayList<String>>();
		Scanner sc = null;
		for (String s : inputStrings) {
			sc = new Scanner(s);
			String s1 = sc.next();
			String s2 = sc.next();
			// TODO code duplicate, may need to take it to a sep function
			// one way
			if (graph.containsKey(s1)) {
				graph.get(s1).add(s2);
			} else {
				ArrayList<String> links = new ArrayList<String>();
				links.add(s2);
				graph.put(s1, links);
			}
			// opposite way
			if (graph.containsKey(s2)) {
				graph.get(s2).add(s1);
			} else {
				ArrayList<String> links = new ArrayList<String>();
				links.add(s1);
				graph.put(s2, links);
			}
		}
		sc.close();
	}
}
