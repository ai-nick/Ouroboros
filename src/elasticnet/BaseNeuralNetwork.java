package elasticnet;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;

public class BaseNeuralNetwork implements INeuralNet
{
	//no comment
	public int num_activations = 0;
	ArrayList<Long> input_ids = new ArrayList<Long>();
	ArrayList<NodeGene> activation_nodes = new ArrayList<NodeGene>();
	public boolean fully_activated;
	HashMap<Long, NodeGene> nodes = new HashMap<Long, NodeGene>();
	//HashMap<Long, ConnectionGene> conns = new HashMap<Long, ConnectionGene>();
	ArrayList<Long> activated_conns = new ArrayList<Long>();
	public boolean feed_forward = true;
	int num_output = 0;
	int outs_count = 0;
	ArrayList<Integer> output_ids = new ArrayList<Integer>();

	public BaseNeuralNetwork(BaseGenome genome_in
			)
	{
		//System.out.print("running genome ");
		//System.out.println(genome_in.id);
		this.fully_activated = false;
		
		HashMap<Long, NodeGene> all_ids = genome_in.get_all_nodes();
		
		int node_count = all_ids.size();
		
		for(int i = 0; i < node_count; i++)
		{
			int key = all_ids.get(i);
			NodeGene add_node = node_genes.get(key).get(genome_in.id);
			this.nodes.put(key, add_node);
			int num_conns = add_node.connections.size();
			for(int x = 0; x < num_conns; x++)
			{
				int gene_id = add_node.connections.get(x);
				this.conns.put(gene_id, conn_genes.get(gene_id).get(genome_in.id));
			}
		}
		this.output_ids = genome_in.output_nodes;
		
		this.outs_count = this.output_ids.size();
	}
	
	@Override
	public void set_input(double[] input)
	{
		int number_inputs = this.input_ids.size();
		
		for(int ix = 0; ix < number_inputs; ix++)
		{
			NodeGene current = this.nodes.get(this.input_ids.get(ix));
			
			current.set_current_val(input[ix]);
			
			this.activation_nodes.add(current);
		}
	}
	
	@Override
	public void Activate() {
		//no comment
		ArrayList<NodeGene> next_actives = new ArrayList<NodeGene>();
		
		int loop_count = this.activation_nodes.size();
		
		for(int ix = 0; ix < loop_count; ix++)
		{
			NodeGene current = this.activation_nodes.get(ix);
			
			if (current.is_output() != true)
			{
				int num_connections = current.connections.size();
				
				// next node is set to null 
				for(int x = 0; x < num_connections; x++)
				{
					if(this.activated_conns.contains(current.connections.get(x)) != true)
					{
						ConnectionGene next_conn = this.conns.get(current.connections.get(x));
						if(next_conn == null)
						{
							System.out.print("null conn encountered: ");
							System.out.println(current.connections.get(x));
							System.out.println(this.conns.toString());
						}
						else
						{
							NodeGene next_node = this.nodes.get(next_conn.to_node);
							if(next_node != null) {
								next_node.add_to_current_value(current.get_current_val() * next_conn.get_weight());
								
								next_node.current_val = Activator.activate(next_node.activation, next_node.current_val);
								
								if(!next_actives.contains(next_node))
								{	
									if(next_node.is_output == true || next_node.is_input == true)
									{
										this.outs_count++;
										next_node.visits++;
									}
									else
									{
										next_actives.add(next_node);
									}
								}	
							}	
						}
					}
				}
			}
		}
		this.activation_nodes.clear();
		this.num_activations++;
		if(next_actives.isEmpty() == false)
		{
			this.activation_nodes.addAll(next_actives);
			this.Activate();
			return;
		}
		else
		{
			this.outs_count = 0;
			this.num_activations = 0;
			return;
		}
	}

	@Override
	public void Reset() {
		// TODO Auto-generated method stub
		// somehow getting here with null nodes
		for(Integer key : this.nodes.keySet())
		{
			if(this.nodes.get(key) != null)
			{
				this.nodes.get(key).current_val = 0.0;	
			}
			else
			{
				this.nodes.remove(key);
			}
		}
		this.outs_count = 0;
	}

	@Override
	public ArrayList<Double> get_output() {
		// TODO Auto-generated method stub
		ArrayList<Double> outs = new ArrayList<Double>();
		int loop_count = this.output_ids.size();
		//System.out.println(loop_count);
		for(int i = 0; i < loop_count; i++)
		{
			outs.add(this.nodes.get(this.output_ids.get(i)).current_val);
		}
		return outs;
	}
	
	public String as_json()
	{
		Gson gson = new Gson();
		String json_string = gson.toJson(this);
		return json_string;
	}
}
