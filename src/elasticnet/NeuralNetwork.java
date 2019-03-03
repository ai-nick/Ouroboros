package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork implements INeuralNet {

	public int current_layer = 0;
	public int num_activations = 0;
	ArrayList<Integer> input_ids;
	ArrayList<INode> activation_nodes;
	public boolean fully_activated;
	HashMap<Integer, INode> nodes = new HashMap<Integer, INode>();
	public boolean is_recurrent;
	
	public NeuralNetwork(ArrayList<Integer> in_nodes_ids, ArrayList<INode> nodes_incoming)
	{
		this.input_ids = in_nodes_ids;
		for(int ix = 0; ix < nodes_incoming.size(); ix++)
		{
			nodes.put(nodes_incoming.get(ix).get_node_id(), nodes_incoming.get(ix));
		}
	}
	
	@Override
	public void Activate() {
		// inputs needs to be same length as 
		ArrayList<INode> next_actives = new ArrayList<INode>();
		int outs_count = 0;
		for(int ix = 0; ix < next_actives.size(); ix++)
		{
			INode current = this.activation_nodes.get(ix);
			current.activate();
			if (current.is_output() != true)
			{
				for(int x = 0; ix < current.get_connections().size(); x++)
				{
					INode next_node = current.get_connections().get(x).get_next_node();
					next_node.set_current_val(current.get_current_val() * current.get_connections().get(x).get_weight());
					if(!next_actives.contains(next_node))
					{
						next_actives.add(next_node);	
					}
				}	
			}
			else
			{
				outs_count++;
			}
		}
		if(outs_count == this.activation_nodes.size())
		{
			this.activation_nodes = next_actives;
			return;
		}
		else
		{
			this.activation_nodes = next_actives;
			this.Activate();
		}
	}

	public void Set_Input_Values(ArrayList<Double> inputs)
	{
		for(int ix = 0; ix < this.activation_nodes.size(); ix++)
		{
			INode current = this.nodes.get(this.input_ids.get(ix));
			current.set_current_val(inputs.get(ix));
		}
	}
	@Override
	public void Reset() {
		// TODO Auto-generated method stub
		for(int key : this.nodes.keySet())
		{
			this.nodes.get(key).set_current_val(0.0);
		}
	}

	@Override
	public ArrayList<Inode> get_output() {
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

}
