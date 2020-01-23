package elasticnet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.soap.Node;

import com.google.gson.Gson;

public class BaseGenome {

	public Long id;
	Integer gen_born = 0;
	Long population_hash;
	int species_id = -1;
	public double fitness = -1.0;
	public boolean needs_validation = true;
	public boolean has_validation = false;
	public HashMap<Long, NodeGene>  input_nodes = new HashMap<Long, NodeGene>();
	public HashMap<Long, NodeGene>  hidden_nodes = new HashMap<Long, NodeGene>();
	public HashMap<Long, NodeGene> output_nodes = new HashMap<Long, NodeGene>();
	HashMap<Long, Double> fit_dists = new HashMap<Long, Double>();
	public boolean is_recursive = false;
	public String peer_eval_id = "";
	public String peer_validation_id = "";
	// for network evaluation
	ArrayList<Long> activated_conns = new ArrayList<Long>();
	public boolean feed_forward = true;

	public BaseGenome(Long p_hash, Long genome_id, int gen) {
		this.id = genome_id;
		this.population_hash = p_hash;
		this.gen_born = gen;
	}
	
	public BaseGenome(double test_fit)
	{
		fitness = test_fit;
	}
	
	public BaseGenome(BaseGenome clone_this, long genome_id)
	{
		this.id = genome_id;
		this.population_hash = clone_this.population_hash;
		this.gen_born = clone_this.gen_born + 1;
		this.input_nodes = new HashMap<Long, NodeGene>(clone_this.input_nodes);
		this.hidden_nodes = new HashMap<Long, NodeGene>(clone_this.hidden_nodes);
		this.output_nodes = new HashMap<Long, NodeGene>(clone_this.output_nodes);
	}
	
	public void activate(double[] inputs)
	{
		//ArrayList input_ordered = this.set_inputs(inputs);
		
		int input_count = this.input_nodes.size();

		this.set_inputs(inputs);
		
		HashMap<Long, NodeGene> all_nodes = this.get_all_nodes();
		
		ArrayList<NodeGene> actives = new ArrayList<NodeGene>(this.input_nodes.values());
		
		ArrayList<Long> activated_conns = new ArrayList<Long>();
		
		while(actives.iterator().hasNext())
		{
			NodeGene n = actives.iterator().next();
			
			if(n.connections != null)
			{
				for(ConnectionGene g : n.connections.values())
				{
					if(activated_conns.contains(g.inno_id) == false)
					{
						NodeGene next_node = all_nodes.get(g.to_node);
						if(next_node == null)
						{
							System.out.print("null to node: ");
							System.out.println(g.to_node);
						}
						next_node.add_to_current_value(Activator.tanh_activation(n.current_val * g.get_weight()));
						
						actives.add(next_node);
						
						activated_conns.add(g.inno_id);
					}
				}	
			}
			
			actives.remove(n);
		}
	}
	
	public ArrayList<Double> get_output()
	{
		ArrayList<Double> out_vals = new ArrayList<Double>();
		
		ArrayList<Long> outs_ordered = new ArrayList<Long>(this.output_nodes.keySet());
		
		int num_out = outs_ordered.size();
		
		Collections.sort(outs_ordered);
		
		for(int ix = 0; ix < num_out; ix++)
		{
			out_vals.add(this.output_nodes.get(outs_ordered.get(ix)).current_val);
		}
		return out_vals;
	}
	
	public void reset_vals()
	{
		HashMap<Long, NodeGene> all_nodes = this.get_all_nodes();
		
		for(NodeGene g : all_nodes.values())
		{
			g.current_val = 0.0;
		}
	}
	
	public void set_node(NodeGene node)
	{
		if(node.is_input == true)
		{
			this.input_nodes.put(node.inno_id, node);
			return;
		}
		if(node.is_output == true)
		{
			this.output_nodes.put(node.inno_id, node);
			return;
		}
		this.hidden_nodes.put(node.inno_id, node);
		return;
	}
	
	public void remove_conns_to_nulls()
	{
		
		HashMap<Long, NodeGene> all_nodes = this.get_all_nodes();
		
		for(NodeGene g_in : all_nodes.values()) {
			
			Iterator it = g_in.connections.entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<Long, ConnectionGene> entry = (Map.Entry<Long, ConnectionGene>)it.next();
				
				ConnectionGene cg = entry.getValue();
				
				if (g_in.is_input == true)
				{
					if (this.input_nodes.containsKey(cg.to_node) == false)
					{
						it.remove();
					}
				}
				else if (g_in.is_output == true)
				{
					if (this.output_nodes.containsKey(cg.to_node) == false)
					{
						it.remove();
						//hidden_conns.add(g_in.inno_id).connections.remove(cg.inno_id);
					}
				}
				else  if (g_in.is_input == false && g_in.is_output == false)
				{
					if(this.hidden_nodes.containsKey(cg.to_node) == false)
					{
						it.remove();
						//ouput_conns.add(g_in.inno_id).connections.remove(cg.inno_id);
					}	
				}
			}
		}
		return;
	}
	
	public long mutate_genome(long new_id, 
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
			//System.out.println("deleting node here");
			mutate_delete_node(inno_service);
		}
		if (rand.nextFloat() < (config.delete_conn_prob/prob_sum))
		{
			//System.out.println("deleting conn here");
			mutate_delete_conn(inno_service);
		}
		if (rand.nextFloat() < (config.add_conn_prob/prob_sum))
		{
			//System.out.println("adding conn here");
			mutate_add_conn(inno_service);
		}
		if (rand.nextFloat() < (config.add_node_prob/prob_sum))
		{
			//this is where we are loosing conn pointers from node genes
			//System.out.println("adding node here");
			mutate_add_node(inno_service, config.defaultActivation);
		}
		return inno_service.next_inno_id;
	}
	
	public void mutate_delete_node(InnovationService inno_service)
	{
		if(this.hidden_nodes.size() > 0)
		{
			int rand_index = this.get_random_in_range(this.hidden_nodes.size());
			long key = (long)this.hidden_nodes.keySet().toArray()[rand_index];
			this.hidden_nodes.remove(key);
			this.remove_conns_to_node(key, inno_service);
			return;	
		}
	}
	
	public void mutate_delete_conn(InnovationService inno_service)
	{
		ArrayList<Long[]> conn_ids = this.get_conn_ids();
		
		if(conn_ids.size() == 0)
		{
			return;
		}
		int rand_index = this.get_random_in_range(conn_ids.size());
		
		Long[] remove_ids = conn_ids.get(rand_index);
		
		this.get_node_by_id(remove_ids[1]).connections.remove(remove_ids[0]);
		
		return;
	}
	
	public void mutate_add_node(InnovationService inno_service, String default_activation)
	{
		boolean new_structure = false;
		
		ArrayList<Long[]> conn_ids = this.get_conn_ids();
		if(conn_ids.size() == 0)
		{
			return;
		}
		int rand_index = this.get_random_in_range(conn_ids.size());
		
		Long split_id = conn_ids.get(rand_index)[0];
		
		ConnectionGene split_conn = this.get_conn_with_node_and_conn_id(split_id, conn_ids.get(rand_index)[1]);
		
		Long inno_id = inno_service.path_exists(split_conn.from_node, split_conn.from_node);
		
		if(inno_id == null)
		{
			inno_id = inno_service.get_next_inno_id();
			new_structure = true;
		}
		NodeGene add_this = new NodeGene(inno_id);
		
		ConnectionGene conn_one = new ConnectionGene(split_conn.from_node, inno_id, inno_id + 1);
		
		this.get_node_by_id(split_conn.from_node).connections.put(conn_one.inno_id, conn_one);
		
		ConnectionGene conn_two = new ConnectionGene(inno_id, split_conn.to_node, inno_id +2);
		
		add_this.connections.put(conn_two.inno_id, conn_two);
		
		if(new_structure == true)
		{
			inno_service.node_ids.add(inno_id);
			inno_service.add_conn(conn_one.inno_id, conn_one.from_node, conn_one.to_node);
			inno_service.add_conn(conn_two.inno_id, conn_two.from_node, conn_two.to_node);
		}
		
		this.hidden_nodes.put(inno_id, add_this);
		
		return;
	}
	
	public void mutate_add_conn(InnovationService inno_service)
	{
		ArrayList<Long> all_node_ids = this.get_all_node_innos();
		
		long rand_from = all_node_ids.get(this.get_random_in_range(all_node_ids.size()));
		
		long rand_to = all_node_ids.get(this.get_random_in_range(all_node_ids.size()));
		
		if(rand_from == rand_to && this.is_recursive != true)
		{
			return;
		}
		
		this.make_conn(rand_from, rand_to, inno_service);
	}
	
	public void mutate_weights(double rate, double factor, double min, double max)
	{
		Random dice = new Random();
		
		for(NodeGene next : this.input_nodes.values())
		{
			for(ConnectionGene conn : next.connections.values())
			{
				if(dice.nextFloat() < rate)
				{
					Double weight_val = conn.atts.get("weight");
					
					Double current_plus_gauss = weight_val + (dice.nextGaussian() * factor);
					
					//TODO clamp the current plus gauss to config min and max
					Double clamped = Math.max(Math.min(current_plus_gauss, max), min);
					
					conn.atts.replace("weight", clamped);
				}
			}
		}
		
		for(NodeGene next : this.hidden_nodes.values())
		{
			for(ConnectionGene conn : next.connections.values())
			{
				if(dice.nextFloat() < rate)
				{
					Double weight_val = conn.atts.get("weight");
					
					Double current_plus_gauss = weight_val + (dice.nextGaussian() * factor);
					
					//TODO clamp the current plus gauss to config min and max
					Double clamped = Math.max(Math.min(current_plus_gauss, max), min);
					
					conn.atts.replace("weight", clamped);
				}
			}
		}
		
		for(NodeGene next : this.output_nodes.values())
		{
			for(ConnectionGene conn : next.connections.values())
			{
				if(dice.nextFloat() < rate)
				{
					Double weight_val = conn.atts.get("weight");
					
					Double current_plus_gauss = weight_val + (dice.nextGaussian() * factor);
					
					//TODO clamp the current plus gauss to config min and max
					Double clamped = Math.max(Math.min(current_plus_gauss, max), min);
					
					conn.atts.replace("weight", clamped);
				}
			}
		}
	}
	
	public double get_prime(int species_size)
	{
		return this.fitness/species_size;
	}
	
	public HashMap<Long, NodeGene> get_all_nodes()		
	{
		if (this.input_nodes == null)
		{
			System.out.println("null inputs wtf mate");
		}
		HashMap<Long, NodeGene> all_nodes = new HashMap<Long, NodeGene>(this.input_nodes);
		all_nodes.putAll(this.hidden_nodes);
		all_nodes.putAll(this.output_nodes);
		return all_nodes;
	}
	
	private void remove_conns_to_node(Long node_id, InnovationService inno)
	{
		HashMap<Long, NodeGene> all_nodes = this.get_all_nodes();
		for(NodeGene ng : all_nodes.values())
		{
			ng.connections.values().removeIf(e -> e.to_node == node_id);
		}
		return;
	}
	
	private ArrayList<Long> set_inputs(double[] inputs)
	{
		ArrayList<Long> sorted_keys = new ArrayList<Long>(this.input_nodes.keySet());
		Collections.sort(sorted_keys);
		int loop_count = sorted_keys.size();
		for(int i = 0; i < loop_count; i++)
		{
			this.input_nodes.get(sorted_keys.get(i)).current_val = inputs[i];
		}
		return sorted_keys;
	}
	
	private void make_conn(Long from_node_id, Long to_node_id, InnovationService inno_service)
	{
		Long conn_inno = inno_service.conn_exists(new Long[]{to_node_id, from_node_id});
		
		if(conn_inno == null)
		{
			conn_inno = inno_service.get_next_inno_id();
			inno_service.add_conn(conn_inno, from_node_id, to_node_id);
		}
		NodeGene from_node = this.get_node_by_id(from_node_id);
		
		from_node.connections.put(conn_inno, new ConnectionGene(from_node_id, to_node_id, conn_inno));
		
		return;
	}
	
	private ArrayList<Long> get_all_node_innos()
	{
		ArrayList<Long> returnThis = new ArrayList<Long>(this.input_nodes.keySet());
		
		returnThis.addAll(this.hidden_nodes.keySet());
		
		returnThis.addAll(this.output_nodes.keySet());
		
		return returnThis;
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
		if(this.input_nodes.keySet().contains(node_id))
		{
			return this.input_nodes.get(node_id).connections.get(conn_id);
		}
		
		if(this.hidden_nodes.keySet().contains(node_id))
		{
			return this.hidden_nodes.get(node_id).connections.get(conn_id);
		}
		
		if(this.output_nodes.keySet().contains(node_id))
		{
			return this.output_nodes.get(node_id).connections.get(conn_id);
		}
		
		return null;
	}
	
	public NodeGene get_node_by_id(Long inno_id)
	{
		if(this.input_nodes.containsKey(inno_id))
		{
			return this.input_nodes.get(inno_id);
		}
		
		if(this.hidden_nodes.containsKey(inno_id))
		{
			return this.hidden_nodes.get(inno_id);
		}
		
		if(this.output_nodes.containsKey(inno_id))
		{
			return this.output_nodes.get(inno_id);
		}
		
		return null;
	}
	
	public ArrayList<Long[]> get_conn_ids()
	{
		ArrayList<Long[]> ids = new ArrayList<Long[]>();
		
		for (Long x : this.input_nodes.keySet())
		{
			NodeGene current = this.input_nodes.get(x);
			
			for(long i : current.connections.keySet())
			{
				ids.add(new Long[] {current.connections.get(i).inno_id, current.inno_id});
			}
		}
		
		for (Long x : this.hidden_nodes.keySet())
		{
			NodeGene current = this.hidden_nodes.get(x);
			
			for(long i : current.connections.keySet())
			{
				ids.add(new Long[] {current.connections.get(i).inno_id, current.inno_id});
			}
		}
		
		for (Long x : this.output_nodes.keySet())
		{
			NodeGene current = this.output_nodes.get(x);
			
			for(long i : current.connections.keySet())
			{
				ids.add(new Long[] {current.connections.get(i).inno_id, current.inno_id});
			}
		}
		return ids;
	}
	
	public ArrayList<Long> get_conn_ids_simple()
	{
		ArrayList<Long> ids = new ArrayList<Long>();
		
		for (Long x : this.input_nodes.keySet())
		{
			NodeGene current = this.input_nodes.get(x);
			
			for(Long i : current.connections.keySet())
			{
				ids.add(current.connections.get(i).inno_id);
			}
		}
		
		for (Long x : this.hidden_nodes.keySet())
		{
			NodeGene current = this.hidden_nodes.get(x);
			
			for(Long i : current.connections.keySet())
			{
				ids.add(current.connections.get(i).inno_id);
			}
		}
		
		for (Long x : this.output_nodes.keySet())
		{
			NodeGene current = this.output_nodes.get(x);

			for(Long i : current.connections.keySet())
			{
				ids.add(current.connections.get(i).inno_id);
			}
		}
		return ids;
	}
	
	public void create_from_scratch(NeatConfig config, Long timestamp, InnovationService inno_service)
	{
		this.population_hash = timestamp;
		int num_in = config.num_input;
		int num_out = config.num_output;
		long inno = (long)0;
		for (int ix = 0; ix < num_in; ix++)
		{
			NodeGene new_node = new NodeGene(inno);
			new_node.is_input = true;
			new_node.is_output = false;
			this.input_nodes.put(inno, new_node);
			if(inno_service.node_ids.contains(inno) != true)
			{
				inno_service.node_ids.add(inno);
			}
			inno++;
		}
		for (int ix = 0; ix < num_out; ix++)
		{
			NodeGene new_node = new NodeGene(inno);
			new_node.is_input = false;
			new_node.is_output = true;
			this.output_nodes.put(inno, new_node);
			if(inno_service.node_ids.contains(inno) != true)
			{
				inno_service.node_ids.add(inno);
			}
			inno++;
		}
		this.connect_fully(inno_service, inno);
	}
	
	private void connect_fully(InnovationService inno_service, long inno_num)
	{
		int num_in = this.input_nodes.size();
		 
		for(Long node_key : this.input_nodes.keySet())
		{
			NodeGene from_node = this.input_nodes.get(node_key);
			
			int num_out = this.output_nodes.size();
			
			for(Long out_key : this.output_nodes.keySet())
			{
				ConnectionGene new_conn = new ConnectionGene(from_node.inno_id, out_key, inno_num);
				
				from_node.connections.put(inno_num, new_conn);
				
				if(inno_service.conn_coo.containsKey(inno_num) == false)
				{
					inno_service.add_conn(inno_num, from_node.inno_id, out_key);
				}
				inno_num++;
			}
		}
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
