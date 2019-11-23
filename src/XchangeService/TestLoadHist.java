package XchangeService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class TestLoadHist {

	public static void main(String[] args) throws IOException {
		HistProvider hs = new HistProvider();
		hs.build_simple_input();
		for(int x = 0; x < hs.get_simple()[0].length; x++)
		{
			System.out.print(hs.get_simple()[0][x]);
			System.out.print(" ");
		}
	}
}
