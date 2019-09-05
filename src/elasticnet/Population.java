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
	int best_genome_id;
	int num_genomes;
	int hash_id;
	int min_species_size = 5;
	String fitness_function = "";
	String ts = "";
	int current_gen = 0;
	int inno_num = 0;
	int next_genome_id = 0;
	int next_species_id = 0;
	ArrayList<Species> pop_species = new ArrayList<Species>();
	NeatConfig config;
	int pop_size = 0;
	public HashMap<Integer, Genome> genomes = new HashMap<Integer, Genome>();
	public HashMap<Integer, HashMap<Integer,NodeGene>> node_genes = new HashMap<Integer, HashMap<Integer, NodeGene>>();
	public HashMap<Integer, HashMap<Integer,ConnectionGene>> connection_genes = new HashMap<Integer, HashMap<Integer,ConnectionGene>>();
	public SorterUtil sorter = new SorterUtil();
	public Population(int gen,  NeatConfig config_in, int pop_size) 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
		this.pop_size = pop_size;
		this.config = config_in;
		this.inno_num = (config_in.num_input * config_in.num_output)+config_in.num_input+config_in.num_output;
		this.current_gen = gen;
		if (this.current_gen == 0) {
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
				Genome gBaby = new Genome(this.hash_id, ix);
				
				//System.out.print("setting up genome: ");
				//System.out.println(ix);
				
				this.inno_num = gBaby.create_from_scratch(this.config, this.hash_id, this.node_genes, this.connection_genes);
				
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
			}
			else
			{
				if(one_id >= two.gene_id_min && one_id <= two.gene_id_max)
				{
					d += 1.0;
				}
				else
				{
					e += 1.0;
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
				if(two_conns.get(ix) >= one.gene_id_min && two_conns.get(ix) <= one.gene_id_max)
				{
					d += 1.0;
				}
				else
				{
					e += 1.0;
				}	
			}
		}
		if(loop_count < one_conns.size())
		{
			loop_count = one_conns.size();
		}
		s += (e*speciation_coefficients[0])/loop_count;
		s += (d*speciation_coefficients[1])/loop_count;
		s += w*speciation_coefficients[2];
		one.fit_dists.put(two.id, s);
		return s;
	}
	
	
	// split genomes into species using compat dists
	
	public void speciate_population()
	{
		// initialize array of speciated genome ids
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		// get the compat distance from config
		double compat_t = this.config.compat_threshold;
		// coeeficients 
		double[] speciation_coeff = { 1.0, 1.0, 1.0 };
		// set first species and rep genome
		if (this.pop_species.size() == 0)
		{
			Genome first_rep = this.genomes.get(this.genomes.keySet().iterator().next());
			this.pop_species.add(new Species(next_species_id, first_rep.id));
			next_species_id++;
			speciated.add(first_rep.id);
		}
		// we have atleast one species by this point in our control flow
		for(int x : this.genomes.keySet())
		{
			//check if its first species rep_index, not elegant but have to handle
			if(!speciated.contains(this.genomes.get(x).id)) 
			{
				boolean species_found = false;
				
				Genome current_genome = this.genomes.get(x);
				
				int num_species = this.pop_species.size();
				//this should be while loop, with a conditional of count == num_species - 1
				//
				for(int i = 0; i < num_species; i++)
				{
					//check if we added it to one already, could be better and 
					//break loop once on is found, TODO 
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
		int[] sorted_species_ids = new int[num_species];
		int saved_sum = 0;
		int keep_top = (int)((double)num_genomes * elitism_percent);
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			num_genomes = current.member_ids.size();
			adj_fit_sums.put(x, current.get_adjusted_fitness_sum(this.genomes));
		}
	}
	
	// determine the number of genomes each species should reproduce
	// TODO need to hash out how to let species grow, target the desired pop_size
	public void the_reproduction_function()
	{
		// dictionary with fitness sums for each species 
		// TODO sort this shit
		HashMap<Integer, Double> adj_fit_sums = new HashMap<Integer, Double>();
		int num_species = this.pop_species.size();
		double elitism_percent = this.config.elitism;
		int[] sorted_species_ids = new int[num_species];
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(x, current.get_adjusted_fitness_sum(this.genomes));
			sorted_species_ids[x] = current.speciesID;
		}
		sorter.quick_sort_big_dumb(sorted_species_ids, adj_fit_sums, 0, num_species-1);
		System.out.println(num_species);
		int saved_sum = 0;
		int keep_top = (int)((double)num_genomes * elitism_percent);
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		//TODO run quick sort on the species ids and the adj_fit_sums hashmap
		// then use x to scale out species size with any species that is in the lower
		// half going extinct
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			keep_top = current.member_ids.size() * (int)elitism_percent;
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				//reduce the species to only the elite genomes
				current.have_mercy(keep_top, this.genomes, this.connection_genes, this.node_genes);
				//breed_all_remaining(current);				
			}
		}
		// now we handle reproducing the correct amount of genomes
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
					int genome_id = this.pop_species.get(ix).member_ids.get(y);
					if(y <= 2)
					{
						System.out.println("breeding asexual");
						this.breed_asexual(this.genomes.get(genome_id), current_species);	
					} 
					else if(y < spec_size -1)
					{
						System.out.println("breeding with sex");
						int other_genome_id = current_species.member_ids.get(y+1);
						this.cross_breed(this.genomes.get(genome_id), this.genomes.get(other_genome_id));
					}	
				}
				need_new--;
			}
			elite_iterator++;
		}
	}
	
	public void breed_asexual(Genome single_parent, Species the_species)
	{
		Genome offspring = new Genome(single_parent, this.next_genome_id, this.connection_genes, this.node_genes);
		
		offspring.mutate_genome(this.inno_num, this.config, this.node_genes, this.connection_genes);
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
					cross_breed(this.genomes.get(the_species.member_ids.get(i)), this.genomes.get(the_species.member_ids.get(x)));
				}
			}
		}
	}
	
	// breed two genomes, params are the ids
	// breeding should be done with nodes not conns
	public void cross_breed(Genome a, Genome b)
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
		Genome offspring = new Genome(this.hash_id, this.next_genome_id);
		
		int in_count = this.config.num_input;
		int out_count = this.config.num_output;
		
		for(int i = 0; i < in_count; i++)
		{
			int node_id = GenomeA.input_nodes.get(i);
			offspring.input_nodes.add(node_id);
			NodeGene clone_node = this.node_genes.get(node_id).get(GenomeA.id);
			this.node_genes.get(node_id).put(offspring.id, clone_node);
		}
		for(int i = 0; i < out_count; i++)
		{
			int node_id = GenomeA.output_nodes.get(i);
			offspring.output_nodes.add(node_id);
			NodeGene clone_node = this.node_genes.get(node_id).get(GenomeA.id);
			this.node_genes.get(node_id).put(offspring.id, clone_node);
		}
		
		ArrayList<Integer> ga_node_genes = GenomeA.get_all_nodes();
		ArrayList<Integer> gb_node_genes = GenomeB.get_all_nodes();
		int node_counter = ga_node_genes.size();
		// as per the paper disjoint and excess we use the fitter genomes (a) 
		// genes, if both genomes have the gene we cross over
		for (int k = 0; k < node_counter; k++)
		{
			int gA_id = ga_node_genes.get(k);
			
			NodeGene gA = this.node_genes.get(gA_id).get(GenomeA.id);
			
			if(gb_node_genes.contains(gA_id) == false)
			{
				this.node_genes.get(gA_id).put(offspring.id, gA);
			}
			else
			{	
				NodeGene gB = this.node_genes.get(gA_id).get(GenomeB.id);
				
				NodeGene crossed_over = _cross_over_nodes(gA, gB);
				
				this.node_genes.get(gA_id).put(offspring.id, crossed_over);
			}
		}
		this.genomes.put(offspring.id, offspring);
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
	
	
	// private methods ie cant imagine neededing to call these anywhere other
	// than public methods above, feel free to tell me im wrong if i am
	
	private ConnectionGene _cross_over_conns(ConnectionGene a, ConnectionGene b)
	{
		System.out.print("crossing over conns: ");
		System.out.print(a.inno_id);
		System.out.print(" and ");
		System.out.print(b.inno_id);
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
	
	private NodeGene _cross_over_nodes(NodeGene a, NodeGene b)
	{
		System.out.print("crossing over nodes: ");
		System.out.print(a.inno_id);
		System.out.print(" and ");
		System.out.print(b.inno_id);
		NodeGene new_node = new NodeGene(a.inno_id, this.hash_id);
		new_node.level = a.level;
		new_node.is_input = a.is_input;
		new_node.is_output = a.is_output;
		ArrayList<Integer> new_conns;
		boolean use_a;
		if(Math.random() > .5)
		{
			new_conns = a.connections;
			use_a = true;
		}
		else
		{
			new_conns = b.connections;
			use_a = false;
		}
		
		new_node.connections = new ArrayList<Integer>(new_conns);
		int num_conns = new_node.connections.size();
		for(int conn_idx = 0; conn_idx < num_conns; conn_idx++)
		{
			if(use_a == true)
			{
				
			}
			else
			{
				
			}
		}
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
}
