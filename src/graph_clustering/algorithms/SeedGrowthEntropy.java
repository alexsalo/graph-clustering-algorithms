package graph_clustering.algorithms;

import graph_clustering.utils.GraphInitializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SeedGrowthEntropy {

	private static final String FILENAME = "assignment4_data.txt";
	private static final String RESULT_FILENAME = "report_entropy.txt";
	private static Map<String, ArrayList<String>> graph;

	/**
	 * Cluster consists of seed and its neighbors. One node may be in more than
	 * one cluster for density based clustering. Cluster also can have Graph
	 * Entropy.
	 * 
	 * @author nazarov
	 */
	public static class Cluster implements Comparable<Cluster> {

		private final Integer clusterId;
		private final Set<String> members;
		Double entropy = (double) Integer.MAX_VALUE;
		String seed;

		/**
		 * Default constructor.
		 * 
		 * @param clusterId
		 */
		public Cluster(Integer clusterId) {
			this.clusterId = clusterId;
			members = new HashSet<String>(); // we require out of order removal
		}

		/**
		 * @return the clusterId
		 */
		public Integer getClusterId() {
			return clusterId == null ? 0 : clusterId;
		}

		/**
		 * @return the members
		 */
		public Set<String> getAllMembers() {
			return members;
		}

		/**
		 * Sort by cluster size.
		 */
		public int compareTo(Cluster o) {
			if (o.members.size() > this.members.size()) {
				return 1;
			} else if (o.members.size() == this.members.size()) {
				return 0;
			} else {
				return -1;
			}

		}

		/**
		 * @return the entropy
		 */
		public Double getEntropy() {
			return entropy == null ? 0.0 : entropy;
		}

		/**
		 * @param entropy
		 *            the entropy to set
		 */
		public void setEntropy(Double entropy) {
			this.entropy = entropy;
		}

		public void addMember(String node) {
			members.add(node);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((clusterId == null) ? 0 : clusterId.hashCode());
			result = prime * result
					+ ((entropy == null) ? 0 : entropy.hashCode());
			result = prime * result
					+ ((members == null) ? 0 : members.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Cluster other = (Cluster) obj;
			if (clusterId == null) {
				if (other.clusterId != null)
					return false;
			} else if (!clusterId.equals(other.clusterId))
				return false;
			if (entropy == null) {
				if (other.entropy != null)
					return false;
			} else if (!entropy.equals(other.entropy))
				return false;
			if (members == null) {
				if (other.members != null)
					return false;
			} else if (!members.equals(other.members))
				return false;
			return true;
		}

	}

	/**
	 * This service perform computations related to the properties for a
	 * {@link Cluster}.
	 * 
	 * @author nazarov
	 * 
	 */
	public static class ClusterServices {
		/**
		 * Return only border members. The nodes for which some children are not
		 * in the cluster.
		 * 
		 * Includes seed if seed is a boarder node.
		 * 
		 * @param adjList
		 * @param cluster
		 * @return
		 */
		public static List<String> getBordMemb(
				Map<String, ArrayList<String>> adjList, Cluster cluster) {
			List<String> bourderMemb = new ArrayList<String>();
			for (String node : cluster.getAllMembers()) {
				if (NodeService.isBorder(cluster, adjList.get(node))) {
					bourderMemb.add(node);
				}
			}
			return bourderMemb;
		}

		/**
		 * Get the nodes not in the cluster that have at least one link to the
		 * cluster
		 * 
		 * @param borderMemb
		 * @param cluster
		 * @param adjList
		 * @return
		 */
		public static List<String> getOutBordNodes(List<String> borderMemb,
				Cluster cluster, Map<String, ArrayList<String>> adjList) {
			List<String> outBordNodes = new ArrayList<String>();

			for (String nodeBordInner : borderMemb) {
				for (String nodeBordOut : adjList.get(nodeBordInner)) {
					if (!cluster.getAllMembers().contains(nodeBordOut)) {
						outBordNodes.add(nodeBordOut);
					}
				}
			}
			return outBordNodes;
		}
	}

	/**
	 * This service performs computations related to properties associated with
	 * a vertex.
	 * 
	 * @author nazarov
	 * 
	 */
	public static class NodeService {

		/**
		 * Check if all members of this node are in the cluster.
		 * 
		 * @param cluster
		 * @param membChildren
		 * @return
		 */
		public static boolean isBorder(Cluster cluster,
				List<String> membChildren) {
			for (String child : membChildren) {
				if (!cluster.getAllMembers().contains(child)) {
					return true; // a child of the cluster node is outside the
									// cluster
				}
			}
			return false;
		}

		/**
		 * Compute size of inner links of the children list for a particular
		 * node/vertex.
		 * 
		 * @param cluster
		 * @param list
		 * @return
		 */
		public static int getInLinkSize(Cluster cluster, List<String> list) {
			int inLsize = 0;
			for (String child : list) {
				if (cluster.getAllMembers().contains(child)) {
					inLsize++;
				}
			}
			return inLsize;
		}

		/**
		 * Get border children of particular vertex, where boarder members are
		 * Vertices that connected to the vertex of interest and are not in the
		 * cluster that the parent vertex is in.
		 * 
		 * @param cluster
		 * @param childrenBorderNode
		 * @return
		 */
		public static List<String> getBordChildren(Cluster cluster,
				List<String> childrenBorderNode) {
			List<String> bordChildren = new LinkedList<String>();
			for (String child : childrenBorderNode) {
				if (!cluster.getAllMembers().contains(child)) {
					bordChildren.add(child);
				}
			}
			return bordChildren;
		}
	}

	private static final int BASE = 2; // base for the entropy computation
	// base log for the log_2 computation
	private static final double BASE_LOG = Math.log(BASE);

	/**
	 * Compute clusters for the graph.
	 * 
	 * @param adjList
	 * @return
	 */
	public List<Cluster> computeGraphClusters(
			Map<String, ArrayList<String>> adjList) {
		List<Cluster> allClusters = new ArrayList<Cluster>();
		boolean seedSelected = false;
		Integer clusterId = 1;
		// a set of all protein ids that could be seeds
		Set<String> seeds = new HashSet<String>(adjList.keySet());
		do { // grow a cluster from the seed, while there is one
			String seed = selectSeed(adjList, seeds);
			if (seed != null) {
				seedSelected = true;
				final Cluster cluster = new Cluster(clusterId);
				cluster.addMember(seed);
				cluster.seed = seed;
				cluster.getAllMembers().addAll(adjList.get(seed));
				boolean hasNeigbors = growSeedRemove(cluster, adjList);
				if (hasNeigbors) { // seed is a cluster already
					growSeedAdd(cluster, adjList);
				}// keep only nodes not in any cluster
				seeds.removeAll(cluster.getAllMembers());
				allClusters.add(cluster);
				clusterId++;
			} else {
				seedSelected = false;
			}
		} while (seedSelected);
		return allClusters;
	}

	/**
	 * Helper function to convert list of clusters to a list of sets.
	 * 
	 * @param allClusters
	 * @return
	 */
	@SuppressWarnings("unused")
	private ArrayList<HashSet<String>> convertToSetList(
			List<Cluster> allClusters) {
		ArrayList<HashSet<String>> allClustersSet = new ArrayList<HashSet<String>>();

		for (Cluster cluster : allClusters) {
			allClustersSet.add((HashSet<String>) cluster.getAllMembers());
		}
		return allClustersSet;
	}

	/**
	 * Iteratively remove a neighbor from cluster if entropy decreased keep the
	 * change.
	 * 
	 * @param cluster
	 * @param adjList
	 * @return hasNeigbors
	 */
	private boolean growSeedRemove(Cluster cluster,
			Map<String, ArrayList<String>> adjList) {
		boolean hasNeigbors = true;
		List<String> borderMemb = ClusterServices.getBordMemb(adjList, cluster);
		if (borderMemb.isEmpty()) {
			hasNeigbors = false;
		}
		Double entropy = computeEntropyGraph(cluster, adjList, borderMemb);
		cluster.setEntropy(entropy);
		borderMemb.remove(cluster.seed);
		for (String node : borderMemb) { // try to remove all border neighbors
			cluster.getAllMembers().remove(node);
			// recompute entropy
			List<String> borderMembInner = ClusterServices.getBordMemb(adjList,
					cluster);
			entropy = computeEntropyGraph(cluster, adjList, borderMembInner);
			if (entropy < cluster.getEntropy()) {
				cluster.setEntropy(entropy); // update cluster entropy
			} else {
				cluster.getAllMembers().add(node);
			}
		}
		return hasNeigbors;
	}

	/**
	 * Iteratively add a node on the outer boundary of the cluster if entropy
	 * decreased keep the change.
	 * 
	 * 1 add outer node 2 check if entropy decreased 3 if decreased keep node
	 * and try to add another node 4 if did not change remove the outer node and
	 * try other outer nodes 5 stop if tried all outer nodes and entropy did not
	 * decrease.
	 * 
	 * @param cluster
	 * @param adjList
	 * @param allClusters
	 */
	private void growSeedAdd(Cluster cluster,
			Map<String, ArrayList<String>> adjList) {

		List<String> borderMembList = ClusterServices.getBordMemb(adjList,
				cluster);
		Queue<String> nodesToTryBorder = new LinkedList<String>();
		nodesToTryBorder.addAll(borderMembList);

		while (!nodesToTryBorder.isEmpty()) {
			String currBorderNode = nodesToTryBorder.poll();
			if (currBorderNode == null) {
				break;
			}
			List<String> outNodes = NodeService.getBordChildren(cluster,
					adjList.get(currBorderNode));
			Queue<String> bordNodeOutChildren = new LinkedList<String>();
			bordNodeOutChildren.addAll(outNodes);

			while (!bordNodeOutChildren.isEmpty()) {
				String nodeToAdd = bordNodeOutChildren.poll();
				if (currBorderNode == null) {
					break;
				}
				/**
				 * Add vertex and recompute entropy if decreased keep vertex
				 */
				cluster.addMember(nodeToAdd);
				borderMembList = ClusterServices.getBordMemb(adjList, cluster);
				Double entropy = computeEntropyGraph(cluster, adjList,
						borderMembList);

				if (entropy < cluster.getEntropy()) {
					cluster.setEntropy(entropy); // update cluster entropy
					// add another border node
					nodesToTryBorder.offer(nodeToAdd);
				} else {
					cluster.getAllMembers().remove(nodeToAdd);
				}
			}
		}
	}

	/**
	 * Select seed. Select node with the highest degree (# of neighbors) The
	 * seed should not be in another cluster.
	 * 
	 * @param adjList
	 * @param allClusters
	 * @return
	 */
	private String selectSeed(Map<String, ArrayList<String>> adjList,
			Set<String> seedCandidates) {
		String seed = null;
		int highestDeg = 0;
		for (String node : seedCandidates) {
			int degrSize = adjList.get(node).size();
			if (degrSize > highestDeg) {
				highestDeg = degrSize;
				seed = node;
			}
		}
		return seed;
	}

	/**
	 * Compute Graph Entropy. Measure cluster entropy. We always want
	 * entropy(uncertainty) to decrease.
	 * 
	 * @param cluster
	 * @param adjList
	 * @return
	 */
	protected Double computeEntropyGraph(Cluster cluster,
			Map<String, ArrayList<String>> adjList, List<String> borderMemb) {
		Double entropy = 0.0;
		if (borderMemb.isEmpty()) {
			return entropy;
		}
		for (String node : borderMemb) {
			entropy += computeEntropyVert(node, cluster, adjList);
		}
		List<String> outBordNodes = ClusterServices.getOutBordNodes(borderMemb,
				cluster, adjList);
		for (String node : outBordNodes) {
			entropy += computeEntropyVert(node, cluster, adjList);
		}
		return entropy;
	}

	/**
	 * Compute Vertex Entropy.
	 * 
	 * @param node
	 * @param cluster
	 * @param adjList
	 * @return
	 */
	protected Double computeEntropyVert(String node, Cluster cluster,
			Map<String, ArrayList<String>> adjList) {
		Double vertEntr = 0.0;
		int neighborSize = adjList.get(node).size();
		// get inner link size
		int innerLink = NodeService.getInLinkSize(cluster, adjList.get(node));
		int outerLink = neighborSize - innerLink;
		if (innerLink == outerLink) {
			return 1.0;
		}
		if (outerLink == 0 || innerLink == 0) {
			return 0.0;
		}
		double probInnLink = innerLink / (neighborSize * 1.0);
		double probOutLink = (neighborSize - innerLink) / (neighborSize * 1.0);
		// - p innerLink * log_2 (p innerLink) - p outLink * log_2 (p outLink)
		vertEntr = -probInnLink * Math.log(probInnLink) / BASE_LOG
				- probOutLink * Math.log(probOutLink) / BASE_LOG;
		return vertEntr;
	}

	/**
	 * Display clustering results.
	 * 
	 * @param clusters
	 * @param resultFilename
	 */
	private static void storeResult(List<Cluster> clusters,
			String resultFilename) {

		Collections.sort(clusters);
		@SuppressWarnings("unused")
		int counter = 0;
		try (Writer writer = new BufferedWriter(new FileWriter(resultFilename))) {			
			for (Cluster cluster : clusters) {
				if (cluster.getAllMembers().size() < 3) {
					continue;
				}
				// System.out.print(cluster.getAllMembers().size() + " ");
				for (String node : cluster.getAllMembers()) {
					// System.out.print(node + " ");
					writer.write(node + " ");
				}
				counter++;
				writer.write(System.lineSeparator());
				// System.out.println();
			}
			writer.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		//System.out.println("Cluster count for each size, s.t. size => count");
		//System.out.println("Total clusters: " + counter);

	}

	public static void main(String[] args) {
		Instant startTime = Instant.now();

		graph = GraphInitializer.initGraph(FILENAME);
		System.out.println(graph);
		SeedGrowthEntropy entropy = new SeedGrowthEntropy();
		List<Cluster> final_clusters = entropy.computeGraphClusters(graph);
		storeResult(final_clusters, RESULT_FILENAME); // My function that takes
														// list of clusters

		Instant endTime = Instant.now();
		System.out.println(Duration.between(startTime, endTime)
				+ entropy.getClass().getName());
	}

}
