package elasticnet.tests;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import elasticnet.*;
public class TestGenome {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Genome test_genome = new Genome(0);
		
		int fake_pop_hash = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).hashCode();
		
		test_genome.create_from_scratch(0, new NeatConfig(), fake_pop_hash);
		
		
		//System.out.println(test_genome.as_json());
		
		NeuralNetwork test_net = new NeuralNetwork(test_genome);
		
		double[] test_input = {1.0, 1.0, 1.0, 1.0};
		
		test_net.feed_forward = true;
		
		test_net.set_input(test_input);
		
		test_net.Activate();
		
		System.out.println(test_net.get_output().toString());
	}

}
