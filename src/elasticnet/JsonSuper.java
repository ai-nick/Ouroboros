package elasticnet;

import java.util.HashMap;

public class JsonSuper {

	HashMap<String, String> jsonObj;
	
	public JsonSuper(String objString)
	{
		if(objString.length() == 0)
		{
			jsonObj = new HashMap<String, String>();
			return;
		}
		else
		{
			String toProcess = objString;
			while (toProcess != "")
			{
				String newKey = toProcess.substring(1).split(":")[0];
				
			}
		}
	}
	public JsonSuper getNestJsonObj(String keyString)
	{
		return new JsonSuper(jsonObj.get(keyString));
	}
}
