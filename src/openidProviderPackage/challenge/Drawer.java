package openidProviderPackage.challenge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Drawer {

	private static final int IMAGE_SIZE = 300;
	private static final int LINE_WIDTH = 2;

	public BufferedImage drawChallengeImage(Challenge c) {
		BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
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
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Square square = c.getSquare(i, j);

				if (square.isFilled()) {
					drawSquare(square, i * IMAGE_SIZE / 3, j * IMAGE_SIZE / 3);
				}
			}
		}
	}

	private void drawSquare(Square square, int x, int y) {

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
	public static void main(String[] args) {
		Challenge c = new Challenge();
		final BufferedImage image = new Drawer().drawChallengeImage(c);

		JPanel jPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(java.awt.Graphics g) {
				g.setColor(Color.green);
				g.fillRect(0, 0, 1000, 1000);
				g.drawImage(image, 500, 200, 300, 300, this);
			};
		};

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(jPanel);
		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		f.pack();
		f.setVisible(true);
	}
}