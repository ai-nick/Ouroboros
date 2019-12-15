package elasticnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GeneService {
	public ArrayList<Long> node_ids;
	
	public HashMap<Long, long[]> conn_coo;
	
	public GeneService()
	{
		this.node_ids = new ArrayList<Long>();
		this.conn_coo = new HashMap<Long, long[]>();
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
}
