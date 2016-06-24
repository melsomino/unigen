package org.unified.declaration;


import java.util.Map;

public class Declaration_attribute {
	public final String name;
	public Object value;

	public Declaration_attribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public <Value> Value getEnum(Map<String, Value> valuesByLowercaseName) throws Declaration_error {
		if (value instanceof String) {
			String string = (String) value;
			Value enumValue = valuesByLowercaseName.get(string);
			if (enumValue != null) {
				return enumValue;
			}
		}
		throw new Declaration_error(null, "Expected one of: " + valuesByLowercaseName.values(), null);
	}

	public String getString() throws Declaration_error {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		throw new Declaration_error(null, "Expected string value", null);
	}

	private static float parseFloat(String string) throws Declaration_error {
		return Float.parseFloat(string);
	}

	public float getFloat() throws Declaration_error {
		Exception parseError = null;
		try {
			if (value instanceof String) {
				return Float.parseFloat((String) value);
			}
		} catch (Exception error) {
			parseError = error;
		}
		throw new Declaration_error(null, "Expected float value", parseError);
	}

	public boolean getBool() throws Declaration_error {
		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			String string = (String) value;
			if ("true".equalsIgnoreCase(string)) {
				return true;
			}
			if ("false".equalsIgnoreCase(string)) {
				return false;
			}
		}
		throw new Declaration_error(null, "Boolean value expected (\"true\" or \"false\")", null);
	}


}
