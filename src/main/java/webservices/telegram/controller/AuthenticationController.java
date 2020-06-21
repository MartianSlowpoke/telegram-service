package webservices.telegram.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import webservices.telegram.dao.user.UserDAO;
import webservices.telegram.exception.UserDaoException;
import webservices.telegram.exception.user.NotValidAuthenticationRequestDataException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.AuthenticationBuilder;
import webservices.telegram.model.user.User;

@Controller("authentication-controller")
public class AuthenticationController {

	private UserDAO userDAO;

	public AuthenticationController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	// Basic bXljcmF6eWVtYWlsQHVrci5uZXQ6MDUwOA==
	
	@RequestMapping(method = RequestMethod.GET, value = "login", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User logIn(@RequestHeader(value = "Authorization", required = true) String authHeader,
			HttpServletRequest request) throws IllegalArgumentException, UserDaoException {
		Authentication auth = extract(authHeader);
		User user = userDAO.logIn(auth);
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, value = "logout")
	public void logOut(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		eraseCookie(request, response);
	}

	@ExceptionHandler
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Exception> handle(Exception e) {
		if (e.getClass().isAnnotationPresent(ResponseStatus.class)) {
			HttpStatus statusCode = e.getClass().getAnnotation(ResponseStatus.class).code();
			ResponseEntity<Exception> entity = new ResponseEntity<Exception>(e, statusCode);
			return entity;
		}
		return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Authentication extract(String authHeader) {
		try {
			String base64Credentials = authHeader.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			final String[] values = credentials.split(":", 2);
			return new AuthenticationBuilder().email(values[0]).password(values[1]).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotValidAuthenticationRequestDataException();
		}
	}

	private static void eraseCookie(HttpServletRequest req, HttpServletResponse resp) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				resp.addCookie(cookie);
			}
	}

}
