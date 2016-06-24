package org.unified.module;

import org.unified.declaration.Declaration_element;
import org.unified.module.cloud.Cloud_api;
import org.unified.module.cloud.Cloud_api_loader;
import org.unified.module.storage.Storage;
import org.unified.module.storage.Storage_loader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Module_loader {

	public static Module load(Path module_file) throws Exception {
		return load(Declaration_element.load_from(module_file), module_file);
	}





	public static Module load(Declaration_element[] declarations, Path source) throws Exception {
		String module_name = null;
		Storage storage = null;
		Cloud_api cloud = null;

		for (Declaration_element element : declarations) {
			if (element.name_is("module")) {
				module_name = element.value();
			} else if (element.name_is("storage")) {
				storage = Storage_loader.load_from(element, module_name);
			} else if (element.name_is("cloud")) {
				cloud = Cloud_api_loader.load_from(element, module_name);
			}
		}

		return new Module(module_name, source, storage, cloud);
	}





	public interface Element_handler {
		void apply(Declaration_element declaration) throws Exception;
	}

	public interface Object_factory<T> {
		T create_from(Declaration_element declaration) throws Exception;
	}

	public interface Array_factory<T> {
		T[] create(int size);
	}





	public static void enum_default_items(Declaration_element element, String optional_child_name, Element_handler element_handler) throws Exception {
		if (element != null && optional_child_name != null) {
			element = element.first_child_with_name(optional_child_name);
		}
		if (element == null) {
			return;
		}
		for (Declaration_element child_element : element.children) {
			if (child_element.attributes.length > 0 && !child_element.attributes[0].name.startsWith("+")) {
				element_handler.apply(child_element);
			}
		}
	}





	public static <T> T[] load_default_items(Declaration_element element, String optional_child_name, Array_factory<T> array_factory, Object_factory<T> item_loader) throws Exception {
		List<T> items = new ArrayList<>();
		enum_default_items(element, optional_child_name, child_element -> items.add(item_loader.create_from(child_element)));
		return items.toArray(array_factory.create(items.size()));
	}


}
