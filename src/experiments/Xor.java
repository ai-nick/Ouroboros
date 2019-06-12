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
	
	public void run_pop() {
		this.permute_signs(2);
		
		double[] inputs = new double[2];
		
		int num_epochs = this.signs.length/2;
		
		int pop_size = 100;
		
		Population test_pop = new Population(0, new NeatConfig(), pop_size);
		
		test_pop.set_up_first_pop();
		
		int num_gens = 10;
		
		for(int i = 0; i < num_gens; i++)
		{
			System.out.println("new gen");
			int num_genomes = test_pop.get_num_genomes();
			
			for(int x = 0; x < num_genomes; x++)
			{
				Genome current_genome = test_pop.get_genome(x);
				
				current_genome.fitness = 4.0;
				
				NeuralNetwork test_net = new NeuralNetwork(current_genome);
				
				test_net.feed_forward = true;
				
				for(int z = 0; z < this.signs.length; z++)
				{
					String sign_patt = signs[z];
					
					double output = 0.0;
					
					for(int f = 0; f < num_epochs; f++)
					{	
						char x_or_char = sign_patt.charAt(f);
						
						if (x_or_char == '1')
						{
							inputs[f] = 1.0;
						}
						else
						{
							inputs[f] = 0.0;
						}
						
						if (inputs[f] + inputs[f] == 1.0)
						{
							output = 1.0;
						}
					}
					
					test_net.set_input(inputs);
					
					test_net.Activate();
					
					current_genome.fitness -= Math.pow(test_net.get_output().get(0) - output, 2);
					
					test_net.Reset();
					
					output = 0.0;	
				}
				
				System.out.println(current_genome.fitness);
			}
			test_pop.speciate_population();
		}		
	}
	
	public void permute_signs(int coord_len) {
		int num_permutes = (int)Math.pow(coord_len, 2.0);
		
		String str_len = "%" + Integer.toString(coord_len) + "s"; 
		
		for(long ix = 0; ix < num_permutes; ix++) {
			this.signs[(int)ix] = String.format(str_len, Long.toBinaryString(ix)).replace(' ', '0');
		}
	}

}
