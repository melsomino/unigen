package org.unified.module.cloud;

import org.unified.Unified_error;
import org.unified.declaration.Declaration_attribute;
import org.unified.declaration.Declaration_element;
import org.unified.dev.generator.Generator;
import org.unified.module.Compound_name;
import org.unified.module.Module_loader;

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
			struct_types_by_name.put(type_name, struct_type);
		});

		Cloud_method[] methods = Module_loader.load_default_array(cloud_element, null, Cloud_method[]::new,
			(method_element) -> load_method_from(method_element, module_name, url, protocol, struct_types_by_name));

		Cloud_struct_type[] struct_types = new Cloud_struct_type[struct_types_by_name.size()];
		int index = 0;
		for (Cloud_type_declaration declaration : struct_types_by_name.values()) {
			struct_types[index++] = declaration.struct_type();
		}
		return new Cloud_api(struct_types, methods);
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

		Cloud_type_declaration params = load_type_declaration(params_element, module_name, params_struct_type_name, true, struct_types_by_name);
		Cloud_type_declaration result = load_type_declaration(result_element, module_name, result_struct_type_name, false, struct_types_by_name);

		return new Cloud_method(url, protocol, compound_name.name, compound_name.identifier, params, result);
	}





	private static class Declaration_summary {
		final Cloud_type_modifier modifier;
		final Cloud_type_encoding encoding;
		final Cloud_primitive_type primitive_type;





		Declaration_summary(Cloud_type_modifier modifier, Cloud_type_encoding encoding, Cloud_primitive_type primitive_type) {
			this.modifier = modifier;
			this.encoding = encoding;
			this.primitive_type = primitive_type;
		}





		static Declaration_summary from(Declaration_element element) throws Unified_error {
			Cloud_type_modifier modifier = Cloud_type_modifier.Missing;
			Cloud_type_encoding encoding = Cloud_type_encoding.JsonValue;
			Cloud_primitive_type primitive_type = null;

			for (Declaration_attribute attribute : element.attributes) {
				String lowercase_name = attribute.name.toLowerCase();
				switch (lowercase_name) {
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
						Cloud_primitive_type test_primitive_type = Cloud_primitive_type.try_parse(lowercase_name);
						if (test_primitive_type != null) {
							if (primitive_type != null) {
								throw new Unified_error("Primitive type " + primitive_type.name + " redeclared to " + test_primitive_type.name);
							}
							primitive_type = test_primitive_type;
						}
				}
			}
			return new Declaration_summary(modifier, encoding, primitive_type);
		}

	}





	private static Cloud_type_declaration load_type_declaration(Declaration_element element, String module_name, String struct_type_name, boolean used_in_method_params,
		Map<String, Cloud_type_declaration> struct_types_by_name) throws Exception {
		if (element == null) {
			return null;
		}

		Declaration_summary summary = Declaration_summary.from(element);

		if (summary.primitive_type != null) {
			return new Cloud_type_declaration(summary.primitive_type, summary.modifier, Cloud_type_encoding.JsonValue);
		}

		Cloud_struct_field[] fields = Module_loader.load_default_array(element, null, Cloud_struct_field[]::new, (field_element) -> {
			Compound_name compound_name = new Compound_name(field_element, Compound_name.Identifier_style.Camel);
			Cloud_type_declaration declaration = load_type_declaration(field_element, module_name, struct_type_name + Generator.uppercase_first_letter(compound_name.identifier),
				used_in_method_params, struct_types_by_name);
			return new Cloud_struct_field(compound_name.name, compound_name.identifier, declaration);
		});


		Cloud_struct_type struct_type = new Cloud_struct_type(module_name, struct_type_name, fields);
		Cloud_type_declaration declaration = new Cloud_type_declaration(struct_type, summary.modifier, summary.encoding);

		struct_types_by_name.put(struct_type.interface_name, declaration);

		if (used_in_method_params) {
			struct_type.used_in_method_params = true;
		}

		return declaration;
	}


}
