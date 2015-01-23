package serverPackage;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import relyingPartypackage.*;
import webSocketPackage.Chatroom;
import webSocketPackage.ChatroomSocket;
import openidProviderPackage.*;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import dbPackage.OPdbConnection;
import dbPackage.RPdbConnection;
import filters.AuthenticatedFilter;

/**
 * Main class for starting and setting up the server for both the relying party
 * and the openID provider
 */
public class ServerBooter {

	/**
	 * The port to be used
	 */
	private final static int port = 8054;

	/**
	 * The embedded Jetty server we will use.
	 */
	private Server server = null;

	/**
	 * The chatroom for this server.
	 */
	private static Chatroom chatroom = new Chatroom();

	public static void main(String[] args) {

		// Create the object
		ServerBooter server = new ServerBooter();
		// start the server
		server.startServer();
	}

	/**
	 * Starts the server at the specified port and sets up URIs and the like
	 * 
	 */
	public void startServer() {
		server = new Server(port);
		//OPdbConnection.createClass();
		//RPdbConnection.createClass();

		WebAppContext contextFilter = generateResourcesAndFilters();

		ServletContextHandler context = generateRESTApi();

		// set up the web socket handler
		ContextHandler wsocketHandler = setupWebSocket();

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { wsocketHandler, context,
				contextFilter });

		server.setHandler(handlers);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			System.out.println("Server crashed while starting");
		}
		System.out.println("Started.");
	}

	/**
	 * Starts the chatroom with a websocket for two way communication
	 * 
	 * @return
	 */
	private ContextHandler setupWebSocket() {
		ContextHandler contextHandler = new ContextHandler();
		contextHandler.setContextPath("/");
		contextHandler.setHandler(new WebSocketHandler() {

			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.setCreator(new WebSocketCreator() {
					public Object createWebSocket(UpgradeRequest req,
							UpgradeResponse resp) {
						String query = req.getRequestURI().toString();
						if ((query == null) || (query.length() <= 0)) {
							try {
								resp.sendForbidden("Unspecified query");
							} catch (IOException e) {

							}
							return null;
						}
						return new ChatroomSocket();
					}
				});
			}
		});
		return contextHandler;
	}

	/**
	 * Sets up a context for the server of where the resources are to be found
	 * and accessed
	 * 
	 * @return
	 */
	private WebAppContext generateResourcesAndFilters() {

		WebAppContext cntxt = new WebAppContext();
		cntxt.setResourceBase("WEB-INF");
		cntxt.setWelcomeFiles(new String[] { "RPViews/index.html" });

		// cntxt.addFilter(new FilterHolder(new AuthenticatedFilter()),
		// "*.html",
		// EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST,
		// DispatcherType.FORWARD));

		return cntxt;
	}

	/**
	 * Set up all the servlets to be used and their corresponding URI
	 * 
	 * @return
	 */
	private ServletContextHandler generateRESTApi() {

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);

		context.addServlet(new ServletHolder(new RPLogin()),
				"/RelyingParty/Login/*");

		context.addServlet(new ServletHolder(new RPAuthRespHandler()),
				"/RelyingParty/callback/*");

		context.addServlet(new ServletHolder(new OPAuthRequestHandler()),
				"/OpenIdProvider/AuthenticationRequest/*");

		context.addServlet(new ServletHolder(new OPLoginHandler()),
				"/OpenIdProvider/OpLogin/*");

		context.addServlet(new ServletHolder(new OPCreateUserHandler()),
				"/OpenIdProvider/OpCreateUser/*");

		context.addServlet(new ServletHolder(new OPSetUserPicture()),
				"/OpenIdProvider/OPSetPic/*");

		context.addServlet(new ServletHolder(new OPGetUserPicHandler()),
				"/OpenIdProvider/OPGetPic/*");

		context.addServlet(new ServletHolder(new OPDataProvider()),
				"/OpenIdProvider/getRelyingPartyData/*");

		context.addServlet(new ServletHolder(new OPRespondAuthentication()),
				"/OpenIdProvider/OPSubmitAuthorization/*");

		context.addServlet(new ServletHolder(new OPLogOut()),
				"/OpenIdProvider/OPLogOut/*");

		return context;
	}

	/**
	 * Returns the chatroom, of which there is only one in this simple
	 * application.
	 * 
	 * @return
	 */
	public static Chatroom getChatroom() {
		return ServerBooter.chatroom;
	}
}
