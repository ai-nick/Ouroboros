package elasticnet;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;


public class FractalTree {
	public double[] coord;
	public double width;
	public double weight;
	public double lvl;
	public String[] signs;
	public FractalTree[] children;
	public FractalTree(double[] c, double width, int lvl) {
		this.coord = c;
		this.width = width;
		this.lvl = lvl;
		this.children = new FractalTree[(int)Math.pow(2.0, (double)c.length)];
		this.permute_signs(this.coord.length);
	}
	public void subdivide_into_children() {
		for(int idx = 0; idx < this.children.length; idx++) {
			String sign_pattern = this.signs[idx];
			double[]  new_coord = new double[this.coord.length];
			for(int idx_2 = 0; idx_2 < this.coord.length; idx_2++) {
				char sign = sign_pattern.charAt(idx_2);
				if(sign == '1') {
					new_coord[idx_2] = coord[idx_2] + this.width/2.0;
				} else {
					new_coord[idx_2] = coord[idx_2] - this.width/2.0;
				}
			}
		}
	}
	
	public void permute_signs(int coord_len) {
		String str_len = "%" + Integer.toString(coord_len) + "s";
		for(long ix = 0; ix < this.children.length; ix++) {
			this.signs[(int)ix] = String.format(str_len, Long.toBinaryString(ix)).replace(' ', '0');
		}
	}
	
}
