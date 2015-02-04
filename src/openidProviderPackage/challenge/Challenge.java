package openidProviderPackage.challenge;

import dbPackage.User;

public class Challenge {

	private static final int MIN_FILLED_SQUARE = 3;
	private static final int MAX_FILLED_SQUARE = 6;

	private Square[][] matrix;

	public Challenge(Square[][] matrix) {
		this.matrix = matrix;
	}

	public Challenge() {
		matrix = new Square[3][3];

		fillMatrixWithEmptySquare();
		fillSomeSquares();
	}

	private void fillSomeSquares() {
		long nbFilledSquares = Math.round(Math.random() * (MAX_FILLED_SQUARE - MIN_FILLED_SQUARE))
				+ MIN_FILLED_SQUARE;

		for (int i = 0; i < nbFilledSquares; i++) {
			Square emptySquare = selectRandomEmptySquare();
			emptySquare.fill();
		}
	}

	private Square selectRandomEmptySquare() {
		int x;
		int y;

		do {
			x = (int) Math.round(Math.random() * 2);
			y = (int) Math.round(Math.random() * 2);
		} while (matrix[x][y].isFilled());

		return matrix[x][y];
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

	public String resolveFor(User user) {

		return null;
		// TODO Auto-generated method stub

	}

}
