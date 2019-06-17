package PoloService;
import java.util.*;


public class PaperPortfolioService {
	
	Map<String,Double> bal_sheet = new HashMap<String, Double>();
	int num_sells;
	int num_buys;
	double start_amount;
	double full_balance;
	
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
		return "";
	}
	
	public String sell_short_leveraged(String coin, double amnt, double leverage, double price)
	{
		return "";
	}
	
	public String buy_coin(String coin, double amnt, double price) {
		if((amnt*price)*.01 > this.bal_sheet.get("BTC")) {
			return "error not enough btc";
		} else {
			this.bal_sheet.put("BTC", this.bal_sheet.get("BTC") - (amnt*price)*.01);
			this.bal_sheet.put(coin, amnt-(amnt*.01));
			return String.format("bought: %d of %s", this.bal_sheet.get(coin), coin);
		}
	}
	
	public String sell_coin(String coin, double price) {
		double amnt = this.bal_sheet.get(coin);
		if(amnt == 0.0) {
			return String.format("cant sell current $s balance = 0.0", coin);
		}else {
			this.bal_sheet.put("BTC", amnt*price*.01);
			this.bal_sheet.put(coin, 0.0);
			return String.format("sold %d %s at %d", amnt, coin, price);
		}
	}
}
