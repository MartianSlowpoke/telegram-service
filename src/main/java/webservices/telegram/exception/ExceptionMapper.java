package webservices.telegram.exception;

import javax.servlet.http.HttpServletResponse;

public class ExceptionMapper {

	public ExceptionMapper() {

	}

	public int map(String message) {
		switch (message) {
		case "user with such credentials doesn't exist":
			return HttpServletResponse.SC_NOT_FOUND;
		case "email is empty":
			return HttpServletResponse.SC_BAD_REQUEST;
		case "password is empty":
			return HttpServletResponse.SC_BAD_REQUEST;
		case "user id is null":
			return HttpServletResponse.SC_BAD_REQUEST;
		case "user login is null":
			return HttpServletResponse.SC_BAD_REQUEST;
		default:
			return 0;
		}
	}

}
