package openidProviderPackage.challenge;

public class Challenge {

	private Square[][] matrix;

	public Challenge() {
		matrix = new Square[3][3];

		fillMatrixWithEmptySquare();
		fillSomeSquares();
	}

	private void fillSomeSquares() {
		// between 3 and 6 squares can be filled
		long nbFilledSquares = Math.round(Math.random() * 3) + 3;

		for (int i = 0; i < nbFilledSquares; i++) {
			int x = (int) Math.random() * 3;
			int y = (int) Math.random() * 3;
			matrix[x][y].fill();
		}
	}

	private void fillMatrixWithEmptySquare() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				matrix[i][j] = new Square();
			}
		}
	}

	public Square getSquare(int x, int y) {
		return matrix[x][y];
	}

}
