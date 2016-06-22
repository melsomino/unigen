package org.unified.dev.generator.ios.cloud;

import org.unified.dev.generator.Generator;
import org.unified.module.Module;
import org.unified.module.cloud.Cloud_api;
import org.unified.templates.Template;
import org.unified.templates.Template_engine;

import java.nio.file.Path;
import java.util.Date;

public class Ios_cloud_generator extends Generator {

	private final String out_path;





	public Ios_cloud_generator(Module module, Date time, Path out_path) throws Exception {
		super(module, time);
		this.out_path = out_path.toString();
	}


	@Override
	public void generate() throws Exception {
		Ios_cloud_types swift = new Ios_cloud_types();
		Template_engine templates = new Template_engine("swift", Ios_cloud_generator.class, "templates");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unified.module.cloud.*;";
		templates.shared_arg_names_and_types = new Object[]{
			"swift", Ios_cloud_types.class, "module", Module.class, "generation_source", String.class, "generation_time", Date.class
		};

		Template interface_template = templates.load("interface", null, "api", Cloud_api.class);
		Template implementation_template = templates.load("implementation", null, "api", Cloud_api.class);

		new_location(out_path);

		new_location("Interfaces/Unified");
		interface_template.generate_to_file(current_placement.resolve(module.name + "CloudApi.swift"), swift, module, source, time, module.cloud_api);
		close_location();

		new_location("Implementation/Unified");
		implementation_template.generate_to_file(current_placement.resolve("Default" + module.name + "CloudApi.swift"), swift, module, source, time, module.cloud_api);
		close_location();

		close_location();
	}


}
