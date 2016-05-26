package org.unigen.model.cloud;

import org.unigen.Unigen_exception;

public enum Cloud_type_encoding {
	JsonValue,
	JsonString,;

	public static Cloud_type_encoding parse(String string) throws Unigen_exception {
		if (string == null || string.isEmpty()) {
			return JsonValue;
		}
		String lowered = string.trim().toLowerCase();
		if (lowered.equals("jsonstring")) {
			return JsonString;
		}
		throw new Unigen_exception("Invalid encoding: " + string);
	}
}
