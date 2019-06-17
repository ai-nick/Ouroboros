package XchangeService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class TestLoadHist {

	public static void main(String[] args) throws IOException {
		try(FileInputStream inputStream = new FileInputStream("xbtusd.json")) {     
		    String everything = IOUtils.toString(inputStream);
		    // do something with everything string
			HistDataBar[] container = new Gson().fromJson(everything, HistDataBar[].class);
			System.out.println(container.length);
			System.out.println(container[0].close);
		}
	}
}
