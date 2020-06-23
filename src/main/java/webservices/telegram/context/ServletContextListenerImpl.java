package webservices.telegram.context;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.java_websocket.server.WebSocketServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServletContextListenerImpl implements ServletContextListener {

	private ApplicationContext context = new ClassPathXmlApplicationContext("servlet-context.xml");

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebSocketServer wsServer = (WebSocketServer) context.getBean("ws-server");
		wsServer.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			WebSocketServer wsServer = (WebSocketServer) context.getBean("ws-server");
			wsServer.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
