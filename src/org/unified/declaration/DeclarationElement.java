package org.unified.declaration;


import org.unigen.Parser;
import org.unigen.Unigen_exception;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DeclarationElement {
	public final DeclarationAttribute[] attributes;
	public final DeclarationElement[] children;





	public DeclarationElement(DeclarationAttribute[] attributes, DeclarationElement[] children) {
		this.attributes = attributes;
		this.children = children;
	}





	public String getName() {
		return attributes.length > 0 ? attributes[0].name : null;
	}





	public static DeclarationElement[] load(Path file_path) throws DeclarationError {
		try {
			String file_content = new String(Files.readAllBytes(file_path), Charset.forName("UTF-8"));
			return parse(file_content);
		} catch (IOException e) {
			throw new DeclarationError("Can not load declarations from file: " + file_path.toAbsolutePath(), e);
		}
	}





	public static DeclarationElement[] parse(String source) throws DeclarationError {
		Parser parser = new Parser(source, "", Parser.Whitespaces.Not_pass);
		parser.whitespace_test = Parser::is_whitespace;
		return parseElements(parser, 0);
	}





	public String get_string_attribute(String name) throws DeclarationError {
		for (DeclarationAttribute attribute : attributes) {
			if (name.equalsIgnoreCase(attribute.name)) {
				return attribute.getString();
			}
		}
		return null;
	}





	public static DeclarationElement find_first_element_with_name(String name, DeclarationElement[] declarations) {
		for (DeclarationElement element : declarations) {
			if (name.equalsIgnoreCase(element.getName())) {
				return element;
			}
		}
		return null;
	}





	public int get_int_attribute(String name, int defaultValue) throws DeclarationError {
		String string = get_string_attribute(name);
		return string != null ? Integer.parseInt(string) : defaultValue;
	}


	// Parser





	private static DeclarationElement[] parseElements(Parser parser, int elementIndent) throws DeclarationError {
		List<DeclarationElement> elements = new ArrayList<>();
		DeclarationElement element = parseElement(parser, elementIndent);
		while (element != null) {
			elements.add(element);
			element = parseElement(parser, elementIndent);
		}
		return elements.toArray(new DeclarationElement[elements.size()]);
	}





	private static DeclarationElement parseElement(Parser parser, int elementIndent) throws DeclarationError {
		if (parser.eof()) {
			return null;
		}

		pass_empty_and_comment_lines(parser);

		if (!pass_indent(parser, elementIndent)) {
			return null;
		}

		DeclarationAttribute[] attributes = parse_attributes(parser, elementIndent);
		DeclarationElement[] children = parseElements(parser, elementIndent + 1);

		return new DeclarationElement(attributes, children);
	}





	private static void pass_empty_and_comment_lines(Parser parser) {
		while (!parser.eof()) {
			int save_pos = parser.pos;
			parser.pass_whitespaces();
			if (parser.pass("#")) {
				parser.pass_until_eof_or(Parser::is_new_line, Parser.Whitespaces.Not_pass);
			}
			if (!parser.eof() || parser.pass(Parser::is_new_line, Parser.Whitespaces.Not_pass) == null) {
				parser.pos = save_pos;
				return;
			}
		}
	}





	private static DeclarationAttribute[] parse_attributes(Parser parser, int elementIndent) throws DeclarationError {
		List<DeclarationAttribute> attributes = new ArrayList<>();
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
			attributes.add(new DeclarationAttribute(name, value));
		}
		return attributes.toArray(new DeclarationAttribute[attributes.size()]);
	}





	private static Object pass_attribute_value(Parser parser) throws DeclarationError {
		if (parser.pass("(", Parser.Whitespaces.Pass)) {
			List<String> values = new ArrayList<>();
			while (!parser.pass(")", Parser.Whitespaces.Pass)) {
				Object value = pass_name_or_value(parser);
				if (value == null) {
					throw new DeclarationError("Invalid value list", null);
				}
				values.add((String) value);
			}
			return values.toArray(new String[values.size()]);
		}
		return pass_name_or_value(parser);
	}





	private static String pass_name_or_value(Parser parser) throws DeclarationError {
		if (parser.pass("'", Parser.Whitespaces.Pass)) {
			String value = parser.pass_until("'");
			try {
				parser.expect("'", Parser.Whitespaces.Pass);
			} catch (Unigen_exception unigen_exception) {
				throw new DeclarationError("Invalid value", unigen_exception);
			}
			return value;
		}
		return parser.pass_until_eof_or(DeclarationElement::isNameOrValueTerminator, Parser.Whitespaces.Pass);
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





	private static String expect_name(Parser parser, Parser.Whitespaces whitespaces) throws DeclarationError {
		String passed = pass_name_or_value(parser);
		if (passed != null) {
			if (whitespaces == Parser.Whitespaces.Pass) {
				parser.pass_whitespaces();
			}
			return passed;
		}
		throw new DeclarationError("name expected", null);
	}





	private static boolean isNameOrValueChar(char c) {
		return !(Character.isWhitespace(c) || c == '=' || c == '(' || c == ')' || c == '\'' || c == '~');
	}





	private static boolean isNameOrValueTerminator(char c) {
		return !isNameOrValueChar(c);
	}





	public boolean name_is(String name) {
		return name.equalsIgnoreCase(getName());
	}
}
