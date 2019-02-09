package elasticnet;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.*;

public class Population {
	
	Genome[] genomes;
	int num_genomes;
	int num_gens;
	int hash_id;
	int generations = 0;
	String fitness_function = "";
	String ts = "";
	int current_gen = 0;
	int inno_num = 0;
	
	public Population() 
	{
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
	}
	
	public Population(int num_genomes, int gens) 
	{
		this.num_genomes = num_genomes;
		this.num_gens = gens;
	}
	
	public void speciate()
	{
		
	}
	
	public double speciate_genomes(Genome one, Genome two) {
		
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
