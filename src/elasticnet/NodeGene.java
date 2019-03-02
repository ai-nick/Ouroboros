package elasticnet;
import java.util.ArrayList;

public class NodeGene {
	boolean is_input;
	boolean is_output;
	String activation = "";
	int inno_id;
	ArrayList<Double> coordinate;
	boolean is_recurrent;
	ArrayList<IConnection> connections = new ArrayList<IConnection>();
	
	public NodeGene(int inno_id, String act) {
		this.inno_id = inno_id;
		this.activation = act;
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
}
