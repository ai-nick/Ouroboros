package elasticnet;

import java.util.ArrayList;

public class QuadTree {
	
	ArrayList<Integer> rootCoord = new ArrayList<Integer>();
	ArrayList<Integer> inCoord = new ArrayList<Integer>();
	
	public QuadTree(ArrayList<Integer> incoming) {
		Integer dimensions = incoming.size();
		for(int i = 0; i < dimensions; i++) {
			this.rootCoord.add(i);
		}
		this.inCoord = incoming;
	}
}


