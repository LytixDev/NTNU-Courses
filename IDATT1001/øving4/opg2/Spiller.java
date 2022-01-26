import java.util.Random;

class Spiller {

	int sumPoeng = 0;
	Random terning = new Random();

	public int getSumPoeng() {
		return sumPoeng;
	}

	public void kastTerningen() {
		int terningkast = terning.nextInt(6) + 1;
			if (terningkast != 1)
				sumPoeng += terningkast;
			else
				sumPoeng = 0;
	}

	public boolean erFerdig() {
		if (sumPoeng >= 100)
			return true;
		else
			return false;
	}
}
