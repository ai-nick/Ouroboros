package elasticnet;
import java.util.ArrayList;


public class Species {
	int speciesID;
	ArrayList<Integer> member_ids;
	int rep_id;
	int pop_id;
	double adjust_fit_sum = 0.0;
	int num_genomes = 0;
	
	public Species(int id)
	{
		this.speciesID = id;
	}
	
	public double get_adjusted_fitness_sum(ArrayList<Genome> genomes)
	{
		for(int x = 0; x < genomes.size(); x++)
		{
			this.adjust_fit_sum += genomes.get(this.member_ids.get(x)).get_prime(this.member_ids.size());
		}
		return this.adjust_fit_sum;
	}
	
	public void add_genome(int genomeId)
	{
		this.member_ids.add(genomeId);
		this.num_genomes++;
	}
	
	public int get_num_genomes()
	{
		return this.num_genomes;
	}
	
}
