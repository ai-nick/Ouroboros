package elasticnet;
import java.util.*;


public abstract class GnosticNet {
	
	ArrayList<Integer> dShape = new ArrayList<Integer>();
	Integer numDimensions;
	
	Map<Integer, Integer> subMap = new HashMap<Integer, Integer>();
	
	ArrayList<Map> metaAF = new ArrayList<Map>();
	
	
	public GnosticNet(Integer numDimensions, ArrayList<Integer> dimensionsShape) {
		this.dShape = dimensionsShape;
		this.numDimensions = numDimensions;
		this.map_shapes();
	}
	
	public void map_shapes() {
		for(int ix = 0; ix < this.dShape.size(); ix++) {
			this.subMap.put(ix, this.dShape.get(ix));
		}
		this.recurse_shapes();
	}
	
	public void recurse_shapes() {
		for(int ix = 0; ix < this.subMap.size(); ix++) {
			Integer currentShape = this.subMap.get(ix);
			
			for(int ix2 = 0; ix2 < currentShape; ix2++) {
				
			}
			
		}
	}

}
