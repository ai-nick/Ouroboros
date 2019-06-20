package XchangeService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class HistProvider {
	// this isnt very durable for variable history lengths
	// so TODO allow dynamic array instantiation
	int look_back = 21;
	
	public HistDataBar[] hist_list = new HistDataBar[400];
	
	double[][] ann_input = new double[400-10][10];
	
	double[][] simple_input = new double[400][5];
	
	int num_bars;
	
	double smooth = 2.0;
	
	int long_look_back;
	
	public HistProvider() throws FileNotFoundException, IOException
	{
		try(FileInputStream inputStream = new FileInputStream("xbtusd.json")) {     
		    String everything = IOUtils.toString(inputStream);
		    // do something with everything string
			hist_list = new Gson().fromJson(everything, HistDataBar[].class);
			num_bars = hist_list.length;
		}
		
		System.out.println(hist_list.length);
	}
	
	public double[][] get_simple(){
		return this.simple_input;
	}
	
	public void build_simple_input()
	{
		for(int i = 0; i < num_bars; i++)
		{
			HistDataBar current = this.hist_list[i];
			simple_input[i] = current.get_simple();
		}
	}
	
	public void calc_vwap(int look_back, int input_array_idx)
	{
		for(int i = look_back; i < num_bars; i++)
		{
			double total_weight = 0.0;
			
			double total_volume = 0.0;
			
			for (int ix = 0; ix < look_back; ix++)
			{
				total_weight += this.hist_list[i-look_back].close * this.hist_list[i-look_back].volume;
				total_volume += this.hist_list[i-look_back].volume;
			}
			ann_input[i-look_back][input_array_idx] += total_weight/total_volume;
			System.out.println("vwap = ");
			System.out.println(ann_input[i-look_back][input_array_idx]);
		}
	}
	
	public void macd(int look_back, int input_array_idx)
	{
		for (int i = look_back; i < num_bars; i++)
		{
			
		}
	}
	
	// will need to burn a day
	public void sma(int look_back, int input_array_idx)
	{
		for (int i = look_back; i < num_bars; i++)
		{
			double close_total = 0.0;
			
			for(int ix = 0; ix < look_back; ix++)
			{
				close_total += this.hist_list[i-look_back].close;
			}
			close_total = close_total / look_back;
			//put it at the predetermined index int he input array
			this.ann_input[i-look_back][input_array_idx] = close_total;
		}	
	}

}
