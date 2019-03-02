package elasticnet;

import java.util.ArrayList;
import java.util.List;

public interface INode {
	
	public double get_current_val();
	
	public double set_current_val(double value);
	
	public int get_level();
	
	public ArrayList<Double> get_coord();
	
	public boolean get_is_recurrent();
	
	public double activate();
	
	public int get_node_id();
	
	public ArrayList<IConnection> get_connections();
}
