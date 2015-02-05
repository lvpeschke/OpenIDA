package test.challenge;

import static org.junit.Assert.*;
import openidProviderPackage.challenge.Challenge;
import openidProviderPackage.challenge.Square;

import org.junit.Before;
import org.junit.Test;

import dbPackage.User;

public class ChallengeResolverTest {

	private Challenge someChallenge;
	private User someUser;

	@Before
	public void setUp() {
		setUpDiagonalABC3FirstColorsChallenge();
		setUpAnyUser();
	}

	private void setUpAnyUser() {
		boolean[] colors = new boolean[] { false, true, false, false, true, true }; // brown,
																					// green,
																					// purple
		boolean[][] positions = new boolean[][] { { true, true, true }, { false, false, true },
				{ false, false, false } };
		String password = "CHRLIE";
		someUser = new User("Lena", positions, colors, password);

	}

	private void setUpDiagonalABC3FirstColorsChallenge() {
		Square[][] matrix = new Square[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == j) {
					char letter = (char) (65 + i); // will be A, B then C
					matrix[i][j] = new Square(letter, i);
				} else {
					matrix[i][j] = new Square();
				}
			}
		}

		matrix[2][1] = new Square('K', 0); // black

		someChallenge = new Challenge(matrix);
	}

	@Test
	public void withGivenChallengeAndUserThenLetterInSelectedPositionsArePartOfTheAnswer() {
		assertTrue(someChallenge.resolveFor(someUser).equals("ABC"));
	}

	public static void main(String[] args) {
		ChallengeResolverTest c = new ChallengeResolverTest();
		c.setUp();
		c.withGivenChallengeAndUserThenLetterInSelectedPositionsArePartOfTheAnswer();
	}
}
