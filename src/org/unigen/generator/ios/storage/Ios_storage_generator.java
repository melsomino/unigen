package org.unigen.generator.ios.storage;

import org.unigen.Main;
import org.unigen.generator.Generator;
import org.unigen.model.Uni;
import org.unigen.model.storage.Storage;
import org.unigen.temple.Template;
import org.unigen.temple.Template_engine;


public class Ios_storage_generator extends Generator
{

	public Ios_storage_generator(Uni uni) throws Exception {
		super(uni);
	}


	@Override
	public void generate() throws Exception {
		Ios_storage_types swift = new Ios_storage_types();
		Template_engine templates = new Template_engine("swift", Ios_storage_generator.class, "templates");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unigen.model.storage.*;";
		templates.shared_arg_names_and_types = new Object[]{
			"swift", Ios_storage_types.class, "uni", Uni.class
		};

		Template interface_template = templates.load("interface", null, "storage", Storage.class);
		Template implementation_template = templates.load("implementation", null, "storage", Storage.class);

		new_location(uni.out.ios);

		new_location("Interface/Uni");
		interface_template.generate_to_file(current_placement.resolve(uni.storage.module + "Storage.swift"), swift, uni, uni.storage);
		close_location();

		new_location("Implementation/Uni");
		implementation_template.generate_to_file(current_placement.resolve("Default" + uni.storage.module + "Storage.swift"), swift, uni, uni.storage);
		close_location();

		close_location();
	}


}
