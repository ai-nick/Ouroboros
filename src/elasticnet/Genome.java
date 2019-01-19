package elasticnet;
import java.util.HashMap;
public class Genome {

	int gen_born;
	int population_hash;
	String nodeGeneType;
	String connectionGeneType;
	Genotype[] gTypes;
	
	public Genome(int p_hash) {
		this.nodeGeneType = "default";
		this.connectionGeneType = "default";
		this.population_hash = p_hash;
	}
	
	public Genome(Genotype[] gs) {
		this.gTypes = gs;
	}
	public void ingestConfig(HashMap<String, String> config) {
		this.nodeGeneType = config.get("nGeneType");
		this.connectionGeneType = config.get("cGeneType");
	}	
}
