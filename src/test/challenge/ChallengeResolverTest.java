package test.challenge;

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
		// new User("Lena", positions, colors, password)

	}

	private void setUpDiagonalABC3FirstColorsChallenge() {
		Square[][] matrix = new Square[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == j) {
					char letter = (char) (65 + i); // will be A, B then C
					//matrix[i][j] = new Square(letter, Square.COLORS[i]);
				} else {
					matrix[i][j] = new Square();
				}
			}
		}

		someChallenge = new Challenge(matrix);
	}

	@Test
	public void withGivenChallengeAndUserThenLetterInSelectedPositionsArePartOfTheAnswer() {
		// TODO
	}
}
