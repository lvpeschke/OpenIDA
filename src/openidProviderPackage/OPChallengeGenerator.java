package openidProviderPackage;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import openidProviderPackage.challenge.Challenge;
import openidProviderPackage.challenge.Drawer;
import dbPackage.OPdbConnection;
import dbPackage.User;
import dbPackage.UserNotFoundException;

/**
 * This class generates the challenge for the authentication
 */
// added
public class OPChallengeGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private OPdbConnection dbConnection;
	private Drawer drawer;
	
	// make username globally available for GET and POST
	private String receivedUserName = null;

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
		drawer = new Drawer();
	}

	// used when the server sends the challenge to the browser (generates the get-response)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// generate new challenge
		Challenge c = new Challenge();		
		// remember the answer to the challenge for the user
		putExpectedAnswerIntoDB(c, receivedUserName);		
		// reset the user name
		receivedUserName = null;
		// send the challenge
		setResponse(response, c);
	}
	
	// used when the browser posts the username (handles the post-message)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String username = request.getParameter("user-login");
		// set the username to make it accessible globally
		receivedUserName = username;
	}

	/**
	 * Add a challenge to the response for the user
	 * The response will send the challenge matrix as an image to the user's browser.
	 * 
	 * @param response
	 * @param c
	 * @throws IOException
	 */
	private void setResponse(HttpServletResponse response, Challenge c) throws IOException {
		System.out.println("Sending a new challenge...");
		response.setContentType("image/jpeg");
		ImageIO.write(drawer.drawChallengeImage(c), "jpg", response.getOutputStream());
	}

	/**
	 * Put the expected answer to the challenge for the user in the DB
	 * 
	 * @param c
	 * @param username
	 */
	private void putExpectedAnswerIntoDB(Challenge c, String username) {
		try {
			User user = dbConnection.getUserInfo(username);
			String answer = c.resolveFor(user);
			dbConnection.saveExpectedAnswerOfUser(username, answer.toUpperCase());
			System.out.println("Expected answer for user "+ username + " --> " + answer);
		} catch (UserNotFoundException e) {
			System.err.println("The following user was not found in the DB: " + username);
		}
	}
}
