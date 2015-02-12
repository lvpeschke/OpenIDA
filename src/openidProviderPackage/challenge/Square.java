package openidProviderPackage.challenge;

/**
 * Class holding the matrix information
 *
 */
// added
public class Square {

	private boolean isFilled;
	private char letter;
	private int color;

	/**
	 * Constructor for an empty square
	 */
	public Square() {
		isFilled = false;
	}

	/**
	 * Constructor for a filled square
	 * 
	 * @param letter
	 * @param color
	 */
	public Square(char letter, int color) {
		this.isFilled = true;
		this.letter = letter;
		this.color = color;
	}

	/**
	 * Determines whether a square is filled with content (letter and colour)
	 * 
	 * @return
	 */
	public boolean isFilled() {
		return isFilled;
	}

	/**
	 * Randomly fill a square with a letter and a colour
	 */
	public void fill() {
		selectRandomLetter();
		selectRandomColor();

		this.isFilled = true;
	}

	/**
	 * Set the square's colour to a random colour among the 6 possible
	 */
	private void selectRandomColor() {
		int randomColorIndex = (int) Math.round(Math.random() * 5);
		color = randomColorIndex;
	}

	/**
	 * Set the square's letter to a random capital letter between A and Z
	 */
	private void selectRandomLetter() {
		letter = (char) (Math.round(Math.random() * 25 + 65));
	}

	/*
	 * Getters
	 */
	public char getLetter() {
		return letter;
	}
	public int getColor() {
		return color;
	}
}