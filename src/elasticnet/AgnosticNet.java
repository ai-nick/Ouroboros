package elasticnet;

public interface AgnosticNet {

	public void ingestGenome(Genome g);
	
	public Genome deriveGenome();
	
	public String toJson();
	
	public void setType(String t);
	
	public String getType();
}
