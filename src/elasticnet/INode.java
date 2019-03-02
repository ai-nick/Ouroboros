package elasticnet;

import java.util.List;

public interface INode {
	
	public double get_current_val();
	
	public double set_current_val(double value);
	
	public String get_activation();
	
	public int get_level();
	
	public List<Double> get_coord();
	
	public boolean get_is_recurrent();
}
