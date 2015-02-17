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
	
	// make username globally available
	private String receivedUserName = null;

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
		drawer = new Drawer();
	}

	// used when the server sends the challenge to the browser (get-response)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet in ChallengeGenerator invoked!!!");

		Challenge c = new Challenge();

		//String username = request.getPathInfo();
		//String requestString = request.toString();
		//String username = request.getParameter("user");
		//System.out.println("in request user is: " + username);
		//System.out.println("the request is: " + requestString);
		
		putExpectedAnswerIntoDB(c, receivedUserName);
		receivedUserName = null;

		setResponse(response, c);
	}
	
	// used when the browser posts the username
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost in ChallengeGenerator invoked!!!");
		
		String username = request.getParameter("user-login");
		System.out.println("in request user is: " + username);
		
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
		System.out.println("setResponse invoked");
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
		System.out.println("putExpectedAnswerIntoDB invoked, answer should follow");
		try {
			User user = dbConnection.getUserInfo(username);
			String answer = c.resolveFor(user);
			System.out.println("answer: " + answer);
			dbConnection.saveExpectedAnswerOfUser(username, answer.toUpperCase());
		} catch (UserNotFoundException e) {
			System.out.println("exception: " + e.getMessage());
			// do nothing
		}
	}
}
