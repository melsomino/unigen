package org.unified.module.cloud;

import org.unified.Unified_error;

public enum Cloud_type_modifier {
	Missing,
	Array,
	Object,
	Record,
	Recordset,
	Parameters;

	public static Cloud_type_modifier parse(String string) throws Unified_error {
		if (string == null || string.isEmpty()) {
			return Missing;
		}
		String lowered = string.trim().toLowerCase();
		if (lowered.equals("array") || lowered.equals("[]")) {
			return Array;
		}
		if (lowered.equals("object") || lowered.equals("{}")) {
			return Object;
		}
		if (lowered.equals("record") || lowered.equals("?")) {
			return Record;
		}
		if (lowered.equals("recordset") || lowered.equals("*")) {
			return Recordset;
		}
		if (lowered.equals("parameters") || lowered.equals(":")) {
			return Parameters;
		}
		throw new Unified_error("Invalid modifier: " + string);
	}

	public boolean is_array() {
		return this == Array || this == Recordset;
	}
}
