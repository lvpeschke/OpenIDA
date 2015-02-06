package openidProviderPackage;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
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

	private OPdbConnection dbConnection;

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handlNewUserAttempt(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
	private void handlNewUserAttempt(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String userName = request.getParameter("user");
		String password = request.getParameter("pwd");
		String positions = request.getParameter("positions"); // not yet in the right format
		String[] colors = request.getParameterValues("color"); // not yet in the right format

		if (validateAttempt(password, positions, colors)) {
			byte[] positionsForDB = extractPositionsFromString(positions);
			byte colorsForDB = extractColorsFromStringArray(colors);

			dbConnection.saveNewUser(userName, password, positionsForDB, colorsForDB);
			setOPSession(request, userName);
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
	private boolean validateAttempt(String password, String positions, String[] colors) {
		if ( checkColors(colors) && checkPassword(password) && checkPositions(positions)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkColors(String[] colors) {
		return (countDifferentColors(colors) > 1) && (countDifferentColors(colors) < 5);
	}

	private boolean checkPassword(String password) {
		return (password.length() < 13) && (countDifferentLettersInString(password) > 3);
	}

	private boolean checkPositions(String positions) {
		return (checkIfNumber(positions) && 
				(countDifferentLettersInString(positions) > 2) && 
				(countDifferentLettersInString(positions) < 8));
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
	private static int countDifferentLettersInString(String input) {
		ArrayList<Character> counter = new ArrayList<Character>();

		for (int i=0; i<input.length(); i++) {
			if (!counter.contains(input.charAt(i))) {
				counter.add(input.charAt(i));
			}
		}

		return counter.size();
	}

	private static boolean checkIfNumber(String input) {
		try {
			if (Integer.parseInt(input) < 0) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private static int countDifferentColors(String[] input) {
		int counter = 0;
		for (int i=0; i<input.length; i++) {
			if (input[i] != null) {
				counter++;
			}
		}
		return counter;
	}

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

	private static byte extractColorsFromStringArray(String[] input) {
		byte colors = 0;
		for (int i=0; i<input.length; i++) {
			colors += 2^Integer.parseInt(input[i]); // dangerous!!			
		}
		return colors;
	}
}