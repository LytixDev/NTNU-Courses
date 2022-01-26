public class Fraction {

	private int teller;
	private int nevner;

	public Fraction(int teller, int nevner) {
		if(nevner == 0)
			throw new IllegalArgumentException("Nevner kan ikke være 0");

		this.teller = teller;
		this.nevner = nevner;
	}

	public Fraction(int teller) {
		this.teller = teller;	
		this.nevner = 1;
	}

	// get metoder
	public int getTeller() {
		return teller;
	}

	public int getNevner() {
		return nevner;
	}

	// overskride toString
	public String toString() {
		return teller + "/" + nevner;
	}

	//public void forkort() {
	//	boolean ferdig = false;
	//	int faktor = 2;

	//	while(!ferdig) {
	//		if((faktor > nevner && faktor > teller) || (nevner % faktor == 0 && teller % faktor == 0)) {
	//			ferdig = true;
	//		} else {
	//			faktor++;
	//		}
	//		
	//		if(faktor <= nevner && faktor <= teller) {
	//			teller /= faktor;
	//			nevner /= faktor;
	//		}
	//	}

	//}

	// funksjonalitet
	public void summer(Fraction fraction){
		if(nevner == fraction.getNevner()) {
			teller += fraction.getTeller();
		} else {
			int andreTeller = fraction.getTeller() * nevner;

			teller *= fraction.getNevner();
			nevner *= fraction.getNevner();

			teller += andreTeller;
		}
	}	

	public void subtraher(Fraction fraction){
		if(nevner == fraction.getNevner()) {
			teller -= fraction.getTeller();
		} else {
			int andreTeller = fraction.getTeller() * nevner;

			teller *= fraction.getNevner();
			nevner *= fraction.getNevner();

			teller -= andreTeller;
		}
	}

	public void multipliser(Fraction fraction){
		teller *= fraction.getTeller();	
		nevner *= fraction.getNevner();	
	}

	public void divider(Fraction fraction){
		teller *= fraction.getNevner();	
		nevner *= fraction.getTeller();	
	}

}
