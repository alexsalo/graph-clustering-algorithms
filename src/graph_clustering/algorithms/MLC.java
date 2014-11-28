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
	private static final double PRECISION = 0.00001; 
	private static final int POWER = 2;
	private static final int INFLATION = 2;
	private static ArrayList<String> inputStrings;
	private static final String FILENAME = "test.txt";// "assignment4_data.txt";
	private static final String RESULT_FILENAME = "MLC.txt";
	private static ArrayList<HashSet<String>> final_clusters = new ArrayList<HashSet<String>>();
	private static ArrayList<String> nodes;
	private static Double[][] table;
	private static int N;

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		initTable();
		printTable();
		for (int i = 1; i < 30; i++){
			expand();
			printTable();
			inflate();
			printTable();
		}
		// ReportPrinter.printReport(final_clusters, RESULT_FILENAME);

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime));
	}
	
	static void expand(){
		table = multiply(table, table);
	}
	
	static void inflate(){
		//power
		for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                table[i][j] = Math.pow(table[i][j], INFLATION);
            }
        }

		//normalize
		Double[] sums = new Double[N];
		for (int i = 0; i < N; i++) 
			sums[i] = 0.0;
		for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
            	sums[i] += table[j][i];
            }
        }
		for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
            	table[j][i] = table[j][i] / sums[i];
            }
        }
	}
	
	static void printTable(){
		System.out.println();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(table[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
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
		table = new Double[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				table[i][j] = 0.0;
		for (String s : inputStrings) {
			sc = new Scanner(s);
			String s1 = sc.next();
			String s2 = sc.next();
			int i1 = nodes.indexOf(s1);
			int i2 = nodes.indexOf(s2);
			table[i1][i2] = 1.0;
			table[i2][i1] = 1.0;
		}

		// self loop
		for (int i = 0; i < N; i++)
			table[i][i] = 1.0;

		// normalize
		int[] sums = new int[N];
		for (int i = 0; i < N; i++) {
			sums[i] = 0;
			for (int j = 0; j < N; j++)
				sums[i] += table[i][j];
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
	
	private static Double[][] multiply(Double[][] A, Double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        Double[][] C = new Double[aRows][bColumns];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
	
}
