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

	public BufferedImage drawChallengeImage(Challenge c) {
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		drawMatrix(c, g);

		return image;
	}

	private void drawMatrix(Challenge c, Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE); // background

		g.setColor(Color.BLACK);
		drawBorders(g);
		drawTicTacToe(g);

		drawSquares(g, c);
	}

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

	private void drawSquare(Graphics2D g, Square square, int x, int y) {
		g.setColor(COLORS[square.getColor()]);
		g.setFont(new Font("Courier", Font.PLAIN, FONT_SIZE));
		g.drawString("" + square.getLetter(), x, y);

	}

	private void drawTicTacToe(Graphics2D g) {
		g.setStroke(new BasicStroke(LINE_WIDTH));
		drawHorizontalTTTLines(g);
		drawVerticalTTTLines(g);
	}

	private void drawVerticalTTTLines(Graphics2D g) {
		g.drawLine(IMAGE_SIZE / 3, 0, IMAGE_SIZE / 3, IMAGE_SIZE);
		g.drawLine(2 * IMAGE_SIZE / 3, 0, 2 * IMAGE_SIZE / 3, IMAGE_SIZE);
	}

	private void drawHorizontalTTTLines(Graphics2D g) {
		g.drawLine(0, IMAGE_SIZE / 3, IMAGE_SIZE, IMAGE_SIZE / 3);
		g.drawLine(0, 2 * IMAGE_SIZE / 3, IMAGE_SIZE, 2 * IMAGE_SIZE / 3);
	}

	private void drawBorders(Graphics2D g) {
		g.setStroke(new BasicStroke(2 * LINE_WIDTH));
		g.drawLine(0, 0, IMAGE_SIZE, 0);
		g.drawLine(0, IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE);
		g.drawLine(0, 0, 0, IMAGE_SIZE);
		g.drawLine(IMAGE_SIZE, 0, IMAGE_SIZE, IMAGE_SIZE);
	}

	/*
	 * TESTING
	 */
	// public static void main(String[] args) {
	// Challenge c = new Challenge();
	// final BufferedImage image = new Drawer().drawChallengeImage(c);
	//
	// JPanel jPanel = new JPanel() {
	// private static final long serialVersionUID = 1L;
	//
	// public void paintComponent(java.awt.Graphics g) {
	// g.setColor(Color.green);
	// g.fillRect(0, 0, 1000, 1000);
	// g.drawImage(image, 500, 200, 300, 300, this);
	// };
	// };
	//
	// JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// f.add(jPanel);
	// f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	// f.pack();
	// f.setVisible(true);
	// }
}