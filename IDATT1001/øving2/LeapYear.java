import java.util.*;

class LeapYear {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Skriv inn et årstall: ");
		int year = Integer.parseInt(in.nextLine());

		boolean leapYear = false;

		// først sjekker om hundreår
		if(year % 100 == 0) {
			if(year % 400 == 0) {
				leapYear = true;
			}
		}
		else {
			if(year % 4 == 0) {
				leapYear = true;
			}
		}

		if(leapYear == true) {
			System.out.println(year + " er et skuddår");
		}
		else {
			System.out.println(year + " er ikke et skuddår");
		}

				
	}
}
