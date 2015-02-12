package openidProviderPackage.challenge;

import dbPackage.User;

/**
 * The challenge object
 */
// added
public class Challenge {

	private static final int MIN_FILLED_SQUARE = 3;
	private static final int MAX_FILLED_SQUARE = 6;

	private Square[][] matrix;

	/**
	 * Constructor
	 * 
	 * @param matrix
	 */
	public Challenge(Square[][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * Constructor that initializes the matrix and randomly fills it
	 */
	public Challenge() {
		matrix = new Square[3][3];

		fillMatrixWithEmptySquare();
		fillSomeSquares();
	}

	/**
	 * Randomly fill the squares of the challenge matrix
	 */
	private void fillSomeSquares() {
		long nbFilledSquares = Math.round(Math.random() * (MAX_FILLED_SQUARE - MIN_FILLED_SQUARE))
				+ MIN_FILLED_SQUARE;

		for (int i = 0; i < nbFilledSquares; i++) {
			Square emptySquare = selectRandomEmptySquare();
			emptySquare.fill();
		}
	}

	/**
	 * Randomly select a square in the challenge matrix
	 * 
	 * @return
	 */
	private Square selectRandomEmptySquare() {
		int x;
		int y;

		do {
			x = (int) Math.round(Math.random() * 2);
			y = (int) Math.round(Math.random() * 2);
		} while (matrix[x][y].isFilled());

		return matrix[x][y];
	}

	/**
	 * Initialize the challenge matrix with empty squares
	 */
	private void fillMatrixWithEmptySquare() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				matrix[i][j] = new Square();
			}
		}
	}

	/**
	 * Get a square inside the challenge matrix
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Square getSquare(int x, int y) {
		return matrix[x][y];
	}

	/**
	 * Get the challenge's answer for a given user
	 * 
	 * @param user
	 * @return
	 */
	public String resolveFor(User user) {
		String answer = "";

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				answer = addLetterForAllCorrespondingSquares(user, answer, i, j);
				// be careful with order of letters
			}
		}
		
		if (answer.isEmpty()) {
			answer = user.getPassword();
		}
		return answer;
	}

	/**
	 * Conditionally add a letter from a square from the challenge matrix to
	 * the answer string if it matches the user's secret
	 * 
	 * @param user
	 * @param answer
	 * @param i
	 * @param j
	 * @return
	 */
	private String addLetterForAllCorrespondingSquares(User user, String answer, int i, int j) {
		Square square = matrix[i][j];

		if (square.isFilled()) {
			if (isSquareCorrespondToUserChoices(square, i, j, user)) {
				answer += square.getLetter();
			}
		}

		return answer;
	}

	/**
	 * Determine whether a square from the challenge marix should be a part of the
	 * answer based on a user's secret (positions, colors, letters)
	 * 
	 * @param square
	 * @param i
	 * @param j
	 * @param user
	 * @return
	 */
	private boolean isSquareCorrespondToUserChoices(Square square, int i, int j, User user) {
		boolean[][] positions = user.getPositions();
		boolean[] colors = user.getColors();
		String password = user.getPassword();

		if (positions[i][j]) {
			return true;
		}

		if (colors[square.getColor()]) {
			return true;
		}

		if (password.indexOf(square.getLetter()) > -1) {
			return true;
		}
		return false;
	}
}
