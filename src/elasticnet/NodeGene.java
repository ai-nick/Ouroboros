package elasticnet;
import java.util.ArrayList;

public class NodeGene {
	// just a real baseline implementation of how i think a neat node would be implemented
	boolean is_input;
	boolean is_output;
	String activation = "";
	int inno_id;
	ArrayList<Double> coordinate;
	boolean is_recurrent;
	ArrayList<IConnection> connections = new ArrayList<IConnection>();
	double current_val;
	
	
	public NodeGene(int inno_id, String act) {
		this.inno_id = inno_id;
		this.activation = act;
	}
	
	public void set_current_val(double value)
	{
		this.current_val = value;
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
	
	public ArrayList<IConnection> get_connections()
	{
		return this.connections;
	}
}
