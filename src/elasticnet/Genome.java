package elasticnet;
import java.util.HashMap;
public class Genome {

	NodeGene[] nGenes = {};
	ConnectionGene[] cGenes = {};
	public int level;
	int gen_born;
	int species_id;
	String population_hash;
	String nodeGeneType;
	String connectionGeneType;
	
	public Genome(String p_hash) {
		this.level = 0;
		this.species_id = 0;
		this.nodeGeneType = "default";
		this.connectionGeneType = "default";
		this.population_hash = p_hash;
	}
	
	public Genome(String p_hash, int level, int species_id) {
		this.level = level;
		this.species_id = species_id;
		this.population_hash = p_hash;
	}
	
	public Genome(String p_hash, int level, int species_id, NodeGene[] nG, ConnectionGene[] cG) {
		this.level = level;
		this.species_id = species_id;
		this.nGenes = nG;
		this.cGenes = cG;
		this.population_hash = p_hash;
	}
	
	public void ingestConfig(HashMap<String, String> config) {
		this.nodeGeneType = config.get("nGeneType");
		this.connectionGeneType = config.get("cGeneType");
	}
	
	public Integer[] retrieveGlobalInnoNums() {
		Integer[] nums = {};
		for(int inno_id = 0; inno_id < this.cGenes.length; inno_id++) {
			nums[inno_id] = this.cGenes[inno_id].getInnoId();
		}
		return nums;
	}
	
	public NodeGene getNodeGeneAtIndex(int idx) {
		return this.nGenes[idx];
	}
	
	public ConnectionGene getConnectionGeneAtIndex(int idx) {
		return this.cGenes[idx];
	}
	
}
