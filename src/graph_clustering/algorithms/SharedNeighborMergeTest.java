package graph_clustering.algorithms;

import graph_clustering.utils.GraphInitializer;
import graph_clustering.utils.ReportPrinter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class SharedNeighborMergeTest {
	private static final String FILENAME = "assignment4_data.txt";
	private static final String RESULT_FILENAME = "report_shared_neigbor_merge_entropy.txt";
	private static final double TRESHOLD = 0.3;
	private static HashMap<String, ArrayList<String>> graph;
	private static HashMap<Integer, HashSet<String>> clusters = new HashMap<Integer, HashSet<String>>();
	private static ArrayList<HashSet<String>> final_clusters = new ArrayList<HashSet<String>>();
	private static double[][] distanceMatix;
	private static ArrayList<String> names;
	private static HashSet<String> removedNames = new HashSet<String>();

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		graph = GraphInitializer.initGraph(FILENAME);
		System.out.println(graph.size());

		initClusters();
		initDistanceMatrix();
		merge();
		ReportPrinter.printReport(final_clusters, RESULT_FILENAME);

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime));
	}

	static boolean merge() {
		// most similar genes a and b
		String a = null;
		String b = null;
		// their cluster labels
		int ai = 0;
		int bj = 0;
		do {
			// find most similar
			int[] result = findNextMostSimilar();
			a = names.get(result[0]);
			b = names.get(result[1]);
			ai = findCluster(a);
			bj = findCluster(b);

			// entropy
			double initEntropy = 0;
			if (removedNames.size() < 2525) {
				initEntropy = gEntropy(ai);

				// merge
				HashSet<String> aCluster = new HashSet<String>(clusters.get(ai));
				HashSet<String> bCluster = new HashSet<String>(clusters.get(bj));
				clusters.get(ai).addAll(bCluster);
				clusters.remove(bj);
				removedNames.addAll(bCluster);

				// if entorpy increased - revert
				double newEntropy = gEntropy(ai);
				if (newEntropy < initEntropy) {
					clusters.get(ai).retainAll(aCluster);
					clusters.put(bj, bCluster);
					System.out.println("revert changes");
				} else {
					// if resulting cluster's density
					double d = density(clusters.get(ai));
					if (d < TRESHOLD) {
						final_clusters.add(aCluster);
						final_clusters.add(bCluster);
						clusters.remove(ai);
						removedNames.addAll(aCluster);
						// System.out.print("clustered: " + a + " -> " + b +
						// " size: ");
						// System.out.println(aCluster.size() +
						// bCluster.size());
					}
					System.out
							.print(Math.floor(10000
									* (double) removedNames.size()
									/ names.size()) / 100);
					System.out.println(" %");
				}
			}else break;
		} while (clusters.size() > 1);

		return true;
	}

	static void initClusters() {
		int i = 0;
		for (String key : graph.keySet())
			clusters.put(i++, new HashSet<String>(Arrays.asList(key)));
	}

	static double jaccard(String s1, String s2) {
		HashSet<String> intersection = new HashSet<String>(graph.get(s1));
		HashSet<String> union = new HashSet<String>(intersection);
		intersection.retainAll(graph.get(s2));
		union.addAll(graph.get(s2));
		double result;
		if (intersection.size() > 0)
			result = (double) intersection.size() / union.size();
		else
			result = union.size();
		return result;
	}

	static double density(HashSet<String> subgraph) {
		double E = 0;
		for (String s : subgraph) {
			HashSet<String> links = new HashSet<String>(graph.get(s));
			links.retainAll(subgraph);
			E += links.size();
		}
		int V = subgraph.size();
		return E / (V * (V - 1)); // edges already counted twice
	}

	static void initDistanceMatrix() {
		names = new ArrayList<String>(graph.keySet());
		int N = names.size();
		distanceMatix = new double[N][N];
		for (int i = 0; i < N - 1; i++)
			for (int j = i + 1; j < N; j++)
				distanceMatix[i][j] = jaccard(names.get(i), names.get(j));
		System.out.println("matrix initiated");
	}

	static int[] findNextMostSimilar() {
		double max = 0;
		int imax = 0;
		int jmax = 0;
		ArrayList<Integer> indicies = getIndices();
		for (int i : indicies)
			for (int j : indicies)
				if (j > i && distanceMatix[i][j] <= 1
						&& distanceMatix[i][j] > max
						&& !isTheSameCluster(names.get(i), names.get(j))) {
					max = distanceMatix[i][j];
					imax = i;
					jmax = j;
				}
		if (imax == 0 || jmax == 0) {
			double minunion = Double.MAX_VALUE;
			for (int i : indicies)
				for (int j : indicies)
					if (j > i && distanceMatix[i][j] > 1
							&& distanceMatix[i][j] < minunion) {
						minunion = distanceMatix[i][j];
						imax = i;
						jmax = j;
					}
		}
		return new int[] { imax, jmax };
	}

	static ArrayList<Integer> getIndices() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < names.size(); i++)
			if (!removedNames.contains(names.get(i)))
				list.add(i);
		return list;
	}

	static boolean isTheSameCluster(String s1, String s2) {
		for (int c : clusters.keySet())
			if (clusters.get(c).size() > 1)
				if (clusters.get(c).contains(s1)
						&& clusters.get(c).contains(s2))
					return true;
		return false;
	}

	static int findCluster(String s) {
		for (int c : clusters.keySet())
			if (clusters.get(c).contains(s))
				return c;
		return -1;
	}

	private static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}

	private static double vEntropy(String name, int clustid) {
		HashSet<String> cluster = new HashSet<String>(clusters.get(clustid));
		int i = 0;
		for (String s : graph.get(name))
			if (cluster.contains(s))
				i++;
		double pi = (double) i / graph.get(name).size();
		double po = 1 - pi;
		return (pi == 0 || po == 0) ? 0 : -pi * log2(pi) - po * log2(po);
	}

	public static double gEntropy(int clustid) {
		double gentropy = 0;
		HashSet<String> entropySet = new HashSet<String>();
		for (String s : clusters.get(clustid))
			entropySet.addAll(graph.get(s));
		for (String s : entropySet) {
			gentropy += vEntropy(s, clustid);
		}
		return gentropy;
	}
}
