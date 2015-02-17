package openidProviderPackage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbPackage.OPdbConnection;

/**
 * The class handling login attempts to the openID provider
 * 
 */
public class OPLoginHandler extends HttpServlet {
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
		System.out.println("doGet in LoginHandler!!");
		handlLoginAttempt(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost in LoginHandler!!");
		handlLoginAttempt(request, response);
	}

	/**
	 * The main method for login attempts on the openidProvider
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	// adapted
	private void handlLoginAttempt(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		System.out.println("handlLoginAttempt invoked");

		String userName = request.getParameter("user-login2");
		String answer = request.getParameter("answer");
		
		System.out.println("user2 - answer: " + userName + ", " + answer);

		String redirect;
		if (validateLogin(userName, answer)) {
			setOPSession(request, userName);
			redirect = "/OPViews/loggedIn.html";
		} else {
			redirect = "/OPViews/logIn.html";
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print("{\"redirect\": \"" + redirect + "\"}");
		out.flush();
	}

	/**
	 * Checks so that it's a valid user attempting to login
	 * 
	 * @param username
	 *            - The users username
	 * @param answer
	 *            - The users password
	 * @return
	 */
	// adapted
	private boolean validateLogin(String username, String answer) {
		System.out.println("validateLogin...");
		boolean result = dbConnection.validateUserAuthentication(username, answer);
		// delete the expected answer from the DB: no matter what, the challenge is over
		dbConnection.deleteExpectedAnswer(username);
		return result;
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