package elasticnet;
import java.util.*;


public abstract class GnosticNet {
	
	ArrayList<Integer> dShape = new ArrayList<Integer>();
	Integer numDimensions;
	
	Map subMap = new HashMap<Integer, Integer>();
	
	Map metaAF = new HashMap<HashMap, HashMap>();
	
	public GnosticNet(Integer numDimensions, ArrayList<Integer> dimensionsShape) {
		this.dShape = dimensionsShape;
		this.numDimensions = numDimensions;
		this.convert_and_quad();
	}
	
	public void convert_and_quad() {
		
	}

}
