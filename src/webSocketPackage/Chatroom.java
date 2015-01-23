package webSocketPackage;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;

import serverPackage.ServerBooter;

/**
 * The chatroom that users connect to
 * 
 */
public class Chatroom {

	/**
	 * This is the list of messages. They only persist for as long as the server
	 * is running.
	 */
	Vector<ChatroomMessage> messages = new Vector();

	/**
	 * These are the participants in the chatroom, identified by their websocket
	 * session
	 */
	Vector<Session> participants = new Vector();

	/**
	 * The constructor. Initializes the keep-alive calls that will send messages
	 * to the chat participants every 15 seconds to keep the sessions alive.
	 */
	public Chatroom() {
		ScheduledExecutorService ses = Executors
				.newSingleThreadScheduledExecutor();

		ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for (Session participant : ServerBooter.getChatroom().participants) {
					try {
						participant.getRemote().sendString("keep-alive");
					} catch (IOException ex) {
						ServerBooter.getChatroom().participants
								.remove(participant);
						System.out
								.println("Websocket Error " + ex.getMessage());
					}
				}
			}
		}, 15, 15, TimeUnit.SECONDS);
	}

	/**
	 * Adds a new chatroom message and broadcasts it to the chatroom
	 * participants.
	 * 
	 * @param crm
	 *            The ChatroomMessage to add.
	 */
	public void addMessage(ChatroomMessage crm) {
		messages.add(crm);
		Vector<Session> sessionsToRemove = new Vector();
		for (Session participant : participants) {
			if (participant.isOpen()) {
				try {
					participant.getRemote().sendString(crm.print());
				} catch (IOException ex) {
					participants.remove(participant);
					System.out.println("Websocket Error " + ex.getMessage());
				}
			} else {
				sessionsToRemove.add(participant);
			}
		}
		for (Session participant : sessionsToRemove) {
			participants.remove(participant);
		}
	}

	/**
	 * Adds a new participant to the broadcast list.
	 * 
	 * @param participant
	 *            The Session to add to the participant list.
	 */
	public void addParticipant(Session participant) {
		participants.add(participant);
	}

	/**
	 * Returns the last "count" messages from the conversation.
	 * 
	 * @param count
	 *            The number of messages to print.
	 * @return
	 */
	public String print(int count) {
		StringBuffer sb = new StringBuffer();
		if (messages.size() < count) {
			for (int i = 0; i < messages.size(); i++) {
				ChatroomMessage crm = messages.get(i);
				sb.append(crm.print());
			}
		} else {
			for (int i = messages.size() - count; i < messages.size(); i++) {
				ChatroomMessage crm = messages.get(i);
				sb.append(crm.print());
			}
		}
		return sb.toString();
	}
}
