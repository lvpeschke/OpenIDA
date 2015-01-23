package openidProviderPackage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The class handling the logout from the user on the openID provider
 * 
 */
public class OPLogOut extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logOut(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logOut(request, response);
	}

	/**
	 * Logs out the user by removing the session attribute acknowledging
	 * authentication
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void logOut(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("OPUserName");
		response.sendRedirect("/OPViews/logIn.html");
	}

}
