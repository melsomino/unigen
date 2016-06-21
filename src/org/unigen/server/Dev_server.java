package org.unigen.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.unified.declaration.DeclarationElement;
import org.unified.declaration.DeclarationError;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Dev_server {
	private final List<Path> repository_paths = new ArrayList<>();

	public void add_repository(Path repository_file_path) {
		repository_paths.add(repository_file_path);
	}


	public void configure(Path configuration_file_path) throws DeclarationError {
		org.apache.log4j.BasicConfigurator.configure();

		DeclarationElement[] configuration = DeclarationElement.load(configuration_file_path);
		System.out.println("Load configuration from: " + configuration_file_path.toAbsolutePath());
		for (DeclarationElement element : configuration) {
			if ("repository".equalsIgnoreCase(element.name)) {
				String repository_path = element.get_string_attribute("path");
				if (repository_path != null) {
					add_repository(Paths.get(repository_path));
					System.out.println("Add repository: " + repository_path);
				}
			}
		}
	}


	private String read_command() throws IOException {
		String line = "";
		while (true) {
			char c = (char) System.in.read();
			if (c == '\r' || c == '\n') {
				return line;
			}
			line += c;
		}
	}

	private Server create_web_server() {
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8080);
		server.addConnector(connector);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		// Add a websocket to a specific path spec
		ServletHolder holder_events = new ServletHolder("ws-events", Dev_events_servlet.class);
		context.addServlet(holder_events, "/events/*");

		ServletHolder holder_html = new ServletHolder("html", Dev_html_servlet.class);
		context.addServlet(holder_html, "/*");

		context.setAttribute("repository_paths", repository_paths);
		return server;
	}

	public void run() {
		Server web_server = create_web_server();
		System.out.println("Type 'quit' or 'q' to stop");
		try {
			web_server.start();
			while (true) {
				System.out.print("> ");
				String command = read_command();
				if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
					System.out.print("Dev server stopped...");
					break;
				}
				System.out.println("Invalid command: " + command);
			}
			web_server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
