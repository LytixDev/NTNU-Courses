public class Klientprogram {

	public static void main(String[] args) {
		Tekstanalyse t = new Tekstanalyse("Dette er en streng");
		t.print();
		int unique = t.uniqueLetters();
		int total = t.totalLetters();
		String percentNotLetter = t.notLetters();
		int amountLetter = t.amountLetter('e');
		String mostFrequent = t.mostFrequent();

		System.out.println("Antall forksjellige bokstaver " + unique);
		System.out.println("Antall bokstaver " + total);
		System.out.println("Ikke bokstav " + percentNotLetter);
		System.out.println("Antall 'e'" + amountLetter);
		System.out.println("Mest frekvente bokstav(er)" + mostFrequent);

	}
}
