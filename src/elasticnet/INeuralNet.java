package elasticnet;

import java.util.ArrayList;

public interface INeuralNet {
	public void Activate();
	public void Reset();
	public ArrayList<INode> get_output();
}
