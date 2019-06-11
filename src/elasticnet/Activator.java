package elasticnet;

public class Activator {
	
	public static double sin_activation(double in_val)
	{
		return Math.sin(in_val);
	}
	
	public static double tanh_activation(double in_val)
	{
		return Math.tanh(in_val);
	}
	
	public static double cube_activation(double in_val)
	{
		return Math.pow(in_val, 3);
	}
	
	public static double square_activation(double in_val)
	{
		return Math.pow(in_val, 2);
	}
	
	public static double cos_activation(double in_val)
	{
		return Math.cos(in_val);
	}
	
	public static double sigmoid_activation(double in_val)
	{
		return 1 / (1 + Math.exp(-in_val));
	}
	
	public static double activate(String funcName, double val)
	{
		switch(funcName) {
		case "sigmoid":
			return sigmoid_activation(val);
		case "tanh":
			return tanh_activation(val);
		case "square":
			return square_activation(val);
		case "cube":
			return cube_activation(val);
		case "cos":
			return cos_activation(val);
		case "sin":
			return sin_activation(val);
		default:
			return val; // if you give me nothing i give you nothing 
		}
	}

}
