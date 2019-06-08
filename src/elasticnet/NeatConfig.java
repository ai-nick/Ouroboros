package elasticnet;


// oi at a neat config ye ot theh
// ew ot ay loiscence fo at? 
public class NeatConfig {
	
	String defaultActivation = "sigmoid";
	
	int num_inputs = 0;
	
	int num_output = 0;
	
	int num_hidden = 0;
	
	Double elitism = 0.89;
	
	Double add_conn_prob = .15;
	Double add_node_prob = .15;
	Double delete_conn_prob = .1;
	Double delete_node_prob = .1;
	Double compat_threshold = 3.0;
	
	//mins and maxs
	
	Double weight_min = -30.0;
	Double weight_max = 30.0;
	
	Double response_min = -30.0;
	Double response_max = 30.0;
	
	Double bias_min = -30.0;
	Double bias_max = 30.0;
}
