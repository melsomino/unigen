package org.unigen.model.storage;

public class Storage
{
	public final String module;
	public final int version;
	public final Record_type[] record_types;
	public final Storage_table[] tables;
	public final Storage_query[] queries;


	public Storage(String name, int version, Record_type[] record_types, Storage_table[] tables, Storage_query[] queries)
	{
		this.module = name;
		this.version = version;
		this.record_types = record_types;
		this.tables = tables;
		this.queries = queries;
	}
}
