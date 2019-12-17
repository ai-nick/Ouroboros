package elasticnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class InnovationService {
	public ArrayList<Long> node_ids;
	
	public HashMap<Long, Long[]> conn_coo;
	
	public InnovationService()
	{
		this.node_ids = new ArrayList<Long>();
		this.conn_coo = new HashMap<Long, Long[]>();
	}
	
	public Long get_next_node_id()
	{
		long next = Collections.max(node_ids);
		return next;
	}
	
	public long get_next_conn_id()
	{
		return Collections.max(conn_coo.keySet());
	}
	
	public Long conn_exists(Long[] conn_to_from)
	{
		if(this.conn_coo.values().contains(conn_to_from))
		{
			for(Long key : this.conn_coo.keySet())
			{
				if (this.conn_coo.get(key) == conn_to_from)
				{
					return key;
				}
			}
		}
		
		return null;
	}
}
