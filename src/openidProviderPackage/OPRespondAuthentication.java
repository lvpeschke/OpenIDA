package openidProviderPackage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

import dbPackage.OPdbConnection;

/**
 * The class handling the replying of AuthenticationRequests to relying parties
 * 
 */
public class OPRespondAuthentication extends HttpServlet {

	private OPdbConnection dbConnection;
	private HashMap<String, String> hashMap = new HashMap<>();
	private String issuerName = "http://tddd17identityProvider.com";

	/**
	 * Create a database connection
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createAuthenticationSuccessResponse(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		createAuthenticationSuccessResponse(request, response);
	}

	/**
	 * The main method for creating a authenticationresponse and sending it back
	 * to the relayingparty
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void createAuthenticationSuccessResponse(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		boolean sentResponse = false;

		generateResponseMap(request);
		if (!hashMap.isEmpty()) {
			for (Entry<String, String> entry : hashMap.entrySet()) {
				if ("tddd17ClientId".equals(entry.getKey())
						&& "on".equals(entry.getValue())) {
					AuthenticationRequest req = getCorrectAuthRequest(request);
					// Create response
					String authString = generateAuthenticationSuccessResponseString(
							req, request);
					sentResponse = true;
					response.sendRedirect(authString);
				}
			}
			if (!sentResponse) {
				// No response was sent, send user back
				response.sendRedirect("/OPViews/loggedIn.html");
			}
		} else {
			// No user supplied Authenticationrequest to respond to
			response.sendRedirect("/OPViews/loggedIn.html");
		}
	}

	/**
	 * Creates a authenticationSuccessRespons in the form of a string with a
	 * little hax
	 * 
	 * @param jwtIdToken
	 *            - the idtoken to be sent
	 * @return
	 */
	private String generateAuthenticationSuccessResponseString(
			AuthenticationRequest req, HttpServletRequest request) {
		AuthenticationSuccessResponse authenticationResponse = null;
		String trueAuthenticationString = "";
		PlainJWT jwtIdToken = null;
		AuthorizationCode code = null;
		AccessToken accToken = null;

		JWTClaimsSet claimsSet = generateJWTClaims(req, request);
		jwtIdToken = new PlainJWT(claimsSet);

		try {
			authenticationResponse = new AuthenticationSuccessResponse(new URI(
					""), code, jwtIdToken, accToken, req.getState());

			trueAuthenticationString = req.getRedirectionURI().toString() + "?"
					+ authenticationResponse.toURI().toString().substring(1);
		} catch (URISyntaxException | SerializeException e1) {
			e1.printStackTrace();
		}
		return trueAuthenticationString;
	}

	private boolean containsIDToken(AuthenticationRequest req) {
		if (OIDCResponseTypeValue.ID_TOKEN.equals(req.getResponseType())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean containsCode(AuthenticationRequest req) {
		if (ResponseType.Value.CODE.equals(req.getResponseType())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Puts all the user response parameters in a hashmap, somewhat of a dirty
	 * code, but used since we only will have one authenticationRequest waiting
	 * for the user
	 * 
	 * @param req
	 */
	private void generateResponseMap(HttpServletRequest req) {
		Enumeration<String> parameterNames = req.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = req.getParameter(paramName);
			hashMap.put(paramName, paramValue);
		}
	}

	/**
	 * Generates a JWTTOken which contains information about ISS, subject, etc.
	 * 
	 * @param request
	 *            - The user request
	 * 
	 * @return - The Jwt token for the AuthenticationRequest
	 */
	private JWTClaimsSet generateJWTClaims(AuthenticationRequest req,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("OPUserName");
		JWTClaimsSet jwtClaims = new JWTClaimsSet();
		jwtClaims.setIssuer(issuerName);
		jwtClaims.setSubject(userName);
		List<String> aud = new ArrayList<>();
		System.out.println(req.getClientID().toString()); //TODO
		aud.add(req.getClientID().toString());
		jwtClaims.setAudience(aud);
		jwtClaims.setExpirationTime(new Date(
				new Date().getTime() + 1000 * 60 * 10));
		jwtClaims.setNotBeforeTime(new Date());
		jwtClaims.setIssueTime(new Date());
		jwtClaims.setJWTID(UUID.randomUUID().toString());
		return jwtClaims;
	}

	/**
	 * Fetches the AuthenticationRequest with the users nonce
	 * 
	 * @param request
	 * @return
	 */
	private AuthenticationRequest getCorrectAuthRequest(
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		String nonce = (String) session.getAttribute("nonce");
		if (nonce != null) {
			return getUsersWaitingAuthRequest(nonce);
		}
		return null;
	}

	/**
	 * Gets the AuthenticationRequest with the same nonce
	 * 
	 * @param state
	 *            - The nonce the user had when creating the AuthRequest and is
	 *            stored in a cookie for sessioning
	 */
	private AuthenticationRequest getUsersWaitingAuthRequest(String nonce) {
		return dbConnection.getAuthenticationRequest(nonce);
	}
}
