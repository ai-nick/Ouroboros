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
	
	BasePopulation pop;
	
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
			ex.printStackTrace(System.out);
		}
	}
	
	public void run_experiment() throws FileNotFoundException, IOException {
		this.hs = new HistProvider();
		
		this.hs.build_simple_input();
		
		this.pop = new BasePopulation(0, new NeatConfig(this.hs.get_simple()[0].length, 1, "tanh"), this.pop_size, false);
		
		double best = 1000.0;
		
		for (int ix = 0; ix < this.num_gens; ix++)
		{
			for(Long x : this.pop.BaseGenomes.keySet())
			{	
				//System.out.println(x);
				BaseGenome current = this.pop.BaseGenomes.get(x);
				
				this.port = new PaperPortfolio(1000.0, "usdt");
				
				current.fitness = this.port.get_start_amount();
				
				int count = this.hs.hist_list.length;
				
				//Neuralcurrentwork current = new Neuralcurrentwork(current, this.pop.node_genes, this.pop.connection_genes);
				
				current.feed_forward = true;
				
				double fixed_order_size = this.port.get_start_amount()/10;
				
				for (int i = 0; i < count; i++)
				{
					//current.set_input();
					
					current.activate(this.hs.get_simple()[i]);
					
					double buy_sell = current.get_output().get(0);
					
					if (buy_sell > .5)
					{
						this.port.buy_coin(this.hs.hist_list[i].symbol, fixed_order_size, this.hs.hist_list[i].close);
					}
					else if (buy_sell < -.5)
					{
						//ystem.out.println(this.hs.hist_list[i].symbol);
						this.port.sell_coin_long(this.hs.hist_list[i].symbol, this.hs.hist_list[i].close);
					}
					//current.Reset();
				}
				this.port.sell_coin_long(this.hs.hist_list[count-1].symbol, this.hs.hist_list[count-1].close);
				current.fitness = this.port.get_balance();
				System.out.println(current.fitness);
				if (current.fitness > best) {
					best = current.fitness;
					System.out.println(best);
				}
			}
			this.pop.speciate_BasePopulation();
			
			this.pop.the_reproduction_function();
		}
	}
}
