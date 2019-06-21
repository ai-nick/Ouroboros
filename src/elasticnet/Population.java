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
	
	public ArrayList<Genome> genomes = new ArrayList<Genome>();
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
	ArrayList<NodeGene> hidden_nodes = new ArrayList<NodeGene>();
	ArrayList<ConnectionGene> connection_genes = new ArrayList<ConnectionGene>();
	
	public Population(int gen,  NeatConfig config_in, int pop_size) 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
		this.pop_size = pop_size;
		this.config = config_in;
		this.current_gen = gen;
		if (gen == 0) {
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
				
				this.inno_num = gBaby.create_from_scratch(this.inno_num, this.config, this.hash_id);
				
				this.genomes.add(gBaby);
				
				this.num_genomes++;
			}
			this.next_genome_id = this.pop_size;
		}
	}
	
	public double compat_distance(Genome one, Genome two, double[] speciation_coefficients) {
		double w = 0.0;
		double d = 0.0;
		double s = 0.0;
		double e = 0.0;
		int loop_count = one.gene_ids.size();
		int[] j = new int[loop_count];
		for(int idx = 0; idx < loop_count; idx++)
		{
			if(two.gene_ids.contains(one.gene_ids.get(idx)))
			{
				if(one.conn_genes.containsKey(idx))
				{
					w = Math.abs((one.conn_genes.get(idx).atts.get("weight") - two.conn_genes.get(idx).atts.get("weight")));
				}
			}
			else
			{
				if(one.gene_ids.get(idx) >= two.gene_id_min && one.gene_ids.get(idx) <= two.gene_id_max)
				{
					d += 1.0;
				}
				else
				{
					e += 1.0;
				}
			}
		}
		
		loop_count = two.gene_ids.size();
		
		for(int ix = 0; ix < loop_count; ix++)
		{
			if(two.gene_ids.get(ix) >= one.gene_id_min && two.gene_ids.get(ix) <= one.gene_id_max)
			{
				d += 1.0;
			}
			else
			{
				e += 1.0;
			}
		}
		if(loop_count < one.gene_ids.size())
		{
			loop_count = one.gene_ids.size();
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
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		
		double compat_t = this.config.compat_threshold;
		
		double[] speciation_coeff = { 1.0, 1.0, 1.0 };
		// check if its the first round of speciation
		// if it is we will have an empty array 
		// and can just choose a random genome to represent 
		// the "seed" species
		if (this.pop_species.size() == 0)
		{
			Random rnd = new Random();
			int randindex = rnd.nextInt(this.genomes.size());
			Genome first_rep = this.genomes.get(randindex);
			this.pop_species.add(new Species(next_species_id, first_rep.id));
			next_species_id++;
			speciated.add(first_rep.id);
		}
		for(int x = 0; x < this.num_genomes; x++)
		{
			//check if its first species rep_index, not elegant but have to handle
			if(!speciated.contains(this.genomes.get(x).id)) 
			{
				boolean species_found = false;
				
				Genome current_genome = this.genomes.get(x);
				
				int num_species = this.pop_species.size();
				//loop through species and see if the genome is compatible with any
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
	
	// determine the number of genomes each species should reproduce
	// TODO need to hash out how to let species grow, target the desired pop_size
	public void the_reproduction_function()
	{
		HashMap<Integer, Double> adj_fit_sums = new HashMap<Integer, Double>();
		int num_species = this.pop_species.size();
		double elitism_percent = this.config.elitism;
		int saved_sum = 0;
		// next we will reduce each species by this elitism percent
		// and add the new amount of the species to our save_sum
		for(int x = 0; x < num_species; x++)
		{
			Species current = this.pop_species.get(x);
			adj_fit_sums.put(x, current.get_adjusted_fitness_sum(this.genomes));
			int keep_top = (int)((double)current.num_genomes * elitism_percent);
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				current.have_mercy(keep_top, this.genomes);
				breed_all_remaining(current);				
			}
		}
	}
	
	public void breed_all_remaining(Species the_species)
	{
		for (int i = 0; i < the_species.num_genomes; i++)
		{
			for (int x = 0; x < the_species.num_genomes; x++)
			{
				if (i != x)
				{
					cross_breed(this.genomes.get(the_species.member_ids.get(i)), this.genomes.get(the_species.member_ids.get(x)));
				}
			}
			Genome mutated_genome = new Genome(this.genomes.get(i), this.next_genome_id);
			mutated_genome.mutate_genome(this.inno_num, config);
			this.genomes.add(mutated_genome);
		}
	}
	
	// breed two genomes, params are the ids
	
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
		Genome offspring = new Genome(this.hash_id);
		
		// get the max num of genes for hidden nodes and connections 
		boolean use_a;
		
		int conn_gene_counter = 0;
		
		int node_gene_counter = 0;
		
		if (GenomeA.hidden_nodes.size() > GenomeB.hidden_nodes.size())
		{
			node_gene_counter = GenomeA.output_nodes.size()+GenomeA.input_nodes.size()+GenomeA.hidden_nodes.size();
		}
		else
		{
			node_gene_counter = GenomeB.output_nodes.size()+GenomeB.input_nodes.size()+GenomeB.hidden_nodes.size();
		}
		/*
		if(GenomeA.conn_genes.size() > GenomeB.conn_genes.size())
		{
			conn_gene_counter = GenomeA.conn_genes.size();
			use_a = true;
		}
		else
		{
			conn_gene_counter = GenomeB.conn_genes.size();
			use_a = false;
		}
		*/
		for (Integer k : GenomeA.conn_genes.keySet())
		{
			ConnectionGene gA = GenomeA.conn_genes.get(k);
			if(!GenomeB.conn_genes.containsValue(gA))
			{
				offspring.conn_genes.put(k, gA);
			}
			else
			{
				ConnectionGene gB = GenomeB.conn_genes.get(k);
				offspring.conn_genes.put(k, _cross_over_conns(gA, gB));
			}
		}
		
		// now the connection genes have been handled, on to nodes
		for (Integer ik: GenomeA.get_all_nodes().keySet())
		{
			NodeGene gA = GenomeA.get_all_nodes().get(ik);
			if(!GenomeB.get_all_nodes().containsKey(ik))
			{
				offspring.set_node(gA);
			}
			else
			{
				NodeGene gB = GenomeB.get_all_nodes().get(ik);
				offspring.set_node(_cross_over_nodes(gA, gB));
			}
		}
		
		this.genomes.add(offspring);
		
	}
	
	/*
	public int[] sort_genomes_by_shared_fitness_myway(int[] sort_array)
	{
		int p_idx = sort_array.length;
		int[] return_array = sort_array;
		int scan_id = p_idx - 1;
		while(scan_id != 0)
		{
			if(this.genomes.get(scan_id).fitness >= this.genomes.get(p_idx).fitness)
			{
				int place_hold = 
				return_array[p_idx] = sort_array[scan_id];
				return_array[p_idx-1] = sort_array[p_idx];
				if(scan_id < p_idx-1)
				{
					return_array[scan_id] = sort_array[]
				}
				p_idx--;
				scan_id--;
			}
			else
			{
				scan_id--;
			}
			
			return return_array;
		}

	}
	*/
	
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
		ConnectionGene new_gene = new ConnectionGene(this.inno_num, a.get_id());
		
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
		NodeGene new_node = new NodeGene(this.hash_id, a.get_node_id());
		new_node.level = a.level;
		
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
