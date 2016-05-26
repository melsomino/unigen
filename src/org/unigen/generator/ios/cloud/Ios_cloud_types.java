package org.unigen.generator.ios.cloud;

import org.unigen.Unigen_exception;
import org.unigen.model.cloud.Cloud_primitive_type;
import org.unigen.model.cloud.Cloud_struct_type;
import org.unigen.model.cloud.Cloud_type_declaration;
import org.unigen.model.cloud.Cloud_type_modifier;

public class Ios_cloud_types {


	public String get_primitive_type_name(Cloud_primitive_type type) throws Unigen_exception {
		if (type == Cloud_primitive_type.boolean_type) {
			return "Bool";
		}
		if (type == Cloud_primitive_type.integer_type) {
			return "Int";
		}
		if (type == Cloud_primitive_type.text_type) {
			return "String";
		}
		if (type == Cloud_primitive_type.uuid_type) {
			return "UUID";
		}
		if (type == Cloud_primitive_type.int64_type) {
			return "Long";
		}
		if (type == Cloud_primitive_type.date_time_type) {
			return "NSDate";
		}
		throw new Unigen_exception("Can not determine iOS native type for primitive type: " + type.record_schema_name);
	}


	public String field_type_declaration(Cloud_type_declaration declaration) throws Unigen_exception {
		String declaration_string = interface_declaration(declaration);
		return declaration.modifier.is_array() ? declaration_string + "()" : declaration_string;
	}


	public String param_type_declaration(Cloud_type_declaration declaration) throws Unigen_exception {
		return field_type_declaration(declaration);
	}


	public String interface_declaration(Cloud_type_declaration declaration) throws Unigen_exception {
		String interface_type_name = get_interface_type_name(declaration);
		return declaration.modifier.is_array() ? "[" + interface_type_name + "]" : interface_type_name + "?";
	}


	public String get_interface_type_name(Cloud_type_declaration declaration) throws Unigen_exception {
		return declaration.is_struct_type() ? declaration.struct_type().interface_name : get_primitive_type_name(declaration.primitive_type());
	}

	public String struct_implementation_creation(Cloud_type_declaration declaration) {
		return declaration.modifier.is_array() ? "[" + declaration.struct_type().implementation_name + "]()" : declaration.struct_type().implementation_name + "()";
	}


	public String quoted(String s) {
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r") + "\"";
	}

}
