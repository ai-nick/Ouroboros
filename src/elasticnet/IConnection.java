package elasticnet;

public interface IConnection {
	int get_id();
	double get_weight();
	void set_weight(double w);
	NodeGene get_next_node();
}
