package webservices.telegram.controller;

import java.io.IOException;

import org.java_websocket.server.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("web-socket-controller")
@RequestMapping("websocket")
public class WebSocketController {

	private WebSocketServer wsServer;

	public WebSocketController(WebSocketServer wsServer) {
		super();
		this.wsServer = wsServer;
	}

	@RequestMapping(method = RequestMethod.GET, value = "start")
	public void startServer() {
		System.out.println("started websocket server");
		wsServer.start();
	}

	@RequestMapping(method = RequestMethod.GET, value = "stop")
	public void stopServer() {
		try {
			System.out.println("finished websocket server");
			wsServer.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
