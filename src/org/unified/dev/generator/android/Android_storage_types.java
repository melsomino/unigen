package org.unified.dev.generator.android;


import org.unified.module.storage.Field_type;

public class Android_storage_types
{
	public String from_field_type(Field_type type)
	{
		switch (type) {
			case String:
				return "String";
			case Integer:
				return "Integer";
			case Date_time:
				return "Date";
			case Boolean:
				return "Boolean";
		}
		assert false;
		return "Object";
	}

	public String param_declaration(String name, Field_type type) {
		return from_field_type(type) + " " + name;
	}

	public String make_string_literal(String string, int multiline_indent)
	{
		return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r") + "\"";
	}
}
