package elasticnet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.soap.Node;

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
		population_hash = cloner.population_hash;
		species_id = cloner.species_id;
		conn_genes = new ArrayList<Integer>(cloner.conn_genes);
		input_nodes = new ArrayList<Integer>(cloner.input_nodes);
		hidden_nodes = new ArrayList<Integer>(cloner.hidden_nodes);
		output_nodes = new ArrayList<Integer>(cloner.output_nodes);
	}
	
	public int create_from_scratch(int inno_id, 
			NeatConfig config, 
			int populationHash,
			HashMap<Integer, HashMap<Integer,NodeGene>> node_gene_list,
			HashMap<Integer, HashMap<Integer,ConnectionGene>> conn_gene_list
			)
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
			this.input_nodes.add(new_node.inno_id);
			gene_index = ix;
		}
		gene_index++;
		for (int ix = 0; ix < num_out; ix++)
		{
			NodeGene new_node = new NodeGene(gene_index+ix, this.population_hash, config.output_activation);
			new_node.is_input = false;
			new_node.is_output = true;
			inno_id++;
			this.output_nodes.add(new_node.inno_id);
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
				this.hidden_nodes.add(new_node.inno_id);
			}			
		}
		else 
		{
			inno_id = this.connect_full_initial(gene_index, conn_genes);
		}
		inno_id = this.mutate_genome(inno_id, config, node_gene_list, conn_gene_list);
		this.set_max_and_min();
		return inno_id;
	}
	
	public void set_max_and_min() {
		ArrayList<Integer> all_nodes = this.get_all_nodes();
		int min_node_id = Collections.min(all_nodes);
		int min_conn_id = Collections.min(this.conn_genes);
		int max_conn_id = Collections.max(this.conn_genes);
		int max_node_id = Collections.max(all_nodes);
		if (min_node_id < min_conn_id)
		{
			this.gene_id_min = min_node_id;
		}
		else
		{
			this.gene_id_min = min_conn_id;
		}
		if(max_node_id > max_conn_id)
		{
			this.gene_id_max = max_node_id;
		}
		else
		{
			this.gene_id_max = max_conn_id;
		}
	}
	
	public double get_prime(int num_others)
	{
		return this.fitness/num_others;
	}
	
	public void set_nodes(ArrayList<NodeGene> ngs)
	{
		int num_in = ngs.size();
		for(int i = 0; i < num_in; i++)
		{
			this.input_nodes.add(ngs.get(i).inno_id);
		}
	}
	
	public void set_node(NodeGene ng)
	{
		if(ng.is_input)
		{
			input_nodes.add(ng.inno_id);
		}
		else if (ng.is_output)
		{
			output_nodes.add(ng.inno_id);
		}
		else
		{
			hidden_nodes.add(ng.inno_id);
		}
	}
	
	public void set_connections(ArrayList<ConnectionGene> conns)
	{
		int conn_count = conns.size();
		for(int i = 0; i < conn_count; i++)
		{
			this.conn_genes.add(conns.get(i).inno_id);
		}
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
	
	public ArrayList<Integer> get_all_nodes()
	{
		ArrayList<Integer> all_nodes = new ArrayList<Integer>(this.hidden_nodes);
		all_nodes.addAll(this.input_nodes);
		all_nodes.addAll(this.output_nodes);
		return all_nodes;
	}
	
	public int mutate_genome(int new_id, 
			NeatConfig config,
			HashMap<Integer, HashMap<Integer,NodeGene>> pop_nodes, 
			HashMap<Integer, HashMap<Integer,ConnectionGene>> pop_conns
			)
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
	
	private int mutate_add_conn(int new_id, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns,
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes
			)
	{
		int conn_id = new_id;
		ArrayList<Integer> all_the_nodes = this.get_all_nodes();
		boolean new_structure = true;
		Random dice = new Random();
		
		int to_node_key = (int)all_the_nodes.get(dice.nextInt(all_the_nodes.size()));
		
		int from_node_key = (int)all_the_nodes.get(dice.nextInt(all_the_nodes.size()));
		
		NodeGene from_node = pop_nodes.get(from_node_key).get(this.id);
		
		NodeGene to_node = pop_nodes.get(from_node_key).get(this.id);
		
		// the next to if statements ensure we dont add conns that are either output -> output
		// of input->input
		if(this.output_nodes.contains(to_node_key) && this.output_nodes.contains(from_node_key))
		{
			return new_id;
		}
		if(this.input_nodes.contains(from_node_key) && this.input_nodes.contains(to_node_key))
		{
			return new_id;
		}
		int pop_conn_count = pop_conns.size();
		for(int p = 0; p < pop_conn_count; p++)
		{
			int key = (int)pop_conns.keySet().toArray()[p];
			HashMap<Integer, ConnectionGene> gene_list = pop_conns.get(key);
			ConnectionGene p_conn = gene_list.get(gene_list.keySet().toArray()[0]);
			if (p_conn.to_node != null && p_conn.from_node != null)
			{
				
				if ((p_conn.to_node.inno_id == to_node.inno_id) && (p_conn.from_node.inno_id == from_node.inno_id))
				{
					conn_id = p_conn.inno_id;
					new_structure = false;
				}	
			}
		}
		ConnectionGene new_gene = new ConnectionGene(from_node, to_node, conn_id);
		
		if (new_structure == true)
		{
			HashMap<Integer, ConnectionGene> new_map = new HashMap<Integer, ConnectionGene>();
			new_map.put(this.id, new_gene);
			pop_conns.put(conn_id, new_map);
		}
		pop_conns.get(conn_id).put(this.id, new_gene);
		
		this.conn_genes.add(conn_id);
		
		return new_id;
	}
	
	private int mutate_add_node(int new_id, 
			String activation, 
			HashMap<Integer,HashMap<Integer, NodeGene>> pop_nodes, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns
			)
	{
		//boolean has_hist_id;
		
		int gene_id = new_id;
		
		Random dice = new Random();
		
		if (this.conn_genes.size() == 0)
		{
			return new_id;
		}
		
		int connection_to_split_index = this.conn_genes.get(dice.nextInt(this.conn_genes.size()));
		
		ConnectionGene connection_to_split = pop_conns.get(connection_to_split_index).get(this.id);
		
		int hidden_count = pop_nodes.size();
		
		for(Integer i : pop_nodes.keySet())
		{
			NodeGene pop_node = pop_nodes.get(i).get(this.id);
			//NodeGene pop_node = pop_nodes.get(hid_idx).get(this.id);
			if(!pop_node.is_input && !pop_node.is_input)
			{
				int num_conns = pop_node.connections.size();
				int has_to = -1;
				int has_from = -1;
				for (int ix = 0; ix < num_conns; ix++)
				{
					ConnectionGene this_conn= pop_node.connections.get(ix);
					if (this_conn.from_node == connection_to_split.from_node)
					{
						has_from = this_conn.inno_id;
						NodeGene new_node = pop_nodes.get(new_id).get(pop_node.genome_id);
						int new_loop_count = new_node.connections.size();
						for(int y = 0; y < new_loop_count; y++)
						{
							if(new_node.connections.get(y).to_node == connection_to_split.to_node)
							{
								// mutation already exist and we will use the current inno id from the 
								// master list of genomes
								NodeGene node_to_add = new NodeGene(new_node.inno_id, this.id);
								pop_nodes.get(new_node.inno_id).put(this.id, node_to_add);
								ConnectionGene conn_to_add = new ConnectionGene(node_to_add.inno_id, connection_to_split.to_node, new_node.connections.get(y).inno_id, this.id);
								pop_conns.get(new_node.connections.get(y).inno_id).put(this.id, conn_to_add);
								return new_id;
							}
						}
					}
				}	
			}
		}
		// if we make it here this structure hasnt occured yet
		// so we will add the node and its two new connecitons
		NodeGene new_node = new NodeGene(new_id, activation);
		
		HashMap<Integer, NodeGene> new_node_dict = new HashMap<Integer, NodeGene>();
		
		new_node_dict.put(this.id, new_node);
		
		pop_nodes.put(new_id, new_node_dict);
		
		new_id++;
		
		ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node.inno_id, new_id, this.id);
		
		new_node.connections.add(new_conn_a);
		
		this.conn_genes.add(new_id);
		
		HashMap<Integer, ConnectionGene> new_conn_dict_a = new HashMap<Integer, ConnectionGene>();
		
		new_conn_dict_a.put(this.id, new_conn_a);
		
		pop_conns.put(new_id, new_conn_dict_a);
		
		new_id++;
		
		ConnectionGene new_conn_b = new ConnectionGene(new_node.inno_id, connection_to_split.to_node, new_id, this.id);
		
		new_node.connections.add(new_conn_b);
		
		HashMap<Integer, ConnectionGene> new_conn_dict_b = new HashMap<Integer, ConnectionGene>();
		
		new_conn_dict_b.put(this.id, new_conn_b);
		
		pop_conns.put(new_id, new_conn_dict_b);
		
		this.hidden_nodes.add(new_node.inno_id);
		
		new_id++;
		
		return new_id;
	}
	
	// TODO remove genes from pop's nested hashmaps
	private void mutate_delete_node(HashMap<Integer,HashMap<Integer, ConnectionGene>> pop_conns, HashMap<Integer,HashMap<Integer, NodeGene>> pop_nodes)
	{
		int num_nodes = this.hidden_nodes.size();
		
		if (num_nodes == 0)
		{
			return;
		}
		
		Random dice = new Random();
		
		int node_idx = dice.nextInt(num_nodes);
		
		NodeGene delete_node = pop_nodes.get(node_idx).get(this.id);
		
		int conn_counter = delete_node.connections.size();
		
		for (int ix = 0; ix < conn_counter; ix++)
		{
			this.conn_genes.remove(delete_node.connections.get(ix).get_id());
		}
		
		this.hidden_nodes.remove(node_idx);
		
		return;
	}
	//TODO pass in pops nested conn dictionary and remove the conns entry
	private void mutate_delete_conn()
	{
		Random dice = new Random();
		
		if (this.conn_genes.size() == 0)
		{
			return;
		}
		
		int delete_key = dice.nextInt(this.conn_genes.size());
		
		this.conn_genes.remove(delete_key);
	}
	
	private int connect_full_initial(int new_id, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns, 
			HashMap<Integer, HashMap<Integer, NodeGene>> node_conns)
	{
		int num_in = this.input_nodes.size();
		int num_out = this.output_nodes.size();
		for(int ix = 0; ix < num_in; ix++)
		{
			for (int ixx = 0; ixx < num_out; ixx++)
			{
				// do we really need to pass in the whole node, seems like just the ids out suffice
				NodeGene from_node = node_conns.get(this.input_nodes.get(ix)).get(this.id);
				NodeGene to_node = node_conns.get(this.output_nodes.get(ixx)).get(this.id);
				
				ConnectionGene new_gene = new ConnectionGene(from_node, to_node, new_id);
				if(pop_conns.keySet().contains(new_id))
				{
					pop_conns.get(new_id).put(this.id, new_gene);	
				}
				else
				{
					HashMap<Integer, ConnectionGene> new_dict = new HashMap<Integer, ConnectionGene>();
					pop_conns.put(new_id, new_dict);
				}
				this.conn_genes.add(new_id);
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
