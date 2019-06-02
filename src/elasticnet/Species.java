package elasticnet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

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
	
	public Genome mutate_genome(Genome g, int new_id, HashMap<String, String> config)
	{
		Genome new_g = new Genome(g, new_id);
		// get our innovation probabilities from the config dictionary
		Double add_conn_prob = Double.parseDouble(config.get("prob_add_con"));
		Double delete_conn_prob = Double.parseDouble(config.get("prob_delete_con"));
		Double add_node_prob = Double.parseDouble(config.get("prob_add_node"));
		Double delete_node_prob = Double.parseDouble(config.get("prob_delete_con"));
		String default_activation = config.get("default_activation");
		
		Double prob_sum = add_conn_prob + delete_conn_prob + add_node_prob + delete_node_prob;
		
		if (Math.random() < (add_conn_prob/prob_sum))
		{
			mutate_add_conn(new_g, add_conn_prob, prob_sum, new_id);
		}
		if (Math.random() < (add_node_prob/prob_sum))
		{
			
		}
		
		return new_g;
	}
	
	private void mutate_add_conn(Genome new_g, Double add_conn_prob, Double prob_sum, int new_id)
	{
		HashMap<Integer, NodeGene> all_the_nodes = new_g.get_all_nodes();
		
		for (Integer k : all_the_nodes.keySet())
		{
			if (all_the_nodes.get(k).is_output == false)
			{
				if (Math.random() < (add_conn_prob/prob_sum))
				{
					int to_node_key = (int)all_the_nodes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, all_the_nodes.size())];
					
					int from_node_key = (int)all_the_nodes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, all_the_nodes.size())];
					
					NodeGene from_node = all_the_nodes.get(from_node_key);
					NodeGene to_node = all_the_nodes.get(to_node_key);
					
					if(new_g.output_nodes.contains(to_node) && new_g.output_nodes.contains(from_node))
					{
						return;
					}
					if(new_g.input_nodes.contains(from_node_key) && new_g.input_nodes.contains(to_node))
					{
						return;
					}
					ConnectionGene new_gene = new ConnectionGene(from_node, to_node, new_id);
					new_g.conn_genes.put(new_id, new_gene);
				}	
			}
		}
	}
	
	private void mutate_add_node(Genome new_g, Double add_node_prob, Double prob_sum, int new_id, String activation)
	{
		int connection_to_split_index = (int)new_g.conn_genes.keySet().toArray()[ThreadLocalRandom.current().nextInt(0, new_g.conn_genes.size())];
		
		ConnectionGene connection_to_split = new_g.conn_genes.get(connection_to_split_index);
		
		NodeGene new_node = new NodeGene(new_id, activation);
	}
	
}
