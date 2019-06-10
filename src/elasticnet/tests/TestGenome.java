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
		
		try (PrintWriter out = new PrintWriter("genome.json")) {
		    out.println(test_genome.as_json());
		}
		catch ( IOException e)
		{
			System.out.println(e.toString());
		}
		
		System.out.println(test_genome.as_json());
		
		NeuralNetwork test_net = new NeuralNetWork(test_genome.get_all_nodes(), )
	}

}
