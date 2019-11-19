package elasticnet.tests;
import java.util.Random;

import elasticnet.Genome;
import elasticnet.NeatConfig;
import elasticnet.NeuralNetwork;
import elasticnet.Population;

public class TestGenomeCompatDist {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int pop_size = 50;
		Population test_pop = new Population(0, new NeatConfig(), pop_size, false);
		Random dice = new Random();
		Integer[] sorted_idx_list = new Integer[pop_size];
		for(int i = 0; i < pop_size; i++)
		{
			Genome g = test_pop.genomes.get(i);
			int dice_roll = dice.nextInt(10);
			g.fitness = dice_roll;
		}
		System.out.println(test_pop.genomes.size());
		
	}

}
