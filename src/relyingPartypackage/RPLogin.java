package relyingPartypackage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

import dbPackage.RPdbConnection;

/**
 * The class handling login attempts at the relying party
 * 
 */
public class RPLogin extends HttpServlet {

	private ClientID clientID = new ClientID("tddd17ClientId");
	private URI redirectBackURI;
	private URI openIdProviderURI;
	private RPdbConnection dbConnection;
	private static State state;
	private Nonce nonce;

	/**
	 * Create a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = RPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createAuthenticationrequestFlow(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createAuthenticationrequestFlow(request, response);
	}

	/**
	 * The main method for the flow of generating an Authenticationrequest
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 * @throws ServletException
	 */
	private void createAuthenticationrequestFlow(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		createURIS();
		generateOneTimers();

		// TODO Call the correct method below

		// AuthenticationRequest req =
		// createAuthenticationRequestXXXXXXXXXXXXXXXX;
		try {
			saveAuthRequest(req);
			saveNonceInSession(request, req);
			req.toHTTPRequest().send();
			response.sendRedirect("/OPViews/logIn.html");
		} catch (SerializeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the one-time State and Nonce to be used when creating the
	 * AuthenticationRequest
	 */
	private void generateOneTimers() {
		state = new State();
		nonce = new Nonce();
	}

	/**
	 * Creates the URIs for the AuthenticationRequest
	 */
	private void createURIS() {
		try {
			redirectBackURI = new URI(
					"http://127.0.0.1:8054/RelyingParty/callback");
			openIdProviderURI = new URI(
					"http://127.0.0.1:8054/OpenIdProvider/AuthenticationRequest");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a AuthenticationRequest with a id token
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithIDToken1() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI, new ResponseType(
						OIDCResponseTypeValue.ID_TOKEN), Scope.parse("openid"),
				clientID, redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Creates a AuthenticationRequest with a id token
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithIDToken2() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI, new ResponseType(
						OIDCResponseTypeValue.ID_TOKEN), Scope.parse("oid"),
				clientID, redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Creates a AuthenticationRequest with a code
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithCode1() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI, new ResponseType(ResponseType.Value.CODE),
				Scope.parse("oid"), clientID, redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Creates a AuthenticationRequest with a code
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithCode2() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI, new ResponseType(ResponseType.Value.CODE),
				Scope.parse("openid"), clientID, redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Creates a AuthenticationRequest with a id token and code
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithIDTokenAndCode1() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI,
				new ResponseType(OIDCResponseTypeValue.ID_TOKEN,
						ResponseType.Value.CODE), Scope.parse("openid"),
				clientID, redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Creates a AuthenticationRequest with a id token and code
	 * 
	 * @return AuthenticationRequest
	 */
	private AuthenticationRequest createAuthenticationRequestWithIDTokenAndCode2() {
		AuthenticationRequest req = new AuthenticationRequest(
				openIdProviderURI,
				new ResponseType(OIDCResponseTypeValue.ID_TOKEN,
						ResponseType.Value.CODE), Scope.parse("oid"), clientID,
				redirectBackURI, state, nonce);
		return req;
	}

	/**
	 * Saves the nonce in the session so that it can be used on the OP side to
	 * know which Request belong to the end user
	 */
	private void saveNonceInSession(HttpServletRequest request,
			AuthenticationRequest req) {
		HttpSession session = request.getSession();
		session.setAttribute("nonce", req.getNonce().toString());
	}

	/**
	 * Saves the AuthenticationRequest in the db
	 * 
	 * @param req
	 *            - The AuthenticationRequest to be saved
	 */
	private void saveAuthRequest(AuthenticationRequest req) {
		dbConnection.saveAuthRequestToDB(req);
	}
}