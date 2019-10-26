package elasticnet;
import elasticnet.NeuralNetwork;
import java.util.ArrayList;
import java.util.HashMap;

public class CPPN extends NeuralNetwork{

	ArrayList<INode> nodes = new ArrayList<INode>();
	Integer coord_len = 0;
	
	public CPPN(Genome genome_in, HashMap<Integer, HashMap<Integer, NodeGene>> node_genes,
			HashMap<Integer, HashMap<Integer, ConnectionGene>> conn_genes, int coord_length) {
		super(genome_in, node_genes, conn_genes);
		this.coord_len = coord_length;
	}
}

