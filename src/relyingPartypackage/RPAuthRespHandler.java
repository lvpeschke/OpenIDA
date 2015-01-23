package relyingPartypackage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import dbPackage.RPdbConnection;

/**
 * The class getting the response from a openID provider regarding the
 * authenticationrequest
 * 
 */
public class RPAuthRespHandler extends HttpServlet {

	private RPdbConnection dbConnection;

	/**
	 * Creates a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = RPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleResponse(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		handleResponse(request, response);
	}

	/**
	 * The main callback method handling the response from the openIdProvider
	 * when it has generated a AuthenticationResponse
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void handleResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		URI requestURI = recreateURI(request);

		AuthenticationResponse authResponse = null;
		try {
			authResponse = AuthenticationResponseParser.parse(requestURI);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (authResponse instanceof AuthenticationErrorResponse) {
		} else {
			AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResponse;
			if (isSameRequest(successResponse)) {
				JWT token = successResponse.getIDToken();
				setUserRPSessionName(token, request);
				response.sendRedirect("/RPViews/chat.html");
			} else {
			}
		}
	}

	/**
	 * Creates a cookie for the end user to prove that he is authenticated, and
	 * is used when sending the chat messages
	 * 
	 * @param token
	 *            - The id token taken from the AuthenticationSuccessResponse
	 * @param resp
	 *            - The object handling the response back to the user
	 */
	private void setUserRPSessionName(JWT token, HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			String name = token.getJWTClaimsSet().getSubject().toString();
			saveSessionIdToNameInDB(session.getId(), name);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the session id and username in the db for use in the chat
	 * 
	 * @param id
	 *            - The session id
	 * @param name
	 *            - The username
	 */
	private void saveSessionIdToNameInDB(String id, String name) {
		dbConnection.saveSessionIDToName(id, name);
	}

	/**
	 * Compares the request gotten with the requests stored
	 * 
	 * @param resp
	 *            - The AuthenticationResponse from the openIdProvider
	 * @return - If the response is to a previous request
	 */
	private boolean isSameRequest(AuthenticationSuccessResponse resp) {
		return dbConnection.compareAuthRequest(resp);
	}

	/**
	 * Hax to create a URI from the response from the openIdProvider, so that it
	 * can be used to create a AuthenticonResponse object
	 * 
	 * @param request
	 *            - The param from the openIdserver
	 * @return
	 */
	private URI recreateURI(HttpServletRequest request) {
		URI requestURI = null;
		String reqUrl = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += "?" + queryString;
		}
		try {
			requestURI = new URI(reqUrl);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		return requestURI;
	}
}
