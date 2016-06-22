package org.unified;


public class Parser {

	public interface Test_char {
		boolean pass(char c);
	}

	public enum Whitespaces {
		Pass, Not_pass
	}

	public final String source;
	private final String context_name;
	public int pos;
	private Whitespaces default_whitespaces;
	private StringBuilder accumulator = new StringBuilder();
	public Test_char whitespace_test = Parser::whitespace_or_new_line;

	public static boolean is_whitespace(char c) {
		return c == ' ' || c == '\t';
	}

	public static boolean is_new_line(char c) {
		return c == '\r' || c == '\n';
	}

	public static boolean whitespace_or_new_line(char c) {
		return is_whitespace(c) || is_new_line(c);
	}


	public Parser(String source, String context_name, Whitespaces default_whitespaces) {
		this.source = source;
		this.context_name = context_name;
		pos = 0;
		this.default_whitespaces = default_whitespaces;
		if (default_whitespaces == Whitespaces.Pass) {
			pass_whitespaces();
		}
	}


	public boolean eof() {
		return pos >= source.length();
	}


	public String context() {
		return ", " + context_name + ", at: " + source.substring(pos, Math.min(pos + 20, source.length()));
	}


	public void pass_whitespaces() {
		while (pos < source.length() && whitespace_test.pass(source.charAt(pos))) {
			++pos;
		}
	}


	public String pass_name(Whitespaces whitespaces) {
		int savePos = pos;
		accumulator.setLength(0);
		if (pos < source.length() && Character.isJavaIdentifierStart(source.charAt(pos))) {
			accumulator.append(source.charAt(pos));
			++pos;
			while (pos < source.length() && Character.isJavaIdentifierPart((source.charAt(pos)))) {
				accumulator.append(source.charAt(pos));
				++pos;
			}
		}
		if (accumulator.length() > 0) {
			if (whitespaces == Whitespaces.Pass) {
				pass_whitespaces();
			}
			return accumulator.toString();
		}
		pos = savePos;
		return null;
	}


	public String pass_name() {
		return pass_name(default_whitespaces);
	}


	public String pass_qualified_name() {
		return pass_qualified_name(default_whitespaces);
	}


	public String pass_qualified_name(Whitespaces whitespaces) {
		int save_pos = pos;
		String name = pass_name(Whitespaces.Not_pass);
		if (name != null) {
			while (pass(".", Whitespaces.Not_pass)) {
				name += ".";
				String next = pass_name(Whitespaces.Not_pass);
				if (next == null) {
					pos = save_pos;
					return null;
				}
				name += next;
			}
		}
		if (name != null && whitespaces == Whitespaces.Pass) {
			pass_whitespaces();
		}
		return name;
	}


	public boolean pass(String expected) {
		return pass(expected, default_whitespaces);
	}


	public String pass(Test_char test, Whitespaces whitespaces) {
		int start_pos = pos;
		while (pos < source.length() && test.pass(source.charAt(pos))) {
			++pos;
		}
		if (pos == start_pos) {
			return null;
		}
		int end_pos = pos;
		if (whitespaces == Whitespaces.Pass) {
			pass_whitespaces();
		}
		return source.substring(start_pos, end_pos);
	}


	public boolean pass(String expected, Whitespaces whitespaces) {
		int expected_length = expected.length();
		if (pos + expected_length > source.length()) {
			return false;
		}
		if (expected.equals(source.substring(pos, pos + expected_length))) {
			pos += expected_length;
			if (whitespaces == Whitespaces.Pass) {
				pass_whitespaces();
			}
			return true;
		}
		return false;
	}


	public void expect(String expected) throws Unified_error {
		expect(expected, default_whitespaces);
	}


	public void expect(String expected, Whitespaces whitespaces) throws Unified_error {
		if (!pass(expected, whitespaces)) {
			throw new Unified_error("Expected \"" + expected + "\"" + context());
		}
	}


	public String pass_until(String terminator) {
		int terminator_pos = source.indexOf(terminator, pos);
		if (terminator_pos < 0) {
			return null;
		}
		String passed = source.substring(pos, terminator_pos);
		pos = terminator_pos;
		return passed;
	}


	public String pass_until_eof_or(Test_char terminator, Whitespaces whitespaces) {
		int start_pos = pos;
		while (pos < source.length() && !terminator.pass(source.charAt(pos))) {
			++pos;
		}
		int end_pos = pos;
		if (pos > start_pos && whitespaces == Whitespaces.Pass) {
			pass_whitespaces();
		}
		return end_pos > start_pos ? source.substring(start_pos, end_pos) : null;
	}


	public String expect_qualified_name() throws Unified_error {
		return expect_qualified_name(default_whitespaces);
	}


	public String expect_qualified_name(Whitespaces whitespaces) throws Unified_error {
		String name = pass_qualified_name(whitespaces);
		if (name != null) {
			return name;
		}
		throw new Unified_error("Expected qualified name " + context());
	}


	public String expect_name() throws Unified_error {
		return expect_name(default_whitespaces);
	}


	public String expect_name(Whitespaces whitespaces) throws Unified_error {
		String name = pass_name(whitespaces);
		if (name != null) {
			return name;
		}
		throw new Unified_error("Expected name " + context());
	}


	public String pass_until_end() {
		int save_pos = pos;
		pos = source.length();
		return save_pos < source.length() ? source.substring(save_pos) : null;
	}
}
