package org.unified.dev.generator.android;

import org.unified.Unified_error;
import org.unified.module.cloud.*;

public class Android_cloud_types  {

	public String get_primitive_type_name(Cloud_primitive_type type) throws Unified_error {
		if (type == Cloud_primitive_type.boolean_type) {
			return "Boolean";
		}
		if (type == Cloud_primitive_type.integer_type) {
			return "Integer";
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
			return "Date";
		}
		throw new Unified_error("Can not determine Android native type for primitive type: " + type.record_schema_name);
	}

	public String field_type_declaration(Cloud_type_declaration declaration) throws Unified_error {
		return ""; //TODO:type_declaration(field_type_name(struct_type_name, field), field.declaration.modifier);
	}

	public String struct_implementation_creation(Cloud_struct_type struct_type, Cloud_type_modifier modifier) {
		switch(modifier) {
			case Array:
			case Recordset:
				return "new " + struct_type.interface_name + "[0]";
			default:
				return "new " + struct_type.interface_name + "()";
		}
	}


	public String param_type_declaration(Cloud_type_declaration declaration) throws Unified_error {
		return null;
	}


	public String type_declaration(String type_name, Cloud_type_modifier modifier) {
		switch(modifier) {
			case Array:
			case Recordset:
				return type_name + "[]";
			default:
				return type_name;
		}
	}

}
