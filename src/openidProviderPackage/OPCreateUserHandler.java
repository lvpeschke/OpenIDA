package openidProviderPackage;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbPackage.OPdbConnection;

/**
 * The class handling attempts of creating a new user
 * 
 */
public class OPCreateUserHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private OPdbConnection dbConnection;

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet in CreateUser invoked!!!");
		handlNewUserAttempt(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost in CreateUser invoked!!!");
		handlNewUserAttempt(request, response);
	}

	/**
	 * The main method for creating new user
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	// adapted
	private void handlNewUserAttempt(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String userName = request.getParameter("user");
		System.out.println("user : " + userName);
		String password = request.getParameter("pwd");
		System.out.println("pwd : " + password);
		String positions = request.getParameter("positions"); // not yet in the right format
		System.out.println("pos : " + positions);
		String[] colors = request.getParameterValues("color"); // not yet in the right format
		System.out.println("colors : " + colors.toString());

		if (validateAttempt(password, positions, colors)) {
			// save user to the DB
			System.out.println("handlNewUserAttempt - passed test");
			int positionsForDB = extractPositionsFromStringToInt(positions);
			int colorsForDB = extractColorsFromStringArrayToInt(colors);
			dbConnection.saveNewUser(userName, password, positionsForDB, colorsForDB);
			// log the user in
			setOPSession(request, userName);
			// redirect
			response.sendRedirect("/OPViews/loggedIn.html");
		} else {
			response.sendRedirect("/OPViews/logIn.html");
		}
	}

	/**
	 * Checks so that it's a valid user attempting to sign up
	 * 
	 * @param password
	 *            - First password
	 * @param password2
	 *            - Second password
	 * @return - Returns true if the passwords are demed correct, otherwise
	 *         false
	 */
	// adapted
	private boolean validateAttempt(String password, String positions, String[] colors) {
		if (checkColors(colors) && checkPassword(password) && checkPositions(positions)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks that 2-4 colors are chosen
	 * 
	 * @param colors
	 * @return
	 */
	// added
	private boolean checkColors(String[] colors) {
		return (colors.length > 1) && (colors.length < 5);
	}

	/**
	 * Checks that the password has 4 different letters and at most 12 characters
	 * 
	 * @param password
	 * @return
	 */
	// added
	private boolean checkPassword(String password) {
		return (password.length() < 13) && (countDifferentLettersInString(password) > 3);
	}

	/**
	 * Checks that 3-7 positions are chosen
	 * 
	 * @param positions
	 * @return
	 */
	// added
	private boolean checkPositions(String positions) {
		// delete everything that is not a number
		String digits = discardNonDigits(positions);
		// actual check
		int numberOfDigits = countDifferentDigitsInString(digits);
		return (numberOfDigits > 2) &&
			   (numberOfDigits < 8);
	}

	/**
	 * Logs in the user by saving the users session id in the database
	 * 
	 * @param request
	 *            - The object handling the request
	 * @param userName
	 *            - The username of the user
	 */
	private void setOPSession(HttpServletRequest request, String userName) {
		HttpSession session = request.getSession();
		dbConnection.logInUser(session.getId());
		session.setAttribute("OPUserName", userName);
	}

	// Static methods
	/**
	 * Returns the substring only containing the characters 0-9
	 * 
	 * @param input
	 * @return
	 */
	private static String discardNonDigits(String input) {
		return input.replaceAll("[^\\d]", "");
	}
	
	/**
	 * Count the number of different letter characters in a String
	 * 
	 * @param input
	 * @return -1 if input contains no-letter-characters, otherwise the number of different letters
	 */
	// added
	private static int countDifferentLettersInString(String input) {
		ArrayList<Character> counter = new ArrayList<Character>();

		for (int i = 0; i < input.length(); i++) {
			char temp = input.charAt(i);
			// return immediately if not a letter
			if (!Character.isLetter(temp)) {
				return -1;
			}
			// add the letter to the counter
			if (!counter.contains(temp)) {
				counter.add(temp);
			}
		}
		return counter.size();
	}
	
	/**
	 * Count the number of different digits in a String
	 * 
	 * @param input
	 * @return -1 if input contains no-digit-characters, otherwise the number of different digits
	 */
	// added
	private static int countDifferentDigitsInString(String input) {
		ArrayList<Character> counter = new ArrayList<Character>();

		for (int i = 0; i < input.length(); i++) {
			char temp = input.charAt(i);
			// return immediately if not a digit
			if (!Character.isDigit(temp)) {
				return -1;
			}
			// add the letter to the counter
			if (!counter.contains(temp)) {
				counter.add(temp);
			}
		}
		return counter.size();
	}

	/**
	 * Extracts the selected matrix positions from a String and converts them to int
	 * 
	 * @param input
	 * @return
	 */
	// TODO check math
	// added
	private static int extractPositionsFromStringToInt(String input) {
		int positions = 0;
		
		// discards leading + if there is one
		if (input.charAt(0) == '+') {
			input = input.substring(1);
			System.err.println("There was a '+'-sign in the chosen matrix positions at user creation.");
		}

		// input contains a number, checked in checkIfNumber
		for (int i = 0; i < input.length(); i++) {
			int number = Integer.parseInt("" + input.charAt(i)); // dangerous!!
			number--;
			positions += ((int) Math.pow(2.0, number));
		}
		System.out.println("final positions: " + positions);
		return positions;
	}

	/**
	 * Extracts the selected colors from a String[] and converts them to int
	 * 
	 * @param input
	 * @return
	 */
	// TODO check math
	// added
	private static int extractColorsFromStringArrayToInt(String[] input) {
		int colors = 0;
		for (int i = 0; i < input.length; i++) {
			int colorInt = Integer.parseInt(input[i]); // dangerous!!
			colors += ((int) Math.pow(2.0, colorInt));
		}
		System.out.println("final color: " + colors);
		return colors;
	}
}