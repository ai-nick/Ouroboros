package elasticnet;
import java.util.ArrayList;

// keeping this lightweight, only storing indexes into the "population"
// array list of genomes
public class Species {
	int speciesID;
	ArrayList<Integer> member_ids;
	int rep_id;
	int pop_id;
	double adjust_fit_sum = 0.0;
	int num_genomes = 0;
	double best_fitness = 0.0;
	int best_genome_index;
	
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
	
	public void add_genome(int genomeId, double fitness)
	{
		this.member_ids.add(genomeId);
		this.num_genomes++;
	}
	
	public int get_best_genome_idx()
	{
		return 0;
	}
	
	public void have_mercy(int num_elites, ArrayList<Genome> genomes)
	{
		
	}
	
	public Genome mutate_genome(Genome g)
	{
		Genome new_g = clone(g);
	}
}
