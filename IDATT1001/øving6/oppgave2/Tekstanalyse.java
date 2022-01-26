import java.util.ArrayList;

public class Tekstanalyse {
	private int[] antallTegn = new int[30];
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzæøå";
	private String text;

	public Tekstanalyse(String text) {
		this.text = text;
		findChars(text.toLowerCase());
	}

	private void findChars(String text) {
		for(int i=0; i < text.length(); i++) {
			char c = text.charAt(i);

			if(ALPHABET.indexOf(c) == -1) {
				antallTegn[29] += 1;
			}
			else {
				antallTegn[ALPHABET.indexOf(c)] += 1;
			}
		}
	}

	public int uniqueLetters(){
		int unique = 0;		

		for(int i=0; i < antallTegn.length-1; i++) {  // -1 fordi vi ser bare på bokstaver
			if(antallTegn[i] != 0)
				unique += 1;
		}

		return unique;
	}

	public int totalLetters(){
		int total = 0;

		for(int i=0; i < antallTegn.length-1; i++) {  // -1 fordi vi ser bare på bokstaver
			total += antallTegn[i];
		}
		return total;
	}

	public String notLetters(){
		int total = totalLetters() + antallTegn[29];
		double percentNotLetter = (double) antallTegn[29] / (double) total;
		return percentNotLetter + "%";
	}

	public int amountLetter(char letter){
		return antallTegn[ALPHABET.indexOf(letter)];
	}

	public String mostFrequent() {
			int maxAntall = 0;
			StringBuilder str = new StringBuilder("");

			for (int i=0; i < antallTegn.length - 1; i++) {
				if(antallTegn[i] > maxAntall) {
					maxAntall = antallTegn[i];
					if (str.length() != 0 ) {
						str.deleteCharAt(str.length() - 1);
					}
					str.append(ALPHABET.charAt(i));
				} else if(antallTegn[i] == maxAntall && str.length() != 0) {
					str.append(ALPHABET.charAt(i));
				}
			}

			return str.toString();
		}

	public void print() {
		for(int i=0; i < antallTegn.length-1; i++) {
			System.out.println("Antall " + ALPHABET.charAt(i) + "=" + antallTegn[i]);
		}
		System.out.println("Antall ikke bokstaver=" + antallTegn[29]);
	}
}
