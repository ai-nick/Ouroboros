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

	public Integer id;
	Integer gen_born = 0;
	//ArrayList<Integer> gene_ids = new ArrayList<Integer>();
	Long population_hash = 0;
	int species_id = 0;
	public double fitness = -1.0;
	public int avg_w = 0;
	public boolean needs_validation = true;
	public boolean has_validation = false;
	//public ArrayList<Integer> conn_genes = new ArrayList<Integer>();
	public ArrayList<Integer> input_nodes = new ArrayList<Integer>();
	public ArrayList<Integer> hidden_nodes = new ArrayList<Integer>();
	public ArrayList<Integer> output_nodes = new ArrayList<Integer>();
	HashMap<Integer, Double> fit_dists = new HashMap<Integer, Double>();
	public boolean is_recursive = false;
	public String peer_eval_id = "";
	public String peer_validation_id = "";
	
	//TODO set max and min conn in all mutation methods
	//TODO and upon construction
	public Genome(Long p_hash, int genome_id) {
		this.id = genome_id;
		this.population_hash = p_hash;
	}
	
	public Genome(double test_fit)
	{
		fitness = test_fit;
	}
	
	public Genome(Genome cloner, int id,
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns,
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes
			)
	{
		this.id = id;
		gen_born = cloner.gen_born + 1;
		population_hash = cloner.population_hash;
		species_id = cloner.species_id;
		input_nodes = new ArrayList<Integer>(cloner.input_nodes);
		hidden_nodes = new ArrayList<Integer>(cloner.hidden_nodes);
		output_nodes = new ArrayList<Integer>(cloner.output_nodes);
		ArrayList<Integer> all_nodes = this.get_all_nodes();
		int node_count = all_nodes.size();
		for(int ix = 0; ix < node_count; ix++)
		{
			NodeGene to_add = new NodeGene(pop_nodes.get(all_nodes.get(ix)).get(cloner.id));
			
			int conns = to_add.connections.size();
			for (int x = 0; x < conns; x++)
			{
				ConnectionGene conn_copy = new ConnectionGene(pop_conns.get(to_add.connections.get(x)).get(cloner.id));
				
				pop_conns.get(conn_copy.inno_id).put(this.id, conn_copy);
			}
			
			pop_nodes.get(to_add.inno_id).put(this.id, to_add);
		}
	}
	// used when mutating this genome, leaves the original alone 
	// pretty sure we need to pass in pops master map of genes here and
	// add entries for this id
	public Genome(Genome cloner, int id,
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns,
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes,
			boolean clone_conns
			)
	{
		this.id = id;
		gen_born = cloner.gen_born + 1;
		population_hash = cloner.population_hash;
		species_id = cloner.species_id;
		input_nodes = new ArrayList<Integer>(cloner.input_nodes);
		hidden_nodes = new ArrayList<Integer>(cloner.hidden_nodes);
		output_nodes = new ArrayList<Integer>(cloner.output_nodes);
		if(clone_conns == true)
		{
			ArrayList<Integer> all_nodes = this.get_all_nodes();
			int node_count = all_nodes.size();
			for(int ix = 0; ix < node_count; ix++)
			{
				NodeGene to_add = new NodeGene(pop_nodes.get(all_nodes.get(ix)).get(cloner.id));
				
				int conns = to_add.connections.size();
				for (int x = 0; x < conns; x++)
				{
					ConnectionGene conn_to_add = pop_conns.get(to_add.connections.get(x)).get(cloner.id);
					
					ConnectionGene conn_copy = new ConnectionGene(conn_to_add);
					
					pop_conns.get(conn_copy.inno_id).put(this.id, conn_copy);
				}
				
				pop_nodes.get(to_add.inno_id).put(this.id, to_add);
			}	
		}
	}
	
	public int create_from_scratch(NeatConfig config, 
			Long populationHash,
			HashMap<Integer, HashMap<Integer,NodeGene>> node_gene_list,
			HashMap<Integer, HashMap<Integer,ConnectionGene>> conn_gene_list
			)
	{
		int num_in = config.num_input;
		
		int num_hidden = config.num_hidden;
		
		int num_out = config.num_output;
		
		this.population_hash = populationHash;
		
		int inno_id = 0;
		
		for (int ix = 0; ix < num_in; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id);
			new_node.is_input = true;
			new_node.is_output = false;
			inno_id++;
			this.input_nodes.add(new_node.inno_id);
			if(node_gene_list.containsKey(new_node.inno_id)) {
				node_gene_list.get(new_node.inno_id).put(this.id, new_node);
			}
			else
			{
				HashMap<Integer, NodeGene> initial_map = new HashMap<Integer, NodeGene>();
				initial_map.put(this.id, new_node);
				node_gene_list.put(new_node.inno_id, initial_map);
			}
		}
		//gene_index++;
		for (int ix = 0; ix < num_out; ix++)
		{
			NodeGene new_node = new NodeGene(inno_id, config.output_activation);
			new_node.is_input = false;
			new_node.is_output = true;
			inno_id++;
			this.output_nodes.add(new_node.inno_id);
			if(node_gene_list.containsKey(new_node.inno_id)) {
				node_gene_list.get(new_node.inno_id).put(this.id, new_node);
			}
			else
			{
				HashMap<Integer, NodeGene> initial_map = new HashMap<Integer, NodeGene>();
				initial_map.put(this.id, new_node);
				node_gene_list.put(new_node.inno_id, initial_map);
			}
		}
		//gene_index++;
		if (num_hidden > 0)
		{
			for (int ix = 0; ix < num_hidden; ix++)
			{
				NodeGene new_node = new NodeGene(inno_id);
				new_node.is_input = false;
				new_node.is_output = false;
				inno_id++;
				this.hidden_nodes.add(new_node.inno_id);
				if(node_gene_list.containsKey(new_node.inno_id)) {
					node_gene_list.get(new_node.inno_id).put(this.id, new_node);
				}
				else
				{
					HashMap<Integer, NodeGene> initial_map = new HashMap<Integer, NodeGene>();
					initial_map.put(this.id, new_node);
					node_gene_list.put(new_node.inno_id, initial_map);
				}
			}			
		}
		inno_id = this.connect_full_initial(inno_id, conn_gene_list, node_gene_list);
		inno_id = this.mutate_genome(inno_id, config, node_gene_list, conn_gene_list);
		return inno_id;
	}
	
	public double get_prime(int num_others)
	{
		return this.fitness/num_others;
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
		ArrayList<Integer> all_nodes;
		if(this.hidden_nodes != null && this.hidden_nodes.isEmpty() == false)
		{
			all_nodes = new ArrayList<Integer>(this.hidden_nodes);	
		}
		else
		{
			all_nodes = new ArrayList<Integer>();
		}
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
		if (rand.nextFloat() < (config.delete_node_prob/prob_sum))
		{
			//System.out.println("deleting node here");
			mutate_delete_node(pop_conns, pop_nodes);
		}
		if (rand.nextFloat() < (config.delete_conn_prob/prob_sum))
		{
			//System.out.println("deleting conn here");
			mutate_delete_conn(pop_conns, pop_nodes);
		}
		if (rand.nextFloat() < (config.add_conn_prob/prob_sum))
		{
			//System.out.println("adding conn here");
			new_id = mutate_add_conn(new_id, pop_conns, pop_nodes);
		}
		if (rand.nextFloat() < (config.add_node_prob/prob_sum))
		{
			//this is where we are loosing conn pointers from node genes
			//System.out.println("adding node here");
			new_id = mutate_add_node(new_id, config.defaultActivation, pop_nodes, pop_conns);
		}
		return new_id;
	}
	
	private int mutate_add_conn(int new_id, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns,
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes
			)
	{
		// if we have no hidden nodes yet its already fully connected and 
		// there is no where to add a conn
		if(this.hidden_nodes.size() == 0)
		{
			return new_id;
		}
		
		int conn_id = new_id;
		
		ArrayList<Integer> all_the_nodes = this.get_all_nodes();
		
		boolean new_structure = true;
		
		int to_node_key = this.get_random_in_range(all_the_nodes.size());
		
		int from_node_key = this.get_random_in_range(all_the_nodes.size());
		
		ArrayList<Integer> all_conns = this.get_all_conn_ids(pop_nodes);
		
		int num_conns = all_conns.size();
		
		for(int i = 0; i < num_conns; i++)
		{
			ConnectionGene next_gene = pop_conns.get(all_conns.get(i)).get(this.id);
			
			if(next_gene.to_node == to_node_key && next_gene.from_node == from_node_key)
			{
				//god doesnt play dice, this mutation wasnt meant to be
				return new_id;
			}
		}
		if(this.output_nodes.contains(to_node_key) && this.output_nodes.contains(from_node_key))
		{
			return new_id;
		}
		if(this.input_nodes.contains(from_node_key) && this.input_nodes.contains(to_node_key))
		{
			return new_id;
		}
		if(this.output_nodes.contains(from_node_key) || this.input_nodes.contains(to_node_key))
		{
			return new_id;
		}
		if(from_node_key == to_node_key && this.is_recursive == false)
		{
			return new_id;
		}
		NodeGene from_node = pop_nodes.get(all_the_nodes.get(from_node_key)).get(this.id);
		
		// the next to if statements ensure we dont add conns that are either output -> output
		// of input->input

		for(Integer p : pop_conns.keySet())
		{
			HashMap<Integer, ConnectionGene> gene_list = pop_conns.get(p);
			if(gene_list.keySet().iterator().hasNext())
			{
				ConnectionGene p_conn = gene_list.get(gene_list.keySet().iterator().next());
				
				if ((p_conn.to_node == to_node_key) && (p_conn.from_node == from_node.inno_id))
				{
					conn_id = p_conn.inno_id;
					new_structure = false;
				}		
			}
		}
		ConnectionGene new_gene = new ConnectionGene(from_node.inno_id, to_node_key, conn_id, this.id);
		if (new_structure == true)
		{
			while(pop_conns.containsKey(conn_id) || pop_nodes.containsKey(conn_id))
			{
				conn_id++;
			}
			new_gene.inno_id = conn_id;
			HashMap<Integer, ConnectionGene> new_map = new HashMap<Integer, ConnectionGene>();
			new_map.put(this.id, new_gene);
			pop_conns.put(conn_id, new_map);
		}
		else 
		{
			pop_conns.get(conn_id).put(this.id, new_gene);
			conn_id++;
		}
		//this.conn_genes.add(new_gene.inno_id);
		from_node.connections.add(new_gene.inno_id);
		return conn_id;
	}
	
	//TODO ensure we are removing conn ref from node
	private int mutate_add_node(int new_id, 
			String activation, 
			HashMap<Integer,HashMap<Integer, NodeGene>> pop_nodes, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns
			)
	{
		//boolean has_hist_id;

		//we cant split a conn if we dont have any
		
		ArrayList<Integer> conn_ids = this.get_all_conn_ids(pop_nodes);
		
		if (conn_ids.size() == 0)
		{
			return new_id;
		}
		
		int gene_id = new_id;
		
		int connection_to_split_index = conn_ids.get(this.get_random_in_range(conn_ids.size()));
		
		//System.out.println("splitting conn");
		//System.out.println(connection_to_split_index);
		
		ConnectionGene connection_to_split = pop_conns.get(connection_to_split_index).get(this.id);
		
		boolean struct_exists = false;
		
		// will store the existing connection inno ids if 
		// this genetic structure has already been evolved
		// by a different genome
		int conn_a_id = -1;
		int conn_b_id = -1;
		
		if(connection_to_split == null)
		{
			System.out.println("null connn chosen, doesnt exist in the master hash map");
			System.out.print("conn id: ");
			System.out.println(connection_to_split_index);
		}
		
		HashMap<Integer, NodeGene> node_gene_set = pop_nodes.get(connection_to_split.from_node);
		
		// this block amounts to a search of the nodes thier connections
		// to see if the path from_node -> some other node -> to node already exists in 
		// another genomme, if it does, we use those ids
		for(int n : node_gene_set.keySet())
		{
			NodeGene check_it = node_gene_set.get(n);
			
			int check_conn_count = check_it.connections.size();
			
			for(int nx = 0; nx < check_conn_count; nx++)
			{
				ConnectionGene next_conn = pop_conns.get(check_it.connections.get(nx)).get(n);
				// null pointers happening here due to problems in cross over methods of population class
				if(next_conn == null)
				{
					System.out.print("null conn: ");
					System.out.print(check_it.connections.get(nx));
					System.out.print(" in genome: ");
					System.out.println(n);
				}
				NodeGene connected = pop_nodes.get(next_conn.to_node).get(n);
				int check_conn_count_2 = connected.connections.size();
				for(int nx2 = 0; nx2 < check_conn_count_2; nx2++)
				{
					ConnectionGene second_conn = pop_conns.get(connected.connections.get(nx2)).get(n);
					if(second_conn.to_node == connection_to_split.to_node)
					{
						struct_exists = true;
						conn_a_id = next_conn.inno_id;
						conn_b_id = second_conn.inno_id;
						gene_id = connected.inno_id;
					}
				}
			}
		}

		// if we make it here this structure hasnt occured yet
		// so we will add the node and its two new connecitons
		
		// delcare our new node
		NodeGene new_node;
		
		if(struct_exists == true)
		{
			// uses ids of the existing structure to build our node and connections
			new_node = new NodeGene(gene_id, activation);
			
			ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node.inno_id, conn_a_id, this.id);
			
			//this.conn_genes.add(conn_a_id);
			
			pop_conns.get(conn_a_id).put(this.id, new_conn_a);
			
			ConnectionGene new_conn_b = new ConnectionGene(new_node.inno_id, connection_to_split.to_node, conn_b_id, this.id);
			
			//this.conn_genes.add(conn_b_id);
			
			pop_conns.get(conn_b_id).put(this.id, new_conn_b);
			
			new_node.connections.add(new_conn_b.inno_id);
			
			pop_nodes.get(gene_id).put(this.id, new_node);
			
			this.hidden_nodes.add(gene_id);
			
			NodeGene from_node = pop_nodes.get(connection_to_split.from_node).get(this.id);
			
			from_node.connections.add(new_conn_a.inno_id);
			
			from_node.connections.remove(from_node.connections.indexOf(connection_to_split_index));
			
			pop_nodes.get(from_node.inno_id).replace(this.id, from_node);
			
			new_id = Collections.max(conn_ids) + 1;
		}
		else
		{
			while(pop_conns.containsKey(new_id) == true || pop_nodes.containsKey(new_id) == true)
			{
				new_id++;
			}
			new_node = new NodeGene(new_id, activation);
			
			new_id++;
			
			ConnectionGene new_conn_a = new ConnectionGene(connection_to_split.from_node, new_node.inno_id, new_id, this.id);
			
			//this.conn_genes.add(new_id);
			
			HashMap<Integer, ConnectionGene> new_conn_dict_a = new HashMap<Integer, ConnectionGene>();
			
			new_conn_dict_a.put(this.id, new_conn_a);
			
			pop_nodes.get(connection_to_split.from_node).get(this.id).connections.add(new_conn_a.inno_id);
			
			pop_conns.put(new_id, new_conn_dict_a);
			
			//System.out.println("add new conn gene with id: " + new_conn_a.inno_id);
			
			new_id++;
			
			ConnectionGene new_conn_b = new ConnectionGene(new_node.inno_id, connection_to_split.to_node, new_id, this.id);
			
			//this.conn_genes.add(new_id);
			
			HashMap<Integer, ConnectionGene> new_conn_dict_b = new HashMap<Integer, ConnectionGene>();
			
			new_conn_dict_b.put(this.id, new_conn_b);
			
			pop_conns.put(new_id, new_conn_dict_b);
			
			//System.out.println("add new conn gene with id: " + new_conn_b.inno_id);
			
			new_node.connections.add(new_conn_b.inno_id);
			
			HashMap<Integer, NodeGene> new_node_dict = new HashMap<Integer, NodeGene>();
			
			new_node_dict.put(this.id, new_node);
			
			pop_nodes.put(new_node.inno_id, new_node_dict);
			
			this.hidden_nodes.add(new_node.inno_id);
			
			//System.out.println("add node gene with id: " + new_node.inno_id);
			
			NodeGene from_node = pop_nodes.get(connection_to_split.from_node).get(this.id);
			
			from_node.connections.remove(from_node.connections.indexOf(connection_to_split_index));
			
			pop_nodes.get(from_node.inno_id).replace(this.id, from_node);
			
			new_id++;
		}
		//this.conn_genes.remove(this.conn_genes.indexOf(connection_to_split_index));
		
		pop_conns.get(connection_to_split_index).remove(this.id);
		
		return new_id;
	}
	
	// TODO remove genes from pop's nested hashmaps
	private void mutate_delete_node(HashMap<Integer,HashMap<Integer, ConnectionGene>> pop_conns, HashMap<Integer,HashMap<Integer, NodeGene>> pop_nodes)
	{
		int num_nodes = this.hidden_nodes.size();
		// if we have no nodes just return its doomed and will die out yolo or w/e
		if (num_nodes == 0)
		{
			return;
		}
		// get a random index
		int node_idx = this.get_random_in_range(num_nodes);
		// get the hidden node id at that index
		int delete_id = this.hidden_nodes.get(node_idx);
		//get the node from our master hashmap
		
		//System.out.print("delete node id: ");
		//System.out.println(delete_id);
		
		// gonna do this a bit differently, a way that makes sense lol
		ArrayList<Integer> all_conns = this.get_all_conn_ids(pop_nodes);
		
		int num_all_conns = all_conns.size();
		
		for(int ix = 0; ix < num_all_conns; ix++)
		{
			ConnectionGene next_conn = pop_conns.get(all_conns.get(ix)).get(this.id);
			if(next_conn.to_node == delete_id)
			{
				NodeGene from_node = pop_nodes.get(next_conn.from_node).get(this.id);
				from_node.connections.remove(from_node.connections.indexOf(next_conn.inno_id));
				pop_nodes.get(from_node.inno_id).replace(this.id, from_node);
				pop_conns.get(next_conn.inno_id).remove(this.id);
			}
			if(next_conn.from_node == delete_id)
			{
				pop_conns.get(next_conn.inno_id).remove(this.id);
			}
		}
		/*
		ArrayList<Integer> delete_node_conns = pop_nodes.get(delete_id).get(this.id).connections;
		
		int num_delete_conns = delete_node_conns.size();
		
		for(int x = 0; x < num_delete_conns; x++)
		{
			
		}
		*/
		pop_nodes.get(delete_id).remove(this.id);
		
		this.hidden_nodes.remove(this.hidden_nodes.indexOf(delete_id));
		
		return;
	}
	//TODO pass in pops nested conn dictionary and remove the conns entry
	private void mutate_delete_conn(HashMap<Integer,HashMap<Integer, ConnectionGene>> pop_conns, HashMap<Integer,HashMap<Integer, NodeGene>> pop_nodes)
	{
		ArrayList<Integer> all_conns = this.get_all_conn_ids(pop_nodes);
		Random dice = new Random();
		
		if (all_conns.size() == 0)
		{
			return;
		}
		
		int delete_key = this.get_random_in_range(all_conns.size());
		
		int delete_id = all_conns.get(delete_key);
		
		//System.out.print("deleting conn id: ");
		
		//System.out.println(delete_id);
		//System.out.print("for genome: ");
		
		//System.out.println(this.id);
		
		ConnectionGene delete_conn = pop_conns.get(delete_id).get(this.id);
		
		NodeGene from_node = pop_nodes.get(delete_conn.from_node).get(this.id);
		
		from_node.connections.remove(from_node.connections.indexOf(delete_id));
		
		pop_nodes.get(delete_conn.from_node).replace(this.id, from_node);
		
		pop_conns.get(delete_id).remove(this.id);
	}
	
	private int connect_full_initial(int new_id, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns, 
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes)
	{
		int num_in = this.input_nodes.size();
		int num_out = this.output_nodes.size();
		for(int ix = 0; ix < num_in; ix++)
		{
			NodeGene from_node = pop_nodes.get(this.input_nodes.get(ix)).get(this.id);
			for (int ixx = 0; ixx < num_out; ixx++)
			{
				// do we really need to pass in the whole node, seems like just the ids out suffice
				NodeGene to_node = pop_nodes.get(this.output_nodes.get(ixx)).get(this.id);
				
				ConnectionGene new_gene = new ConnectionGene(from_node.inno_id, to_node.inno_id, new_id, this.id);
				
				//this.conn_genes.add(new_gene.inno_id);
				
				from_node.connections.add(new_gene.inno_id);
				
				if(pop_conns.keySet().contains(new_gene.inno_id))
				{
					pop_conns.get(new_gene.inno_id).put(this.id, new_gene);
				}
				else
				{
					HashMap<Integer, ConnectionGene> new_dict = new HashMap<Integer, ConnectionGene>();
					new_dict.put(this.id, new_gene);
					pop_conns.put(new_gene.inno_id, new_dict);
				}
				pop_nodes.get(from_node.inno_id).replace(this.id, from_node);
				new_id++;
			}
		}
		return new_id;
	}
	
	
	// removes the genomes genes from the master map passed in from the population class
	
	public void remove_genes_from_pop(HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes, HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns)
	{
		ArrayList<Integer> all_node_ids = this.get_all_nodes();
		
		int num_nodes = all_node_ids.size();
		
		for(int d = 0; d < num_nodes; d++)
		{
			int gene_key = all_node_ids.get(d);
			
			NodeGene current = pop_nodes.get(gene_key).get(this.id);
			
			int node_conn_count = current.connections.size();
			
			for(int c = 0; c < node_conn_count; c++)
			{
				//this.conn_genes.remove(this.conn_genes.indexOf(to_remove.inno_id));
				pop_conns.get(current.connections.get(c)).remove(this.id);
			}
			
			pop_nodes.get(gene_key).remove(this.id);
		}
		/*
		 * this can be deleted in due time, moving away form dependancy on 
		 * genome -> conn_genes as its redundant considering all nodes contain ids of there 
		 * conns
		int num_conns = this.conn_genes.size();
		
		for(int d = 0; d < num_conns; d++)
		{
			int gene_key = this.conn_genes.get(d);
			
			pop_conns.get(gene_key).remove(this.id);
		}
		*/
		return;
	}
	// return array of all conn ids
	// by looping through nodes and 
	// and adding the conn ids to an array
	public ArrayList<Integer> get_all_conn_ids(HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes)
	{
		ArrayList<Integer> all_nodes = this.get_all_nodes();
		int nodes_count = all_nodes.size();
		ArrayList<Integer> conns = new ArrayList<Integer>();
		for(int ix = 0; ix < nodes_count; ix++)
		{
			NodeGene current = pop_nodes.get(all_nodes.get(ix)).get(this.id);
			int conns_count = current.connections.size();
			for(int ix2 = 0; ix2 < conns_count; ix2++)
			{
				conns.add(current.connections.get(ix2));
			}
		}
		return conns;
	}
	
	public boolean check_for_nulls(
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns,
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes
			)
	{
		boolean has_nulls = false;
		ArrayList<Integer> all_nodes = this.get_all_nodes();
		int num_nodes = all_nodes.size();
		for(int x = 0; x < num_nodes; x++)
		{
			NodeGene next_node = pop_nodes.get(all_nodes.get(x)).get(this.id);
			if(next_node == null)
			{
				has_nulls = true;
			}
			else
			{
				int conn_count = next_node.connections.size();
				for(int i = 0; i < conn_count; i++)
				{
					ConnectionGene next_conn = pop_conns.get(next_node.connections.get(i)).get(this.id);
					if(next_conn == null)
					{
						has_nulls = true;
					}
					else
					{
						HashMap<Integer, NodeGene> node_map = pop_nodes.get(next_conn.to_node);
						if (node_map == null)
						{
							System.out.print("null node map for genome ");
							System.out.print(this.id);
							System.out.print(" conn_gene id: ");
							System.out.print(next_conn.inno_id);
							System.out.print(" node id: ");
							System.out.println(next_conn.to_node);
							has_nulls = true;
						}
						else
						{
							NodeGene to_node = node_map.get(this.id);
							if(to_node == null)
							{
								has_nulls = true;
							}	
						}	
					}
				}
			}
		}
		return has_nulls;
	}
	
	private int get_random_in_range(int range_len)
	{
		Random dice = new Random();
		
		return dice.nextInt(range_len);
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		Genome empty_self = new Genome(this.id, this.population_hash);
		String empty_json = gson.toJson(this);
		return empty_json;
	}
}
