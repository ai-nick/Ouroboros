package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Genome {

	public int id;
	int gen_born;
	int[] gene_ids;
	int population_hash;
	int species_id;
	public double fitness;
	public int avg_w;
	HashMap<Integer, ConnectionGene> conn_genes;
	ArrayList<NodeGene> input_nodes;
	ArrayList<NodeGene> hidden_nodes;
	ArrayList<NodeGene> output_nodes;
	public int gene_id_min, gene_id_max;
	HashMap<Integer, Double> fit_dists;
	
	public Genome(int p_hash) {
		this.population_hash = p_hash;
		this.fitness = 0.0;
	}
	public Genome(double test_fit)
	{
		fitness = test_fit;
	}
	public double get_prime(int num_others)
	{
		return this.fitness/num_others;
	}
	
	public void set_nodes(ArrayList<NodeGene> ngs)
	{
		this.input_nodes = ngs;
	}
	
	public void set_node(NodeGene ng)
	{
		if(ng.is_input)
		{
			input_nodes.add(ng);
		}
		else if (ng.is_output)
		{
			output_nodes.add(ng);
		}
		else
		{
			hidden_nodes.add(ng);
		}
	}
	
	public void set_connections(HashMap<Integer, ConnectionGene> conngs)
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
	
	public HashMap<Integer, NodeGene> get_all_nodes()
	{
		HashMap<Integer, NodeGene> all_nodes = new HashMap<Integer, NodeGene>();
		int in_count = input_nodes.size();
		for (int i = 0; i < in_count; i++)
		{
			all_nodes.put(input_nodes.get(i).inno_id, input_nodes.get(i));
		}
		int hidden_count = hidden_nodes.size();
		for (int i = 0; i < hidden_count; i++)
		{
			all_nodes.put(hidden_nodes.get(i).inno_id, hidden_nodes.get(i));
		}
		int out_count = output_nodes.size();
		for (int i = 0; i < out_count; i++)
		{
			all_nodes.put(output_nodes.get(i).inno_id, output_nodes.get(i));
		}
		return all_nodes;
	}
}
