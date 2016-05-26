package org.unigen.model.storage;

public class Table_index
{
	final String name;
	final Table_field[] fields;
	final boolean primary;
	final boolean clustered;





	public Table_index(String name, Table_field[] fields, boolean primary, boolean clustered)
	{
		this.name = name;
		this.fields = fields;
		this.primary = primary;
		this.clustered = clustered;
	}
}
