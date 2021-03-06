package elasticnet;
import java.util.HashMap;

import com.google.gson.Gson;

import io.netty.util.internal.ThreadLocalRandom;

public class ConnectionGene implements IConnection {

	public Long inno_id;
	Long from_node;
	Long to_node;
	int activation_level = 0;
	//int genome_id;
	HashMap<String, Double> atts;
	double min = -3.0;
	double max = 3.0;
	
	public ConnectionGene(Long f, Long t, Long inno) {
		this.atts = new HashMap<String, Double>();
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
		//this.genome_id = genome_id;
		this.set_weight(ThreadLocalRandom.current().nextDouble(min, max));
	}
	
	public ConnectionGene(long f, long t, long inno, double weight) {
		this.atts = new HashMap<String, Double>();
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
		this.set_weight(weight);
	}
	
	public ConnectionGene(ConnectionGene to_clone)
	{
		this.inno_id = to_clone.inno_id;
		this.to_node = to_clone.to_node;
		this.from_node = to_clone.from_node;
		this.activation_level = to_clone.activation_level;
		this.atts = new HashMap<String, Double>(to_clone.atts);
	}
	
	public void set_weight(double w) {
		this.atts.put("weight", w);
	}
	
	public long get_id()
	{
		return inno_id;
	}
	public double get_weight() {
		return this.atts.get("weight");
	}
	public long get_from() {
		return this.from_node;
	}
	
	public long get_to() {
		return this.to_node;
	}
	
	public void set_from(long f) {
		this.from_node = f;
	}
	
	public void set_to(long t) {
		this.to_node = t;
	}
	
	public long get_next_node()
	{
		return to_node;
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
