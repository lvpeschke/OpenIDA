package openidProviderPackage.challenge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Class needed to display the challenge
 * 
 */
// added
public class Drawer {

	private static final int IMAGE_SIZE = 200;
	private static final int LINE_WIDTH = 2;
	private static final int FONT_SIZE = 40;
	public static final Color[] COLORS = { Color.BLACK, new Color(176, 117, 0), Color.BLUE,
		Color.RED, Color.GREEN, new Color(168, 95, 210) };

	/**
	 * Constructor
	 * 
	 * @param c
	 * @return
	 */
	public BufferedImage drawChallengeImage(Challenge c) {
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		drawMatrix(c, g);

		return image;
	}

	/**
	 * Create an image for the challenge matrix
	 * 
	 * @param c
	 * @param g
	 */
	private void drawMatrix(Challenge c, Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE); // background

		g.setColor(Color.BLACK);
		drawBorders(g);
		drawTicTacToe(g);

		drawSquares(g, c);
	}

	/**
	 * Draw the squares and their content for the challenge matrix
	 * 
	 * @param g
	 * @param c
	 */
	private void drawSquares(Graphics2D g, Challenge c) {

		int margin = (int) ((IMAGE_SIZE / 3 - FONT_SIZE) / 1.5);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Square square = c.getSquare(i, j);

				if (square.isFilled()) {
					drawSquare(g, square, j * IMAGE_SIZE / 3 + margin, (i + 1) * IMAGE_SIZE / 3
							- margin);
				}
			}
		}
	}

	/**
	 * Draw one square and a coloured letter inside it
	 * 
	 * @param g
	 * @param square
	 * @param x
	 * @param y
	 */
	private void drawSquare(Graphics2D g, Square square, int x, int y) {
		g.setColor(COLORS[square.getColor()]);
		g.setFont(new Font("Courier", Font.PLAIN, FONT_SIZE));
		g.drawString("" + square.getLetter(), x, y);

	}

	/**
	 * Draw a matrix
	 * 
	 * @param g
	 */
	private void drawTicTacToe(Graphics2D g) {
		g.setStroke(new BasicStroke(LINE_WIDTH));
		drawHorizontalTTTLines(g);
		drawVerticalTTTLines(g);
	}

	/**
	 * Draw vertical lines
	 * 
	 * @param g
	 */
	private void drawVerticalTTTLines(Graphics2D g) {
		g.drawLine(IMAGE_SIZE / 3, 0, IMAGE_SIZE / 3, IMAGE_SIZE);
		g.drawLine(2 * IMAGE_SIZE / 3, 0, 2 * IMAGE_SIZE / 3, IMAGE_SIZE);
	}

	/**
	 * Draw horizintal lines
	 * 
	 * @param g
	 */
	private void drawHorizontalTTTLines(Graphics2D g) {
		g.drawLine(0, IMAGE_SIZE / 3, IMAGE_SIZE, IMAGE_SIZE / 3);
		g.drawLine(0, 2 * IMAGE_SIZE / 3, IMAGE_SIZE, 2 * IMAGE_SIZE / 3);
	}

	/**
	 * Draw borders
	 * 
	 * @param g
	 */
	private void drawBorders(Graphics2D g) {
		g.setStroke(new BasicStroke(2 * LINE_WIDTH));
		g.drawLine(0, 0, IMAGE_SIZE, 0);
		g.drawLine(0, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE);
		g.drawLine(0, 0, 0, IMAGE_SIZE);
		g.drawLine(IMAGE_SIZE, 0, IMAGE_SIZE, IMAGE_SIZE);
	}
}
