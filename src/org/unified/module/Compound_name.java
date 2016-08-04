package org.unified.module;

import org.unified.declaration.Declaration_error;
import org.unified.declaration.Declaration_element;
import org.unified.dev.generator.Generator;

public class Compound_name {
	public final String name;
	public final String identifier;





	public Compound_name(Declaration_element declaration, Identifier_style identifierStyle) throws Declaration_error {
		name = declaration.name();
		String specified_identifier = declaration.get_string_attribute("as", null);
		if (specified_identifier != null) {
			identifier = specified_identifier;
		} else {
			identifier = identifierStyle == Identifier_style.Pascal ? Generator.uppercase_first_letter(Generator.normalize_identifier(name)) : Generator.normalize_identifier(name);
		}
	}





	public enum Identifier_style {
		As_is,
		Pascal,
		Camel
	}

}
