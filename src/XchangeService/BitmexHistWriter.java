package XchangeService;

import java.io.IOException;
import java.util.ArrayList;
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
			this.marketDataService
		}
	}
	
	public void try_get_tickers()
	{
		try
		{
			this.write_hist_to_csv();	
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}
	}
}
