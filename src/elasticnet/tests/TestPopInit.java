package elasticnet.tests;
import java.io.IOException;
import java.io.PrintWriter;

import elasticnet.*;

public class TestPopInit {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Population test_pop = new Population(0, new NeatConfig(), 100);
		
		test_pop.set_up_first_pop();
		
		try (PrintWriter out = new PrintWriter("pop.json")) {
		    out.println(test_pop.as_json());
		}
		catch ( IOException e)
		{
			System.out.println(e.toString());
		}

	}

}
