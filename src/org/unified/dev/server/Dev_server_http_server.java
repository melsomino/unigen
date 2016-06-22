package org.unified.dev.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.unified.declaration.Declaration_element;
import org.unified.declaration.Declaration_error;
import org.unified.dev.Dev_configuration;

class Dev_server_http_server {


	Dev_server_http_server(Dev_server server) {
		this.server = server;
	}





	void configure(Dev_configuration configuration) throws Declaration_error {
		Declaration_element http_declaration = Declaration_element.first_element_with_name("http", configuration.declarations);
		int port = http_declaration != null ? http_declaration.get_int_attribute("port", 8080) : 8080;

		if (http_server == null) {

			http_server = new Server();
			connector = new ServerConnector(http_server);
			http_server.addConnector(connector);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			http_server.setHandler(context);

			ServletHolder holder_events = new ServletHolder("ws-events", Dev_server_websocket_servlet.class);
			context.addServlet(holder_events, "/events/*");

			ServletHolder holder_html = new ServletHolder("html", Dev_server_html_servlet.class);
			context.addServlet(holder_html, "/*");

			context.setAttribute("dev_server", server);
		}
		connector.setPort(port);
	}





	void start() throws Exception {
		http_server.start();
	}





	void stop() throws Exception {
		http_server.stop();
	}

	// Internals

	private final Dev_server server;
	private Server http_server;
	private ServerConnector connector;

}
