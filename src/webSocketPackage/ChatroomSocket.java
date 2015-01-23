package webSocketPackage;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import dbPackage.RPdbConnection;
import serverPackage.ServerBooter;

/**
 * The websocket the user connects to
 * 
 */
public class ChatroomSocket extends WebSocketAdapter {

	private Session session;
	private int amountOfLastMessages = 5;
	private String userName = null;
	private RPdbConnection dbConnection;

	// @OnWebSocketConnect
	@Override
	/**
	 * Adds the session to the chatroom participants list, and sends the
	 * user the last messages in the conversation.
	 */
	public void onWebSocketConnect(Session session) {
		System.out.println("Websocket connection from->"
				+ session.getRemoteAddress().getAddress());
		this.session = session;
		dbConnection = RPdbConnection.getConnection();
		getUserName(session);
		dbConnection = null;
		ServerBooter.getChatroom().addParticipant(session);
		try {
			this.session.getRemote().sendString(
					ServerBooter.getChatroom().print(amountOfLastMessages));
		} catch (IOException ex) {
			System.out.println("Error in websocket" + ex.getMessage());
		}
	}

	// @OnWebSocketMessage
	@Override
	public void onWebSocketBinary(byte[] bytes, int x, int y) {
		// not used
	}

	/**
	 * Gets the user name from the db based on the session id
	 * 
	 * @param session
	 */
	private void getUserName(Session session) {
		List<HttpCookie> cookies = session.getUpgradeRequest().getCookies();
		for (HttpCookie httpCookie : cookies) {
			if ("JSESSIONID".equals(httpCookie.getName())) {
				userName = dbConnection.getSessionIdName(httpCookie.getValue());
			}
		}
	}

	// @OnWebSocketMessage
	@Override
	/**
	 * Adds the message from the user to the chatroom conversation.
	 */
	public void onWebSocketText(String message) {
		if (message != null && !message.equals("keep-alive")) {
			ChatroomMessage crm = new ChatroomMessage(userName, message);
			ServerBooter.getChatroom().addMessage(crm);
		}
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		System.out.println("Error on websocket" + cause.getMessage());
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		System.out.println("Closing the websocket from->"
				+ session.getRemoteAddress().getAddress());
	}
}
