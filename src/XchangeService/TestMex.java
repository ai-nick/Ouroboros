package XchangeService;

public class TestMex {

	public static void main(String[] args) {
		BitmexHistWriter test_writer = new BitmexHistWriter();
		
		test_writer.try_get_tickers();
	}
}
