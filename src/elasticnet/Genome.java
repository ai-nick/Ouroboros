package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

public class Genome {

	public int id = 0;
	int gen_born = 0;
	//ArrayList<Integer> gene_ids = new ArrayList<Integer>();
	int population_hash = 0;
	int species_id = 0;
	public double fitness = -1.0;
	public int avg_w = 0;
	ArrayList<Integer> conn_genes = new ArrayList<Integer>();
	ArrayList<Integer> input_nodes = new ArrayList<Integer>();
	ArrayList<Integer> hidden_nodes = new ArrayList<Integer>();
	ArrayList<Integer> output_nodes = new ArrayList<Integer>();
	public int gene_id_min, gene_id_max = 0;
	HashMap<Integer, Double> fit_dists = new HashMap<Integer, Double>();
	
	public Genome(int p_hash, int genome_id) {
		this.id = genome_id;
		this.population_hash = p_hash;
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
		conn_genes = new HashMap<Integer, ConnectionGene>(cloner.conn_genes);
		input_nodes = new ArrayList<NodeGene>(cloner.input_nodes);
		hidden_nodes = new ArrayList<NodeGene>(cloner.hidden_nodes);
		output_nodes = new ArrayList<NodeGene>(cloner.output_nodes);
	}
	
	public int create_from_scratch(int inno_id, NeatConfig config, int populationHash, ArrayList<NodeGene> hidden_node_genes, ArrayList<ConnectionGene> conn_genes)
	{
		int num_in = config.num_input;
		int num_hidden = config.num_hidden;
		int num_out = config.num_output;
		this.population_hash = populationHash;
		int gene_index = 0;
		for (int ix = 0; ix < num_in; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id, this.population_hash);
			new_node.is_input = true;
			new_node.is_output = false;
			inno_id++;
			this.input_nodes.add(new_node);
			this.gene_ids.add(inno_id);
			gene_index = ix;
		}
		gene_index++;
		for (int ix = 0; ix < num_out; ix++)
		{
			NodeGene new_node = new NodeGene(gene_index+ix, this.population_hash, config.output_activation);
			new_node.is_input = false;
			new_node.is_output = true;
			inno_id++;
			this.output_nodes.add(new_node);
			this.gene_ids.add(inno_id);
		}
		gene_index++;
		if (num_hidden > 0)
		{
			for (int ix = 0; ix < num_hidden; ix++)
			{
				NodeGene new_node = new NodeGene(inno_id, this.population_hash);
				new_node.is_input = false;
				new_node.is_output = false;
				inno_id++;
				this.hidden_nodes.add(new_node);
				this.gene_ids.add(inno_id);
			}			
		}
		else 
		{
			inno_id = this.connect_full_initial(gene_index, conn_genes);
		}
		inno_id = this.mutate_genome(inno_id, config, hidden_node_genes, conn_genes);
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
	
	public int mutate_genome(int new_id, NeatConfig config, ArrayList<NodeGene> pop_nodes, ArrayList<ConnectionGene> pop_conns)
	{
		Random rand = new Random();
		
		String default_activation = config.defaultActivation;
		
		Double prob_sum = config.add_conn_prob + config.delete_conn_prob + config.add_node_prob + config.delete_node_prob;
		
		if (prob_sum < 1.0)
		{
			prob_sum = 1.0;
		}
		if (rand.nextFloat() < (config.add_conn_prob/prob_sum))
		{
			System.out.println("adding conn here");
			new_id = mutate_add_conn(new_id, pop_conns);
		}
		if (rand.nextFloat() < (config.add_node_prob/prob_sum))
		{
			System.out.println("adding node here");
			new_id = mutate_add_node(new_id, config.defaultActivation, pop_nodes, pop_conns);
		}
		if (rand.nextFloat() < (config.delete_node_prob/prob_sum))
		{
			System.out.println("deleting node here");
			mutate_delete_node();
		}
		if (rand.nextFloat() < (config.delete_conn_prob/prob_sum))
		{
			System.out.println("deleting conn here");
			mutate_delete_conn();
		}
		return new_id;
	}
	
	private int mutate_add_conn(int new_id, ArrayList<ConnectionGene> pop_conns)
	{
		int conn_id = new_id;
		HashMap<Integer, NodeGene> all_the_nodes = this.get_all_nodes();
		
		Random dice = new Random();
		
		int to_node_key = (int)all_the_nodes.keySet().toArray()[dice.nextInt(all_the_nodes.size())];
		
		int from_node_key = (int)all_the_nodes.keySet().toArray()[dice.nextInt(all_the_nodes.size())];
		
		NodeGene from_node = all_the_nodes.get(from_node_key);
		
		NodeGene to_node = all_the_nodes.get(to_node_key);
		
		if(this.output_nodes.contains(to_node) && this.output_nodes.contains(from_node))
		{
			return new_id;
		}
		if(this.input_nodes.contains(from_node_key) && this.input_nodes.contains(to_node))
		{
			return new_id;
		}
		int pop_conn_count = pop_conns.size();
		for(int p = 0; p < pop_conn_count; p++)
		{
			ConnectionGene p_conn = pop_conns.get(p);
			if (p_conn.to_node != null && p_conn.from_node != null)
			{
				
				if ((p_conn.to_node.inno_id == to_node.inno_id) && (p_conn.from_node.inno_id == from_node.inno_id))
				{
					conn_id = p_conn.inno_id;
				}	
			}
		}
		ConnectionGene new_gene = new ConnectionGene(from_node, to_node, conn_id);
		
		pop_conns.put(conn_id, new_gene);
		
		this.gene_ids.add(conn_id);
		
		pop_conns.add(new_gene);
		
		return new_id;
	}
	
	private int mutate_add_node(int new_id, String activation, ArrayList<NodeGene> pop_nodes, ArrayList<ConnectionGene> pop_conns)
	{
		//boolean has_hist_id;
		
		int gene_id = new_id;
		
		Random dice = new Random();
		
		if (this.conn_genes.size() == 0)
		{
			return new_id;
		}
		
		int connection_to_split_index = (int)this.conn_genes.keySet().toArray()[dice.nextInt(this.conn_genes.size())];
		
		ConnectionGene connection_to_split = this.conn_genes.get(connection_to_split_index);
		
		int hidden_count = pop_nodes.size();
		
		for(int i = 0; i < hidden_count; i++)
		{
			NodeGene pop_node = pop_nodes.get(i);
			int num_conns = pop_node.connections.size();
			int has_to = -1;
			int has_from = -1;
			for (int ix = 0; ix < num_conns; ix++)
			{
				ConnectionGene this_conn = pop_node.connections.get(ix);
				if (this_conn.from_node.inno_id == connection_to_split.from_node.inno_id)
				{
					has_from = this_conn.inno_id;
				}
				if (this_conn.to_node.inno_id == connection_to_split.to_node.inno_id)
				{
					has_to = this_conn.inno_id;
				}
				if (has_to > -1 && has_from > -1)
				{
					gene_id = pop_node.inno_id;
					NodeGene new_node = new NodeGene(gene_id, activation);
					this.gene_ids.add(gene_id);
					
					ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node, has_from);
					
					this.conn_genes.put(has_from, new_conn_a);
					
					this.gene_ids.add(has_from);
					
					ConnectionGene new_conn_b = new ConnectionGene(new_node, connection_to_split.to_node, has_to);
					
					this.conn_genes.put(has_to, new_conn_b);
					
					this.gene_ids.add(has_to);
					
					return new_id;
				}
			}
		}
		
		NodeGene new_node = new NodeGene(new_id, activation);
		
		this.gene_ids.add(new_id);
		
		new_id++;
		
		ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node, new_id);
		
		this.conn_genes.put(new_id, new_conn_a);
		
		this.gene_ids.add(new_id);
		
		pop_conns.add(new_conn_a);
		
		new_id++;
		
		ConnectionGene new_conn_b = new ConnectionGene(new_node, connection_to_split.to_node, new_id);
		
		this.conn_genes.put(new_id, new_conn_b);
		
		this.gene_ids.add(new_id);
		
		pop_conns.add(new_conn_b);
		
		this.hidden_nodes.add(new_node);
		
		this.gene_ids.add(new_id);
		
		return new_id;
	}
	
	
	private void mutate_delete_node()
	{
		int num_nodes = this.hidden_nodes.size();
		
		if (num_nodes == 0)
		{
			return;
		}
		
		Random dice = new Random();
		
		int node_idx = dice.nextInt(num_nodes);
		
		NodeGene delete_node = this.hidden_nodes.get(node_idx);
		
		int conn_counter = delete_node.connections.size();
		
		for (int ix = 0; ix < conn_counter; ix++)
		{
			this.conn_genes.remove(delete_node.connections.get(ix).get_id());
			this.gene_ids.remove(delete_node.connections.get(ix).get_id());
		}
		
		this.hidden_nodes.remove(node_idx);
		
		this.gene_ids.remove(Integer.valueOf(node_idx));
		
		return;
	}
	
	private void mutate_delete_conn()
	{
		Random dice = new Random();
		
		if (this.conn_genes.size() == 0)
		{
			return;
		}
		
		int delete_key = dice.nextInt(this.conn_genes.size());
		
		this.conn_genes.remove(delete_key);
		
		this.gene_ids.remove(Integer.valueOf(delete_key));
	}
	
	private int connect_full_initial(int new_id, ArrayList<ConnectionGene> pop_conns)
	{
		int num_in = this.input_nodes.size();
		int num_out = this.output_nodes.size();
		for(int ix = 0; ix < num_in; ix++)
		{
			for (int ixx = 0; ixx < num_out; ixx++)
			{
				// do we really need to pass in the whole node, seems like just the ids out suffice
				NodeGene from_node = this.input_nodes.get(ix);
				NodeGene to_node = this.output_nodes.get(ixx);
				
				ConnectionGene new_gene = new ConnectionGene(from_node, to_node, new_id);
				pop_conns.add(new_gene);
				this.conn_genes.put(new_id, new_gene);
				this.gene_ids.add(new_id);
				from_node.connections.add(new_gene);
				new_id++;
			}
		}
		return new_id;
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		Genome empty_self = new Genome(this.id, this.population_hash);
		String empty_json = gson.toJson(this);
		return empty_json;
	}
}
