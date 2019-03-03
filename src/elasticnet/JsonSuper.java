package elasticnet;

import java.util.HashMap;

public class JsonSuper {

	HashMap<String, String> jsonObj;
	
	public JsonSuper getNestJsonObj(String keyString)
	{
		return new JsonSuper(jsonObj.get(keyString));
	}
}
