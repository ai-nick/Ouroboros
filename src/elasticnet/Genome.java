package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
	
	// used when mutating this genome, leaves the original alone 
	public Genome(Genome cloner, int id)
	{
		this.id = id;
		gen_born = cloner.gen_born + 1;
		gene_ids = cloner.gene_ids;
		population_hash = cloner.population_hash;
		species_id = cloner.species_id;
		fitness = 0;
		conn_genes = new HashMap<Integer, ConnectionGene>(cloner.conn_genes);
		input_nodes = new ArrayList<NodeGene>(cloner.input_nodes);
		hidden_nodes = new ArrayList<NodeGene>(cloner.hidden_nodes);
		output_nodes = new ArrayList<NodeGene>(cloner.output_nodes);
	}
	
	public int create_from_scratch(int inno_id, NeatConfig config)
	{
		int num_in = config.num_input;
		int num_hidden = config.num_hidden;
		int num_out = config.num_output;
		for (int ix = 0; ix < num_in; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id, this.population_hash);
			new_node.is_input = true;
			new_node.is_output = false;
			inno_id++;
			this.input_nodes.add(new_node);
		}
		
		for (int ix = 0; ix < num_hidden; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id, this.population_hash);
			new_node.is_input = false;
			new_node.is_output = false;
			inno_id++;
			this.hidden_nodes.add(new_node);
		}
		
		for (int ix = 0; ix < num_out; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id, this.population_hash);
			new_node.is_input = false;
			new_node.is_output = true;
			inno_id++;
			this.output_nodes.add(new_node);
		}
		this.mutate_genome(inno_id, config);
		return inno_id;
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
	public void mutate_genome(int new_id, NeatConfig config)
	{
		String default_activation = config.defaultActivation;
		
		Double prob_sum = config.add_conn_prob + config.delete_conn_prob + config.add_node_prob + config.delete_node_prob;
		
		if (Math.random() < (config.add_conn_prob/prob_sum))
		{
			mutate_add_conn(new_id);
		}
		if (Math.random() < (config.add_node_prob/prob_sum))
		{
			mutate_add_node(new_id, config.defaultActivation);
		}
		if (Math.random() < (config.delete_node_prob/prob_sum))
		{
			mutate_delete_node();
		}
		if (Math.random() < (config.delete_conn_prob/prob_sum))
		{
			mutate_delete_conn();
		}
		return;
	}
	
	private void mutate_add_conn(int new_id)
	{
		HashMap<Integer, NodeGene> all_the_nodes = this.get_all_nodes();
		
		int to_node_key = (int)all_the_nodes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, all_the_nodes.size())];
		
		int from_node_key = (int)all_the_nodes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, all_the_nodes.size())];
		
		NodeGene from_node = all_the_nodes.get(from_node_key);
		NodeGene to_node = all_the_nodes.get(to_node_key);
		
		if(this.output_nodes.contains(to_node) && this.output_nodes.contains(from_node))
		{
			return;
		}
		if(this.input_nodes.contains(from_node_key) && this.input_nodes.contains(to_node))
		{
			return;
		}
		ConnectionGene new_gene = new ConnectionGene(from_node, to_node, new_id);
		
		this.conn_genes.put(new_id, new_gene);
		
		return;
	}
	
	private void mutate_add_node(int new_id, String activation)
	{
		int connection_to_split_index = (int)this.conn_genes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, this.conn_genes.size())];
		
		ConnectionGene connection_to_split = this.conn_genes.get(connection_to_split_index);
		
		NodeGene new_node = new NodeGene(new_id, activation);
		
		new_id++;
		
		ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node, new_id);
		
		this.conn_genes.put(new_id, new_conn_a);
		
		new_id++;
		
		ConnectionGene new_conn_b = new ConnectionGene(new_node, connection_to_split.from_node, new_id);
		
		this.conn_genes.put(new_id, new_conn_b);
		
		this.hidden_nodes.add(new_node);
		
		return;
	}
	
	
	private void mutate_delete_node()
	{
		int num_nodes = this.hidden_nodes.size();
		
		Random dice = new Random();
		
		NodeGene delete_node = this.hidden_nodes.get(dice.nextInt(num_nodes));
		
		int conn_counter = delete_node.connections.size();
		
		for (int ix = 0; ix < conn_counter; ix++)
		{
			this.conn_genes.remove(delete_node.connections.get(ix).get_id());
		}
		
		this.hidden_nodes.remove(delete_node.inno_id);
		
		return;
	}
	
	private void mutate_delete_conn()
	{
		Random dice = new Random();
		
		int delete_key = dice.nextInt(this.conn_genes.size());
		
		this.conn_genes.remove(delete_key);
	}
}
