package org.unigen.model.storage;

public class Table_index
{
	public final String name;
	public final Table_field[] fields;
	public final boolean primary;





	public Table_index(String name, Table_field[] fields, boolean primary)
	{
		this.name = name;
		this.fields = fields;
		this.primary = primary;
	}
}
