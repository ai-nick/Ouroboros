package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork implements INeuralNet {

	public int current_layer = 0;
	public int num_activations = 0;
	ArrayList<Integer> input_ids;
	HashMap<Integer, INode> nodes = new HashMap<Integer, INode>();
	
	public NeuralNetwork(ArrayList<Integer> in_nodes_ids, ArrayList<INode> nodes_incoming)
	{
		this.input_ids = in_nodes_ids;
		for(int ix = 0; ix < nodes_incoming.size(); ix++)
		{
			nodes.put(nodes_incoming.get(ix).get_node_id(), nodes_incoming.get(ix));
		}
	}
	
	@Override
	public void Activate(ArrayList<Double> inputs) {
		// inputs needs to be same length as 
		for(int ix = 0; ix < input_ids.size(); ix++)
		{
			INode current = nodes.get(input_ids.get(ix));
			current.set_current_val(inputs.get(ix));
			for(int x = 0; ix < current.get_connections().size(); x++)
			{
				
			}
		}
		
	}
	
	@Override
	public void Activate()
	{
		
	}

	@Override
	public void Reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void get_output() {
		// TODO Auto-generated method stub

	}

}
