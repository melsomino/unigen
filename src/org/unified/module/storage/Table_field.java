package org.unified.module.storage;

import org.unified.declaration.Declaration_attribute;
import org.unified.declaration.Declaration_element;
import org.unified.declaration.Declaration_error;

public class Table_field {
	public final String name;
	public final Field_type type;
	public final boolean not_null;
	public final boolean included_in_primary_key;
	public final String alias;





	public Table_field(String name, Field_type type, boolean not_null, boolean included_in_primary_key, String alias) {
		this.name = name;
		this.type = type;
		this.not_null = not_null;
		this.included_in_primary_key = included_in_primary_key;
		this.alias = alias;
	}

	public static Table_field from(Declaration_element element) throws Declaration_error {
		Field_type type = Field_type.String;
		boolean not_null = false;
		boolean included_in_primary_key = false;
		String alias = null;

		for (Declaration_attribute attribute : element.attributes) {
			String lowercase_name = attribute.name.toLowerCase();
			switch (lowercase_name) {
				case "primary-key":
				case "+":
					not_null = true;
					included_in_primary_key = true;
					break;
				case "not-null":
				case "!":
					not_null = true;
					break;
				case "as":
					alias = attribute.getString();
				default:
					Field_type test_type = Field_type.try_parse(lowercase_name);
					if (test_type != null) {
						type = test_type;
					}
			}
		}
		return new Table_field(element.name(), type, not_null, included_in_primary_key, alias);
	}

}
