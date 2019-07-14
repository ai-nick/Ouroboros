package elasticnet;
import java.util.HashMap;

import com.google.gson.Gson;

import io.netty.util.internal.ThreadLocalRandom;

public class ConnectionGene implements IConnection {

	int inno_id;
	int from_node = -1;
	int to_node = -1;
	int activation_level = 0;
	int genome_id;
	HashMap<String, Double> atts = new HashMap<String, Double>();
	double min = -3.0;
	double max = 3.0;
	
	public ConnectionGene(int f, int t, int inno, int genome_id) {
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
		this.genome_id = genome_id;
		this.set_weight(ThreadLocalRandom.current().nextDouble(min, max));
	}
	
	public ConnectionGene(int f, int t, int inno, double weight) {
		this.inno_id = inno;
		this.from_node = f;
		this.to_node = t;
		this.set_weight(weight);
	}
	
	public ConnectionGene(int inno_id) {
		this.inno_id = inno_id;
	}
	
	public void set_weight(double w) {
		this.atts.put("weight", w);
	}
	
	public int get_id()
	{
		return inno_id;
	}
	public double get_weight() {
		return this.atts.get("weight");
	}
	public int get_from() {
		return this.from_node;
	}
	
	public int get_to() {
		return this.to_node;
	}
	
	public void set_from(int f) {
		this.from_node = f;
	}
	
	public void set_to(int t) {
		this.to_node = t;
	}
	
	public int getInnoId() {
		return this.inno_id;
	}
	
	public int get_next_node()
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
