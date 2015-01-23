package webSocketPackage;

/**
 * This class represents a chatroom message.
 * 
 */
public class ChatroomMessage {

	/**
	 * The user the message came from.
	 */
	String fromUser = null;

	/**
	 * The message itself.
	 */
	String message = null;

	public ChatroomMessage(String fromUser, String message) {
		this.fromUser = fromUser;
		this.message = message;
	}

	/**
	 * Returns a formatted version of the message.
	 * 
	 * @return
	 */
	public String print() {
		return "<p>[" + fromUser + "] " + message + "</p>";
	}
}
