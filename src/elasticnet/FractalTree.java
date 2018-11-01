package elasticnet;
import java.lang.Math;
public class FractalTree {
	Double phi = (1.0 + Math.sqrt(5.0))/2;
	Double[] coord;
	Boolean is_root = false;
	FractalTree[] children;
	
	public FractalTree(Double[] c, Boolean ir) {
		for(int v = 0; v < c.length; v++) {
			this.coord[v] = c[v];
			this.coord[v+c.length] = 1.0;
		}
		this.is_root = ir;
	}
	
	public FractalTree() {
		this.coord[0] = 1.0;
		this.is_root = true;
	}
	
	public FractalTree(int len_of_coord) {
		for(int x = 0; x < len_of_coord; x++) {
			this.coord[x] = 0.0;
			this.coord[len_of_coord+x] = 1.0;
		}
	}
	
	public void add_subs(Double[] new_c) {
		if(is_root) {
			while(new_c.length < this.coord.len)
		}
	}
	
}
