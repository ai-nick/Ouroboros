package elasticnet.tests;
import java.text.SimpleDateFormat;
import java.util.Date;

import elasticnet.*;
public class TestGenome {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Genome test_genome = new Genome(0);
		int fake_pop_hash = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).hashCode();
		test_genome.create_from_scratch(0, new NeatConfig(), fake_pop_hash);
		System.out.println(test_genome.as_json());
	}

}
