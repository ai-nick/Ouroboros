package elasticnet;

import java.util.ArrayList;

public interface INeuralNet {
	public void set_input(double[] input);
	public void Activate();
	public void Reset();
	public ArrayList<INode> get_output();
}
