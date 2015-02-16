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

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
		drawer = new Drawer();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet in ChallengeGenerator invoked!!!");

		Challenge c = new Challenge();

		String username = request.getPathInfo();
		
		System.out.println("in request user is: " + username);
		
		putExpectedAnswerIntoDB(c, username);

		setResponse(response, c);
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
		try {
			User user = dbConnection.getUserInfo(username);
			String answer = c.resolveFor(user);
			System.out.println("putExpectedAnswerIntoDB invoked: answer");
			dbConnection.saveExpectedAnswerOfUser(username, answer.toUpperCase());
		} catch (UserNotFoundException e) {
			// do nothing
		}
	}
}
