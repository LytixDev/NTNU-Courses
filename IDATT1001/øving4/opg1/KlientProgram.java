import java.util.*;
public class KlientProgram {
	
	public static void main(String[] args) {
		Valuta usd = new Valuta("usd");
		Valuta eur = new Valuta("eur");
		Valuta sek = new Valuta("sek");


		boolean running = true;	
		Valuta current = usd;  // må initialisere verdiene ellers compiler errror
		String a = "";
		while (running == true) {
			Scanner in = new Scanner(System.in);

			System.out.println("Velg valuta:");
			System.out.println("1: Dollar");
			System.out.println("2: Euro");
			System.out.println("3: Svenske Kroner");
			System.out.println("4: Avslutt");

			int option = in.nextInt();	

			if (option == 1) {
				current = usd;
				a = "usd";
			} else if (option == 2) {
				current = eur;
				a = "eur";
			} else if (option == 3) {
				current = sek;
				a = "sek";
			} else if (option == 4)
				running = false;

			if (option != 4) {
				System.out.println("Hvor mye " + a + " vil du konvertere til nok?");	
				double value = in.nextInt();
				System.out.println(value + a + " blir " + current.tilNorske(value) + "nok");
			}
		}
	}
}
