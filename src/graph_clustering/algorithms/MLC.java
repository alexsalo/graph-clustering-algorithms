package graph_clustering.algorithms;

import graph_clustering.utils.GraphInitializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class MLC {
	private static ArrayList<String> inputStrings;
	private static final String FILENAME = "test.txt";// "assignment4_data.txt";
	private static final String RESULT_FILENAME = "MLC.txt";
	private static ArrayList<HashSet<String>> final_clusters = new ArrayList<HashSet<String>>();
	private static ArrayList<String> nodes;
	private static double[][] table;
	private static int N;

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		initTable();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(table[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		// ReportPrinter.printReport(final_clusters, RESULT_FILENAME);

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime));
	}

	private static void initTable() {
		readFile();
		HashSet<String> names = new HashSet<String>();
		Scanner sc = null;
		for (String s : inputStrings) {
			sc = new Scanner(s);
			names.add(sc.next());
			names.add(sc.next());
		}
		nodes = new ArrayList<String>(names);
		N = nodes.size();
		table = new double[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				table[i][j] = 0;
		for (String s : inputStrings) {
			sc = new Scanner(s);
			String s1 = sc.next();
			String s2 = sc.next();
			int i1 = nodes.indexOf(s1);
			int i2 = nodes.indexOf(s2);
			table[i1][i2] = 1;
			table[i2][i1] = 1;
		}

		// self loop
		for (int i = 0; i < N; i++)
			table[i][i] = 1;

		// normalize
		int[] sums = new int[N];
		for (int i = 0; i < N; i++) {
			sums[i] = 0;
			for (int j = 0; j < N; j++)
				sums[i] += (int) table[i][j];
		}
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				table[j][i] = table[j][i] / sums[i]; //by design j-i
	}

	private static void readFile() {
		Scanner sc;
		try {
			sc = new Scanner(new BufferedReader(new FileReader(FILENAME)));
			inputStrings = new ArrayList<String>();
			while (sc.hasNext())
				inputStrings.add(sc.next() + " " + sc.next());
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
