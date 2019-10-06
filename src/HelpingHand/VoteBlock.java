package HelpingHand;

public class VoteBlock {
	public String node_id = "";
	public String population_hash;
	public String vote_value;
	
	public VoteBlock(String n_hash, String p_hash, String v)
	{
		this.node_id = n_hash;
		this.population_hash = p_hash;
		this.vote_value = v;
	}
}
