package org.unified.templates;

import org.unified.Parser;
import org.unified.Unified_error;
import org.unified.dev.generator.Generator;
import org.unified.templates.compiler.ClassGenerator;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Template_engine {
	private final String host_language;
	private final Class source_class;
	private final String source_relative_path;
	private final String generated_class_prefix;
	public String shared_header;
	public Object[] shared_arg_names_and_types;



	public Template_engine(String host_language, Class source_class, String source_relative_path) {
		this.host_language = host_language;
		this.source_class = source_class;
		this.source_relative_path = source_relative_path;
		this.generated_class_prefix = source_class.getSimpleName() + "_" + source_relative_path.replace("/", "_").toLowerCase() + "_";
	}



	public Template load(String template_name, String header, Object... arg_names_and_types) throws Exception {
		String generator_class_name = Generator.uppercase_first_letter(generated_class_prefix) + template_name.toLowerCase();
		StringBuilder code = new StringBuilder();

		if (shared_header != null) {
			code.append(shared_header);
		}
		if (header != null) {
			code.append(header);
		}

		code.append("public class ").append(generator_class_name).append(" {\n");
		code.append("\tpublic void generate(").append("PrintWriter _out");
		List<Class> arg_types_builder = new ArrayList<>();
		arg_types_builder.add(PrintWriter.class);
		if (shared_arg_names_and_types != null) {
			add_arg_names_and_types(arg_types_builder, code, shared_arg_names_and_types);
		}
		add_arg_names_and_types(arg_types_builder, code, arg_names_and_types);
		Class[] arg_types = arg_types_builder.toArray(new Class[arg_types_builder.size()]);


		code.append(") throws Exception{\n");

		parse_template(template_name, code);

		code.append("\t}\n");
		code.append("}\n");
//		System.out.print(code);

		Class generator_class = create_generator_class(generator_class_name, code.toString());
		return new Template(generator_class_name, code.toString(), generator_class.getConstructor().newInstance(), arg_types);
	}




	private Class create_generator_class(String class_name, String code) throws MalformedURLException, ClassNotFoundException {
		var generator = new ClassGenerator();
		return generator.generate(class_name, code);
	}



	private void parse_template(String template_name, StringBuilder code) throws Exception {
		Parser parser;
		String resource_path = Paths.get(source_relative_path, template_name + ".st" + host_language).toString();
		try (InputStream templates_stream = source_class.getResourceAsStream(resource_path)) {
			if (templates_stream == null) {
				throw new Unified_error("Can not found template: " + template_name);
			}
			parser = new Parser(read_all_text(templates_stream, 1024), template_name, Parser.Whitespaces.Pass);
		}

		while (!parser.eof()) {
			parse_text(parser, code);
			if (parser.eof()) {
				break;
			}
			parser.expect("~", Parser.Whitespaces.Not_pass);
			if (parser.pass("+")) {
				parse_include(parser, code);
			} else if (parser.pass("=")) {
				parse_expression(parser, code);
			} else {
				parse_code(parser, code);
			}
		}
	}



	private void parse_include(Parser parser, StringBuilder code) throws Exception {
		String template_name = parser.pass_until("~").trim();
		parser.pass("~", Parser.Whitespaces.Not_pass);
		parse_template(template_name, code);
	}



	private void parse_text(Parser parser, StringBuilder code) {
		String text = parser.pass_until("~");
		if (text == null) {
			text = parser.pass_until_end();
		}
		if (text != null && !text.isEmpty()) {
			gen_text("\t\t", text, code);
		}
	}



	private void parse_code(Parser parser, StringBuilder code) throws Unified_error {
		String java_code = parser.pass_until("~");
		parser.pass("~", Parser.Whitespaces.Not_pass);
		if (!parser.pass("~", Parser.Whitespaces.Not_pass)) {
			if (parser.pass("\n", Parser.Whitespaces.Not_pass)) {
				parser.pass("\r", Parser.Whitespaces.Not_pass);
			} else if (parser.pass("\r", Parser.Whitespaces.Not_pass)) {
				parser.pass("\n", Parser.Whitespaces.Not_pass);
			}
		}
		if (java_code == null) {
			throw new Unified_error("Invalid template: unterminated code block" + parser.context());
		}
		code.append(java_code);
		code.append("\n");
	}



	private void parse_expression(Parser parser, StringBuilder code) throws Unified_error {
		String expression = parser.pass_until("~");
		if (expression == null) {
			throw new Unified_error("Invalid template: unterminated expression" + parser.context());
		}
		parser.pass("~", Parser.Whitespaces.Not_pass);
		code.append("\t\t").append("_out.print(").append(expression.trim()).append(");\n");
	}



	void add_arg_names_and_types(List<Class> arg_types, StringBuilder params, Object... arg_names_and_types) {
		for (int i = 0; i < arg_names_and_types.length - 1; i += 2) {
			String arg_name = (String) arg_names_and_types[i];
			Class arg_type = (Class) arg_names_and_types[i + 1];
			arg_types.add(arg_type);
			params.append(", ").append(arg_type.getTypeName()).append(' ').append(arg_name);
		}

	}



	public void gen_text(String indent, String text, StringBuilder code) {
		code.append(indent).append("_out.print(\"").append(text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")).append("\");\n");
	}



	public static String read_text_resource(Class resource_owner, String resource_path) throws IOException {
		try (InputStream text_stream = resource_owner.getResourceAsStream(resource_path)) {
			return text_stream != null ? read_all_text(text_stream, 1024) : null;
		}
	}



	public static String read_text_file(Path path) throws IOException {
		try (InputStream text_stream = Files.newInputStream(path)) {
			return text_stream != null ? read_all_text(text_stream, 1024) : null;
		}
	}



	private static String read_all_text(final InputStream stream, final int buffer_size) throws IOException {
		final char[] buffer = new char[buffer_size];
		final StringBuilder out = new StringBuilder();
		try (Reader in = new InputStreamReader(stream, "UTF-8")) {
			for (; ; ) {
				int read = in.read(buffer, 0, buffer.length);
				if (read < 0) {
					break;
				}
				out.append(buffer, 0, read);
			}
		}
		return out.toString();
	}
}
