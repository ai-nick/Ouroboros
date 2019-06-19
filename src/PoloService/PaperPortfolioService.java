package PoloService;
import java.util.*;


public class PaperPortfolioService {
	// these maps will also contain base pair balances
	Map<String,Double[]> long_positions = new HashMap<String, Double[]>();
	Map<String, Double[]> short_positions = new HashMap<String, Double[]>();
	int num_sells;
	int num_buys;
	double start_amount;
	double full_balance;
	double liq_level;
	
	public PaperPortfolioService(int stamnt) {
		this.start_amount = stamnt;
	}
	
	public int get_sells() {
		return this.num_sells;
	}
	public int get_buys() {
		return this.num_buys;
	}
	
	public String buy_long_leveraged(String coin, double amnt, double leverage, double price)
	{
		double lev_amnt = amnt * leverage;
		double qty_less_fees = (lev_amnt/price)*.99;
		this.long_positions.put(coin, new Double[] {qty_less_fees, price});
		this.liq_level = qty_less_fees - amnt;
		return "bought : " + coin;
	}
	
	public String sell_short_leveraged(String coin, double amnt, double leverage, double price)
	{
		double lev_amnt = amnt * leverage;
		double qty_less_fees = (lev_amnt/price)*.99;
		this.long_positions.put(coin, new Double[] {qty_less_fees, price});
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
	
	public String sell_coin(String coin, double price) {
		double amnt = this.long_positions.get(coin);
		if(amnt == 0.0) {
			return String.format("cant sell current $s balance = 0.0", coin);
		}else {
			this.bal_sheet.put("BTC", new Double[] {amnt*price*.01});
			this.bal_sheet.put(coin, new0.0);
			return String.format("sold %d %s at %d", amnt, coin, price);
		}
	}
}
