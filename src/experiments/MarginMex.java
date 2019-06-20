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
	
	PaperPortfolio port;
	
	public MarginMex(Integer num_gens, Integer pop_size)
	{
		this.num_gens = num_gens;
		this.pop_size = pop_size;
		
		try
		{
			this.run_experiment();
		}
		catch (Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	public void run_experiment() throws FileNotFoundException, IOException {
		this.hs = new HistProvider();
		
		this.hs.build_simple_input();
		
		this.pop = new Population(this.num_gens, new NeatConfig(this.hs.get_simple()[0].length, 3, "tanh"), this.pop_size);
		
		double best = 1000.0;
		
		for (int ix = 0; ix < this.num_gens; ix++)
		{
			for(int x = 0; x < this.pop_size; x++)
			{
				Genome current = this.pop.get_genome(x);
				
				this.port = new PaperPortfolio(1000.0, "usdt");
				
				current.fitness = this.port.get_start_amount();
				
				int count = this.hs.hist_list.length;
				
				NeuralNetwork net = new NeuralNetwork(current);
				
				net.feed_forward = true;
				
				double fixed_order_size = this.port.get_start_amount()/10;
				
				for (int i = 0; i < count; i++)
				{
					net.set_input(this.hs.get_simple()[ix]);
					
					net.Activate();
					double buy_sell = net.get_output().get(0);
					if (buy_sell > .5)
					{
						this.port.buy_coin(this.hs.hist_list[i].symbol, fixed_order_size, this.hs.hist_list[i].close);
					}
					else if (buy_sell < -.5)
					{
						this.port.sell_coin_long(this.hs.hist_list[i].symbol, this.hs.hist_list[i].close);
					}
					net.Reset();
				}
				this.port.sell_coin_long(this.hs.hist_list[count-1].symbol, this.hs.hist_list[count-1].close);
				current.fitness = this.port.get_balance();
				if (current.fitness > best) {
					best = current.fitness;
					System.out.println(best);
				}
			}
			this.pop.speciate_population();
			
			this.pop.the_reproduction_function();
		}
	}
}
