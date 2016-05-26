package org.unigen.generator.ios.cloud;

import org.unigen.Main;
import org.unigen.generator.Generator;
import org.unigen.model.Uni;
import org.unigen.model.cloud.Cloud_api;
import org.unigen.temple.Template;
import org.unigen.temple.Template_engine;

public class Ios_cloud_generator extends Generator {

	public Ios_cloud_generator(Uni uni) throws Exception {
		super(uni);
	}


	@Override
	public void generate() throws Exception {
		Ios_cloud_types swift = new Ios_cloud_types();
		Template_engine templates = new Template_engine("swift", Ios_cloud_generator.class, "templates");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unigen.model.cloud.*;";
		templates.shared_arg_names_and_types = new Object[]{
			"swift", Ios_cloud_types.class, "uni", Uni.class
		};

		Template interface_template = templates.load("interface", null, "api", Cloud_api.class);
		Template implementation_template = templates.load("implementation", null, "api", Cloud_api.class);

		new_location(uni.out.ios);
		for (Cloud_api api : uni.cloud.apis) {
			new_location("Interface/Uni");
			interface_template.generate_to_file(current_placement.resolve(api.name + "CloudApi.swift"), swift, uni, api);
			close_location();

			new_location("Implementation/Uni");
			implementation_template.generate_to_file(current_placement.resolve("Default" + api.name + "CloudApi.swift"), swift, uni, api);
			close_location();
		}
		close_location();
	}


}
