package PoloService;
import java.util.*;


public class PaperPortfolioService {
	// these maps will also contain base pair balances
	// the double array will be structured as this
	// {pos_amnt, price, liq_price}
	Map<String,Double[]> long_positions = new HashMap<String, Double[]>();
	Map<String, Double[]> short_positions = new HashMap<String, Double[]>();
	int num_sells;
	int num_buys;
	double start_amount;
	double full_balance;
	double liq_level;
	String base_currency;
	
	public PaperPortfolioService(int stamnt, String base_currency) {
		this.start_amount = stamnt;
		this.base_currency = base_currency;
	}
	
	public int get_sells() {
		return this.num_sells;
	}
	public int get_buys() {
		return this.num_buys;
	}
	
	public double check_margin_position(String coin, double price)
	{
		Double[] position = this.long_positions.get(coin);
		Double change = price - position[1];
		Double percent_change = (change/position[1])*100;
		if (change > 0)
		{
			return percent_change;
		}
		else
		{
			if(percent_change < position[2])
			{
				this.long_positions.remove(coin);
				return 0.0;
			}
			else
			{
				return percent_change;
			}
		}
	}
	
	public String buy_long_leveraged(String coin, double amnt, double leverage, double price)
	{
		double lev_amnt = amnt * leverage;
		double qty_less_fees = (lev_amnt/price)*.99;
		double liq_percent = ((-amnt/price)/qty_less_fees);
		this.long_positions.put(coin, new Double[] {qty_less_fees, price, qty_less_fees-(amnt/price)});
		this.liq_level = qty_less_fees - amnt;
		return "bought : " + coin;
	}
	
	public String sell_short_leveraged(String coin, double amnt, double leverage, double price)
	{
		double lev_amnt = amnt * leverage;
		double qty_less_fees = (lev_amnt/price)*.99;
		double liq_percent = ((amnt/price)/qty_less_fees);
		this.long_positions.put(coin, new Double[] {qty_less_fees, price, liq_percent});
		this.liq_level = qty_less_fees - amnt;
		return "bought : " + coin;
	}
	
	public String buy_coin(String coin, double amnt, double price, String base) {
		if((amnt*price)*.01 > this.long_positions.get(base)[0]) {
			return "error not enough btc";
		} else {
			this.long_positions.put("BTC", new Double[] {this.long_positions.get(base)[0] - (amnt*price)*.01, price});
			this.long_positions.put(coin, new Double[]{amnt-(amnt*.01), price});
			return String.format("bought: %d of %s", this.long_positions.get(coin), coin);
		}
	}
	
	public String sell_coin_long(String coin, double price) {
		double amnt = this.long_positions.get(coin)[0];
		if(amnt == 0.0) {
			return String.format("cant sell current $s balance = 0.0", coin);
		}else {
			this.long_positions.put("BTC", new Double[] {amnt*price*.01, 0.0});
			this.long_positions.remove(coin);
			return String.format("sold %d %s at %d", amnt, coin, price);
		}
	}
}
