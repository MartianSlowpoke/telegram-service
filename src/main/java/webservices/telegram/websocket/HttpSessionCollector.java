package webservices.telegram.websocket;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import webservices.telegram.model.user.User;

public class HttpSessionCollector implements HttpSessionListener {

	private static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("created session " + se.getSession().getId());
		HttpSession session = se.getSession();
		sessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("deleted session " + se.getSession().getId());
		sessions.remove(se.getSession().getId());
	}

	public static User get(String sessionId) {
		HttpSession session = sessions.get(sessionId);
		System.out.println("gotten session " + session);
		if (session == null || session.getAttribute("user") == null)
			throw new IllegalArgumentException("I can't obtain a user by sessionId " + sessionId);
		return (User) session.getAttribute("user");
	}

}
