package org.unified.module.cloud;

import org.unified.Unified_error;
import org.unified.dev.generator.Generator;
import org.unified.module.Module;

public class Cloud_api {
	public final Cloud_struct_type[] struct_types;
	public final Cloud_method[] methods;





	public Cloud_api(Cloud_struct_type[] struct_types, Cloud_method[] methods) {
		this.struct_types = struct_types;
		this.methods = methods;
	}





	public String make_type_name(Module module, String type_name) {
		return Generator.uppercase_first_letter(module.name) + "CloudApiTypes." + Generator.uppercase_first_letter(type_name);
	}





	public final String make_struct_interface_type_name(Module module, String name_candidate, Cloud_type_declaration declaration) throws Unified_error {
		return declaration != null && declaration.is_struct_type() ? make_interface_type_name(module, name_candidate) : null;
	}





	public String make_interface_type_name(Module module, String type_name) {
		return Generator.uppercase_first_letter(module.name) + "CloudApi" + Generator.uppercase_first_letter(type_name);
	}
}
