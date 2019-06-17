package XchangeService;

import com.google.gson.Gson;

public class TestMex {

	public static void main(String[] args) {
		BitmexHistWriter test_writer = new BitmexHistWriter();
		
		//System.out.print(test_writer.all_pairs);
		
		String data = test_writer.get_from_mex("");
		
		HistDataBar[] container = new Gson().fromJson(data, HistDataBar[].class);
		
		System.out.println(container[0].close);
	}
}