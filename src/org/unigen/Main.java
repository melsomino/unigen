package org.unigen;

import org.unified.declaration.DeclarationElement;
import org.unigen.generator.Uni_generator;
import org.unigen.model.Uni_loader;
import org.unigen.server.Dev_server;

import java.nio.file.Paths;


public class Main {

	public static void main(String[] args) {
		try {
			Dev_server dev_server = new Dev_server();
			dev_server.configure(Paths.get(args[0]));
			dev_server.run();
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
