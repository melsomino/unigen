package org.unigen.model.cloud;

public class Cloud {
	final String out_package;
	final String url;
	public final Cloud_api[] apis;

	public Cloud(String out_package, String url, Cloud_api[] apis) {
		this.out_package = out_package;
		this.url = url;
		this.apis = apis;
	}
}
