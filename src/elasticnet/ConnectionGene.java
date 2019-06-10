package elasticnet;
import java.util.HashMap;

public class ConnectionGene implements IConnection {

	int inno_id;
	NodeGene from_node;
	NodeGene to_node;
	int activation_level;
	HashMap<String, Double> atts = new HashMap<String, Double>();
	int gene_id;
	
	public ConnectionGene(NodeGene f, NodeGene t, int inno) {
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
	}
	
	public ConnectionGene(int inno_id, int gene_id) {
		this.inno_id = inno_id;
		this.gene_id = gene_id;
	}
	
	public void set_weight(double w) {
		this.atts.put("weight", w);
	}
	
	public int get_id()
	{
		return inno_id;
	}
	public double get_weight() {
		return this.atts.get("weight");
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
	
	public NodeGene get_next_node()
	{
		return to_node;
	}
}
