package HelpingHand;

public class FitnessBlock {

	public String population_hash = "";
	public String node_id = "";
	public long genome_id;
	public double fitness;
	public boolean been_verified = false;
	
	public FitnessBlock(String p_hash, String node_id, long g_id, double ft, boolean bv)
	{
		this.population_hash = p_hash;
		this.node_id = node_id;
		this.genome_id = g_id;
		this.fitness = ft;
		this.been_verified = bv;
	}
}
