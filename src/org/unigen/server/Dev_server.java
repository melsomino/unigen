package org.unigen.server;

import org.unified.declaration.DeclarationElement;
import org.unified.declaration.DeclarationError;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Dev_server {
	final List<Path> repository_paths = new ArrayList<>();

	public void add_repository(Path repository_file_path) {
		repository_paths.add(repository_file_path);
	}


	public void configure(Path configuration_file_path) throws DeclarationError {
		DeclarationElement[] configuration = DeclarationElement.load(configuration_file_path);
		System.out.println("Load configuration from: " + configuration_file_path.toAbsolutePath());
		for (DeclarationElement element : configuration) {
			if ("repository".equalsIgnoreCase(element.name)) {
				String repository_path = element.get_string_attribute("path");
				if (repository_path != null) {
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

	public void run() {
		System.out.println("Dev server started...");
		System.out.println("Type 'quit' or 'q' to stop");
		try {
			while (true) {
				System.out.print("> ");
				String command = read_command();
				if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
					System.out.print("Dev server stopped...");
					break;
				}
				System.out.println("Invalid command: " + command);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
