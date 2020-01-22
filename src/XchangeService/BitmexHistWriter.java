package XchangeService;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitmex.BitmexExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.dto.marketdata.*;

public class BitmexHistWriter {
	
	Exchange bitmex = ExchangeFactory.INSTANCE.createExchange(BitmexExchange.class.getName());
	
	List<CurrencyPair> all_pairs = bitmex.getExchangeSymbols();
	
	MarketDataService marketDataService = bitmex.getMarketDataService();
	
	public BitmexHistWriter()
	{
	}
	
	public void get_live_tickers() throws IOException
	{
		int pair_count = all_pairs.size();
		ArrayList<Ticker> all_hist = new ArrayList<Ticker>();
		for (int ix = 0; ix < pair_count; ix++)
		{
			Ticker ticker = marketDataService.getTicker(all_pairs.get(ix));
			System.out.println(ticker.toString());
			all_hist.add(ticker);
		}
	}
	
	public void write_hist_to_csv()
	{
		int pair_count = all_pairs.size();

		for (int ix = 0; ix < pair_count; ix++)
		{
			
		}
	}
	
	public String get_from_mex(String sym)
	{
		String hist_endpoint = "https://www.bitmex.com/api/v1/trade/bucketed?binSize=1h&symbol=XBTUSD&partial=false&count=400&reverse=false";
		/*
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -5);
		Long fiveMonthsAgo = cal.getTimeInMillis();
		//fiveMonthsAgo.
		hist_endpoint += "&count=300";
		hist_endpoint += "&startTime=" + fiveMonthsAgo.toString();
		*/
		try
		{
			String json_hist = getHTML(hist_endpoint);
			return json_hist;
		}
		catch(Exception e)
		{
			return e.toString();
		}
	}
	
	

   public static String getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line);
      }
      rd.close();
      return result.toString();
   }	
}
