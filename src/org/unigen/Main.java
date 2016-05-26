package org.unigen;

import org.unigen.generator.Uni_generator;
import org.unigen.model.Uni_loader;

import java.nio.file.Paths;


public class Main {

	public static void main(String[] args) {
		try {
			Uni_generator.generate(Uni_loader.load(Paths.get(args[0])));
		} catch (Exception e) {
			System.out.flush();
			e.printStackTrace();
		}
	}

}
