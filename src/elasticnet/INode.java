package elasticnet;

import java.util.List;

public interface INode {
	public String get_activation();
	public int get_level();
	public List<Double> get_coord();
	public boolean get_is_recurrent();
}
