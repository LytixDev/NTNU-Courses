public class Matrix {
	private int[][] matrix;

	public Matrix(int[][] matrix) {
		this.matrix = matrix;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public Matrix add(Matrix a, Matrix b){
		if (a.getMatrix().length != b.getMatrix().length)
			return null;

		int rows = a.getMatrix().length;
		int columns = a.getMatrix()[0].length;
		int[][] n = new int[rows][columns];
		
		for (int i=0; i < rows; i++) {
			for (int j=0; j < columns; j++) {
				n[i][j] = a.getMatrix()[i][j] + b.getMatrix()[i][j];
			}
		}
		
		return new Matrix(n);
	}
	
	public Matrix multiply(Matrix a, Matrix b){
		int rowsA = a.getMatrix().length;
		int columnsA = a.getMatrix()[0].length;
		int rowsB = b.getMatrix().length;
		int columnsB = b.getMatrix()[0].length;

		if (columnsA != rowsB)
			return null;
		
		int[][] n = new int[rowsA][columnsB];
		for (int i = 0; i < columnsB; i++) {
			for (int j = 0; j < rowsA; j++) {
				for (int k = 0; k < columnsA; k++) {
					n[j][i] += a.getMatrix()[j][k] * b.getMatrix()[k][i];
				}
			}
		}
		return new Matrix(n);
	}

	public Matrix transpose(){
		int rows = matrix.length;
		int columns = matrix[0].length;
		int[][] n = new int[columns][rows];

		for (int i=0; i < rows; i++) {
			for (int j=0; j < columns; j++) {
				n[j][i] = matrix[i][j];
			}
		}
		return new Matrix(n);

	}

	public String toString(){
		StringBuilder out = new StringBuilder("");

		for (int i=0; i < matrix.length; i++) {
			for (int j=0; j < matrix[i].length; j++) {
				out.append(matrix[i][j]);	
				if (j != matrix[i].length-1)
					out.append(",");
			}
			if (i != matrix.length-1)
				out.append("\n");
		}
		return "" + out;
	}

}
