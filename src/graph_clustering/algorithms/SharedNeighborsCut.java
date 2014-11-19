package graph_clustering.algorithms;

import graph_clustering.utils.GraphInitializer;
import graph_clustering.utils.ReportPrinter;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SharedNeighborsCut {
	private static final String FILENAME = "assignment4_data.txt";
	private static final double TRESHOLD = 0.3;
	private static HashMap<String, ArrayList<String>> graph;
	private static ArrayList<HashSet<String>> final_clusters = new ArrayList<HashSet<String>>();
	private static HashSet<String> seen;
	static PrintWriter writer = null;

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		graph = GraphInitializer.initGraph(FILENAME);
		findDisconnectedSubgraphs();
		ReportPrinter.printReport(final_clusters, "report_shared_neigbor_cut.txt");

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime));
	}	

	static void findDisconnectedSubgraphs() {
		// initial separation of graph by connected components
		HashSet<HashSet<String>> subgraphs = new HashSet<HashSet<String>>();
		HashSet<String> preseen = new HashSet<String>();
		seen = new HashSet<String>();
		while (seen.size() != graph.size()) {			
			HashSet<String> chooseFrom = new HashSet<String>(graph.keySet());
			chooseFrom.removeAll(seen);
			dfs(chooseFrom.iterator().next());
			HashSet<String> subgraph = new HashSet<String>(seen);
			subgraph.removeAll(preseen);
			subgraphs.add(subgraph);
			//System.out.println(seen.size() - preseen.size());
			preseen = new HashSet<String>(seen);
		}
		// for connected components
		for (HashSet<String> subgraph : subgraphs) {
			// either mark as clustered
			if (density(subgraph) >= TRESHOLD) 
				final_clusters.add(subgraph);
			else 
				divideAndConquer(subgraph);			
		}
	}

	static void divideAndConquer(HashSet<String> subgraph) {
		seen = new HashSet<String>();
		connected(subgraph.iterator().next());
		// while graph stays connected - remove edges
	
		while (seen.size() == subgraph.size()){ 
			removeEdge(subgraph);
			connected(subgraph.iterator().next());
		}
		
		// do the same for each of two disconnected subgraphs
		HashSet<String> left = new HashSet<String>(seen);
		HashSet<String> right = new HashSet<String>(subgraph);
		right.removeAll(seen);
		if (right.size() > 1)
			if (density(right) >= TRESHOLD)
				final_clusters.add(right);
			else
				divideAndConquer(right);
		if (left.size() > 1)
			if (density(left) >= TRESHOLD) 
				final_clusters.add(left);
			else	
				divideAndConquer(left);
	}

	static void connected(String start) {
		seen = new HashSet<String>();
		seen.add(start);
		dfs(start);
	}

	static void dfs(String u) {
		for (String s : graph.get(u))
			if (!seen.contains(s)) {
				seen.add(s);
				dfs(s);
			}
	}

	static void removeEdge(HashSet<String> subgraph) {
		double min_jaccard = Integer.MAX_VALUE;
		int max_union = 0;
		String min_a = null;
		String min_b = null;
		for (String from : subgraph) {
			for (String to : graph.get(from)) {
				if (subgraph.contains(to)) {
					double jaccard = jaccard(from, to);
					if (jaccard == 0) {
						min_jaccard = 0;
						int union = union(from, to);
						if (union > max_union) {
							max_union = union;
							min_a = from;
							min_b = to;							
						}
					}
					if (jaccard < min_jaccard) {
						min_jaccard = jaccard;
						min_a = from;
						min_b = to;
					}
				}
			}
		}
		//System.out.println("Removing edge: " + min_a + " -> " + min_b);
		graph.get(min_a).remove(min_b);
		graph.get(min_b).remove(min_a);
	}

	static double jaccard(String s1, String s2) {
		HashSet<String> intersection = new HashSet<String>(graph.get(s1));
		HashSet<String> union = new HashSet<String>(intersection);
		intersection.retainAll(graph.get(s2));
		union.addAll(graph.get(s2));
		return (double) intersection.size() / union.size();
	}

	static int union(String s1, String s2) {
		HashSet<String> union = new HashSet<String>(graph.get(s1));
		union.addAll(graph.get(s2));
		return union.size();
	}

	static double density(HashSet<String> subgraph) {
		double E = 0;
		for (String s : subgraph)
			E += graph.get(s).size();
		int V = subgraph.size();
		return E / (V * (V - 1)); // edges already counted twice
	}
}