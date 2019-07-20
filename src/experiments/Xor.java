package experiments;

import elasticnet.Genome;
import elasticnet.NeatConfig;
import elasticnet.NeuralNetwork;
import elasticnet.Population;

public class Xor {
	public String[] signs = new String[4];
	
	public Xor()
	{
	}
	
	public void generate_data()
	{
		
	}
	
	public void run_pop() {
		this.permute_signs(2);
		
		double[][] inputs = {
				{0.0, 0.0},
				{1.0, 0.0},
				{0.0, 1.0},
				{1.0, 1.0}
		};
		
		double[] outputs = {0.0, 1.0, 1.0, 0.0};
		
		int num_epochs = this.signs.length/2;
		
		int pop_size = 50;
		
		Population test_pop = new Population(0, new NeatConfig(), pop_size);
		
		int num_gens = 10;
		
		double best_fitness = 0.0;
		
		int best_genome_id = 0;
		
		for(int i = 0; i < num_gens; i++)
		{
			for(Genome current_genome : test_pop.genomes.values())
			{	
				current_genome.fitness = 4.0;
				
				NeuralNetwork test_net = new NeuralNetwork(current_genome, test_pop.node_genes);
				
				test_net.feed_forward = true;
				
				for(int z = 0; z < 4; z++)
				{
					System.out.println(z);
					test_net.set_input(inputs[z]);
					
					test_net.Activate();
					
					current_genome.fitness -= Math.pow(test_net.get_output().get(0) - outputs[z], 2);
					
					test_net.Reset();	
				}
				
				if(current_genome.fitness > best_fitness)
				{
					best_genome_id = current_genome.id;
					best_fitness = current_genome.fitness;
					System.out.println(" new best fitness ");
					System.out.println(best_genome_id);
					System.out.println(best_fitness);
				}
				//System.out.println(current_genome.fitness);
				//System.out.println(test_net.as_json());
			}
			test_pop.speciate_population();
			
			test_pop.the_reproduction_function();
			
			System.out.println(test_pop.genomes.size());
		}
		//System.out.println("best genome id: ");
		
		//System.out.println(test_pop.get_best_genome_id());
	}
	
	public void permute_signs(int coord_len) {
		int num_permutes = (int)Math.pow(coord_len, 2.0);
		
		String str_len = "%" + Integer.toString(coord_len) + "s"; 
		
		for(long ix = 0; ix < num_permutes; ix++) {
			this.signs[(int)ix] = String.format(str_len, Long.toBinaryString(ix)).replace(' ', '0');
		}
	}

}
