import java.util.*;

class Primtall {

	void sieveOfEratosthenes(int n) {
		// https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
		boolean prime[] = new boolean[n + 1];

		for(int i = 0; i <= n; i++) {
			prime[i] = true;
		}

		for(int p = 2; p*p <= n; p++) {
            if(prime[p] == true) {
                for(int i = p*p; i <= n; i += p) {
                    prime[i] = false;
				}
            }
        }
		
		if(prime[n] == true && n != 1){
			System.out.println(n + " er et primtall");
		}
		else {
			System.out.println(n + " er ikke et primtall");
		}
	}

	public static void main(String[] args) {
		Primtall g = new Primtall();

		boolean running = true;
		while(running == true){
			System.out.println("Skriv et tall: ");
			Scanner in = new Scanner(System.in);
			int value = Integer.parseInt(in.nextLine());
			g.sieveOfEratosthenes(value);
			
		}
	}
}
