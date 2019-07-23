package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;

import org.omg.CORBA.SystemException;

import com.google.gson.Gson;

public class NeuralNetwork implements INeuralNet {

	//no comment
	public int num_activations = 0;
	ArrayList<Integer> input_ids = new ArrayList<Integer>();
	ArrayList<NodeGene> activation_nodes = new ArrayList<NodeGene>();
	public boolean fully_activated;
	HashMap<Integer, NodeGene> nodes = new HashMap<Integer, NodeGene>();
	public boolean feed_forward;
	int num_output = 0;
	int outs_count = 0;
	ArrayList<Integer> output_ids = new ArrayList<Integer>();
	
	/*
	public NeuralNetwork(Genome g)
	{
		this.nodes = g.get_all_nodes();
		
		int num_in = g.input_nodes.size();
		
		for(int ix = 0; ix < num_in; ix++)
		{	
			int in_id = g.input_nodes.get(ix).inno_id;
			this.input_ids.add(in_id);
			this.nodes.put(in_id, g.input_nodes.get(ix));
		}
		
		this.num_output = g.output_nodes.size();
		
		for(int x = 0; x < this.num_output; x++)
		{
			this.output_ids.add(g.output_nodes.get(x).inno_id);
		}
		//NeuralNetworkSetup(g.hidden_nodes);
		
		int num_hidden = g.hidden_nodes.size();
		for(int ix = 0; ix < num_hidden; ix++)
		{
			nodes.put(g.hidden_nodes.get(ix).get_node_id(), g.hidden_nodes.get(ix));
		}
	}
	*/
	
	public NeuralNetwork(ArrayList<Integer> input_ids, HashMap<Integer, NodeGene> net_nodes)
	{
		this.input_ids = input_ids;
		this.nodes = net_nodes;
	}
	
	public NeuralNetwork(Genome genome_in,
			HashMap<Integer, HashMap<Integer, NodeGene>> node_genes
			)
	{
		this.fully_activated = false;
		
		this.input_ids = genome_in.input_nodes;
		
		ArrayList<Integer> all_ids = genome_in.get_all_nodes();
		
		int node_count = all_ids.size();
		
		for(int i = 0; i < node_count; i++)
		{
			int key = all_ids.get(i);
			
			this.nodes.put(key, node_genes.get(key).get(genome_in.id));
		}
		
		this.output_ids = genome_in.output_nodes;
		
		this.outs_count = this.output_ids.size();
	}
	
	@Override
	public void set_input(double[] input)
	{
		int number_inputs = this.input_ids.size();
		
		for(int ix = 0; ix < number_inputs; ix++)
		{
			NodeGene current = this.nodes.get(this.input_ids.get(ix));
			
			current.set_current_val(input[ix]);
			
			this.activation_nodes.add(current);
		}
	}
	
	@Override
	public void Activate() {
		//no comment
		ArrayList<NodeGene> next_actives = new ArrayList<NodeGene>();
		
		int loop_count = this.activation_nodes.size();
		
		for(int ix = 0; ix < loop_count; ix++)
		{
			NodeGene current = this.activation_nodes.get(ix);
			
			if (current.is_output() != true)
			{
				int num_connections = current.connections.size();
				
				// next node is set to null 
				for(int x = 0; x < num_connections; x++)
				{
					int next_node_id = current.get_connections().get(x).to_node;
					
					NodeGene next_node = this.nodes.get(next_node_id);
					
					next_node.add_to_current_value(current.get_current_val() * current.get_connections().get(x).get_weight());
					
					next_node.current_val = Activator.activate(next_node.activation, next_node.current_val);
					
					this.num_activations++;
					
					if(!next_actives.contains(next_node))
					{
						//System.out.println("wtf how");
						
						next_actives.add(next_node);
						
						if(next_node.is_output == true)
						{
							this.outs_count++;
							next_node.visits++;
						}
					}
				}
			}
		}

		this.activation_nodes = next_actives;
		this.num_activations++;
		if(this.activation_nodes.size() != 0)
		{
			this.Activate();
			return;
		}
		else
		{
			return;
		}
	}

	@Override
	public void Reset() {
		// TODO Auto-generated method stub
		for(Integer key : this.nodes.keySet())
		{
			this.nodes.get(key).current_val = 0.0;
		}
		this.outs_count = 0;
	}

	@Override
	public ArrayList<Double> get_output() {
		// TODO Auto-generated method stub
		ArrayList<Double> outs = new ArrayList<Double>();
		int loop_count = this.output_ids.size();
		for(int i = 0; i < loop_count; i++)
		{
			outs.add(this.nodes.get(this.output_ids.get(i)).current_val);
		}
		return outs;
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
