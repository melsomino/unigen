package org.unigen.model.cloud;

import org.unigen.Unigen_exception;
import org.unigen.generator.Generator;

public class Cloud_api {
	public final String out_package;
	public final String name;
	public final Cloud_struct_type[] struct_types;
	public final Cloud_method[] methods;

	public Cloud_api(String out_package, String name, Cloud_struct_type[] struct_types, Cloud_method[] methods) {
		this.out_package = out_package;
		this.name = name;
		this.struct_types = struct_types;
		this.methods = methods;
	}
	public String make_type_name(String type_name) {
		return Generator.uppercase_first_letter(name) + "CloudApiTypes." + Generator.uppercase_first_letter(type_name);
	}

	public final String make_struct_interface_type_name(String name_candidate, Cloud_type_declaration declaration) throws Unigen_exception {
		return declaration != null && declaration.is_struct_type() ? make_interface_type_name(name_candidate) : null;
	}

	public String make_interface_type_name(String type_name) {
		return Generator.uppercase_first_letter(name) + "CloudApi" + Generator.uppercase_first_letter(type_name);
	}
}
