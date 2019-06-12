package experiments;

import elasticnet.NeatConfig;
import elasticnet.Population;

public class Xor {
	public String[] signs;
	
	public void run_pop() {
		this.permute_signs(2);
		
		int pop_size = 100;
		
		Population test_pop = new Population(0, new NeatConfig(), pop_size);
		
		test_pop.set_up_first_pop();
		
		int num_gens = 100;
		
		for(int i = 0; i < num_gens; i++)
		{
			int num_genomes = test_pop.get_num_genomes();
			
			for(int x = 0; x < num_genomes; x++)
			{
				Genome current_genome = test_pop.get_genome(x);
				
			}
		}		
	}
	
	public double calc_fitness(double[] outputs, double[] inputs)
	{
		
		return 0.0;
	}
	
	public void permute_signs(int coord_len) {
		int num_permutes = (int)Math.pow(coord_len, 2);
		
		String str_len = "%" + Integer.toString(coord_len) + "s"; 
		
		for(long ix = 0; ix < num_permutes; ix++) {
			signs[(int)ix] = String.format(str_len, Long.toBinaryString(ix)).replace(' ', '0');
		}
	}

}
