package org.unified.dev.server;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class Dev_server_websocket_servlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator((servletUpgradeRequest, servletUpgradeResponse) -> new Dev_server_websocket_connection((Dev_server)getServletContext().getAttribute("dev_server")));
	}
}