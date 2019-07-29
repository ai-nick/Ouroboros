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
		
		int pop_size = 50;
		
		Population test_pop = new Population(0, new NeatConfig(), pop_size);
		
		for (int i = 0; i < pop_size; i++)
		{
			Genome next = test_pop.genomes.get(i);
			
			int num_conns = next.conn_genes.size();
			
			for(int x = 0; x < num_conns; x++)
			{
				ConnectionGene next_conn = test_pop.connection_genes.get(next.conn_genes.get(x)).get(next.id);
				if(next_conn == null)
				{
					System.out.println("genome: ");
					System.out.println(next.id);
					System.out.println("null conn encountered, id = " + next.conn_genes.get(x).toString());
				}
			}
		}
		System.out.println(test_pop.connection_genes.get(7).toString());
	}

}
