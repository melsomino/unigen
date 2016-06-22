package org.unified.module.cloud;

import org.unified.Unified_error;

public class Cloud_type_declaration {
	public final Cloud_type type;
	public final Cloud_type_modifier modifier;
	public final Cloud_type_encoding encoding;

	public Cloud_type_declaration(Cloud_type type, Cloud_type_modifier modifier, Cloud_type_encoding encoding) {
		this.type = type;
		this.modifier = modifier;
		this.encoding = encoding;
	}

	public boolean is_struct_type() {
		return type != null && type instanceof Cloud_struct_type;
	}

	boolean is_primitive_type() {
		return type != null && type instanceof Cloud_primitive_type;
	}

	public String record_schema_name() {
		return type.get_record_schema_name();
	}

	public Cloud_primitive_type primitive_type() {
		return (Cloud_primitive_type) type;
	}

	public Cloud_struct_type struct_type() {
		return (Cloud_struct_type) type;
	}

	public String get_to_json_conversion_method_name() throws Unified_error {
		return type.get_to_json_conversion_method_name(modifier, encoding);
	}

	public String get_from_json_conversion_method_name() throws Unified_error {
		return type.get_from_json_conversion_method_name(modifier, encoding);
	}
}
