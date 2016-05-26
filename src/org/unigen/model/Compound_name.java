package org.unigen.model;

import org.unigen.generator.Generator;

public class Compound_name {
	public final String name;
	public final String identifier;

	Compound_name(String name, String identifier) {
		this.name = name;
		this.identifier = identifier;
	}


	public enum IdentifierStyle {
		AsIs,
		Pascal,
		Camel
	}

	public static Compound_name parse(String string, IdentifierStyle identifierStyle) {
		int separator_pos = string.indexOf('~');
		if (separator_pos < 0) {
			String name = string.trim();
			String identifier = name;
			switch(identifierStyle) {
				case Camel:
					identifier = Generator.lowercase_first_letter(name);
					break;
				case Pascal:
					identifier = Generator.uppercase_first_letter(name);
					break;
			}
			return new Compound_name(name, identifier);
		}
		return new Compound_name(string.substring(0, separator_pos).trim(), string.substring(separator_pos + 1).trim());
	}
}
