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
	ArrayList<Integer> member_ids = new ArrayList<Integer>();	
	int rep_id;
	int pop_id;
	double adjust_fit_sum = 0.0;
	int num_genomes = 0;
	double best_fitness = 0.0;
	int best_genome_index;
	int[] sorted_idx_array;
	public Species(int id, int first_id)
	{
		this.speciesID = id;
		this.member_ids.add(first_id);
	}
	
	public double get_adjusted_fitness_sum(ArrayList<Genome> genomes)
	{
		this.sorted_idx_array = new int[this.member_ids.size()];
		
		HashMap<Integer, Double> fit_sort_dict = new HashMap<Integer, Double>();
		
		for(int x = 0; x < this.member_ids.size(); x++)
		{
			int member_id = this.member_ids.get(x);
			Genome fit_genome = genomes.get(member_id);
			this.adjust_fit_sum += fit_genome.get_prime(this.member_ids.size());
			fit_sort_dict.put(this.member_ids.get(x), fit_genome.fitness);
			this.sorted_idx_array[x] = this.member_ids.get(x);
		}
		if(this.member_ids.size() > 1)
		{
			this.quick_sort_big_dumb(this.sorted_idx_array, fit_sort_dict, 0, this.sorted_idx_array.length);	
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
		return this.member_ids.get(this.sorted_idx_array[0]);
	}
	
	public void have_mercy(int num_elites, ArrayList<Genome> genomes)
	{
		for(int i = num_elites-1; i < this.num_genomes; i++)
		{
			genomes.remove(i);
		}
	}
	
	//quick sort on fitness 
	public void quick_sort_big_dumb(int[] sort_array, HashMap<Integer, Double> sort_dict, int left, int right)
	{
		int left_start = left;
		int pivot = right;
		right--;
		while(left<right)
		{
			if(sort_dict.get(sort_array[left]) > sort_dict.get(sort_array[pivot]))
			{
				if(sort_dict.get(sort_array[right]) < sort_dict.get(sort_array[pivot]))
				{
					int t = sort_array[left];
					sort_array[left] = sort_array[right];
					sort_array[right] = t;
					right--;
					left++;
				}
				else
				{
					right--;
				}
			}
			else
			{
				if(sort_dict.get(sort_array[right]) < sort_dict.get(sort_array[pivot]))
				{
					left++;
				}
				else
				{
					left++;
					right--;
				}
			}
		}
		if(sort_dict.get(sort_array[left]) > sort_dict.get(sort_array[pivot]))
		{
			int t = sort_array[left];
			sort_array[left] = sort_array[pivot];
			sort_array[pivot] = t;
		}
		else
		{
			int t = sort_array[left+1];
			sort_array[left+1] = sort_array[pivot];
			sort_array[pivot] = t;
			left++;
		}
		if(left == right)
		{
			left++;
			right--;
		}
		if(right > left_start+1)
		{
			quick_sort_big_dumb(sort_array, sort_dict, left_start, right);	
		}
		if(left < pivot-1)
		{
			quick_sort_big_dumb(sort_array, sort_dict, left, pivot);	
		}
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
