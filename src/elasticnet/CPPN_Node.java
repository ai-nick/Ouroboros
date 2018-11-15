package elasticnet;

import java.util.ArrayList;

public class CPPN_Node{
	double[] position;
	ArrayList<CPPN_CONN> connections;
	int node_id;
	String activation;
	public CPPN_Node(double[] position, String activation) {
		this.position = position;
		this.activation = activation;
	}
	
	public void add_connection(int conn_id, double w) {
		
	}
}

class CPPN_CONN{
	int from_id;
	int to_id;
	double weight;
	public void set_weight(double newWeight) {
		this.weight = newWeight;
	}
	public double get_weight() {
		return weight;
	}
	public void set_from(int f) {
		this.from_id = f;
	}
	public void set_to(int t) {
		this.to_id = t;
	}
	
	public int get_from() {
		return this.from_id;
	}
	public int get_to() {
		return this.to_id;
	}
}
