package elasticnet.tests;
import elasticnet.*;

public class TestPopInit {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Population test_pop = new Population(10, 0, new NeatConfig(), 100);
		
		test_pop.set_up_first_pop();

	}

}
