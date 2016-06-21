package org.unified.declaration;


import java.util.Map;

public class DeclarationAttribute {
	public final String name;
	public Object value;

	public DeclarationAttribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public <Value> Value getEnum(Map<String, Value> valuesByLowercaseName) throws DeclarationError {
		if (value instanceof String) {
			String string = (String) value;
			Value enumValue = valuesByLowercaseName.get(string);
			if (enumValue != null) {
				return enumValue;
			}
		}
		throw new DeclarationError("Expected one of: " + valuesByLowercaseName.values(), null);
	}

	public String getString() throws DeclarationError {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		throw new DeclarationError("Expected string value", null);
	}

	private static float parseFloat(String string) throws DeclarationError {
		return Float.parseFloat(string);
	}

	public float getFloat() throws DeclarationError {
		Exception parseError = null;
		try {
			if (value instanceof String) {
				return Float.parseFloat((String) value);
			}
		} catch (Exception error) {
			parseError = error;
		}
		throw new DeclarationError("Expected float value", parseError);
	}

	public boolean getBool() throws DeclarationError {
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
		throw new DeclarationError("Boolean value expected (\"true\" or \"false\")", null);
	}


}
