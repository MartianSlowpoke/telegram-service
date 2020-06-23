package webservices.telegram.websocket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import webservices.telegram.exception.UserDaoException;
import webservices.telegram.model.user.User;

public class MessengerUpdateServer extends WebSocketServer {

	private UserStateConnectionListener stateListener;
	private ConnectionPermission permission;
	private String id;

	public MessengerUpdateServer(int port, UserStateConnectionListener stateListener, ConnectionPermission permission)
			throws UnknownHostException {
		super(new InetSocketAddress(InetAddress.getByName("localhost"), port));
		this.stateListener = stateListener;
		this.permission = permission;
		id = UUID.randomUUID().toString();
		System.out.println("RUNNED: " + id);
	}

	@Override
	public void onStart() {

	}

	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
			ClientHandshake request) throws InvalidDataException {
		try {
			ServerHandshakeBuilder handShakeBuilder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
			if (!request.getResourceDescriptor().equals("/telegram/updates"))
				throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "not accepted");
			System.out.println("I am connecting to " + id);
			User user = permission.permit(request);
			conn.setAttachment(user);
			return handShakeBuilder;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, e.getMessage());
		}
	}

	@Override
	public void onOpen(WebSocket ws, ClientHandshake handshake) {
		try {
			stateListener.onOpen(ws.getAttachment());
		} catch (IllegalArgumentException | UserDaoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(WebSocket ws, String message) {
		ws.send(message);
	}

	@Override
	public void onClose(WebSocket ws, int reason, String message, boolean isClient) {
		try {
			stateListener.onClose(ws.getAttachment());
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(WebSocket ws, Exception e) {
		e.printStackTrace();
	}

}
