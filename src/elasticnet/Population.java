package elasticnet;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Population {
	
	ArrayList<Genome> genomes = new ArrayList<Genome>();
	int num_genomes;
	int num_gens;
	int hash_id;
	int generations = 0;
	String fitness_function = "";
	String ts = "";
	int current_gen = 0;
	int inno_num = 0;
	ArrayList<Species> pop_species = new ArrayList<Species>();
	HashMap<String, String> config;
	
	public Population() 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
	}
	
	public Population(int num_genomes, int gens, HashMap<String, String> config_in) 
	{
		this.num_genomes = num_genomes;
		this.num_gens = gens;
		this.config = config_in;
	}
	
	public double compat_distance(Genome one, Genome two, Double[] speciation_coefficients) {
		double w = (one.avg_w + two.avg_w) / 2;
		double s = 0.0;
		int e = 0,d = 0;
		int[] j = null;
		for(int idx = 0; idx < one.gene_ids.length; idx++)
		{
			if(Arrays.asList(two).contains(one.gene_ids))
			{
				j[idx] = one.gene_ids[idx];
			}
			else
			{
				if(one.gene_ids[idx] >= two.gene_id_min && one.gene_ids[idx] <= two.gene_id_max)
				{
					d += 1;
				}
				else
				{
					e += 1;
				}
			}
		}
		for(int ix = 0; ix < two.gene_ids.length; ix++)
		{
			if(!Arrays.asList(j).contains(two.gene_ids[ix]))
			{
				if(two.gene_ids[ix] >= one.gene_id_min && two.gene_ids[ix] <= one.gene_id_max)
				{
					d += 1;
				}
				else
				{
					e += 1;
				}
			}
		}
		s += e*speciation_coefficients[0]/10;
		s += d*speciation_coefficients[1]/10;
		s += w*speciation_coefficients[2];
		one.fit_dists.put(two.id, s);
		return s;
	}
	
	public void speciate_population()
	{
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		
		double compat_t = Double.parseDouble(this.config.get("compatability_threshold"));
		
		if (this.pop_species.size() == 0)
		{
			Random rnd = new Random();
			int first_rep_index = rnd.nextInt(this.genomes.size());
			Genome first_rep = this.genomes.get(first_rep_index);
			this.pop_species.add(new Species(first_rep.id));
			speciated.add(first_rep_index);
		}
		for(int x = 0; x < this.num_genomes; x++)
		{
			boolean species_found = false;
			//check if its first species rep_index, not elegant but have to handle
			if(!speciated.contains(x)) 
			{
				Genome current_genome = this.genomes.get(x);
				int num_species = this.pop_species.size();
				for(int i = 0; i < num_species; i++)
				{
					Double dist = this.compat_distance(this.genomes.get(this.pop_species.get(i).rep_id), 
							current_genome,
							new Double[3]);
					if( dist < compat_t)
					{
						this.pop_species.get(i).member_ids.add(current_genome.id);
						speciated.add(current_genome.id);
					}
				}
				if(!speciated.contains(current_genome.id))
				{
					this.pop_species.add(new Species(current_genome.id));
					speciated.add(current_genome.id);
				}
			}
		}
	}
	
	public void set_config(String conf)
	{
		Gson g = new Gson();
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		this.config = g.fromJson(g.toJson(conf), type);
	}
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
