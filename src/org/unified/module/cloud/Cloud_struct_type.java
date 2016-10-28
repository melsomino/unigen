package org.unified.module.cloud;

import org.unified.Unified_error;
import org.unified.dev.generator.Generator;

public class Cloud_struct_type implements Cloud_type {
	public final String interface_name;
	public final String implementation_name;

	public final Cloud_struct_field[] fields;

	public boolean map_decoder_required = false;
	public boolean record_decoder_required = false;
	public boolean recordset_decoder_required = false;
	public boolean array_decoder_required = false;

	public Cloud_struct_type(String module_name, String name, Cloud_struct_field[] fields) {
		this.interface_name = module_name + "CloudApi" + name;
		this.implementation_name = name;
		this.fields = fields;
	}


	@Override
	public String get_record_schema_name() {
		return "Таблица";
	}

	@Override
	public String get_to_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unified_error {
		switch (modifier) {
			case Missing:
			case Object:
				return converter_method(encoding, "{encoding}ObjectFromObject");
			case Record:
				return converter_method(encoding, "{encoding}RecordFromObject");
			case Array:
				return converter_method(encoding, "{encoding}ArrayFromList");
			case Recordset:
				return converter_method(encoding, "{encoding}RecordsetFromList");
			case Parameters:
				return converter_method(encoding, "{encoding}ParameterRecordsetFromList");
		}
		throw new Unified_error("Unknown modifier " + modifier + " (get_to_json_conversion_method_name)");
	}

	@Override
	public String get_from_json_conversion_method_name(Cloud_type_modifier modifier, Cloud_type_encoding encoding) throws Unified_error {
		switch (modifier) {
			case Missing:
			case Object:
				return converter_method(encoding, "objectFrom{Encoding}Object");
			case Record:
				return converter_method(encoding, "objectFrom{Encoding}Record");
			case Array:
				return converter_method(encoding, "listFrom{Encoding}Array");
			case Recordset:
				return converter_method(encoding, "listFrom{Encoding}Recordset");
			case Parameters:
				return converter_method(encoding, "listFrom{Encoding}ParameterRecordset");
		}
		throw new Unified_error("Unknown modifier " + modifier + " (get_from_json_conversion_method_name)");
	}


	private String converter_method(Cloud_type_encoding encoding, String methodName) {
		String lowercaseEncoding = encoding == Cloud_type_encoding.JsonString ? "jsonString" : "json";
		String uppercaseEncoding = encoding == Cloud_type_encoding.JsonString ? "JsonString" : "Json";
		return implementation_name + ".converter." + Generator.format(methodName, "encoding", lowercaseEncoding, "Encoding", uppercaseEncoding);
	}
}
