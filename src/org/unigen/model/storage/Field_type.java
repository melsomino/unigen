package org.unigen.model.storage;

public enum Field_type {
	Uuid,
	String,
	Date_time,
	Integer,
	Boolean;

	public String reader_getter() {
		switch (this) {
			case Uuid:
				return "getUuid";
			case String:
				return "getString";
			case Integer:
				return "getInteger";
			case Date_time:
				return "getDateTime";
			case Boolean:
				return "getBoolean";
		}
		assert false;
		return null;
	}

	public String param_setter() {
		switch (this) {
			case Uuid:
				return "setUuid";
			case String:
				return "setString";
			case Integer:
				return "setInteger";
			case Date_time:
				return "setDateTime";
			case Boolean:
				return "setBoolean";
		}
		assert false;
		return null;
	}

	public String sql_type() {
		switch (this) {
			case Uuid:
				return "TEXT";
			case String:
				return "TEXT";
			case Integer:
				return "INTEGER";
			case Date_time:
				return "TEXT";
			case Boolean:
				return "INTEGER";
		}
		assert false;
		return "TEXT";
	}

	public static Field_type parse(String string) {
		switch (string) {
			case "uuid":
				return Uuid;
			case "string":
				return String;
			case "dateTime":
				return Date_time;
			case "int":
				return Integer;
			case "bool":
				return Boolean;
			default:
				throw new Error("Unknown field type: " + string);
		}
	}
}

