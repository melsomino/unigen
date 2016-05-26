package org.unigen.model.storage;

public class Record_type {
	public final String name;
	public final Table_field[] fields;
	public final boolean is_table_record;

	public Record_type(String name, Table_field[] fields, boolean is_table_record) {
		this.name = name;
		this.fields = fields;
		this.is_table_record = is_table_record;
	}
}
