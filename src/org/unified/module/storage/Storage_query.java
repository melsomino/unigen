package org.unified.module.storage;

import org.unified.Unified_error;

public class Storage_query {
	public enum Returns {
		Iterator, Array, Record;

		public static Returns parse(String string) throws Unified_error {
			if (string == null || string.isEmpty()) {
				return Iterator;
			}
			String lowered = string.trim().toLowerCase();
			if (lowered.equals("array")) {
				return Array;
			}
			if (lowered.equals("record")) {
				return Record;
			}
			if (lowered.equals("iterator")) {
				return Iterator;
			}
			throw new Unified_error("Query \".returns\" has invalid value: " + string);
		}
	}

	public final Returns returns;
	public final String name;
	public final String generate_record_type;
	public final Query_param[] params;
	public final Storage_table table;
	public final Table_field[] fields;
	public final String where;
	public final String orderby;
	public final Record_type record_type;

	public Storage_query(Returns returns, String name, String generate_record_type, Query_param[] params, Storage_table table, Table_field[] fields, String where, String orderby, Record_type record_type) {
		this.returns = returns;
		this.name = name;
		this.generate_record_type = generate_record_type;
		this.params = params;
		this.table = table;
		this.fields = fields;
		this.where = where;
		this.orderby = orderby;
		this.record_type = record_type;
	}
}
