package openidProviderPackage;

import java.io.IOException;

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
		String passwordRpt = request.getParameter("pwdRpt");

		if (validateAttempt(password, passwordRpt)) {
			dbConnection.saveNewUser(userName, password);
			setOPSession(request, userName);
			response.sendRedirect("/OPViews/loggedIn.html");
		} else {
			response.sendRedirect("/OPViews/logIn.html");
		}
	}

	/**
	 * Checks so that it's a valid user attempting to login
	 * 
	 * @param password
	 *            - First password
	 * @param password2
	 *            - Second password
	 * @return - Returns true if the passwords are demed correct, otherwise
	 *         false
	 */
	private boolean validateAttempt(String password, String password2) {
		if (password != null && password2 != null && password.equals(password2)) {
			return true;
		}
		return false;
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
}