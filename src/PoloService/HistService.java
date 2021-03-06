package PoloService;
import java.util.Map;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;


import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexChartData;

public class HistService {
	PoloniexExchangeService pes = new PoloniexExchangeService("", "");
	int look_back;
	List<String> symbols;
	Map<String, Double> single_frame = new HashMap<String, Double>();
	Map<String,List<PoloniexChartData>> histdata = new HashMap<String, List<PoloniexChartData>>();
	
	
	public HistService() {
		this.look_back = 777;
		this.symbols = this.pes.returnAllMarkets();
		this.get_hist(this.symbols.get(0));
		/*
		for(int ix =0; ix < this.symbols.size(); ix++) {
			String current_sym = this.symbols.get(ix);
			this.get_hist(current_sym);
		}
		*/
		System.out.print(this.histdata.get(this.symbols.get(0)).get(0));
		System.out.println(String.format("pulled %d symbols of data", this.symbols.size()));
	}

	private void get_hist(String symbol) {
		this.histdata.put(symbol, this.pes.returnChartData("USDT_BTC", 
                7200L, 
                ZonedDateTime.now(ZoneOffset.UTC).minusDays(777).toEpochSecond()));
		System.out.print(this.histdata.get(symbol).get(0).open.toString());
	}

	public static void main(String[] args) {
		System.out.println("canyouhearme");
		HistService hs = new HistService();
	}
	
}