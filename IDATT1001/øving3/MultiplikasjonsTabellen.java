import java.util.*;

class MultiplikasjonsTabellen {
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Fra : ");
		int fra = Integer.parseInt(in.nextLine());
		System.out.println("Til : ");
		int til = Integer.parseInt(in.nextLine());

		for(int i = fra; i <= til; i++) {
			System.out.println("\n" + i + "gangen");
			for(int j = 1; j <= 10; j++) {
				System.out.println(i + "x" + j + "=" + i*j);
			}
		}

	}
}
