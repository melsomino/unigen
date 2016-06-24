package org.unified.dev.generator.android;


import org.unified.dev.generator.Generator;
import org.unified.module.Module;
import org.unified.module.storage.Storage;
import org.unified.templates.Template;
import org.unified.templates.Template_engine;

import java.nio.file.Path;
import java.util.Date;

public class Android_storage_generator extends Generator {

	private final String out_path;





	public Android_storage_generator(Module module, Date time, Path out_path) throws Exception {
		super(module, time);
		this.out_path = out_path.toString();
	}


	@Override
	public void generate() throws Exception {
		Android_storage_types java = new Android_storage_types();
		Template_engine templates = new Template_engine("java", Android_storage_generator.class, "web/android/storage");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unified.module.storage.*;";
		templates.shared_arg_names_and_types = new Object[]{
			"java", Android_storage_types.class, "module", Module.class
		};

		Template interface_template = templates.load("interface", null, "storage", Storage.class);
		Template implementation_template = templates.load("implementation", null, "storage", Storage.class);

		new_location(out_path);

		new_location("interfaces/module");
		interface_template.generate_to_file(current_placement.resolve(module.storage.module + "Storage.java"), java, module, module.storage);
		close_location();

		new_location("implementation/module");
		implementation_template.generate_to_file(current_placement.resolve("Default" + module.storage.module + "Storage.java"), java, module, module.storage);
		close_location();

		close_location();
	}

}
