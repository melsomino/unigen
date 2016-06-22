package org.unified.module.cloud;

public class Cloud_struct_field {
	public final String name;
	public final String identifier;
	public final Cloud_type_declaration declaration;

	public Cloud_struct_field(String name, String identifier, Cloud_type_declaration declaration) {
		this.name = name;
		this.identifier = identifier;
		this.declaration = declaration;
	}

}
