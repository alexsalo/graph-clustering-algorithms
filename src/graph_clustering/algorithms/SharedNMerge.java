package graph_clustering.algorithms;

import graph_clustering.utils.GraphInitializer;
import graph_clustering.utils.ReportPrinter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class SharedNMerge {
	private static final String FILENAME = "assignment4_data.txt";
	private static final String RESULT_FILENAME = "report_shared_neigbor_merge.txt";
	private static final double TRESHOLD = 0.3;
	private static HashMap<String, ArrayList<String>> graph;
	private static HashMap<Integer, HashSet<String>> clusters = new HashMap<Integer, HashSet<String>>();
	private static ArrayList<HashSet<String>> final_clusters = new ArrayList<HashSet<String>>();

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		graph = GraphInitializer.initGraph(FILENAME);
		System.out.println(graph.size());
		cleanSingles();
		System.out.println(graph.size());
		
		
		//ReportPrinter.printReport(final_clusters, RESULT_FILENAME);

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime));
	}
	
	static void cleanSingles(){
		Iterator<Entry<String, ArrayList<String>>> iter = graph.entrySet().iterator();
		while (iter.hasNext())
			if (iter.next().getValue().size() == 1)
				iter.remove();
	}
}
