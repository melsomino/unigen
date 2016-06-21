package org.unigen.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.unified.declaration.DeclarationElement;
import org.unified.declaration.DeclarationError;

import java.io.IOException;
import java.nio.file.*;

public class Dev_server {
	public Dev_server_configuration configuration;
	private Dev_http http;
	private Dev_watcher watcher;
	public Dev_server_activity activity = new Dev_server_activity();

	public void configure(Path configuration_file_path) throws DeclarationError {
		org.apache.log4j.BasicConfigurator.configure();
		configuration = new Dev_server_configuration(configuration_file_path);
		http = new Dev_http(this);
		watcher = new Dev_watcher(this);
		System.out.println("Configuration loaded from: " + configuration_file_path.toAbsolutePath());
	}




	public void run() {
		try {
			http.start();
			watcher.start();
			System.out.println("Type 'quit' or 'q' to stop");
			while (true) {
				System.out.print("> ");
				String command = read_command();
				if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
					System.out.print("Dev http_server stopped...");
					break;
				}
				System.out.println("Invalid command: " + command);
			}
			http.stop();
			watcher.stop();
		} catch (Exception e) {
			e.printStackTrace();
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


}
