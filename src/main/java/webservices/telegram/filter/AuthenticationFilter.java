package webservices.telegram.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		String uri = req.getRequestURI();
		if (req.getMethod().equals("GET") && uri.equals("/telegram/login")) {
			chain.doFilter(request, response);
			return;
		}

		if (req.getMethod().equals("POST") && uri.equals("/telegram/users")) {
			chain.doFilter(request, response);
			return;
		}

		if (req.getMethod().equals("GET") && uri.equals("/telegram/logout")) {
			chain.doFilter(request, response);
			return;
		}

		if (session == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {

	}

}
