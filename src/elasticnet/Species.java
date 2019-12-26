package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

// keeping this lightweight, only storing indexes into the "population"
// array list of genomes
public class Species {
	int speciesID;
	public ArrayList<Long> member_ids = new ArrayList<Long>();	
	int rep_id;
	int pop_id;
	double adjust_fit_sum = 0.0;
	double best_fitness = 0.0;
	int best_genome_index;
	int[] sorted_idx_array;
	public Species(int id, long first_id)
	{
		this.speciesID = id;
		this.member_ids.add(first_id);
	}
	
	public double get_adjusted_fitness_sum(HashMap<Long,BaseGenome> genomes, SorterUtil sorter_util)
	{
		//System.out.println(this.member_ids);
		this.sorted_idx_array = new int[this.member_ids.size()];
		
		HashMap<Integer, Double> fit_sort_dict = new HashMap<Integer, Double>();
		
		for(int x = 0; x < this.member_ids.size(); x++)
		{
			// suspect we are getting null genomes here, need to check sorting isnt
			// modifying genome id values
			long member_id = this.member_ids.get(x);
			Genome fit_genome = genomes.get(member_id);
			if(fit_genome == null)
			{
				System.out.println("null genome");
			}
			this.adjust_fit_sum += fit_genome.get_prime(this.member_ids.size());
			fit_sort_dict.put(x, fit_genome.fitness);
			this.sorted_idx_array[x] = x;
		}
		if(this.member_ids.size() > 1)
		{
			sorter_util.quick_sort_big_dumb(this.sorted_idx_array, fit_sort_dict, 0, this.sorted_idx_array.length-1);
			//System.out.println(this.sorted_idx_array);
		}
		return this.adjust_fit_sum;
	}
	
	public void add_genome(Long genomeId, double fitness)
	{
		this.member_ids.add(genomeId);
	}
	
	public long get_best_genome_idx()
	{
		return this.member_ids.get(this.sorted_idx_array[this.sorted_idx_array.length - 1]);
	}
	
	public void have_mercy(int num_elites, 
			HashMap<Integer, Genome> genomes, 
			HashMap<Integer, HashMap<Integer, ConnectionGene>> pop_conns, 
			HashMap<Integer, HashMap<Integer, NodeGene>> pop_nodes)
	{
		int num_members = this.sorted_idx_array.length;
		if (num_elites == 0)
		{
			return;
		}
		for(int x = 0; x < num_elites; x++)
		{
			long g_id = this.member_ids.get(this.sorted_idx_array[x]);
			//System.out.println(g_id);
			Genome removing = genomes.get(g_id);
			
			removing.remove_genes_from_pop(pop_nodes, pop_conns);
			
			genomes.remove(g_id);
		}
		ArrayList<Long> new_member_ids = new ArrayList<Long>();
		for(int x = 0; x < num_elites; x++)
		{
			new_member_ids.add(this.member_ids.get(this.sorted_idx_array[(num_members-1)-x]));
		}
		this.member_ids = new_member_ids;
	}
	
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
