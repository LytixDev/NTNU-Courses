import java.util.*;

class Valuta {
	
	private double exchangeRate;

	public Valuta(String a) {
		if (a.equals("usd"))
			exchangeRate = 8.67;
		else if (a.equals("eur"))
			exchangeRate = 10.30;
		else if (a.equals("sek"))
			exchangeRate = 1.01;
	}

	public double tilNorske(double verdi) {
		return verdi * exchangeRate;
	}

	public double fraNorske(double verdi) {
		return verdi / exchangeRate;
	}
}
