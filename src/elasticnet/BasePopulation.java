package elasticnet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BasePopulation {
	Long best_BaseGenome_id;
	Integer num_BaseGenomes;
	int min_species_size = 5;
	String fitness_function = "";
	Long ts;
	Integer current_gen = 0;
	Long inno_num;
	Long next_BaseGenome_id;
	Long next_species_id;
	ArrayList<Species> pop_species = new ArrayList<Species>();
	NeatConfig config;
	Integer pop_size = 0;
	InnovationService innovation = new InnovationService();
	public HashMap<Long, BaseGenome> BaseGenomes = new HashMap<Long, BaseGenome>();
	public SorterUtil sorter = new SorterUtil();
	public boolean is_p2p;
	
	public BasePopulation(int gen,  NeatConfig config_in, int pop_size, boolean is_p2p) 
	{
		this.is_p2p = is_p2p;
		this.ts = System.currentTimeMillis();
		this.pop_size = pop_size;
		this.config = config_in;
		System.out.print("num of initial genes: ");
		System.out.println(this.inno_num);
		this.current_gen = gen;
		if (this.current_gen == 0) {
			this.set_up_first_pop();
		}
	}
	
	public BasePopulation(int gen, NeatConfig config_in, int num_inputs, int num_outputs, int pop_size, boolean is_p2p)
	{
		this.is_p2p = is_p2p;
		this.ts = System.currentTimeMillis();
		this.pop_size = pop_size;
		this.config = config_in;
		System.out.print("num of initial genes: ");
		System.out.println(this.inno_num);
		this.current_gen = gen;
		if(this.current_gen == 0)
		{
			this.set_up_first_pop();
		}
	}
	
	public ArrayList<Species> get_species()
	{
		return this.pop_species;
	}
	
	public void set_best_BaseGenome_id(long id)
	{
		this.best_BaseGenome_id = id;
	}
	
	public long get_best_BaseGenome_id()
	{
		return this.best_BaseGenome_id;
	}
	
	public void set_up_first_pop()
	{
		// if no BaseGenomes are loaded we will start by creating a fresh BasePopulation
		if(pop_size != 0)
		{
			for (int ix = 0; ix < this.pop_size; ix++)
			{
				BaseGenome gBaby = new BaseGenome(ix);
				
				//System.out.print("setting up BaseGenome: ");
				//System.out.println(ix);
				
				gBaby.create_from_scratch(this.config, this.ts, this.innovation);
				
				this.BaseGenomes.put((long)ix,gBaby);
			}
			this.num_BaseGenomes = pop_size;
			
			this.next_BaseGenome_id = (long)this.pop_size;
		}
	}
	
	public double compat_distance(BaseGenome one, BaseGenome two, double[] speciation_coefficients) {
		double w = 0.0;
		double d = 0.0;
		double s = 0.0;
		double e = 0.0;
		int match_count = 0;
		//TODO check conn arrays are not empty
		ArrayList<Long> one_conns = one.get_conn_ids_simple();
		ArrayList<Long> two_conns = two.get_conn_ids_simple();
		int loop_count = one_conns.size();
		//int[] j = new int[loop_count];
		for(int idx = 0; idx < loop_count; idx++)
		{
			//looping through genes
			//and check if we can 
			long one_id = one_conns.get(idx);
			if(two_conns.contains(one_id))
			{
				// any conn inno should have the same from connection across all genomes
				Long[] gene_from_coo = this.innovation.conn_coo.get(one_id);
				ConnectionGene one_gene = one.get_node_by_id(gene_from_coo[0]).connections.get(one_id);
				//w = Math.abs(this.connection_genes.get(one_id).get(one.id).atts.get("weight") - this.connection_genes.get(two_id).get(two.id).atts.get("weight"));
				w = one.
				match_count++;
			}
			else
			{
				if(two_conns.isEmpty() == false)
				{
					if(one_id >= Collections.min(two_conns) && one_id <= Collections.max(two_conns))
					{
						d += 1.0;
					}
					else
					{
						e += 1.0;
					}	
				}
			}
		}
		
		loop_count = two_conns.size();
		
		for(int ix = 0; ix < loop_count; ix++)
		{
			//same loop as before but for the second BaseGenome
			//no need to check if the other contains it 
			//because if it did its already been addressed
			if(one_conns.contains(two_conns.get(ix)) == false)
			{
				if(two_conns.get(ix) >= Collections.min(one_conns) && two_conns.get(ix) <= Collections.max(one_conns))
				{
					d += 1.0;
				}
				else
				{
					e += 1.0;
				}	
			}
		}
		if(one_conns.size() > two_conns.size()) {
			loop_count = one_conns.size();
		}
		loop_count = one_conns.size();
		s += (e*speciation_coefficients[0])/loop_count;
		s += (d*speciation_coefficients[1])/loop_count;
		s += (w*speciation_coefficients[2])/match_count;
		one.fit_dists.put(two.id, s);
		return s;
	}
	
	
	// split BaseGenomes into species using compat dists
	
	public void speciate_BasePopulation()
	{
		// initialize array of speciated BaseGenome ids that have
		// are part of a species
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		//this.pop_species = new ArrayList<Species>();
		// get the compat distance from config
		double compat_t = this.config.compat_threshold;
		// coeeficients 
		double[] speciation_coeff = { 1.0, 1.0, .5 };
		// set first species and rep BaseGenome
		if (this.pop_species.size() == 0)
		{
			// grabs a random BaseGenome from our set
			BaseGenome first_rep = this.BaseGenomes.get(this.BaseGenomes.keySet().iterator().next());
			this.pop_species.add(new Species(next_species_id, first_rep.id));
			next_species_id++;
			speciated.add(first_rep.id);
		}
		// we have atleast one species by this point in our control flow
		for(int x : this.BaseGenomes.keySet())
		{
			// we check if this id already belongs to a species from previous generations
			// TODO check that speciated isnt cleared, otherwise this doesnt make sense
			if(!speciated.contains(this.BaseGenomes.get(x).id)) 
			{
				// finna track if it has a species yet tehe
				boolean species_found = false;
				
				BaseGenome current_BaseGenome = this.BaseGenomes.get(x);
				// we should always have atleast one species here (see line ~197)
				int num_species = this.pop_species.size();
				//this should be while loop, with a conditional of count == num_species - 1
				//
				for(int i = 0; i < num_species; i++)
				{
					//check if we added it to one already, could be better and 
					//break loop once on is found, TODO check our compat dist calculation
					//as we are seeing very high distances, THIS IS A CONCERN
					if(!species_found)
					{
						Double dist = this.compat_distance(this.BaseGenomes.get(this.pop_species.get(i).rep_id), 
								current_BaseGenome,
								speciation_coeff);
						//System.out.println(dist);
						if( dist < compat_t)
						{
							this.pop_species.get(i).member_ids.add(current_BaseGenome.id);
							speciated.add(current_BaseGenome.id);
							species_found = true;
							//found species break our loop
							break;
						}	
					}
				}
				if(!speciated.contains(current_BaseGenome.id))
				{
					this.pop_species.add(new Species(next_species_id, current_BaseGenome.id));
					next_species_id++;
					speciated.add(current_BaseGenome.id);
					species_found = true;
				}
			}
		}
	}
	
	
	public void new_reproduction()
	{
		HashMap<Integer, Double> adj_fit_sums = new HashMap<Integer, Double>();
		int num_species = this.pop_species.size();
		double elitism_percent = this.config.elitism;
		int[] sorted_species_ids = new int[num_species];
		int saved_sum = 0;
		int keep_top;
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(current.speciesID, current.get_adjusted_fitness_sum(this.BaseGenomes, this.sorter));
			sorted_species_ids[x] = current.speciesID;
		}
		sorter.quick_sort_big_dumb(sorted_species_ids, adj_fit_sums, 0, num_species-1);
		//System.out.println(num_species);
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(sorted_species_ids[x]);
			keep_top = (int)((double)current.member_ids.size() * elitism_percent);
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				//reduce the species to only the elite BaseGenomes
				current.have_mercy(keep_top, this.BaseGenomes, this.connection_genes, this.node_genes);
				//breed_all_remaining(current);				
			}
			else
			{
				saved_sum += current.member_ids.size();
			}
		}
		num_species = this.pop_species.size();
		int need_new = this.pop_size - saved_sum;
		System.out.print("size after elitism: ");
		System.out.println(this.BaseGenomes.size());
		while(need_new != 0)
		{
			for(int ix = 0; ix < num_species; ix++)
			{
				Species current_species = this.pop_species.get(sorted_species_ids[ix]);

				int spec_size = current_species.sorted_idx_array.length;
				// if there are only two BaseGenomes in the species bread them asexually
				// if we have room after the first reproduction loop we will 
				// rebread sexually between the 4 species produced in this loop
				if(spec_size > 2)
				{
					// if our species has enough BaseGenomes to breed with sexy times
					// we will do every combination of mates
					for (int a = 0; a < spec_size; a++)
					{
						BaseGenome a_BaseGenome = this.BaseGenomes.get(current_species.member_ids.get(current_species.sorted_idx_array[a]));
						this.breed_asexual(a_BaseGenome, current_species);
						need_new--;
						/*
						for(int b = 0; b < spec_size; b++)
						{
							if(b != a)
							{
								BaseGenome b_BaseGenome = this.BaseGenomes.get(current_species.member_ids.get(current_species.sorted_idx_array[b]));
								
								this.cross_breed(a_BaseGenome, b_BaseGenome, current_species);
								need_new--;
							}
						}
						*/
					}
				}
				else
				{
					//TODO figure out if i should be passing species to cross breed in 
					// the sexual reproduction above as i do below in the asexual
					// breeding method
					BaseGenome asex_BaseGenome = this.BaseGenomes.get(current_species.member_ids.get(0));
					this.breed_asexual(asex_BaseGenome, current_species);
					need_new--;
				}
				if (need_new == 0)
				{
					break;
				}
			}
		}
	}
	
	// determine the number of BaseGenomes each species should reproduce
	// TODO need to hash out how to let species grow, target the desired pop_size
	public void the_reproduction_function()
	{
		// dictionary with fitness sums for each species 
		// TODO sort this shit
		while(this.inno_num <= Collections.max(this.connection_genes.keySet()) || this.inno_num <= Collections.max(this.node_genes.keySet()))
		{
			this.inno_num++;
		}
		HashMap<Integer, Double> adj_fit_sums = new HashMap<Integer, Double>();
		int num_species = this.pop_species.size();
		double elitism_percent = this.config.elitism;
		Integer[] sorted_species_idxs = new Integer[num_species];
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(x, current.get_adjusted_fitness_sum(this.BaseGenomes, this.sorter));
			sorted_species_idxs[x] = x;
		}
		if(sorted_species_idxs.length > 1)
		{
			sorter.quick_sort_big_dumb(sorted_species_idxs, adj_fit_sums, 0, num_species-1);	
		}
		else
		{
			System.out.println("only one species check config");
		}
		Integer saved_sum = 0;
		Integer keep_top = (int)((double)num_BaseGenomes * elitism_percent);
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		//TODO run quick sort on the species ids and the adj_fit_sums hashmap
		// then use x to scale out species size with any species that is in the lower
		// half going extinct
		for(int x = 0; x < num_species; x++)
		{
			// need index here not id, was probably fucking a bunch of stuff up
			Species current = this.pop_species.get(sorted_species_idxs[x]);
			keep_top = current.member_ids.size() * (int)elitism_percent;
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				System.out.print("running elitism for species: ");
				System.out.println(current.speciesID);
				//reduce the species to only the elite BaseGenomes
				current.have_mercy(keep_top, this.BaseGenomes, this.connection_genes, this.node_genes);
				//breed_all_remaining(current);				
			}
		}
		// now we handle reproducing the correct amount of BaseGenomes
		int need_new = this.pop_size - saved_sum;
		int elite_iterator = 0;
		while(need_new != 0)
		{
			for(int ix = 0; ix < num_species; ix++)
			{
				Species current_species = this.pop_species.get(ix);

				int spec_size = current_species.member_ids.size();
				// need to figure out wtf i was even thinking here FUCKING STUPID ASS NICK ASs MFER, WTF VROTHEREN
				for(int y = 0; y < spec_size; y++)
				{
					int BaseGenome_id = this.pop_species.get(ix).member_ids.get(y);
					if(y <= 2)
					{
						//System.out.print("breeding asexual BaseGenome: ");
						//System.out.println(BaseGenome_id);
						this.breed_asexual(this.BaseGenomes.get(BaseGenome_id), current_species);
						//boolean has_nulls = this.BaseGenomes.get(BaseGenome_id).check_for_nulls(this.connection_genes, this.node_genes);
					} 
					else if(y < spec_size -1)
					{
						//System.out.print("breeding with sex BaseGenome: ");
						//System.out.println(BaseGenome_id);
						int other_BaseGenome_id = current_species.member_ids.get(y+1);
						this.cross_breed(this.BaseGenomes.get(BaseGenome_id), this.BaseGenomes.get(other_BaseGenome_id), current_species);
						boolean has_nulls = this.BaseGenomes.get(BaseGenome_id).check_for_nulls(this.connection_genes, this.node_genes);
						if(has_nulls == true)
						{
							System.out.println("null pointer after cross breeding");
						}
					}	
				}
				need_new--;
				//System.out.println(need_new);
				if (need_new == 0)
				{
					break;
				}
			}
			elite_iterator++;
		}
	}
	
	public void breed_asexual(BaseGenome single_parent, Species the_species)
	{
		BaseGenome offspring = new BaseGenome(single_parent, this.next_BaseGenome_id);
		
		this.inno_num = innovation.get_next_inno_id();
		
		this.inno_num = offspring.mutate_genome(, this.config, innovation);
		
		this.next_BaseGenome_id++;
		
		the_species.member_ids.add(offspring.id);
		
		this.BaseGenomes.put(offspring.id, offspring);
		
		return;
	}
	
	//TODO validate the new reproduction logic works and remove this
	public void breed_all_remaining(Species the_species)
	{
		int num_BaseGenomes = the_species.member_ids.size();
		
		if(num_BaseGenomes > this.BaseGenomes.size()){
			System.out.println("species has more members than BasePopulation, error");
		}
		
		for (int i = 0; i < num_BaseGenomes; i++)
		{
			for (int x = 0; x < num_BaseGenomes; x++)
			{
				if (i != x)
				{
					cross_breed(this.BaseGenomes.get(the_species.member_ids.get(i)), this.BaseGenomes.get(the_species.member_ids.get(x)), the_species);
				}
			}
		}
	}
	
	// breed two BaseGenomes, params are the ids
	// breeding should be done with nodes not conns
	// TODO need to make sure we are adding the cross over or mutated genes to 
	// the offspring
	public void cross_breed(BaseGenome a, BaseGenome b, Species the_species)
	{
		// BaseGenome a will be the fitter of the two mates
		BaseGenome BaseGenomeA;
		
		BaseGenome BaseGenomeB;
		
		if(a.fitness > b.fitness)
		{
			BaseGenomeA = a;
			
			BaseGenomeB = b;			
		}
		else
		{
			BaseGenomeA = b;
			
			BaseGenomeB = a;
		}
		
		// give the offspring a new id and increment our next id property
		BaseGenome offspring = new BaseGenome(this.ts, this.next_BaseGenome_id);
		ArrayList<Integer> ga_node_genes = a.get_all_nodes();
		ArrayList<Integer> gb_node_genes = b.get_all_nodes();
		int node_counter = ga_node_genes.size();
		// as per the paper disjoint and excess we use the fitter BaseGenomes (a) 
		// genes, if both BaseGenomes have the gene we cross over
		for (int k = 0; k < node_counter; k++)
		{
			int gA_id = ga_node_genes.get(k);
			
			NodeGene gA = this.node_genes.get(gA_id).get(a.id);
			
			if(gb_node_genes.contains(gA_id) == false)
			{
				// Disjoint or excess so we use A because its the fitter BaseGenome
				NodeGene gene_copy = new NodeGene(gA);
				this.node_genes.get(gA_id).put(offspring.id, gene_copy);
				int num_conns = gene_copy.connections.size();
				for(int g_ix = 0; g_ix < num_conns; g_ix++)
				{
					int conn_id = gene_copy.connections.get(g_ix);
					ConnectionGene conn_copy = new ConnectionGene(this.connection_genes.get(conn_id).get(a.id));
					this.connection_genes.get(conn_id).put(offspring.id, conn_copy);
				}
				offspring.set_node(gene_copy);
			}
			else
			{	
				// perform cross over since the gene isnt disjoint or excess
				// TODO add call to conn cross over appropriately in the node
				// cross over logic
				NodeGene gB = this.node_genes.get(gA_id).get(b.id);
				
				NodeGene crossed_over = _cross_over_nodes(gA, gB, a.id, a.id, offspring.id);
				
				offspring.set_node(crossed_over);
				
				this.node_genes.get(gA_id).put(offspring.id, crossed_over);
			}
		}
		this.BaseGenomes.put(offspring.id, offspring);
		the_species.member_ids.add(offspring.id);
		this.next_BaseGenome_id++;
	}
	

	public int get_num_BaseGenomes()
	{
		return this.BaseGenomes.size();
	}
	
	public BaseGenome get_BaseGenome(int key) {
		return this.BaseGenomes.get(key);
	}
	
	public void set_config(String conf)
	{
		Gson g = new Gson();
		Type type = new TypeToken<NeatConfig>(){}.getType();
		this.config = g.fromJson(g.toJson(conf), type);
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}

	private ConnectionGene _cross_over_conns(ConnectionGene a, ConnectionGene b)
	{
		ConnectionGene new_gene = new ConnectionGene(a.from_node, a.to_node, a.inno_id);
		
		new_gene.activation_level = a.activation_level;
		
		for(String key : a.atts.keySet())
		{
			if(Math.random() > .5)
			{
				new_gene.atts.put(key, a.atts.get(key));
			}
			else
			{
				new_gene.atts.put(key, b.atts.get(key));
			}
		}
		return new_gene;
	}
	
	private NodeGene _cross_over_nodes(NodeGene a, NodeGene b, int BaseGenome_a_id, int BaseGenome_b_id, int BaseGenome_id)
	{
		/*
		System.out.print("crossing over nodes: ");
		System.out.print(a.inno_id);
		System.out.print(" and ");
		System.out.println(b.inno_id);
		*/
		NodeGene new_node = new NodeGene(a);
		new_node.level = a.level;
		new_node.is_input = a.is_input;
		new_node.is_output = a.is_output;
		new_node.connections = new ArrayList<Integer>(a.connections);
		int num_conns = new_node.connections.size();
		for(int conn_idx = 0; conn_idx < num_conns; conn_idx++)
		{
			int gene_id = new_node.connections.get(conn_idx);
			if(b.connections.contains(gene_id) != true)
			{
				ConnectionGene clone_this = this.connection_genes.get(gene_id).get(BaseGenome_a_id);
				if(clone_this == null)
				{
					System.out.println("uh wtf vro");
					System.out.println(gene_id);
				}
				ConnectionGene add_this = new ConnectionGene(clone_this);
				this.connection_genes.get(gene_id).put(BaseGenome_id, add_this);
			}
			else
			{
				//shit this is totally wrong, we need the BaseGenome ids not the inno ids for the second get
				// FOUND THIS LITTLE FUCKER BUG BHABHAHBABHBAHBHA
				ConnectionGene a_conn = this.connection_genes.get(gene_id).get(BaseGenome_a_id);
				ConnectionGene b_conn = this.connection_genes.get(gene_id).get(BaseGenome_b_id);
				ConnectionGene add_this = this._cross_over_conns(a_conn, b_conn);
				this.connection_genes.get(gene_id).put(BaseGenome_id, add_this);
			}
		}
		// lol based
		for(String key : a.atts.keySet())
		{
			if(Math.random() > .5)
			{
				new_node.atts.put(key, a.atts.get(key));
			}
			else
			{
				new_node.atts.put(key, b.atts.get(key));
			}
		}
		return new_node;
	}
	
	private String save_champ_BaseGenome(BaseGenome g) {
		Gson gson = new Gson();
		String empty_json = gson.toJson(this);
		return empty_json;
	}
}
