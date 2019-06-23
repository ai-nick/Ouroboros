package elasticnet.tests;
import elasticnet.*;
public class NodeTests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NodeGene test_node = new NodeGene(0, 0);
		
		System.out.println(test_node.as_json());
		
		ConnectionGene test_conn = new ConnectionGene(0);
		
		System.out.println(test_conn.as_json());
	}

}
