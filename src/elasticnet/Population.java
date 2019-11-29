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
import java.util.Collections;

/**
 * 
 * @author nick5
 * The population class is the main 
 * cohesive class for our neuroevolution 
 * algorithm, it is responsible for generating
 * new genomes, keeping hashmaps of genes and 
 * their historical id's as well
 * as storing the species and genomes 
 * 
 *
 */

public class Population {
	Integer best_genome_id;
	Integer num_genomes;
	int min_species_size = 5;
	String fitness_function = "";
	Long ts;
	Integer current_gen = 0;
	Integer inno_num = 0;
	Integer next_genome_id = 0;
	Integer next_species_id = 0;
	ArrayList<Species> pop_species = new ArrayList<Species>();
	NeatConfig config;
	Integer pop_size = 0;
	public HashMap<Integer, Genome> genomes = new HashMap<Integer, Genome>();
	public HashMap<Integer, HashMap<Integer,NodeGene>> node_genes = new HashMap<Integer, HashMap<Integer, NodeGene>>();
	public HashMap<Integer, HashMap<Integer,ConnectionGene>> connection_genes = new HashMap<Integer, HashMap<Integer,ConnectionGene>>();
	public SorterUtil sorter = new SorterUtil();
	public boolean is_p2p;
	
	public Population(int gen,  NeatConfig config_in, int pop_size, boolean is_p2p) 
	{
		this.is_p2p = is_p2p;
		this.ts = System.currentTimeMillis();
		this.pop_size = pop_size;
		this.config = config_in;
		this.inno_num = (config_in.num_input * config_in.num_output)+config_in.num_input+config_in.num_output;
		System.out.print("num of initial genes: ");
		System.out.println(this.inno_num);
		this.current_gen = gen;
		if (this.current_gen == 0) {
			this.set_up_first_pop();
		}
	}
	
	public Population(int gen, NeatConfig config_in, int num_inputs, int num_outputs, int pop_size, boolean is_p2p)
	{
		this.is_p2p = is_p2p;
		this.ts = System.currentTimeMillis();
		this.pop_size = pop_size;
		this.config = config_in;
		this.inno_num = (num_inputs*num_outputs) + num_inputs + num_outputs;
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
	
	public void set_best_genome_id(int id)
	{
		this.best_genome_id = id;
	}
	
	public int get_best_genome_id()
	{
		return this.best_genome_id;
	}
	
	public void set_up_first_pop()
	{
		// if no genomes are loaded we will start by creating a fresh population
		if(pop_size != 0)
		{
			for (int ix = 0; ix < this.pop_size; ix++)
			{
				Genome gBaby = new Genome(this.ts, ix);
				
				//System.out.print("setting up genome: ");
				//System.out.println(ix);
				
				this.inno_num = gBaby.create_from_scratch(this.config, this.ts, this.node_genes, this.connection_genes);
				
				this.genomes.put(ix,gBaby);
			}
			this.num_genomes = pop_size;
			
			this.next_genome_id = this.pop_size;
		}
	}
	
	public double compat_distance(Genome one, Genome two, double[] speciation_coefficients) {
		double w = 0.0;
		double d = 0.0;
		double s = 0.0;
		double e = 0.0;
		int match_count = 0;
		//TODO check conn arrays are not empty
		ArrayList<Integer> one_conns = one.get_all_conn_ids(this.node_genes);
		ArrayList<Integer> two_conns = two.get_all_conn_ids(this.node_genes);
		int loop_count = one_conns.size();
		//int[] j = new int[loop_count];
		for(int idx = 0; idx < loop_count; idx++)
		{
			//looping through genes
			//and check if we can 
			int one_id = one_conns.get(idx);
			if(two_conns.contains(one_id))
			{
				int two_id = two_conns.get(two_conns.indexOf(one_id));
				w = Math.abs(this.connection_genes.get(one_id).get(one.id).atts.get("weight") - this.connection_genes.get(two_id).get(two.id).atts.get("weight"));
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
			//same loop as before but for the second genome
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
	
	
	// split genomes into species using compat dists
	
	public void speciate_population()
	{
		// initialize array of speciated genome ids that have
		// are part of a species
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		this.pop_species = new ArrayList<Species>();
		// get the compat distance from config
		double compat_t = this.config.compat_threshold;
		// coeeficients 
		double[] speciation_coeff = { 1.0, 1.0, .5 };
		// set first species and rep genome
		if (this.pop_species.size() == 0)
		{
			// grabs a random genome from our set
			Genome first_rep = this.genomes.get(this.genomes.keySet().iterator().next());
			this.pop_species.add(new Species(next_species_id, first_rep.id));
			next_species_id++;
			speciated.add(first_rep.id);
		}
		// we have atleast one species by this point in our control flow
		for(int x : this.genomes.keySet())
		{
			// we check if this id already belongs to a species from previous generations
			// TODO check that speciated isnt cleared, otherwise this doesnt make sense
			if(!speciated.contains(this.genomes.get(x).id)) 
			{
				// finna track if it has a species yet tehe
				boolean species_found = false;
				
				Genome current_genome = this.genomes.get(x);
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
						Double dist = this.compat_distance(this.genomes.get(this.pop_species.get(i).rep_id), 
								current_genome,
								speciation_coeff);
						//System.out.println(dist);
						if( dist < compat_t)
						{
							this.pop_species.get(i).member_ids.add(current_genome.id);
							speciated.add(current_genome.id);
							species_found = true;
							//found species break our loop
							break;
						}	
					}
				}
				if(!speciated.contains(current_genome.id))
				{
					this.pop_species.add(new Species(next_species_id, current_genome.id));
					next_species_id++;
					speciated.add(current_genome.id);
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
		Integer[] sorted_species_ids = new Integer[num_species];
		int saved_sum = 0;
		int keep_top;
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(current.speciesID, current.get_adjusted_fitness_sum(this.genomes, this.sorter));
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
				//reduce the species to only the elite genomes
				current.have_mercy(keep_top, this.genomes, this.connection_genes, this.node_genes);
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
		System.out.println(this.genomes.size());
		while(need_new != 0)
		{
			for(int ix = 0; ix < num_species; ix++)
			{
				Species current_species = this.pop_species.get(sorted_species_ids[ix]);

				int spec_size = current_species.sorted_idx_array.length;
				// if there are only two genomes in the species bread them asexually
				// if we have room after the first reproduction loop we will 
				// rebread sexually between the 4 species produced in this loop
				if(spec_size > 2)
				{
					// if our species has enough genomes to breed with sexy times
					// we will do every combination of mates
					for (int a = 0; a < spec_size; a++)
					{
						Genome a_genome = this.genomes.get(current_species.member_ids.get(current_species.sorted_idx_array[a]));
						this.breed_asexual(a_genome, current_species);
						need_new--;
						/*
						for(int b = 0; b < spec_size; b++)
						{
							if(b != a)
							{
								Genome b_genome = this.genomes.get(current_species.member_ids.get(current_species.sorted_idx_array[b]));
								
								this.cross_breed(a_genome, b_genome, current_species);
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
					Genome asex_genome = this.genomes.get(current_species.member_ids.get(0));
					this.breed_asexual(asex_genome, current_species);
					need_new--;
				}
				if (need_new == 0)
				{
					break;
				}
			}
		}
	}
	
	// determine the number of genomes each species should reproduce
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
		Integer[] sorted_species_ids = new Integer[num_species];
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(current.speciesID, current.get_adjusted_fitness_sum(this.genomes, this.sorter));
			sorted_species_ids[x] = current.speciesID;
		}
		if(sorted_species_ids.length > 1)
		{
			sorter.quick_sort_big_dumb(sorted_species_ids, adj_fit_sums, 0, num_species-1);	
		}
		else
		{
			System.out.println("only one species check config");
		}
		Integer saved_sum = 0;
		Integer keep_top = (int)((double)num_genomes * elitism_percent);
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		//TODO run quick sort on the species ids and the adj_fit_sums hashmap
		// then use x to scale out species size with any species that is in the lower
		// half going extinct
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(sorted_species_ids[x]);
			keep_top = current.member_ids.size() * (int)elitism_percent;
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				System.out.print("running elitism for species: ");
				System.out.println(current.speciesID);
				//reduce the species to only the elite genomes
				current.have_mercy(keep_top, this.genomes, this.connection_genes, this.node_genes);
				//breed_all_remaining(current);				
			}
		}
		// now we handle reproducing the correct amount of genomes
		int need_new = this.pop_size - saved_sum;
		System.out.print("size after elitism: ");
		System.out.println(this.genomes.size());
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
					int genome_id = this.pop_species.get(ix).member_ids.get(y);
					if(y <= 2)
					{
						//System.out.print("breeding asexual genome: ");
						//System.out.println(genome_id);
						this.breed_asexual(this.genomes.get(genome_id), current_species);
						//boolean has_nulls = this.genomes.get(genome_id).check_for_nulls(this.connection_genes, this.node_genes);
					} 
					else if(y < spec_size -1)
					{
						//System.out.print("breeding with sex genome: ");
						//System.out.println(genome_id);
						int other_genome_id = current_species.member_ids.get(y+1);
						this.cross_breed(this.genomes.get(genome_id), this.genomes.get(other_genome_id), current_species);
						boolean has_nulls = this.genomes.get(genome_id).check_for_nulls(this.connection_genes, this.node_genes);
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
	
	public void breed_asexual(Genome single_parent, Species the_species)
	{
		Genome offspring = new Genome(single_parent, this.next_genome_id, this.connection_genes, this.node_genes);
		
		boolean has_nulls = offspring.check_for_nulls(connection_genes, node_genes);
		
		if(has_nulls == true)
		{
			System.out.println("null pointer after cloning");
		}
		
		this.inno_num = offspring.mutate_genome(this.inno_num, this.config, this.node_genes, this.connection_genes);
		
		has_nulls = offspring.check_for_nulls(connection_genes, node_genes);
		
		if(has_nulls == true)
		{
			System.out.println("null pointer after mutating clone");
		}
		this.next_genome_id++;
		
		the_species.member_ids.add(offspring.id);
		
		this.genomes.put(offspring.id, offspring);
		
		return;
	}
	
	//TODO validate the new reproduction logic works and remove this
	public void breed_all_remaining(Species the_species)
	{
		int num_genomes = the_species.member_ids.size();
		
		if(num_genomes > this.genomes.size()){
			System.out.println("species has more members than population, error");
		}
		
		for (int i = 0; i < num_genomes; i++)
		{
			for (int x = 0; x < num_genomes; x++)
			{
				if (i != x)
				{
					cross_breed(this.genomes.get(the_species.member_ids.get(i)), this.genomes.get(the_species.member_ids.get(x)), the_species);
				}
			}
		}
	}
	
	// breed two genomes, params are the ids
	// breeding should be done with nodes not conns
	// TODO need to make sure we are adding the cross over or mutated genes to 
	// the offspring
	public void cross_breed(Genome a, Genome b, Species the_species)
	{
		// genome a will be the fitter of the two mates
		Genome GenomeA;
		
		Genome GenomeB;
		
		if(a.fitness > b.fitness)
		{
			GenomeA = a;
			
			GenomeB = b;			
		}
		else
		{
			GenomeA = b;
			
			GenomeB = a;
		}
		
		// give the offspring a new id and increment our next id property
		Genome offspring = new Genome(this.ts, this.next_genome_id);
		ArrayList<Integer> ga_node_genes = a.get_all_nodes();
		ArrayList<Integer> gb_node_genes = b.get_all_nodes();
		int node_counter = ga_node_genes.size();
		// as per the paper disjoint and excess we use the fitter genomes (a) 
		// genes, if both genomes have the gene we cross over
		for (int k = 0; k < node_counter; k++)
		{
			int gA_id = ga_node_genes.get(k);
			
			NodeGene gA = this.node_genes.get(gA_id).get(a.id);
			
			if(gb_node_genes.contains(gA_id) == false)
			{
				// Disjoint or excess so we use A because its the fitter genome
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
		this.genomes.put(offspring.id, offspring);
		the_species.member_ids.add(offspring.id);
		this.next_genome_id++;
	}
	

	public int get_num_genomes()
	{
		return this.genomes.size();
	}
	
	public Genome get_genome(int key) {
		return this.genomes.get(key);
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
	
	private NodeGene _cross_over_nodes(NodeGene a, NodeGene b, int genome_a_id, int genome_b_id, int genome_id)
	{
		/*
		System.out.print("crossing over nodes: ");
		System.out.print(a.inno_id);
		System.out.print(" and ");
		System.out.println(b.inno_id);
		*/
		NodeGene new_node = new NodeGene(a.inno_id);
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
				ConnectionGene clone_this = this.connection_genes.get(gene_id).get(genome_a_id);
				if(clone_this == null)
				{
					System.out.println("uh wtf vro");
					System.out.println(gene_id);
				}
				ConnectionGene add_this = new ConnectionGene(clone_this);
				this.connection_genes.get(gene_id).put(genome_id, add_this);
			}
			else
			{
				//shit this is totally wrong, we need the genome ids not the inno ids for the second get
				// FOUND THIS LITTLE FUCKER BUG BHABHAHBABHBAHBHA
				ConnectionGene a_conn = this.connection_genes.get(gene_id).get(genome_a_id);
				ConnectionGene b_conn = this.connection_genes.get(gene_id).get(genome_b_id);
				ConnectionGene add_this = this._cross_over_conns(a_conn, b_conn);
				this.connection_genes.get(gene_id).put(genome_id, add_this);
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
	
	private void save_champ_genome(Genome g) {
		
	}
}
