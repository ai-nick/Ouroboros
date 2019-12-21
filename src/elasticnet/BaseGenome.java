package elasticnet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.soap.Node;

import com.google.gson.Gson;

public class BaseGenome {

	public Long id;
	Integer gen_born = 0;
	//ArrayList<Integer> gene_ids = new ArrayList<Integer>();
	Long population_hash;
	int species_id = -1;
	public double fitness = -1.0;
	public int avg_w = 0;
	public boolean needs_validation = true;
	public boolean has_validation = false;
	public HashMap<Long, NodeGene>  input_nodes = new HashMap<Long, NodeGene>();
	public HashMap<Long, NodeGene>  hidden_nodes = new HashMap<Long, NodeGene>();
	public HashMap<Long, NodeGene> output_nodes = new HashMap<Long, NodeGene>();
	HashMap<Integer, Double> fit_dists = new HashMap<Integer, Double>();
	public boolean is_recursive = false;
	public String peer_eval_id = "";
	public String peer_validation_id = "";
	public InnovationService inno_service = new InnovationService();
	
	//TODO set max and min conn in all mutation methods
	//TODO and upon construction
	public BaseGenome(Long p_hash, Long genome_id, int gen) {
		this.id = genome_id;
		this.population_hash = p_hash;
		this.gen_born = gen;
	}
	
	public BaseGenome(double test_fit)
	{
		fitness = test_fit;
	}
	
	public int mutate_genome(int new_id, 
			NeatConfig config,
			InnovationService inno_service
			)
	{
		Random rand = new Random();
		
		String default_activation = config.defaultActivation;
		
		this.mutate_weights(config.mutate_weight_factor, config.weight_mutate_rate, config.weight_min, config.weight_max);
		
		// sum of all mutation probabilities
		Double prob_sum = config.add_conn_prob + config.delete_conn_prob + config.add_node_prob + config.delete_node_prob;
		
		// if sum is less than 1.0 set sum to be 1.0
	
		if (prob_sum < 1.0)
		{
			prob_sum = 1.0;
		}
		if (rand.nextFloat() < (config.delete_node_prob/prob_sum))
		{
			System.out.println("deleting node here");
			mutate_delete_node(inno_service);
		}
		if (rand.nextFloat() < (config.delete_conn_prob/prob_sum))
		{
			System.out.println("deleting conn here");
			mutate_delete_conn(inno_service);
		}
		if (rand.nextFloat() < (config.add_conn_prob/prob_sum))
		{
			System.out.println("adding conn here");
			mutate_add_conn(inno_service);
		}
		if (rand.nextFloat() < (config.add_node_prob/prob_sum))
		{
			//this is where we are loosing conn pointers from node genes
			System.out.println("adding node here");
			mutate_add_node(inno_service, config.defaultActivation);
		}
		return new_id;
	}
	
	public void mutate_delete_node(InnovationService inno_service)
	{
		int rand_index = this.get_random_in_range(this.hidden_nodes.size());
		
		this.hidden_nodes.remove(rand_index);
		
		return;
	}
	
	public void mutate_delete_conn(InnovationService inno_service)
	{
		ArrayList<Long[]> conn_ids = this.get_conn_ids();
		
		int rand_index = this.get_random_in_range(conn_ids.size());
		
		Long remove_id = conn_ids.get(rand_index)[0];
		
		int num_input = this.input_nodes.size();
		return;
	}
	
	public void mutate_add_node(InnovationService inno_service, String default_activation)
	{
		ArrayList<Long[]> conn_ids = this.get_conn_ids();
		
		int rand_index = this.get_random_in_range(conn_ids.size());
		
		Long split_id = conn_ids.get(rand_index)[0];
		
		Long new_inno = inno_service.path_exists(from_node, to_node)
		
		return;
	}
	
	public void mutate_add_conn(InnovationService inno_service)
	{
		return;
	}
	public void mutate_weights(double rate, double factor, double min, double max)
	{
		return;
	}
	
	private ConnectionGene get_conn_by_id(Long inno_id)
	{
		int num_input = this.input_nodes.size();
		
		for(int x = 0; x < num_input; x++)
		{
			NodeGene next_node = this.input_nodes.get(x);
			
			if(next_node.connections.keySet().contains(inno_id))
			{
				return next_node.connections.get(inno_id);
			}
		}
		
		int num_hidden = this.hidden_nodes.size();
		
		for(int x = 0; x < num_hidden; x++)
		{
			NodeGene next_node = this.hidden_nodes.get(x);
			
			if(next_node.connections.keySet().contains(inno_id))
			{
				return next_node.connections.get(inno_id);
			}
		}
		
		int num_output = this.output_nodes.size();
		
		for(int x = 0; x < num_output; x++)
		{
			NodeGene next_node = this.output_nodes.get(x);
			
			if(next_node.connections.keySet().contains(inno_id))
			{
				return next_node.connections.get(inno_id);
			}
		}
		
		return null;
	}
	
	private ConnectionGene get_conn_with_node_and_conn_id(Long conn_id, Long node_id)
	{
		if(this.input_nodes.contains(node_id))
		{
			return this.input_nodes.get(node_id).connections.get(conn_id);
		}
		return null;
	}
	
	private ArrayList<Long[]> get_conn_ids()
	{
		ArrayList<Long[]> ids = new ArrayList<Long[]>();
		
		int num_nodes_input = this.input_nodes.size();
		
		for (int x = 0; x < num_nodes_input; x++)
		{
			NodeGene current = this.input_nodes.get(x);
			
			int conns_count = current.connections.size();
			
			for(int i = 0; i < conns_count; i++)
			{
				ids.add(new Long[] {current.connections.get(i).inno_id, current.inno_id});
			}
		}
		
		int num_nodes_hidden = this.hidden_nodes.size();
		
		for (int x = 0; x < num_nodes_hidden; x++)
		{
			NodeGene current = this.hidden_nodes.get(x);
			
			int conns_count = current.connections.size();
			
			for(int i = 0; i < conns_count; i++)
			{
				ids.add(new Long[] {current.connections.get(i).inno_id, current.inno_id});
			}
		}		
		
		return ids;
	}
	
	private int get_random_in_range(int range_len)
	{
		Random dice = new Random();
		
		return dice.nextInt(range_len);
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String empty_json = gson.toJson(this);
		return empty_json;
	}
}
