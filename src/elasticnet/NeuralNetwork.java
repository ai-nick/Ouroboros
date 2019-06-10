package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

public class NeuralNetwork implements INeuralNet {

	//no comment
	public int num_activations = 0;
	ArrayList<Integer> input_ids = new ArrayList<Integer>();
	ArrayList<INode> activation_nodes;
	public boolean fully_activated;
	HashMap<Integer, NodeGene> nodes = new HashMap<Integer, NodeGene>();
	public boolean feed_forward;
	int num_output = 0;
	int outs_count = 0;
	
	
	public NeuralNetwork(Genome g)
	{
		this.nodes = g.get_all_nodes();
		
		int num_in = g.input_nodes.size();
		
		for(int ix = 0; ix < num_in; ix++)
		{
			this.input_ids.add(g.input_nodes.get(ix).inno_id);
		}
		
		this.num_output = g.output_nodes.size();
		
		//NeuralNetworkSetup(g.hidden_nodes);
		
		int num_hidden = g.hidden_nodes.size();
		
		for(int ix = 0; ix < num_hidden; ix++)
		{
			nodes.put(g.hidden_nodes.get(ix).get_node_id(), g.hidden_nodes.get(ix));
		}
	}
	
	
	@Override
	public void set_input(double[] input)
	{
		int number_inputs = this.input_ids.size();
		for(int ix = 0; ix < number_inputs; ix++)
		{
			INode current = this.nodes.get(this.input_ids.get(ix));
			current.set_current_val(input[ix]);
			this.activation_nodes.add(current);
		}
	}
	
	@Override
	public void Activate() {
		//no comment
		ArrayList<INode> next_actives = new ArrayList<INode>();
		int loop_count = this.activation_nodes.size();
		for(int ix = 0; ix < loop_count; ix++)
		{
			INode current = this.activation_nodes.get(ix);
			current.activate();
			if (current.is_output() != true)
			{
				int num_connections = current.get_connections().size();
				for(int x = 0; ix < num_connections; x++)
				{
					INode next_node = current.get_connections().get(x).get_next_node();
					next_node.add_to_current_value(current.get_current_val() * current.get_connections().get(x).get_weight());
					if(!next_actives.contains(next_node))
					{
						next_actives.add(next_node);	
					}
				}
			}
			else
			{
				this.outs_count++;
			}
		}
		if(this.outs_count == this.num_output)
		{
			this.fully_activated = true;
			this.activation_nodes = next_actives;
			return;
		}
		else
		{
			this.activation_nodes = next_actives;
			this.num_activations++;
			if(this.feed_forward == true)
			{
				this.Activate();				
			}
			else
			{
				return;
			}
		}
	}

	@Override
	public void Reset() {
		// TODO Auto-generated method stub
		for(int key : this.nodes.keySet())
		{
			this.nodes.get(key).set_current_val(0.0);
		}
		this.outs_count = 0;
	}

	@Override
	public ArrayList<INode> get_output() {
		// TODO Auto-generated method stub
		if(this.fully_activated)
		{
			return this.activation_nodes;
		}
		else
		{
			return new ArrayList<INode>();
		}
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
