package openidProviderPackage.challenge;

import java.awt.Color;

public class Square {

	private boolean isFilled;
	private char letter;
	private int color;

	public Square() {
		isFilled = false;
	}

	public Square(char letter, int color) {
		this.isFilled = true;
		this.letter = letter;
		this.color = color;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void fill() {
		selectRandomLetter();
		selectRandomColor();

		this.isFilled = true;
	}

	private void selectRandomColor() {
		int randomColorIndex = (int) Math.round(Math.random() * 5);
		color = randomColorIndex;
	}

	private void selectRandomLetter() {
		// a letter between capital A and Z
		letter = (char) (Math.round(Math.random() * 25 + 65));
	}

	public char getLetter() {
		return letter;
	}

	public int getColor() {
		return color;
	}
}