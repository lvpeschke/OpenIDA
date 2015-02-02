package openidProviderPackage.challenge;

import java.awt.Color;

public class Square {

	private static final Color[] COLORS = { Color.BLACK, new Color(176, 117, 0), Color.BLUE,
			Color.RED, Color.GREEN, new Color(168, 95, 210) };

	private boolean isFilled;
	private char letter;
	private Color color;

	public Square() {
		isFilled = false;
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
		int randomColorIndex = (int) Math.random() * 6;
		color = COLORS[randomColorIndex];
	}

	private void selectRandomLetter() {
		// a letter between capital A and Z
		letter = (char) (Math.round(Math.random() * 26 + 65));
	}

	public char getLetter() {
		return letter;
	}

	public Color getColor() {
		return color;
	}
}