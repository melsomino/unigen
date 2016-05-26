package org.unigen.temple;

import net.openhft.compiler.CompilerUtils;
import org.unigen.Parser;
import org.unigen.Unigen_exception;
import org.unigen.generator.Generator;

import java.io.*;
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
		code.append("\tpublic void generate(").append("PrintStream _out");
		List<Class> arg_types_builder =new ArrayList<>();
		arg_types_builder.add(PrintStream.class);
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

		Class generator_class = CompilerUtils.CACHED_COMPILER.loadFromJava(generator_class_name, code.toString());
		return new Template(generator_class.newInstance(), arg_types);
	}


	private void parse_template(String template_name, StringBuilder code) throws Exception {
		Parser parser;
		String resource_path = Paths.get(source_relative_path, template_name+ ".st" + host_language).toString();
		try (InputStream templates_stream = source_class.getResourceAsStream(resource_path)) {
			if (templates_stream == null) {
				throw new Unigen_exception("Can not found template: " + template_name);
			}
			parser = new Parser(read_all_text(templates_stream, 1024), template_name, Parser.Whitespaces.Pass);
		}

		while (!parser.eof()) {
			parse_text(parser, code);
			if (parser.eof()) {
				break;
			}
			parser.expect("~", Parser.Whitespaces.Not_pass);
			if(parser.pass("+")) {
				parse_include(parser, code);
			}
			else if(parser.pass("=")) {
				parse_expression(parser, code);
			}
			else {
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


	private void parse_code(Parser parser, StringBuilder code) throws Unigen_exception {
		String java_code = parser.pass_until("~");
		parser.pass("~", Parser.Whitespaces.Not_pass);
		if (!parser.pass("~", Parser.Whitespaces.Not_pass)) {
			if(parser.pass("\n", Parser.Whitespaces.Not_pass)) {
				parser.pass("\r", Parser.Whitespaces.Not_pass);
			}
			else if(parser.pass("\r", Parser.Whitespaces.Not_pass)) {
				parser.pass("\n", Parser.Whitespaces.Not_pass);
			}
		}
		if (java_code == null) {
			throw new Unigen_exception("Invalid template: unterminated code block" + parser.context());
		}
		code.append(java_code);
		code.append("\n");
	}


	private void parse_expression(Parser parser, StringBuilder code) throws Unigen_exception {
		String expression = parser.pass_until("~");
		if(expression == null) {
			throw new Unigen_exception("Invalid template: unterminated expression" + parser.context());
		}
		parser.pass("~", Parser.Whitespaces.Not_pass);
		code.append("\t\t").append("_out.print(").append(expression.trim()).append(");\n");
	}


	void add_arg_names_and_types(List<Class> arg_types, StringBuilder params, Object... arg_names_and_types) {
		for(int i = 0; i < arg_names_and_types.length - 1; i += 2) {
			String arg_name = (String) arg_names_and_types[i];
			Class arg_type = (Class) arg_names_and_types[i + 1];
			arg_types.add(arg_type);
			params.append(", ").append(arg_type.getTypeName()).append(' ').append(arg_name);
		}

	}
	public void gen_text(String indent, String text, StringBuilder code) {
		code.append(indent).append("_out.print(\"").append(text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")).append("\");\n");
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
