public class Main {
	
	public static void main(String[] args) {
		
		//int[][] v1 = {{1, 2}, {3, 4}};
		//int[][] v2 = {{2, 2}, {1, 1}};
		int[][] v1 = {{1, 0, -1}, {1, 2, 0}};
		int[][] v2 = {{1, 0, -1}, {3, 2, 0}, {-1, 1, 2}};
		Matrix m1 = new Matrix(v1);
		Matrix m2 = new Matrix(v2);
		
		System.out.println("Original matrise\n" + m1);
		System.out.println("Transponert matrise\n" + m1.transpose());
		System.out.println("\n");

		Matrix m3 = m1.add(m1, m2);
		System.out.println(m1 + "\n+\n" + m2 + "\n=\n" + m3);
		System.out.println("\n");
		
		Matrix m4 = m1.multiply(m1, m2);
		System.out.println(m1 + "\n*\n" + m2 + "\n=\n" + m4);
		System.out.println("\n");
	}
}
