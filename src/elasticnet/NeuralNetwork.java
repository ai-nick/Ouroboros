package elasticnet;

import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork implements INeuralNet {

	public int current_layer = 0;
	public int num_activations = 0;
	ArrayList<INode> in_nodes;
	ArrayList<INode> out_nodes;
	ArrayList<INode> hidden_nodes;
	
	public NeuralNetwork(ArrayList<INode> i_nodes, ArrayList<INode> o_nodes, ArrayList<INode> h_nodes)
	{
		this.in_nodes = i_nodes;
		this.out_nodes = o_nodes;
		this.hidden_nodes = h_nodes;
	}
	
	@Override
	public void Activate() {
		// TODO Auto-generated method stub
		
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
