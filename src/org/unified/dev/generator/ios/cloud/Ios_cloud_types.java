package org.unified.dev.generator.ios.cloud;

import org.unified.Unified_error;
import org.unified.dev.generator.Generator;
import org.unified.module.cloud.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class Ios_cloud_types {


	public String get_primitive_type_name(Cloud_primitive_type type) throws Unified_error {
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
			return "Uuid";
		}
		if (type == Cloud_primitive_type.int64_type) {
			return "Int64";
		}
		if (type == Cloud_primitive_type.date_time_type) {
			return "Date";
		}
		throw new Unified_error("Can not determine iOS native type for primitive type: " + type.record_schema_name);
	}





	public String interface_field_type_declaration(Cloud_type_declaration declaration) throws Unified_error {
		return ": " + interface_declaration(declaration);
	}





	public String implementation_field_type_declaration(Cloud_type_declaration declaration) throws Unified_error {
		String declaration_string = interface_declaration(declaration);
		return declaration.modifier.is_array() ? " = " + declaration_string + "()" : ": " + declaration_string;
	}





	public String param_type_declaration(Cloud_type_declaration declaration) throws Unified_error {
		String declaration_string = interface_declaration(declaration);
		return ": " + declaration_string;
	}





	public String interface_declaration(Cloud_type_declaration declaration) throws Unified_error {
		String interface_type_name = get_interface_type_name(declaration);
		return declaration.modifier.is_array() ? "[" + interface_type_name + "?]" : interface_type_name + "?";
	}





	public String get_interface_type_name(Cloud_type_declaration declaration) throws Unified_error {
		return declaration.is_struct_type() ? declaration.struct_type().interface_name : get_primitive_type_name(declaration.primitive_type());
	}





	public String struct_implementation_creation(Cloud_type_declaration declaration) {
		return declaration.modifier.is_array() ? "[" + declaration.struct_type().implementation_name + "]()" : declaration.struct_type().implementation_name + "()";
	}





	public String quoted(String s) {
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r") + "\"";
	}





	private class CodeBuilder {
		StringBuilder code = new StringBuilder();
		String line_indent = "";
		boolean is_first = true;





		CodeBuilder indent() {
			line_indent += "\t";
			return this;
		}





		CodeBuilder unindent() {
			if (line_indent.length() > 0) {
				line_indent = line_indent.substring(1);
			}
			return this;
		}





		CodeBuilder start_line(String... strings) {
			code.append(line_indent);
			return text(strings);
		}





		CodeBuilder text(String... strings) {
			for (String string : strings) {
				code.append(string);
			}
			return this;
		}





		CodeBuilder end_line(String... strings) {
			text(strings);
			code.append("\n");
			return this;
		}





		CodeBuilder line(String... strings) {
			code.append(line_indent);
			text(strings);
			code.append("\n");
			return this;
		}





		@Override
		public String toString() {
			return code.toString();
		}
	}





	private void declare_param(String source, Cloud_type_declaration declaration, CodeBuilder code) throws Unified_error {
		if (declaration.value != null) {
			return;
		}

		if (!declaration.is_struct_type()) {
			if (!code.is_first) {
				code.text(", ");
			}
			code.is_first = false;
			code.text(source, param_type_declaration(declaration));
			return;
		}

		for (Cloud_struct_field field : declaration.struct_type().fields) {
			declare_param(combine(source, field.identifier), field.declaration, code);
		}
	}





	public String method_params_declaration(Cloud_method method) throws Unified_error {
		if (method.params == null) {
			return "";
		}
		if (!method.params.is_struct_type()) {
			return "_ params" + param_type_declaration(method.params);
		}
		CodeBuilder code = new CodeBuilder();
		declare_param("", method.params, code);
		return code.toString();
	}





	private String combine(String a, String b) {
		if (a == null || a.length() == 0) {
			return b;
		}
		return Generator.lowercase_first_letter(a) + Generator.uppercase_first_letter(b);
	}





	private void encode_json_object_param_value(String source, Cloud_type_declaration declaration, CodeBuilder code) throws Unified_error {
		code.end_line("[");
		code.indent();
		for (Cloud_struct_field field : declaration.struct_type().fields) {
			code.start_line("\"" + field.name + "\": ");
			encode_param_value(combine(source, field.identifier), field.declaration, code);
			code.end_line(",");
		}
		code.unindent();
		code.start_line("]");
	}





	private void encode_sbis_record_param_value(String source, Cloud_type_declaration declaration, CodeBuilder code) throws Unified_error {
		code.end_line("[");
		code.indent();
		code.line("[\"s\": [");
		code.indent();
		for (Cloud_struct_field field : declaration.struct_type().fields) {
			code.line("[\"n\": \"", field.name, "\", \"t\": \"", field.declaration.record_schema_name(), "\"],");
		}
		code.unindent();
		code.line("]],");
		code.line("[\"d\": [");
		code.indent();
		for (Cloud_struct_field field : declaration.struct_type().fields) {
			code.start_line("");
			encode_param_value(combine(source, field.identifier), field.declaration, code);
			code.end_line(",");
		}
		code.unindent();
		code.line("]]");
		code.unindent();
		code.start_line("]");
	}





	private void encode_param_value(String source, Cloud_type_declaration declaration, CodeBuilder code) throws Unified_error {
		if (declaration.value != null) {
			if (declaration.value.equals("null")) {
				code.text("NSNull()");
			} else if (declaration.primitive_type() == Cloud_primitive_type.text_type) {
				code.text(quoted(declaration.value));
			} else {
				code.text(declaration.value);
			}
			return;
		}

		if (!declaration.is_struct_type()) {
			code.text(declaration.get_to_json_conversion_method_name(), source, ")");
			return;
		}

		if (declaration.modifier == Cloud_type_modifier.Record) {
			encode_sbis_record_param_value(source, declaration, code);
		} else {
			encode_json_object_param_value(source, declaration, code);
		}
	}





	public String method_params_assignment(Cloud_method method) throws Unified_error {
		if (method.params == null) {
			return "\t\tlet params = [:]";
		}
		if (!method.params.is_struct_type()) {
			return "\t\tlet params = " + method.params.get_to_json_conversion_method_name() + "params)\n";
		}
		CodeBuilder code = new CodeBuilder();
		code.indent().indent();
		code.start_line("let params: Any = ");
		encode_param_value("", method.params, code);
		return code.toString();
	}





	public String struct_type_fields(Cloud_struct_type struct_type) {
		return Arrays.stream(struct_type.fields).map(field -> "\"" + field.name + "\"").collect(Collectors.joining(",", "[", "]"));
	}





	private StringBuilder append_declaration_decoder(Cloud_type_declaration declaration, String primitive_decoder, String struct_decoder, String source, StringBuilder code) {
		if (!declaration.is_struct_type()) {
			Cloud_primitive_type primitive_type = declaration.primitive_type();
			code.append(primitive_decoder).append(".").append(primitive_type.conversation_method_root);
			if (declaration.modifier == Cloud_type_modifier.Array) {
				code.append("Array");
			}
			return code.append("(").append(source).append(")");
		}

		code.append(struct_decoder).append(".");
		switch (declaration.modifier) {
			case Missing:
			case Object:
				code.append("jsonObject");
				break;
			case Record:
				code.append("sbisRecord");
				break;
			case Array:
				code.append("jsonArray");
				break;
			case Recordset:
				code.append("sbisRecordset");
				break;
			default:
				code.append(declaration.modifier);
				break;
		}
		Cloud_struct_type struct_type = declaration.struct_type();
		String name = Generator.lowercase_first_letter(struct_type.implementation_name);
		code.append("(").append(source).append(", type: ").append(struct_type.interface_name).append(".typeConverter)");
		return code;
	}


	private StringBuilder append_declaration_decoder(Cloud_type_declaration declaration, int index, StringBuilder code) {
		return append_declaration_decoder(declaration, "values", "values", Integer.toString(index), code);
	}





	public String struct_type_decoder(Cloud_struct_type struct_type) {
		StringBuilder code = new StringBuilder();
		int index = 0;
		for (Cloud_struct_field field : struct_type.fields) {
			code.append("\t\t\tresult.").append(field.identifier).append(" = ");
			append_declaration_decoder(field.declaration, index, code);
			code.append("\n");
			++index;
		}
		return code.toString();
	}




	public String method_result_decoder(Cloud_type_declaration declaration, String source) {
		return append_declaration_decoder(declaration, "JsonDecoder", "SbisDecoder", source, new StringBuilder()).toString();
	}

}
