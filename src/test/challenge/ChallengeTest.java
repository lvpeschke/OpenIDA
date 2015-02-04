package test.challenge;

import static org.junit.Assert.*;
import openidProviderPackage.challenge.Challenge;
import openidProviderPackage.challenge.Square;

import org.junit.Test;

public class ChallengeTest {

	@Test
	public void testFilledSquareBetween3And6() {
		Challenge c = new Challenge();
		int filledSquare = countFilledSquares(c);

		assertTrue(filledSquare >= 3);
		assertTrue(filledSquare <= 6);
	}

	private int countFilledSquares(Challenge c) {
		int filledSquare = 0;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Square square = c.getSquare(i, j);
				if (square.isFilled()) {
					filledSquare++;
				}
			}
		}
		return filledSquare;
	}

}
