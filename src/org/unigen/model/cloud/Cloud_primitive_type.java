package org.unigen.model.cloud;

import org.unigen.generator.Generator;

import java.util.HashMap;
import java.util.Map;

public class Cloud_primitive_type implements Cloud_type {

	public final String record_schema_name;
	final String conversation_method_root;

	private Cloud_primitive_type(String record_schema_name, String conversation_method_root) {
		this.record_schema_name = record_schema_name;
		this.conversation_method_root = conversation_method_root;
	}

	@Override
	public String get_record_schema_name() {
		return record_schema_name;
	}

	@Override
	public String get_to_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) {
		return "CloudApiPrimitiveTypeConverter.jsonFrom" + Generator.uppercase_first_letter(conversation_method_root);
	}

	@Override
	public String get_from_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) {
		return "CloudApiPrimitiveTypeConverter." + Generator.lowercase_first_letter(conversation_method_root) + "FromJson";
	}

	public final static Cloud_primitive_type integer_type = new Cloud_primitive_type("Число целое", "integer");
	public final static Cloud_primitive_type int64_type = new Cloud_primitive_type("Число целое", "int64");
	public final static Cloud_primitive_type boolean_type = new Cloud_primitive_type("Логическое", "boolean");
	public final static Cloud_primitive_type text_type = new Cloud_primitive_type("Строка", "string");
	public final static Cloud_primitive_type date_time_type = new Cloud_primitive_type("Дата и время", "dateTime");
	public final static Cloud_primitive_type uuid_type = new Cloud_primitive_type("UUID", "uuid");


	public static Cloud_primitive_type try_parse(String string) {
		if (typeByName == null) {
			typeByName = new HashMap<>();
			typeByName.put("int", integer_type);
			typeByName.put("integer", integer_type);
			typeByName.put("int64", int64_type);
			typeByName.put("bool", boolean_type);
			typeByName.put("boolean", boolean_type);
			typeByName.put("text", text_type);
			typeByName.put("string", text_type);
			typeByName.put("date", date_time_type);
			typeByName.put("datetime", date_time_type);
			typeByName.put("uuid", uuid_type);
		}
		return typeByName.get(string.toLowerCase());
	}

	private static Map<String, Cloud_primitive_type> typeByName;
}
