package XchangeService;

public class TestMex {

	public static void main(String[] args) {
		BitmexHistWriter test_writer = new BitmexHistWriter();
		
		String data = test_writer.get_from_mex("");
		
		System.out.println(data);
	}
}
