package elasticnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface INode {
	
	public double get_current_val();
	
	public void set_current_val(double value);
	
	public int get_level();
	
	public boolean get_is_recurrent();
	
	public double activate();
	
	public Long get_node_id();
	
	public boolean is_output();
	
	public HashMap<Long, ConnectionGene> get_connections();
	
	public void add_to_current_value(double val);
	
	public String get_activation();
}
