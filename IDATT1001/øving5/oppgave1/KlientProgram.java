public class KlientProgram {

	public static void main(String[] args) {

		// summere
		Fraction fraction = new Fraction(2, 3);
		Fraction fraction2 = new Fraction(1, 2);

		System.out.println(fraction + "+" + fraction2);
		fraction.summer(fraction2);
		System.out.println(fraction + "\n");
		

		// subtrahere
		Fraction fraction3 = new Fraction(2, 3);

		System.out.println(fraction3 + "-" + fraction2);
		fraction3.subtraher(fraction2);
		System.out.println(fraction3 + "\n");

		// multiplisere
		Fraction fraction4 = new Fraction(2, 3);

		System.out.println(fraction4 + "*" + fraction2);
		fraction4.multipliser(fraction2);
		System.out.println(fraction4 + "\n");
		
		// dividere
		Fraction fraction5 = new Fraction(2, 3);

		System.out.println(fraction5 + "/" + fraction2);
		fraction5.divider(fraction2);
		System.out.println(fraction5);
	}
}
