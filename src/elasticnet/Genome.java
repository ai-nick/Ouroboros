package elasticnet;
import java.util.HashMap;

public class Genome {

	public int[] gene_ids;
	int gen_born;
	int population_hash;
	int species_id;
	String nodeGeneType;
	String connectionGeneType;
	Genotype[] gTypes;
	public double fitness;
	public int avg_w;
	ConnectionGene[] conn_genes;
	NodeGene[] node_genes;
	
	public Genome(int p_hash) {
		this.nodeGeneType = "default";
		this.connectionGeneType = "default";
		this.population_hash = p_hash;
		this.fitness = 0.0;
	}
	
	public Genome(Genotype[] gs) {
		this.gTypes = gs;
		this.fitness = 0.0;
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
	
	public NodeGene get_node_gene(int idx)
	{
		return node_genes[idx];
	}
	
	public ConnectionGene get_connection_gene(int idx)
	{
		return conn_genes[idx];
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
