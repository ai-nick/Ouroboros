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
	
	public HashMap<Integer, Genome> genomes = new HashMap<Integer, Genome>();
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
	HashMap<Integer, HashMap<Integer,NodeGene>> node_genes = new HashMap<Integer, HashMap<Integer, NodeGene>>();
	HashMap<Integer, HashMap<Integer,ConnectionGene>> connection_genes = new HashMap<Integer, HashMap<Integer,ConnectionGene>>();
	
	public Population(int gen,  NeatConfig config_in, int pop_size) 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
		this.pop_size = pop_size;
		this.config = config_in;
		this.inno_num = (config_in.num_input * config_in.num_output)+config_in.num_input+config_in.num_output;
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
				
				this.inno_num = gBaby.create_from_scratch(this.inno_num, this.config, this.hash_id, this.node_genes, this.connection_genes);
				
				this.genomes.put(ix,gBaby);
			}
			this.num_genomes = pop_size;
			
			this.next_genome_id = pop_size;
			
			this.next_genome_id = this.pop_size;
		}
	}
	
	public double compat_distance(Genome one, Genome two, double[] speciation_coefficients) {
		double w = 0.0;
		double d = 0.0;
		double s = 0.0;
		double e = 0.0;
		int loop_count = one.conn_genes.size();
		int[] j = new int[loop_count];
		for(int idx = 0; idx < loop_count; idx++)
		{
			int one_id = one.conn_genes.get(idx);
			if(two.conn_genes.contains(one_id))
			{
				int two_id = two.conn_genes.get(two.conn_genes.indexOf(one_id));
				w = Math.abs(this.connection_genes.get(one.conn_genes.get(idx)).get(one.id).atts.get("weight") - this.connection_genes.get(two.conn_genes.get(idx)).get(two.id).atts.get("weight"));
			}
			else
			{
				if(one.conn_genes.get(idx) >= two.gene_id_min && one.conn_genes.get(idx) <= two.gene_id_max)
				{
					d += 1.0;
				}
				else
				{
					e += 1.0;
				}
			}
		}
		
		loop_count = two.conn_genes.size();
		
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
			Genome first_rep = this.genomes.get(this.genomes.keySet().toArray()[randindex]);
			this.pop_species.add(new Species(next_species_id, first_rep.id));
			next_species_id++;
			speciated.add(first_rep.id);
		}
		for(int x : this.genomes.keySet())
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
			num_genomes = current.member_ids.size();
			adj_fit_sums.put(x, current.get_adjusted_fitness_sum(this.genomes));
			int keep_top = (int)((double)num_genomes * elitism_percent);
			saved_sum += keep_top;
			if(keep_top > 0)
			{
				current.have_mercy(keep_top, this.genomes);
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
				if(this.pop_species.get(ix).member_ids.size() <= elite_iterator+2) {
					int spec_size = this.pop_species.get(ix).member_ids.size();
					int reset_iter_idx = elite_iterator - ((elite_iterator/spec_size)*spec_size);
					int genome_id = this.pop_species.get(ix).member_ids.get(reset_iter_idx);
					int other_genome_id = this.pop_species.get(ix).member_ids.get(0);
					if(this.pop_species.get(ix).member_ids.size() > reset_iter_idx+2)
					{
						other_genome_id = this.pop_species.get(ix).member_ids.get(reset_iter_idx+1);	
					}
					if(other_genome_id == 0)
					{
						this.breed_asexual(this.genomes.get(other_genome_id));		
					} 
					else
					{
						this.cross_breed(this.genomes.get(genome_id), this.genomes.get(other_genome_id));
					}
					need_new--;
				}
				else
				{
					int genome_id = this.pop_species.get(ix).member_ids.get(elite_iterator);
					// lines 231 - 235:  
					// if we have reached least fit we will just mutate the asexually reproduce the fittest
					int other_genome_id = this.pop_species.get(ix).member_ids.get(0);
					
					if(this.pop_species.get(ix).member_ids.size() > elite_iterator+2)
					{
						other_genome_id = this.pop_species.get(ix).member_ids.get(elite_iterator+1);;	
					}
					if(other_genome_id == 0)
					{
						this.breed_asexual(this.genomes.get(other_genome_id));		
					} 
					else
					{
						this.cross_breed(this.genomes.get(genome_id), this.genomes.get(other_genome_id));
					}
					need_new--;	
				}
			}
			elite_iterator++;
		}
	}
	
	public void breed_asexual(Genome single_parent)
	{
		Genome offspring = new Genome(single_parent, this.next_genome_id);
		this.next_genome_id++;
		offspring.mutate_genome(this.inno_num, this.config, this.hidden_nodes, this.connection_genes);
		return;
	}
	
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
	// TODO we are losing hidden nodes here needs to be fixed
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
		
		this.next_genome_id++;
		
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

			if(!GenomeB.conn_genes.keySet().contains(k))
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
		//for (Integer ik: GenomeB.get_al)
		
		this.genomes.put(offspring.id, offspring);
		
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
		ConnectionGene new_gene = new ConnectionGene(a.inno_id);
		
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
		NodeGene new_node = new NodeGene(a.inno_id, this.hash_id);
		new_node.level = a.level;
		new_node.is_input = a.is_input;
		new_node.is_output = a.is_output;
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
