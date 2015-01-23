package openidProviderPackage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;

import dbPackage.OPdbConnection;

/**
 * A class which fetches the pending authenticationrequest from relying parties
 * for the user from the database
 * 
 */
public class OPDataProvider extends HttpServlet {

	private OPdbConnection dbConnection;

	/**
	 * Creates a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		generateJsonResponse(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		generateJsonResponse(request, response);
	}

	/**
	 * Main mehtod which creates json strings of the AuthenticationRequest
	 * waiting for the user, if he has such waiting for him
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void generateJsonResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String jsonResponse = "{";

		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("OPUserName");
		String nonce = (String) session.getAttribute("nonce");
		if (userName != null && nonce != null) {
			AuthenticationRequest req = getUsersWaitingAuthRequest(nonce);
			if (req != null) {
				jsonResponse += "\"" + req.getClientID().toString() + "\":\""
						+ true + "\"}";
			} else {
				jsonResponse += "\"" + "No relyingParty" + "\":\"true\"}";
			}
		} else {
			jsonResponse += "\"" + "No relyingParty" + "\":\"true\"}";
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();
	}

	/**
	 * Gets the AuthenticationRequest with the same nonce
	 * 
	 * @param nonce
	 *            - The nonce the user had when creating the AuthRequest
	 */
	private AuthenticationRequest getUsersWaitingAuthRequest(String nonce) {
		return dbConnection.getAuthenticationRequest(nonce);
	}

}