package org.unified.dev.generator.ios.storage;


import org.unified.module.storage.Field_type;

public class Ios_storage_types
{
	public String from_field_type(Field_type field_type)
	{
		switch (field_type) {
			case Uuid:
				return "UUID";
			case String:
				return "String";
			case Integer:
				return "Int";
			case Date_time:
				return "NSDate";
			case Boolean:
				return "Bool";
		}
		assert false;
		return "Any";
	}


	public String param_declaration(String name, Field_type type) {
		return name + ": " + from_field_type(type);
	}


	public String make_string_literal(String string, int multiline_indent)
	{
		String literal = string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t");
		if (multiline_indent < 0) {
			return "\"" + literal.replace("\n", "\\n").replace("\r", "\\r") + "\"";
		}
		StringBuilder multiline = new StringBuilder();
		int index = 0;
		for(String line : literal.split("\\r?\\n")) {
			if (index > 0) {
				multiline.append(" +\n").append(new String(new char[multiline_indent]).replace("\0", "\t"));
			}
			multiline.append('"').append(line).append("\\n").append('"');
			++index;
		}
		return multiline.toString();
	}
}
