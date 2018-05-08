import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import uwlcs353.DirectedLabelledEdge;
import uwlcs353.Graphs;
import uwlcs353.LabelledEdgeAdjListDirectedGraph;

/**
 * API for Kleinberg & Tardos Exercise 7.16.
 */
public class C7p16 {
	/**
	 * @param targetGroups
	 *            An {@code m}-by-{@code k} array, such that element
	 *            {@code [i-1][j-1]} is {@code true} exactly when Advertiser
	 *            {@code i} accepts its ads being shown to a member of Group
	 *            {@code j}
	 * @param advertiserReach
	 *            An {@code m}-entry array, such that element {@code [i-1]} is the
	 *            number of viewers what Advertiser {@code i} wishes to view its ad
	 * @param groupMember
	 *            An {@code n}-by-{@code k} array, such that element
	 *            {@code [i-1][j-1]} is {@code true} exactly when User {@code i} is
	 *            a member of Group {@code j}
	 * @return An {@link n}-element array such that element {@code
	 * [i-1]} gives the number of the Advertiser whose ad user {@code i} should see
	 *         (or null if no such assignment is possible)
	 */
	public static int[] adSchedule(boolean[][] targetGroups, int[] advertiserReach, boolean[][] groupMember) {
		//Getting sizes of inputs
		int advertisers = targetGroups.length;
		int users = groupMember.length;
		int groups = groupMember[0].length;
		int capacity = advertisers + users + groups;

		//Constructing the Graph
		LabelledEdgeAdjListDirectedGraph<Integer> graph = new LabelledEdgeAdjListDirectedGraph<Integer>(capacity + 2);

		//Adding a source and sink vertex
		int source = graph.addVertex();
		int sink = graph.addVertex();

		// Keeping track of vertex indices in the graph
		int[] advertiserVertices = new int[advertisers];
		int[] userVertices = new int[users];
		int[] groupVertices = new int[groups];

		// Assigning edges from source to advertisers
		// also assigning edges from advertisers to groups per targetGroups
		// O( mk )
		for (int i = 0; i < advertisers; i++) {
			int advertiser = graph.addVertex();
			advertiserVertices[i] = advertiser;
//			System.out.println("Source -> Advertiser: " + i);
			graph.addEdge(source, advertiser, advertiserReach[i]);
			for (int j = 0; j < targetGroups[i].length; j++) {
				if (groupVertices[j] == 0) {
					int group = graph.addVertex();
					groupVertices[j] = group;
				}
				// Advertiser i accepts its ads being shown to a member of Group j
				if (targetGroups[i][j]) {
//					System.out.println("Advertiser: " + advertiserVertices[i]
//							+ " accepts its ads being shown to a member of Group: " + groupVertices[j]);
					graph.addEdge(advertiserVertices[i], groupVertices[j], advertiserReach[i]);
				}
			}
		}

		// Assigning edges from users to sink
		// also assigning edges from groups to users per membership
		// O( nk )
		for (int i = 0; i < users; i++) {
			int user = graph.addVertex();
			userVertices[i] = user;
//			System.out.println("User: " + i + " -> Sink");
			graph.addEdge(user, sink, 1);
			for (int j = 0; j < groupMember[i].length; j++) {
				// User i is a member of Group j
				if (groupMember[i][j]) {
//					System.out.println("User : " + i + " is a member of Group: " + j);
					graph.addEdge(groupVertices[j], userVertices[i], 1);
				}
			}
		}

		// Search for max flow on the graph
		// O( mk + nk )
		Map<DirectedLabelledEdge<Integer>, Integer> flow = Graphs.dinitzMaxFlow(graph, source, sink);

		//Storing Advertisers with Flows to Groups
		// O( mk )
		ArrayList<AdGroupPair> findAdvertiser = new ArrayList<>();
		for (int i = 0; i < advertisers; i++) {
			for (int j = 0; j < groups; j++) {
				if (graph.hasEdge(advertiserVertices[i], groupVertices[j])) {
//					System.out.println("Advertiser : " + i + " -> Group : " + j);
					int check = flow.get(graph.getEdge(advertiserVertices[i], groupVertices[j])).intValue();
					if (check != 0) {
						for(int add = 0; add < check; add++) {
							findAdvertiser.add(new AdGroupPair(advertiserVertices[i], groupVertices[j]));
						}
//						System.out.println(">>>Advertiser : " + i + " -> Group : " + j +" flow: " + check);
					}
				}
			}
		}
		
//		System.out.println("ArrayList: " + Arrays.toString(findAdvertiser.toArray()));
		int[] adsShown = new int[users];
		//Assigning ads to users
		// O( nk )
		for (int i = 0; i < users; i++) {
			for (int j = 0; j < groups; j++) {
				if (graph.hasEdge(groupVertices[j], userVertices[i])) {
//					System.out.println("GroupAd : " + j + " -> User : " + i);
					int check = flow.get(graph.getEdge(groupVertices[j], userVertices[i])).intValue();
					if (check != 0) {
						int index = findAdvert(groupVertices[j], findAdvertiser);
//						System.out.println("index: " + index + " of group: " + groupVertices[j]);
						adsShown[i] = findAdvertiser.remove(index).getAdvertiser();
//						System.out.println(">>>GroupAd : " + j + " -> User : " + i);
					}
				}
			}
		}
		
		return checkValidAssignment(adsShown, advertiserReach, advertiserVertices);
	}

	/**
	 * Checks if the assignment of advertisers to users is valid given the
	 * advertiser reach goals
	 * @param adsShown - the matching of users to ads
	 * @param advertiserReach - goals of the advertisers
	 * @param loc - lookup for indices location in the vertex array
	 * @return array of the working assignment if all advertisers goals are met
	 * or null if not all advertisers goals can be met
	 */
	private static int[] checkValidAssignment(int[] adsShown, int[] advertiserReach, int[] loc) {
		for(int i = 0; i < advertiserReach.length; i++) {
			if(countInArray(adsShown, loc[i]) != advertiserReach[i]) {
				return null;
			}
		}
		return adsShown;
	}
	
	/**
	 * Counts instances of an element in an array
	 * @param array - array used to count instances
	 * @param what - element to count
	 * @return number of instances of the element in the array
	 */
	private static int countInArray(int[] array, int what) {
	    int count = 0;
	    for (int i = 0; i < array.length; i++) {
	        if (array[i] == what) {
	            count++;
	        }
	    }
	    return count;
	}

	/**
	 * findAdvert searches the list of ad-group edges and returns the index
	 * in the ArrayList of an AdGroupPair with group queried
	 * @param group - the group the user is looking for an advertiser for
	 * @param list - the list of AdGroup edge pairs with non-zero flow
	 * @return index in the ArrayList if found or -1 if not found
	 */
	private static int findAdvert(int group, ArrayList<AdGroupPair> list) {
		Iterator<AdGroupPair> iter = list.iterator();
		while( iter.hasNext() ) {
			AdGroupPair search = iter.next();
			if(search.getGroup() == group) {
				return list.indexOf(search);
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		boolean[][] targetGroups = new boolean[][] { { false, true }, { true, true }, { true, false },
				{ true, false } };
		boolean[][] groupMember = new boolean[][] { { true, true }, { false, true }, { true, true }, { false, true },
				{ false, true }, { true, true }, { false, true } };
		int[] advertiserReach = new int[] { 1, 3, 1, 2 };
		int[] assignment = adSchedule(targetGroups, advertiserReach, groupMember);
		System.out.println(Arrays.toString(assignment));
	}
}
