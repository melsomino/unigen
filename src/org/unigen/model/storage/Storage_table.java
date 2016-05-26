package org.unigen.model.storage;


public class Storage_table
{
	public final String name;
	public final Table_field[] fields;
	public final Table_index indexes[];
	public final Record_type record_type;





	public Storage_table(String name, Table_field[] fields, Table_index[] indexes, Record_type record_type)
	{
		this.name = name;
		this.fields = fields;
		this.indexes = indexes;
		this.record_type = record_type;
	}

	public Table_field find_field(String name) {
		for(Table_field field : fields) {
			if (name.equalsIgnoreCase(field.name)) {
				return field;
			}
		}
		return null;
	}

	public boolean is_key_field(Table_field field) {
		for(Table_index index : indexes) {
			if (index.primary) {
				for(Table_field test : index.fields) {
					if (test == field) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
}
