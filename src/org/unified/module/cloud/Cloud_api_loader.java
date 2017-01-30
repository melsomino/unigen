package org.unified.module.cloud;

import org.unified.declaration.Declaration_attribute;
import org.unified.declaration.Declaration_element;
import org.unified.declaration.Declaration_error;
import org.unified.dev.generator.Generator;
import org.unified.module.Compound_name;
import org.unified.module.Module_loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cloud_api_loader {


	public static Cloud_api load_from(Declaration_element cloud_element, String module_name) throws Exception {
		final String url = cloud_element.get_string_attribute("url", null);
		final int protocol = cloud_element.get_int_attribute("protocol", 4);

		Map<String, Cloud_type_declaration> struct_types_by_name = new HashMap<>();

		Module_loader.enum_default_items(cloud_element, "+types", (type_declaration) -> {
			String type_name = type_declaration.name();
			Cloud_type_declaration struct_type = load_type_declaration(type_declaration, module_name, type_name, true, struct_types_by_name);
		});

		Cloud_method[] methods = Module_loader.load_default_items(cloud_element, null, Cloud_method[]::new,
			(method_element) -> load_method_from(method_element, module_name, url, protocol, struct_types_by_name));

		ArrayList<Cloud_struct_type> struct_types = new ArrayList<>();
		for (Map.Entry<String, Cloud_type_declaration> declaration : struct_types_by_name.entrySet()) {
			if (!declaration.getKey().startsWith("#")) {
				struct_types.add(declaration.getValue().struct_type());
			}
		}
		return new Cloud_api(struct_types.toArray(new Cloud_struct_type[struct_types.size()]), methods);
	}



	private static Cloud_method load_method_from(Declaration_element method_element, String module_name, String default_url, int default_protocol,
		Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {
		final String url = method_element.get_string_attribute("url", default_url);
		final int protocol = method_element.get_int_attribute("protocol", default_protocol);
		Compound_name compound_name = new Compound_name(method_element, Compound_name.Identifier_style.Camel);

		Declaration_element params_element = method_element.first_child_with_name("params");
		Declaration_element result_element = method_element.first_child_with_name("result");
		String specified_result_type_name = result_element != null ? result_element.get_string_attribute("as", null) : null;

		String params_struct_type_name = Generator.uppercase_first_letter(compound_name.identifier) + "Params";
		String result_struct_type_name = Generator.uppercase_first_letter(specified_result_type_name != null ? specified_result_type_name : compound_name.identifier);

		Cloud_type_declaration params = load_type_declaration(params_element, module_name, params_struct_type_name, false, struct_types_by_name);
		Cloud_type_declaration result = load_type_declaration(result_element, module_name, result_struct_type_name, true, struct_types_by_name);
		return new Cloud_method(url, protocol, compound_name.name, compound_name.identifier, params, result);
	}



	private static Cloud_type_declaration load_type_declaration(Declaration_element element, String module_name, String struct_type_name, boolean is_public,
		Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {

		if (element == null) {
			return null;
		}

		Declaration_summary summary = Declaration_summary.from(element, struct_types_by_name);

		if (summary.primitive_type != null) {
			return new Cloud_type_declaration(summary.primitive_type, summary.modifier, Cloud_type_encoding.JsonValue, summary.value);
		}

		if (summary.struct_type != null) {
			return new Cloud_type_declaration(summary.struct_type, summary.modifier, summary.encoding, summary.value);
		}

		Cloud_struct_field[] fields = Module_loader.load_default_items(element, null, Cloud_struct_field[]::new, (field_element) -> {
			Compound_name compound_name = new Compound_name(field_element, Compound_name.Identifier_style.Camel);
			Cloud_type_declaration declaration = load_type_declaration(field_element, module_name, struct_type_name + Generator.uppercase_first_letter(compound_name.identifier), is_public,
				struct_types_by_name);
			return new Cloud_struct_field(compound_name.name, compound_name.identifier, declaration);
		});


		Cloud_struct_type struct_type = new Cloud_struct_type(module_name, struct_type_name, fields);
		Cloud_type_declaration declaration = new Cloud_type_declaration(struct_type, summary.modifier, summary.encoding, summary.value);

		if (is_public) {
			struct_types_by_name.put(struct_type.interface_name, declaration);
			struct_types_by_name.put("#" + struct_type.implementation_name, declaration);
		}

		return declaration;
	}



	private static class Declaration_summary {
		final Cloud_type_modifier modifier;
		final Cloud_type_encoding encoding;
		final Cloud_primitive_type primitive_type;
		final Cloud_struct_type struct_type;
		final String value;



		Declaration_summary(Cloud_type_modifier modifier, Cloud_type_encoding encoding, Cloud_primitive_type primitive_type, Cloud_struct_type struct_type, String value) {
			this.modifier = modifier;
			this.encoding = encoding;
			this.primitive_type = primitive_type;
			this.struct_type = struct_type;
			this.value = value;
		}



		static Declaration_summary from(Declaration_element element, Map<String, Cloud_type_declaration> struct_types) throws Declaration_error {
			Cloud_type_modifier modifier = Cloud_type_modifier.Missing;
			Cloud_type_encoding encoding = Cloud_type_encoding.JsonValue;
			Cloud_primitive_type primitive_type = null;
			Cloud_struct_type struct_type = null;
			String value = null;

			for (int i = 1; i < element.attributes.length; ++i) {
				Declaration_attribute attribute = element.attributes[i];
				String lowercase_name = attribute.name.toLowerCase();
				switch (lowercase_name) {
					case "value":
						value = attribute.getString();
						break;
					case "json-string":
						encoding = Cloud_type_encoding.JsonString;
						break;
					case "array":
						modifier = Cloud_type_modifier.Array;
						break;
					case "object":
						modifier = Cloud_type_modifier.Object;
						break;
					case "record":
						modifier = Cloud_type_modifier.Record;
						break;
					case "recordset":
						modifier = Cloud_type_modifier.Recordset;
						break;
					case "parameters":
						modifier = Cloud_type_modifier.Parameters;
						break;
					default:
						Cloud_type_declaration test_struct_type_declaration = struct_types.get("#" + attribute.name);
						if (test_struct_type_declaration != null) {
							Cloud_struct_type test_struct_type = test_struct_type_declaration.struct_type();
							if (primitive_type != null) {
								throw new Declaration_error(element, "Type " + primitive_type.name + " redeclared to " + test_struct_type.interface_name, null);
							}
							if (struct_type != null) {
								throw new Declaration_error(element, "Type " + struct_type.interface_name + " redeclared to " + test_struct_type.interface_name, null);
							}
							struct_type = test_struct_type;
						} else {
							Cloud_primitive_type test_primitive_type = Cloud_primitive_type.try_parse(lowercase_name);
							if (test_primitive_type != null) {
								if (primitive_type != null) {
									throw new Declaration_error(element, "Type " + primitive_type.name + " redeclared to " + test_primitive_type.name, null);
								}
								if (struct_type != null) {
									throw new Declaration_error(element, "Type " + struct_type.interface_name + " redeclared " + test_primitive_type.name, null);
								}
								if (attribute.value != null) {
									value = attribute.getString();
								}
								primitive_type = test_primitive_type;
							}
						}
				}
			}
			return new Declaration_summary(modifier, encoding, primitive_type, struct_type, value);
		}

	}


}
