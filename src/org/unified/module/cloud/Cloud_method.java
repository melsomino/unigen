package org.unified.module.cloud;

public class Cloud_method {
	public final String url;
	public final int protocol;
	public final String method;
	public final String identifier;
	public final Cloud_type_declaration params;
	public final Cloud_type_declaration result;

	public Cloud_method(String url, int protocol, String method, String identifier, Cloud_type_declaration params, Cloud_type_declaration result) {
		this.url = url;
		this.protocol = protocol;
		this.method = method;
		this.identifier = identifier;
		this.params = params;
		this.result = result;
	}

}
