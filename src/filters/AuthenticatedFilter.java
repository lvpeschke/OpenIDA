package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbPackage.OPdbConnection;
import dbPackage.RPdbConnection;

/**
 * Filter to make sure only authenticated users gets access to the correct views
 * 
 */
public class AuthenticatedFilter implements Filter {

	private RPdbConnection RPDBConnection;
	private OPdbConnection OPDBConnection;

	@Override
	public void destroy() {
	}

	/**
	 * The main filter method, needed since OP and RP is in the same server, so
	 * used to differentiate the requests
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String url = req.getRequestURL().toString();

		try {
			if (url.contains("RP")) {
				filterRPRequest(req, res, chain, url);
			} else if (url.contains("OP")) {
				filterOPRequest(req, res, chain, url);
			} else {
				System.out.println("No html for that, redirect to default");
				res.sendRedirect("/");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.sendRedirect("/");
		}
	}

	/**
	 * Filters the requests for html files for the OpenID provider, only
	 * allowing logged in users access to all but the index page
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @param url
	 * @throws IOException
	 * @throws ServletException
	 */
	private void filterOPRequest(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain, String url)
			throws IOException, ServletException {

		HttpSession session = request.getSession();
		String id = (String) session.getId();
		boolean isLoggedIn = OPDBConnection.isLoggedIn(id);
		if (url.contains("loggedIn.html") && isLoggedIn) {
			chain.doFilter(request, response);
		} else if (url.contains("logIn.html")) {
			chain.doFilter(request, response);
		} else {
			response.sendRedirect("/OPViews/logIn.html");
		}
	}

	/**
	 * Filters the requests for html files for the Relying party, only allowing
	 * logged in users access to all but the index page
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @param url
	 * @throws IOException
	 * @throws ServletException
	 */
	private void filterRPRequest(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain, String url)
			throws IOException, ServletException {

		HttpSession session = request.getSession();
		String id = (String) session.getId();
		boolean isLoggedIn = RPDBConnection.isLoggedIn(id);
		if (url.contains("chat.html") && isLoggedIn) {
			chain.doFilter(request, response);
		} else if (url.contains("index.html")) {
			chain.doFilter(request, response);
		} else {
			response.sendRedirect("/RPViews/index.html");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		RPDBConnection = RPDBConnection.getConnection();
		OPDBConnection = OPDBConnection.getConnection();
	}

}
