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
	int[] sorted_index_list;
	int num_genomes;
	int num_gens;
	int hash_id;
	int generations = 0;
	int min_species_size = 5;
	String fitness_function = "";
	String ts = "";
	int current_gen = 0;
	int inno_num = 0;
	int next_genome_id = 0;
	ArrayList<Species> pop_species = new ArrayList<Species>();
	NeatConfig config;
	int pop_size = 0;
	// elected to store this in the genomes themselves
	//HashMap<Integer, IConnection> conn_genes;
	//HashMap<Integer, INode> node_genes;
	
	public Population(int num_genomes, int gens, int gen,  NeatConfig config_in, int pop_size) 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
		this.pop_size = pop_size;
		this.num_genomes = num_genomes;
		this.num_gens = gens;
		this.config = config_in;
		this.sorted_index_list = new int[num_genomes];
		if (gen == 0) {
			this.set_up_first_pop();
		}
		for(int i = 0; i < num_genomes; i++)
		{
			this.sorted_index_list[i] = i;
		}
	}
	
	public void set_up_first_pop()
	{
		// if no genomes are loaded we will start by creating a fresh population
		if(pop_size != 0)
		{
			for (int ix = next_genome_id; ix < this.pop_size+next_genome_id; ix++)
			{
				Genome gBaby = new Genome(ix);
				
				this.inno_num = gBaby.create_from_scratch(this.inno_num, this.config);
			}
			this.next_genome_id = this.pop_size+next_genome_id;
		}
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
	
	
	// split genomes into species using compat dists
	
	public void speciate_population()
	{
		ArrayList<Integer> speciated = new ArrayList<Integer>();
		
		double compat_t = this.config.compat_threshold;
		
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
	
	// determine the number of genomes each species should reproduce
	// TODO need to hash out how to let species grow, target the desired pop_size
	public void the_reproduction_function(int save_top_and_mutate)
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
			current.have_mercy(keep_top, this.genomes);
			breed_all_remaining(current);
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
	
	//sort genomes by fitness
	
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
