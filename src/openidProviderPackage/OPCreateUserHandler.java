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
		int numberOfDigits = countDifferentDigitsInString(positions);
		return checkIfNumber(positions) &&
			   (numberOfDigits > 2) &&
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
	 * Count the number of different letter characters in a string
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
	 * Checks if a String is an integer number
	 * 
	 * @param input
	 * @return
	 */
	// added
	private static boolean checkIfNumber(String input) {
		try {
			if (Integer.parseInt(input) < 0) {
				System.out.println("checkIfNumber : negative int");
				return false;
			}
		} catch (NumberFormatException e) {
			System.out.println("checkIfNumber : not an int");
			return false;
		}
		return true;
	}

	/*
	private static byte[] extractPositionsFromString(String input) {
		byte[] positionsInBytes = { 0, 0, 0 };

		// discards leading + if there is one
		if (input.charAt(0) == '+') {
			input = input.substring(1);
		}

		for (int i = 0; i < input.length(); i++) {
			int number = Integer.parseInt("" + input.charAt(i));
			number--;
			int row = (int) number / 3;
			int column = number % 3;
			positionsInBytes[row] += 2 ^ column;
		}
		return positionsInBytes;
	}
	*/

	/**
	 * Extracts the selected matrix positions from a String and converts them to int
	 * 
	 * @param input
	 * @return
	 */
	// TODO check math
	// added
	private static int extractPositionsFromStringToInt(String input) {

		int positionsInInt = 0;
		
		// discards leading + if there is one
		if (input.charAt(0) == '+') {
			input = input.substring(1);
		}

		// input contains a number, checked in checkIfNumber
		for (int i = 0; i < input.length(); i++) {
			int number = Integer.parseInt("" + input.charAt(i));
			number--;
			positionsInInt += ((int) Math.pow(2.0, number));
		}
		System.out.println("final positions: " + positionsInInt);
		return positionsInInt;
	}

	/*
	private static byte extractColorsFromStringArray(String[] input) {
		byte colors = 0;
		for (int i = 0; i < input.length; i++) {
			int colorInt = Integer.parseInt(input[i]);
			colors += Math.pow(2, colorInt); // dangerous!!
		}
		System.out.println("final color: " + colors);
		return colors;
	}
	*/

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
			int colorInt = Integer.parseInt(input[i]); // dangerous!! check html code
			colors += ((int) Math.pow(2.0, colorInt));
		}
		System.out.println("final color: " + colors);
		return colors;
	}
}