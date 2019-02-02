package elasticnet;
import java.util.HashMap;
public class Genome {

	int gen_born;
	int population_hash;
	String nodeGeneType;
	String connectionGeneType;
	Genotype[] gTypes;
	
	ConnectionGene[] conn_genes;
	NodeGene[] node_genes;
	
	public Genome(int p_hash) {
		this.nodeGeneType = "default";
		this.connectionGeneType = "default";
		this.population_hash = p_hash;
	}
	
	public Genome(Genotype[] gs) {
		this.gTypes = gs;
	}
	public void ingestConfig(HashMap<String, String> config) {
		this.nodeGeneType = config.get("nGeneType");
		this.connectionGeneType = config.get("cGeneType");
	}
	
	public void set_nodes(NodeGene[] ngs)
	{
		this.node_genes = ngs;
	}
	
	public void set_connections(ConnectionGene[] conngs)
	{
		this.conn_genes = conngs;
	}
}
