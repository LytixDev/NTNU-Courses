class SekunderTilTid {
	public static void main(String[] args) {
		int antallSekunder = Integer.parseInt(args[0]);
		
		int timer = antallSekunder / 3600;
		int minutter = antallSekunder % 3600 / 60;
		int sekunder = antallSekunder % 3600 % 60;

		System.out.println("Timer: " + timer);
		System.out.println("Minutter: " + minutter);
		System.out.println("Sekunder: " + sekunder);

	}
}
