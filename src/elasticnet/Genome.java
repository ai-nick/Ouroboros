package elasticnet;
import java.util.HashMap;
import java.util.List;

public class Genome {

	public int id;
	int gen_born;
	int[] gene_ids;
	int population_hash;
	int species_id;
	String nodeGeneType;
	String connectionGeneType;
	public double fitness;
	public int avg_w;
	List<ConnectionGene> conn_genes;
	List<NodeGene> input_nodes;
	List<NodeGene> hidden_nodes;
	List<NodeGene> output_nodes;
	public int gene_id_min, gene_id_max;
	HashMap<Integer, Double> fit_dists;
	
	public Genome(int p_hash) {
		this.nodeGeneType = "default";
		this.connectionGeneType = "default";
		this.population_hash = p_hash;
		this.fitness = 0.0;
	}
	
	public double get_prime(int num_others)
	{
		return this.fitness/num_others;
	}
	
	public void ingestConfig(HashMap<String, String> config) {
		this.nodeGeneType = config.get("nGeneType");
		this.connectionGeneType = config.get("cGeneType");
	}
	
	public void set_nodes(List<NodeGene> ngs)
	{
		this.input_nodes = ngs;
	}
	
	public void set_connections(List<ConnectionGene> conngs)
	{
		this.conn_genes = conngs;
	}
	
	public NodeGene get_node_gene(int idx)
	{
		return input_nodes.get(idx);
	}
	
	public ConnectionGene get_connection_gene(int idx)
	{
		return conn_genes.get(idx);
	}
	
	public void set_species(int id)
	{
		this.species_id = id;
	}
	
	public void set_population_id(int id)
	{
		this.population_hash = id;
	}
	
	public int get_species_id()
	{
		return this.species_id;
	}
	
	public int get_pop_id()
	{
		return this.population_hash;
	}
}
