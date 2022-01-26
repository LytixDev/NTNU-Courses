class BareSekunder {
	
	public static void main(String[] args) {
		int timer = 6;
		int minutter = 53;
		int sekunder = 12;

		int tilSekunder = timer * 3600 + minutter * 60 + sekunder;
		
		System.out.println(timer + "timer, " + minutter + "minutter, " + sekunder + "sekunder blir " + tilSekunder + "sekunder totalt");
		
	}
}

