package elasticnet;

public class Activations {
	
	public static double sin_activation(double in_val)
	{
		return in_val;
	}
	
	public static double tanh_activation(double in_val)
	{
		return in_val;
	}
	
	public static double cube_activation(double in_val)
	{
		return Math.pow(in_val, 3);
	}
	
	public static double square_activation(double in_val)
	{
		return in_val * in_val;
	}
	
	public static double cos_activation(double in_val)
	{
		return in_val;
	}
	
	public static double sigmoid_activation(double in_val)
	{
		return in_val;
	}

}
