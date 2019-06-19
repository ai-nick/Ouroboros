package XchangeService;

public class HistDataBar {
	
	String timestamp;
	
	String symbol;
	double high;
	double open;
	double low;
	double close;
	int trades;
	double volume;
	double vwap;
	double lastSize;
	long turnover;
	double homeNotional;
	double foreignNotional;
	
	public HistDataBar() {
	}
	
	public double[] get_simple() {
		double[] simple = new double[] {
				this.open,
				this.low,
				this.close,
				//this.high,
				this.volume,
				this.vwap
		};
		return simple;
	}
}

