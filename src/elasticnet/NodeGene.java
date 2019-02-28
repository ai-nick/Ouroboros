package elasticnet;
import java.util.List;

public class NodeGene {

	int level = 0;
	String activation = "";
	int genome_id;
	int inno_id;
	List<Double> coordinate;
	boolean is_recurrent;
	
	public NodeGene(int inno_id, String act) {
		this.inno_id = inno_id;
		this.activation = act;
	}
	
	public String get_activation()
	{
		return this.activation;
	}
	
	public int get_level()
	{
		return this.level;
	}
	
	public List<Double> get_coord()
	{
		return this.coordinate;
	}
	
	public boolean get_is_recurrent()
	{
		return this.is_recurrent;
	}
}
