package experiments;
import java.io.FileNotFoundException;
import java.io.IOException;

import XchangeService.*;
import elasticnet.*;
public class MarginMex {
	HistProvider hs;
	
	int num_gens;
	
	int pop_size;
	
	Genome champ;
	
	Population pop;
	
	PaperPortfolio port = new PaperPortfolio(1000.0, "usdt");
	
	public MarginMex(Integer num_gens, Integer pop_size)
	{
		this.num_gens = num_gens;
		this.pop_size = pop_size;
		
	}
	
	public void run_experiment() throws FileNotFoundException, IOException {
		this.hs = new HistProvider();
		
	}
}
