public class KlientProgram {

	public static void main(String[] args) {
		Spiller spillerA = new Spiller();
		Spiller spillerB = new Spiller();

		int runder = 1;
		boolean ferdig = false;
		String vinner = "ingen";
		while (!ferdig) {
			spillerA.kastTerningen();
			spillerB.kastTerningen();

			System.out.println("runde: " + runder);
			System.out.println("A sum: " + spillerA.getSumPoeng());
			System.out.println("B sum: " + spillerB.getSumPoeng());
			System.out.println("\n");
			runder += 1;

			if (spillerA.erFerdig() == true) {
				vinner = "a";
				ferdig = true;
			}
			if (spillerB.erFerdig() == true) {
				if (vinner == "a")
					vinner = "begge";
				else
					vinner = "b";
				ferdig = true;
			}
		}

		System.out.println(vinner + " vant!");
	}
}
