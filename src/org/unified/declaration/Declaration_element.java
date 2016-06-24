package org.unified.declaration;


import org.unified.Parser;
import org.unified.Unified_error;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Declaration_element {
	public final Declaration_attribute[] attributes;
	public final Declaration_element[] children;





	public Declaration_element(Declaration_attribute[] attributes, Declaration_element[] children) {
		this.attributes = attributes;
		this.children = children;
	}





	public String name() {
		return attributes.length > 0 ? attributes[0].name : null;
	}





	public String value() {
		return attributes.length > 1 ? attributes[1].name : null;
	}





	public static Declaration_element[] load_from(Path file_path) throws Declaration_error {
		try {
			String file_content = new String(Files.readAllBytes(file_path), Charset.forName("UTF-8"));
			return parse(file_content);
		} catch (IOException e) {
			throw new Declaration_error(null, "Can not load_from declarations from file: " + file_path.toAbsolutePath(), e);
		}
	}





	public static Declaration_element[] parse(String source) throws Declaration_error {
		Parser parser = new Parser(source, "", Parser.Whitespaces.Not_pass);
		parser.whitespace_test = Parser::is_whitespace;
		return parseElements(parser, 0);
	}





	public String get_string_attribute(String name, String default_value) throws Declaration_error {
		for (Declaration_attribute attribute : attributes) {
			if (name.equalsIgnoreCase(attribute.name)) {
				return attribute.getString();
			}
		}
		return default_value;
	}





	public static Declaration_element first_element_with_name(String name, Declaration_element[] declarations) {
		for (Declaration_element element : declarations) {
			if (name.equalsIgnoreCase(element.name())) {
				return element;
			}
		}
		return null;
	}





	public int get_int_attribute(String name, int defaultValue) throws Declaration_error {
		String string = get_string_attribute(name, null);
		return string != null ? Integer.parseInt(string) : defaultValue;
	}


	// Parser





	private static Declaration_element[] parseElements(Parser parser, int elementIndent) throws Declaration_error {
		List<Declaration_element> elements = new ArrayList<>();
		Declaration_element element = parseElement(parser, elementIndent);
		while (element != null) {
			elements.add(element);
			element = parseElement(parser, elementIndent);
		}
		return elements.toArray(new Declaration_element[elements.size()]);
	}





	private static Declaration_element parseElement(Parser parser, int elementIndent) throws Declaration_error {
		if (parser.eof()) {
			return null;
		}

		pass_empty_and_comment_lines(parser);

		if (!pass_indent(parser, elementIndent)) {
			return null;
		}

		Declaration_attribute[] attributes = parse_attributes(parser, elementIndent);
		Declaration_element[] children = parseElements(parser, elementIndent + 1);

		return new Declaration_element(attributes, children);
	}





	private static void pass_empty_and_comment_lines(Parser parser) {
		while (!parser.eof()) {
			int save_pos = parser.pos;
			parser.pass_whitespaces();
			if (parser.pass("#")) {
				parser.pass_until_eof_or(Parser::is_new_line, Parser.Whitespaces.Not_pass);
			}
			if (parser.eof()) {
				return;
			}
			if (parser.pass(Parser::is_new_line, Parser.Whitespaces.Not_pass) == null) {
				parser.pos = save_pos;
				return;
			}
		}
	}





	private static Declaration_attribute[] parse_attributes(Parser parser, int elementIndent) throws Declaration_error {
		List<Declaration_attribute> attributes = new ArrayList<>();
		while (!parser.eof()) {
			if (parser.pass(Parser::is_new_line, Parser.Whitespaces.Not_pass) != null) {
				int save_pos = parser.pos;
				if (!(pass_indent(parser, elementIndent + 1) && parser.pass("~", Parser.Whitespaces.Pass))) {
					parser.pos = save_pos;
					break;
				}
			}
			String name = expect_name(parser, Parser.Whitespaces.Pass);
			Object value = null;
			if (parser.pass("=", Parser.Whitespaces.Pass)) {
				value = pass_attribute_value(parser);
			}
			attributes.add(new Declaration_attribute(name, value));
		}
		return attributes.toArray(new Declaration_attribute[attributes.size()]);
	}





	private static Object pass_attribute_value(Parser parser) throws Declaration_error {
		if (parser.pass("(", Parser.Whitespaces.Pass)) {
			List<String> values = new ArrayList<>();
			while (!parser.pass(")", Parser.Whitespaces.Pass)) {
				Object value = pass_name_or_value(parser);
				if (value == null) {
					throw new Declaration_error(null, "Invalid value list", null);
				}
				values.add((String) value);
			}
			return values.toArray(new String[values.size()]);
		}
		return pass_name_or_value(parser);
	}





	private static String pass_name_or_value(Parser parser) throws Declaration_error {
		if (parser.pass("'", Parser.Whitespaces.Not_pass)) {
			String value = parser.pass_until("'");
			try {
				parser.expect("'", Parser.Whitespaces.Pass);
			} catch (Unified_error unigen_exception) {
				throw new Declaration_error(null, "Invalid value", unigen_exception);
			}
			return value;
		}
		return parser.pass_until_eof_or(Declaration_element::isNameOrValueTerminator, Parser.Whitespaces.Pass);
	}





	private static boolean pass_indent(Parser parser, int expected_indent) {
		int indent = 0;
		int save_pos = parser.pos;
		if (parser.pass("\t")) {
			++indent;
			while (parser.pass("\t")) {
				++indent;
			}
		} else if (parser.pass("    ")) {
			++indent;
			while (parser.pass("    ")) {
				++indent;
			}
		}
		if (indent != expected_indent) {
			parser.pos = save_pos;
		}
		return indent == expected_indent;
	}





	private static String expect_name(Parser parser, Parser.Whitespaces whitespaces) throws Declaration_error {
		String passed = pass_name_or_value(parser);
		if (passed != null) {
			if (whitespaces == Parser.Whitespaces.Pass) {
				parser.pass_whitespaces();
			}
			return passed;
		}
		throw new Declaration_error(null, "name expected", null);
	}





	private static boolean isNameOrValueChar(char c) {
		return !(Character.isWhitespace(c) || c == '=' || c == '(' || c == ')' || c == '\'' || c == '~');
	}





	private static boolean isNameOrValueTerminator(char c) {
		return !isNameOrValueChar(c);
	}





	public boolean name_is(String name) {
		return name.equalsIgnoreCase(name());
	}





	public Declaration_element first_child_with_name(String name) {
		return first_element_with_name(name, children);
	}
}
