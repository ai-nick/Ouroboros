package elasticnet;
import com.google.gson.*;

public class Species {
	String speciesID = "";
	Genome[] members;
	double shared_fit;
	Gson config;
	double avg_fit;
	public Species(Gson configParams)
	{
		this.config = configParams;
	}
	
	public double set_avg_fitness() {
		double fitness = 0.0;
		for(int ix = 0; ix < members.length; ix++) {
			fitness += members[ix].fitness;
		}
		avg_fit = fitness/members.length;
		return avg_fit;
	}
}
