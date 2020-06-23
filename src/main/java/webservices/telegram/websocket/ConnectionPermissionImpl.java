package webservices.telegram.websocket;

import java.net.HttpCookie;
import java.util.List;

import org.java_websocket.handshake.ClientHandshake;

import webservices.telegram.model.user.User;

public class ConnectionPermissionImpl implements ConnectionPermission {

	@Override
	public User permit(ClientHandshake request) throws IllegalArgumentException {
		String sessionId = extractSessionId(request);
		System.out.println("sessionid inside persmission = " + sessionId);
		User user = HttpSessionCollector.get(sessionId);
		System.out.println("user by this shit " + user.toString());
		return user;
	}

	private String extractSessionId(ClientHandshake request) {
		try {
			List<HttpCookie> cookies = HttpCookie.parse(request.getFieldValue("cookie"));
			for (HttpCookie cookie : cookies) {
				if (cookie.getName().equals("JSESSIONID"))
					return cookie.getValue();
			}
			throw new IllegalArgumentException("there is no cookie with JSESSIONID name");
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("there is no cookie with JSESSIONID name");
		}
	}

}
