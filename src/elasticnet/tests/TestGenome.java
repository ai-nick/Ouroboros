package elasticnet.tests;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import elasticnet.*;
public class TestGenome {
	
	public static void main(String[] args) throws IOException {
		
		int pop_size = 10000;
		
		Population test_pop = new Population(0, new NeatConfig(), pop_size);
		int num_nulls = 0;
		int num_null_nodes = 0;
		int num_null_to_nodes = 0;
		for (int i = 0; i < pop_size; i++)
		{
			Genome next = test_pop.genomes.get(i);
			ArrayList<Integer> all_nodes = next.get_all_nodes();
			int num_nodes = all_nodes.size();
			
			for(int x = 0; x < num_nodes; x++)
			{
				NodeGene next_node = test_pop.node_genes.get(all_nodes.get(x)).get(next.id);
				int num_conns = next_node.get_connections().size();
				for(int ix = 0; ix < num_conns; ix++)
				{
					ConnectionGene next_conn = test_pop.connection_genes.get(next_node.get_connections().get(ix)).get(next.id);
					if(next_conn == null)
					{
						System.out.println("genome: ");
						System.out.println(next.id);
						System.out.println("null conn encountered, id = " + next_node.get_connections().get(ix).toString());
						num_nulls++;
					}
				}
			}
		}
		System.out.println("number of null conns found");
		System.out.println(num_nulls);
		System.out.println("num null from nodes");
		System.out.println(num_null_nodes);
		System.out.println("num null to nodes");
		System.out.println(num_null_to_nodes);
		//System.out.println(test_pop.connection_genes.get(7).toString());
	}

}
