public class Main {
	
	public static void main(String[] args) {
		java.util.Random random = new java.util.Random();
		
		int[] antall = new int[10];
		int antallRunder = 100;

		for(int i=0; i < antallRunder; i++) {
			int tall = random.nextInt(10);
			antall[tall] += 1;
		}

		for(int i=0; i < antall.length; i++) {
			System.out.println("Antall " + i + "=" + antall[i]);
		}
	}
}
