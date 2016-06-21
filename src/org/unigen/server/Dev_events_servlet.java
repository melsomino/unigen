package org.unigen.server;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class Dev_events_servlet extends WebSocketServlet
{
	@Override
	public void configure(WebSocketServletFactory factory)
	{
		factory.register(Dev_events_websocket.class);
	}
}