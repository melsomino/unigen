package org.unigen.model.cloud;

import org.json.simple.JSONObject;
import org.unigen.Unigen_exception;
import org.unigen.generator.Generator;
import org.unigen.model.Compound_name;
import org.unigen.model.Loader;

import java.util.HashMap;
import java.util.Map;

public class Cloud_loader extends Loader {


	public static Cloud load(JSONObject def) throws Exception {
		final String out_package = get_string(".package", "ru.tensor.sbis", def);
		final String url = get_string(".url", "service", def);
		final int protocol = get_integer(".protocol", 4, def);
		Cloud_api[] apis = create_default_items(def, Cloud_api[]::new, (name, value) -> load_api(out_package, url, protocol, name, (JSONObject) value));
		return new Cloud(out_package, url, apis);
	}


	private static Cloud_api load_api(String default_package, String default_url, int default_protocol, String name, JSONObject def) throws Exception {
		final String out_package = get_string(".package", default_package, def);
		final String url = get_string(".url", default_url, def);
		final int protocol = get_integer(".protocol", default_protocol, def);


		Map<String, Cloud_type_declaration> struct_types_by_name = new HashMap<>();

		create_default_items(get_object(".struct_types", def), Cloud_type_declaration[]::new, (type_name, type_def) -> {
			Cloud_type_declaration declaration = load_type_declaration(true, name, "", type_def, struct_types_by_name);
			struct_types_by_name.put(type_name, declaration);
			return declaration;
		});

		Cloud_method[] methods = create_default_items(def, Cloud_method[]::new,
			(method_name, method_def) -> load_method(name, url, protocol, method_name, (JSONObject) method_def, struct_types_by_name));

		Cloud_struct_type[] struct_types = new Cloud_struct_type[struct_types_by_name.size()];
		int index = 0;
		for (Cloud_type_declaration declaration : struct_types_by_name.values()) {
			struct_types[index++] = declaration.struct_type();
		}
		return new Cloud_api(out_package, name, struct_types, methods);
	}


	private static Cloud_method load_method(String api_interface_name, String default_url, int default_protocol, String name, JSONObject def,
		Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {

		final String url = get_string("url", default_url, def);
		final int protocol = get_integer("protocol", default_protocol, def);
		Compound_name compound_name = Compound_name.parse(name, Compound_name.IdentifierStyle.Camel);

		String specified_result_type_name = get_string(".type", get_object("result", def));

		String params_struct_type_name = Generator.uppercase_first_letter(compound_name.identifier) + "Params";
		String result_struct_type_name = Generator.uppercase_first_letter(specified_result_type_name != null ? specified_result_type_name : compound_name.identifier);

		Cloud_type_declaration params = load_type_declaration(true, api_interface_name, params_struct_type_name, def.get("params"), struct_types_by_name);
		Cloud_type_declaration result = load_type_declaration(false, api_interface_name, result_struct_type_name, def.get("result"), struct_types_by_name);

		return new Cloud_method(url, protocol, compound_name.name, compound_name.identifier, params, result);
	}


	private static Cloud_type_declaration load_type_declaration(boolean used_in_method_params, String api_interface_name, String struct_type_name, Object def, Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {

		if (def == null) {
			return null;
		}
		return def instanceof JSONObject
			? load_struct_type_declaration(used_in_method_params, api_interface_name, struct_type_name, (JSONObject) def, struct_types_by_name)
			: parse_type_declaration((def.toString()));
	}


	private static Cloud_type_declaration parse_type_declaration(String string) throws Unigen_exception {
		Cloud_type_modifier modifier = Cloud_type_modifier.Missing;
		if (string.endsWith("[]")) {
			modifier = Cloud_type_modifier.Array;
			string = string.substring(0, string.length() - 2).trim();
		}
		else if (string.endsWith("{}")) {
			modifier = Cloud_type_modifier.Object;
			string = string.substring(0, string.length() - 2).trim();
		}
		else if (string.endsWith("?")) {
			modifier = Cloud_type_modifier.Record;
			string = string.substring(0, string.length() - 1).trim();
		}
		else if (string.endsWith("*")) {
			modifier = Cloud_type_modifier.Recordset;
			string = string.substring(0, string.length() - 1).trim();
		}
		else if (string.endsWith(":")) {
			modifier = Cloud_type_modifier.Parameters;
			string = string.substring(0, string.length() - 1).trim();
		}

		Cloud_primitive_type primitive_type = Cloud_primitive_type.try_parse(string);
		if (primitive_type != null) {
			return new Cloud_type_declaration(primitive_type, modifier, Cloud_type_encoding.JsonValue);
		}
		throw new Unigen_exception("Invalid type declaration: " + string);
	}


	private static Cloud_type_declaration load_struct_type_declaration(boolean used_in_method_params, String api_interface_name, String name, JSONObject def, Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {
		Cloud_struct_field[] fields = create_default_items(def, Cloud_struct_field[]::new, (field_name, field_def) -> load_struct_field(used_in_method_params, api_interface_name, name, field_name, field_def,
			struct_types_by_name));

		Cloud_type_declaration declaration = new Cloud_type_declaration(new Cloud_struct_type(api_interface_name + name, name, fields),
			Cloud_type_modifier.parse(get_string(".modifier", def)),
			Cloud_type_encoding.parse(get_string(".encoding", def)));

		struct_types_by_name.put(declaration.struct_type().interface_name, declaration);
		if (used_in_method_params) {
			declaration.struct_type().used_in_method_params = true;
		}

		return declaration;
	}


	private static Cloud_struct_field load_struct_field(boolean used_in_method_params, String api_interface_name, String struct_type_name, String name_def, Object declaration_def,
		Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {

		Compound_name compound_name = Compound_name.parse(name_def, Compound_name.IdentifierStyle.Camel);
		return new Cloud_struct_field(compound_name.name, compound_name.identifier,
			load_type_declaration(used_in_method_params, api_interface_name, struct_type_name + Generator.uppercase_first_letter(compound_name.identifier), declaration_def, struct_types_by_name));
	}
}
