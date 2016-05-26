package org.unigen.model.storage;

public class Table_field {
	public final String name;
	public final Field_type type;
	public final boolean is_key;

	public Table_field(String name, Field_type type, boolean is_key) {
		this.name = name;
		this.type = type;
		this.is_key = is_key;
	}
}
