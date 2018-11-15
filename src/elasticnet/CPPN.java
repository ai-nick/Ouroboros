package elasticnet;

import java.util.ArrayList;

public class CPPN {
	
	ArrayList<CPPN_Node> nodes = new ArrayList<CPPN_Node>();
	Integer coord_len = 0;
	public CPPN(double[] coords) {
		this.coord_len = coords.length;
	}
}

class CPPN_Node{
	
}