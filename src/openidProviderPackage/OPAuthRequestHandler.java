package openidProviderPackage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

import dbPackage.OPdbConnection;

/**
 * The class that gets authenticationRequests from relying parties
 * 
 */
public class OPAuthRequestHandler extends HttpServlet {

	private Map<String, String> params;
	private OPdbConnection dbConnection;

	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleNewAuthRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleNewAuthRequest(request, response);
	}

	/**
	 * The main method for handling a new AuthenticationRequest
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void handleNewAuthRequest(HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("OP - Recieved an authenticationrequest");

		recreateAuthRequest(request);

		AuthenticationRequest req = null;
		try {
			req = AuthenticationRequest.parse(params);
		} catch (ParseException e) {
			System.out.println("Error parsing request");
			e.printStackTrace();
		}

		if (validateAuthenticatonRequest(req)) {
			saveRequest(req);
		} else {
			System.out
					.println("The request is either invalid or is not a correct request");
		}

	}

	/**
	 * Stores the parameters from the request in a hashmap
	 * 
	 * @param request
	 *            - The object containing the request
	 */
	private void recreateAuthRequest(HttpServletRequest request) {
		params = new HashMap<>();
		if (request != null) {
			params.put("response_type", request.getParameter("response_type"));
			params.put("client_id", request.getParameter("client_id"));
			params.put("redirect_uri", request.getParameter("redirect_uri"));
			params.put("scope", request.getParameter("scope"));
			params.put("state", request.getParameter("state"));
			params.put("nonce", request.getParameter("nonce"));
		}
	}

	/**
	 * Saves the authenticationrequest in db if it vas a valid one
	 * 
	 * @param req
	 *            - The AuthenticationRequest
	 */
	private void saveRequest(AuthenticationRequest req) {
		dbConnection.saveAuthRequestToDB(req);
	}

	/**
	 * Checks whether the authenticationrequest is a valid one according to
	 * parameters
	 * 
	 * @param req
	 *            - The AuthenticationRequest
	 * @return
	 */
	private boolean validateAuthenticatonRequest(AuthenticationRequest req) {
		boolean isValid = false;
		String responseType = req.getResponseType().toString();

		// TODO There exists one or more error in the if statement condition

		if (dbConnection.validateCLientID(req.getClientID())
				&& isIDToken(responseType) && isCode(responseType)) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Checks if the response type is a idToken
	 * 
	 * @param response
	 *            type
	 * @return
	 */
	private boolean isIDToken(String rt) {
		return OIDCResponseTypeValue.ID_TOKEN.toString().equals(rt);
	}

	/**
	 * Checks if the response type is a code
	 * 
	 * @param response
	 *            type
	 * @return
	 */
	private boolean isCode(String rt) {
		return ResponseType.Value.CODE.toString().equals(rt);
	}
}