package graph_clustering.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

public class ReportPrinter {
	private String report_filename;
	private ArrayList<HashSet<String>> final_clusters;
	private static ReportPrinter printer;
	
	protected ReportPrinter(ArrayList<HashSet<String>> final_clusters, String filename){
		this.report_filename = filename;
		this.final_clusters = final_clusters;
	}
	
	public static void printReport(ArrayList<HashSet<String>> final_clusters, String filename){
		if (printer == null)
			printer = new ReportPrinter(final_clusters, filename);
		printer.printReportToFile();
	}
	
	private void SortClusters() {
		// delete all that less than 3
		Iterator<HashSet<String>> itr = final_clusters.iterator();
		while (itr.hasNext()) {
			HashSet<String> i = itr.next();
			if (i.size() < 3) {
				itr.remove();
			}
		}

		// sort by size
		Collections.sort(final_clusters, new Comparator<HashSet<String>>() {
			public int compare(HashSet<String> a1, HashSet<String> a2) {
				return a2.size() - a1.size(); // assumes you want biggest to
												// smallest
			}
		});
	}

	public void printReportToFile() {
		SortClusters();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(report_filename, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//writer.print(final_clusters.size());
		for (int i = 0; i < final_clusters.size(); i++) {			
			//writer.print(final_clusters.get(i).size());
			for (String s : final_clusters.get(i))
				writer.print(" " + s);
			writer.print("\n");
		}
		writer.close();
	}
}

