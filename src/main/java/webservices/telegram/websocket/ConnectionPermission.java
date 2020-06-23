package webservices.telegram.websocket;

import org.java_websocket.handshake.ClientHandshake;

import webservices.telegram.model.user.User;

public interface ConnectionPermission {
	
	public User permit(ClientHandshake request) throws IllegalArgumentException;
	
}
