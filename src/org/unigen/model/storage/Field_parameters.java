package org.unigen.model.storage;

public class Field_parameters
{
	public final Field_type type;
	public final boolean included_in_primary_key;





	Field_parameters(Field_type type, boolean is_primary_key)
	{
		this.type = type;
		this.included_in_primary_key = is_primary_key;
	}





	public static Field_parameters parse(String string)
	{
		boolean is_primary_key = string.endsWith("!");
		if (is_primary_key) {
			string = string.substring(0, string.length() - 1);
		}
		return new Field_parameters(Field_type.parse(string), is_primary_key);
	}


}
