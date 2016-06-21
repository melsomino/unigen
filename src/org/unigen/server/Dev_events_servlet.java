package org.unigen.server;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class Dev_events_servlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator((servletUpgradeRequest, servletUpgradeResponse) -> new Dev_events_websocket((Dev_server)getServletContext().getAttribute("dev_server")));
	}
}