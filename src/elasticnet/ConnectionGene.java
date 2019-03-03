package elasticnet;

public class ConnectionGene implements IConnection {

	int inno_id;
	NodeGene from_node;
	NodeGene to_node;
	int activation_level;
	double weight;
	
	public ConnectionGene(NodeGene f, NodeGene t, int inno) {
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
	}
	
	public void set_weight(double w) {
		this.weight = w;
	}
	
	public double get_weight() {
		return this.weight;
	}
	public NodeGene get_from() {
		return this.from_node;
	}
	
	public NodeGene get_to() {
		return this.to_node;
	}
	
	public void set_from(NodeGene f) {
		this.from_node = f;
	}
	
	public void set_to(NodeGene t) {
		this.to_node = t;
	}
	
	public int getInnoId() {
		return this.inno_id;
	}
	
	public INode get_next_node()
	{
		return to_node;
	}
}
