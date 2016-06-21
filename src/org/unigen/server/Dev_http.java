package org.unigen.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.unified.declaration.DeclarationElement;
import org.unified.declaration.DeclarationError;

public class Dev_http {
	final Dev_server server;
	final Server http_server;

	public Dev_http(Dev_server server) throws DeclarationError {
		this.server = server;

		DeclarationElement http_declaration = DeclarationElement.find_first_element_with_name("http", server.configuration.declarations);
		int port = http_declaration != null ? http_declaration.get_int_attribute("port", 8080) : 8080;

		http_server = new Server();
		ServerConnector connector = new ServerConnector(http_server);
		connector.setPort(port);
		http_server.addConnector(connector);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		http_server.setHandler(context);

		ServletHolder holder_events = new ServletHolder("ws-events", Dev_events_servlet.class);
		context.addServlet(holder_events, "/events/*");

		ServletHolder holder_html = new ServletHolder("html", Dev_html_servlet.class);
		context.addServlet(holder_html, "/*");

		context.setAttribute("dev_server", server);
	}

	public void start() throws Exception {
		http_server.start();
	}

	public void stop() throws Exception {
		http_server.stop();
	}
}
