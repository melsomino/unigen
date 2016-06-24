package org.unified.declaration;

public class Declaration_error extends Exception {
	public Declaration_error(Declaration_element element, String message, Throwable cause) {
		super(format_attributes(element) + "\n^^^\n" + message, cause);
	}

	static String format_attributes(Declaration_element element) {
		if (element == null) {
			return "";
		}
		StringBuilder string = new StringBuilder();
		for(Declaration_attribute attribute : element.attributes) {
			if (string.length() > 0) {
				string.append(' ');
			}
			string.append(attribute.name);
			if (attribute.value instanceof String[]) {
				string.append("=(");
				boolean first = true;
				for (String value : (String[]) attribute.value) {
					if (!first) {
						string.append(' ');
					}
					string.append(value);
					first = false;
				}
				string.append(')');
			}
			else if (attribute.value instanceof String) {
				string.append('=').append(attribute.value);
			}
		}
		return string.toString();
	}
}
