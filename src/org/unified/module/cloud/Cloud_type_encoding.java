package org.unified.module.cloud;

import org.unified.Unified_error;

public enum Cloud_type_encoding {
	JsonValue,
	JsonString;





	public static Cloud_type_encoding parse(String string) throws Unified_error {
		if (string == null || string.isEmpty()) {
			return JsonValue;
		}
		String lowered = string.trim().toLowerCase();
		if (lowered.equals("jsonstring")) {
			return JsonString;
		}
		throw new Unified_error("Invalid encoding: " + string);
	}
}
