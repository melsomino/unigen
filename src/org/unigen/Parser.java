package org.unigen;

public class Parser {

	public enum Whitespaces {
		Pass, Not_pass
	}

	public final String source;
	private final String context_name;
	private int pos;
	private Whitespaces default_whitespaces;
	private StringBuilder accumulator = new StringBuilder();


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
		while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
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


	public void expect(String expected) throws Unigen_exception {
		expect(expected, default_whitespaces);
	}


	public void expect(String expected, Whitespaces whitespaces) throws Unigen_exception {
		if (!pass(expected, whitespaces)) {
			throw new Unigen_exception("Expected \"" + expected + "\"" + context());
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


	public String expect_qualified_name() throws Unigen_exception {
		return expect_qualified_name(default_whitespaces);
	}


	public String expect_qualified_name(Whitespaces whitespaces) throws Unigen_exception {
		String name = pass_qualified_name(whitespaces);
		if (name != null) {
			return name;
		}
		throw new Unigen_exception("Expected qualified name " + context());
	}


	public String expect_name() throws Unigen_exception {
		return expect_name(default_whitespaces);
	}


	public String expect_name(Whitespaces whitespaces) throws Unigen_exception {
		String name = pass_name(whitespaces);
		if (name != null) {
			return name;
		}
		throw new Unigen_exception("Expected name " + context());
	}


	public String pass_until_end() {
		int save_pos = pos;
		pos = source.length();
		return save_pos < source.length() ? source.substring(save_pos) : null;
	}
}
