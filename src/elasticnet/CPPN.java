package elasticnet;

import java.util.ArrayList;

public class CPPN {
	
	ArrayList<INode> nodes = new ArrayList<INode>();
	Integer coord_len = 0;
	public CPPN(double[] coords) {
		this.coord_len = coords.length;
	}
}

