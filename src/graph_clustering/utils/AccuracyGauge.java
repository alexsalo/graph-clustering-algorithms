package graph_clustering.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class AccuracyGauge {
	private static ArrayList<String> inputStrings;
	private static ArrayList<HashSet<String>> final_clusters;
	private static ArrayList<HashSet<String>> ground_truth;
	private static int nclust;
	private static int nground;
	private static double[][] fscores;
	private static AccuracyGauge gauge;

	private AccuracyGauge(){		
	}
	
	public static double fscore(String RESULT_FILENAME, String GROUND_TRUTH) {
		if (gauge == null)
			gauge = new AccuracyGauge();
		final_clusters = initClusters(RESULT_FILENAME);
		ground_truth = initClusters(GROUND_TRUTH);
		initFscores();
		double fscore = findAvgOfMax();
		return fscore;
	}

	private static double fmeasure(HashSet<String> cluster,
			HashSet<String> groundtruth) {
		HashSet<String> intersection = new HashSet<>(cluster);
		intersection.retainAll(groundtruth);
		double recall = (double) intersection.size() / groundtruth.size();
		double precision = (double) intersection.size() / cluster.size();
		double fmeasure = 2 * (recall * precision) / (recall + precision);
		return fmeasure;
	}

	private static void initFscores() {
		nclust = final_clusters.size();
		nground = ground_truth.size();
		fscores = new double[nclust][nground];
		for (int i = 0; i < nclust; i++)
			for (int j = 0; j < nground; j++)
				fscores[i][j] = fmeasure(final_clusters.get(i),
						ground_truth.get(j));
	}

	private static double findAvgOfMax() {
		double[] max = new double[nclust];
		for (int i = 0; i < nclust; i++)
			for (int j = 0; j < nground; j++)
				if (fscores[i][j] > max[i])
					max[i] = fscores[i][j];
		double sum = 0;
		for (int i = 0; i < nclust; i++)
			sum += max[i];
		return sum / nclust;
	}

	private static void readFile(String filename) {
		Scanner sc;
		try {
			sc = new Scanner(new BufferedReader(new FileReader(filename)));
			inputStrings = new ArrayList<String>();
			while (sc.hasNext())
				inputStrings.add(sc.nextLine());
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<HashSet<String>> initClusters(String filename) {
		readFile(filename);
		ArrayList<HashSet<String>> clusters = new ArrayList<HashSet<String>>();
		for (String s : inputStrings) {
			HashSet<String> set = new HashSet<String>();
			Scanner sc = new Scanner(s);
			while (sc.hasNext())
				set.add(sc.next());
			clusters.add(set);
			sc.close();
		}
		return clusters;
	}
}
