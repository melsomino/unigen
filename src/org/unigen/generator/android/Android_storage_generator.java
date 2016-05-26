package org.unigen.generator.android;


import org.unigen.Main;
import org.unigen.generator.Generator;
import org.unigen.model.Uni;
import org.unigen.model.storage.Storage;
import org.unigen.temple.Template;
import org.unigen.temple.Template_engine;

public class Android_storage_generator extends Generator {

	public Android_storage_generator(Uni uni) throws Exception {
		super(uni);
	}


	@Override
	public void generate() throws Exception {
		Android_storage_types java = new Android_storage_types();
		Template_engine templates = new Template_engine("java", Main.class, "templates/android/storage");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unigen.model.storage.*;";
		templates.shared_arg_names_and_types = new Object[]{
			"java", Android_storage_types.class, "uni", Uni.class
		};

		Template interface_template = templates.load("interface", null, "storage", Storage.class);
		Template implementation_template = templates.load("implementation", null, "storage", Storage.class);

		new_location(uni.out.ios);

		new_location("interfaces/uni");
		interface_template.generate_to_file(current_placement.resolve(uni.storage.module + "Storage.java"), java, uni, uni.storage);
		close_location();

		new_location("implementation/uni");
		implementation_template.generate_to_file(current_placement.resolve("Default" + uni.storage.module + "Storage.java"), java, uni, uni.storage);
		close_location();

		close_location();
	}

}
