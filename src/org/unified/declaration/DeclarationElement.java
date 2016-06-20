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
	public final String name;
	public final DeclarationAttribute[] attributes;
	public final DeclarationElement[] children;

	public DeclarationElement(String name, DeclarationAttribute[] attributes, DeclarationElement[] children) {
		this.name = name;
		this.attributes = attributes;
		this.children = children;
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

		if (!passDeclarationIndent(parser, elementIndent)) {
			return null;
		}

		String name = expectName(parser, Parser.Whitespaces.Pass).toLowerCase();
		DeclarationAttribute[] attributes = parseDeclarationAttributes(parser, elementIndent);
		DeclarationElement[] children = parseElements(parser, elementIndent + 1);

		return new DeclarationElement(name, attributes, children);
	}

	private static DeclarationAttribute[] parseDeclarationAttributes(Parser parser, int elementIndent) throws DeclarationError {
		List<DeclarationAttribute> attributes = new ArrayList<>();
		while (!parser.eof()) {
			if (parser.pass(Parser::is_new_line, Parser.Whitespaces.Not_pass) != null) {
				int save_pos = parser.pos;
				if (!(passDeclarationIndent(parser, elementIndent + 1) && parser.pass("~", Parser.Whitespaces.Pass))) {
					parser.pos = save_pos;
					break;
				}
			}
			if (parser.pass("#", Parser.Whitespaces.Pass)) {
				attributes.add(new DeclarationAttribute("id", expectName(parser, Parser.Whitespaces.Pass)));
			}
			else {
				String name = expectName(parser, Parser.Whitespaces.Pass).toLowerCase();
				Object value = null;
				if (parser.pass("=", Parser.Whitespaces.Pass)) {
					value = passAttributeValue(parser);
				}
				attributes.add(new DeclarationAttribute(name, value));
			}
		}
		return attributes.toArray(new DeclarationAttribute[attributes.size()]);
	}


	private static Object passAttributeValue(Parser parser) throws DeclarationError {
		if (parser.pass("(", Parser.Whitespaces.Pass)) {
			List<String> values = new ArrayList<>();
			while (!parser.pass(")", Parser.Whitespaces.Pass)) {
				Object value = passAttributeValue(parser);
				if (value == null) {
					throw new DeclarationError("Invalid value list", null);
				}
				values.add((String)value);
			}
			return values.toArray(new String[values.size()]);
		}
		if (parser.pass("'", Parser.Whitespaces.Pass)) {
			String value = parser.pass_until("'");
			try {
				parser.expect("'", Parser.Whitespaces.Pass);
			} catch (Unigen_exception unigen_exception) {
				throw new DeclarationError("Invalid value", unigen_exception);
			}
			return value;
		}
		return parser.pass_until_eof_or(DeclarationElement::isValueTerminator, Parser.Whitespaces.Pass);
	}

	private static boolean isValueTerminator(char c) {
		return !isNameChar(c);
	}


	private static boolean isNewLine(char c) {
		return c == '\r' || c == '\n';
	}


	private static boolean passDeclarationIndent(Parser parser, int expectedIndent) {
		int indent = 0;
		int savePos = parser.pos;
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
		if (indent != expectedIndent) {
			parser.pos = savePos;
		}
		return indent == expectedIndent;
	}


	private static boolean isNameChar(char c) {
		return !(Character.isWhitespace(c) || c == '=' || c == '(' || c == ')' || c == '#' || c == '\'' || c == '"');
	}

	private static String passName(Parser parser, Parser.Whitespaces whitespaces) {
		return parser.pass(DeclarationElement::isNameChar, whitespaces);
	}


	private static String expectName(Parser parser, Parser.Whitespaces whitespaces) throws DeclarationError {
		String passed = passName(parser, whitespaces);
		if (passed != null) {
			return passed;
		}
		throw new DeclarationError("name expected", null);
	}

	public String get_string_attribute(String name) throws DeclarationError {
		for(DeclarationAttribute attribute : attributes) {
			if (name.equalsIgnoreCase(attribute.name)) {
				return attribute.getString();
			}
		}
		return null;
	}
}
