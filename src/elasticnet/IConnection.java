package elasticnet;

public interface IConnection {
	long get_id();
	double get_weight();
	void set_weight(double w);
	long get_next_node();
}
