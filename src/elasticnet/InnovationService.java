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
	
	public void add_conn(long inno, long from, long to)
	{
		this.conn_coo.put(inno, new Long[]{from, to});
	}
	
	public long get_next_inno_id()
	{
		long max_node = Collections.max(node_ids);
		long max_conn = Collections.max(conn_coo.keySet());
		if(max_node > max_conn)
		{
			return max_node + 1;
		}
		else
		{
			return max_conn + 1;
		}
	}
	
	// if the path exists with a node in between from and to nodes
	// return the middle node id if not returns null
	public Long path_exists(Long from_node, Long to_node)
	{
		Long returnVal = null;
		
		for(Long conn_id : this.conn_coo.keySet())
		{
			Long[] next_conn = this.conn_coo.get(conn_id);
			if(next_conn[0] == from_node && next_conn[1] != to_node)
			{
				for(Long conn_id_2 : this.conn_coo.keySet())
				{
					Long[] second_conn = this.conn_coo.get(conn_id_2);
					if(second_conn[0] == next_conn[1] && second_conn[1] == to_node)
					{
						return conn_id_2;
					}
				}
			}
		}
		
		return returnVal;
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
	
	public ArrayList<Long> get_all_conns_to(long to_node)
	{
		ArrayList<Long> all_conns_to = new ArrayList<Long>();
		return all_conns_to;
	}
}
