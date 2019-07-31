package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

public class NodeGene implements INode {
	// just a real baseline implementation of how i think a neat node would be implemented
	boolean is_input;
	boolean is_output;
	String activation = "sigmoid";
	int inno_id;
	ArrayList<Double> coordinate;
	boolean is_recurrent;
	ArrayList<Integer> connections = new ArrayList<Integer>();
	HashMap<String, Object> atts = new HashMap<String, Object>();
	double current_val = 0.0;
	int level;
	int visits = 0;
	//String layer = "";
	
	public NodeGene(int inno_id, int g_id)
	{
		this.inno_id = inno_id;
	}
	
	public NodeGene(int inno_id, int g_id, String activation)
	{
		this.inno_id = inno_id;
		this.activation = activation;
	}
	public NodeGene(int inno_id, String act) {
		this.inno_id = inno_id;
		this.activation = act;
		this.level = 0;
	}
	
	public NodeGene(int inno_id, String act, int level)
	{
		this.inno_id = inno_id;
		this.activation = act;
		this.level = level;
	}
	
	@Override
	public void set_current_val(double value)
	{
		this.current_val = value;
	}
	
	@Override
	public void add_to_current_value(double val)
	{
		this.current_val += val;
	}
	
	public double get_current_val()
	{
		return this.current_val;
	}
	public String get_activation()
	{
		return this.activation;
	}
	
	
	public ArrayList<Double> get_coord()
	{
		return this.coordinate;
	}
	
	public boolean get_is_recurrent()
	{
		return this.is_recurrent;
	}
	
	public int get_node_id()
	{
		return inno_id;
	}
	
	public ArrayList<Integer> get_connections()
	{
		return this.connections;
	}

	@Override
	public int get_level() {
		// TODO Auto-generated method stub
		return level; 
	}

	@Override
	public double activate() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean is_output()
	{
		return is_output;
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
