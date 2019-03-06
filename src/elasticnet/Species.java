package elasticnet;
import java.util.ArrayList;


public class Species {
	int speciesID;
	ArrayList<Integer> member_ids;
	int rep_id;
	int pop_id;
	double adjust_fit_sum = 0.0;;
	
	public Species(int id)
	{
		this.speciesID = id;
	}
	
	public void get_adjusted_fitness_sum(ArrayList<Genome> genomes)
	{
		for(int x = 0; x < genomes.size(); x++)
		{
			this.adjust_fit_sum += genomes.get(this.member_ids.get(x)).get_prime(this.member_ids.size());
		}
	}
	
	public double get_adjust_sum()
	{
		return this.adjust_fit_sum;
	}
}
