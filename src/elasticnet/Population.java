package elasticnet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Population {
	
	Genome[] genomes;
	int hash_id;
	int generations = 0;
	String fitness_function = "";
	String ts = "";
	public Population() {
		this.ts = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		this.hash_id = this.ts.hashCode();
	}

}
