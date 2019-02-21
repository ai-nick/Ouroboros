package elasticnet;
import java.util.ArrayList;

import com.google.gson.*;

public class Species {
	String speciesID = "";
	ArrayList<Integer> members_id;
	Gson config;
	int rep_id;
	int pop_id;
	double sum_of_shared_primes;
	
	public Species(Gson configParams)
	{
		this.config = configParams;
	}
	
	
	
	public String toJson()
	{
		Gson g = new Gson();
		return g.toJson(this);
	}
	
}
