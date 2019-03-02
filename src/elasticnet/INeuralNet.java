package elasticnet;

import java.util.ArrayList;

public interface INeuralNet {

	public void Activate(ArrayList<Double> inputs);
	public void Activate();
	public void Reset();
	public void get_output();

}
