package org.unified.module;

import org.unified.module.cloud.Cloud_api;
import org.unified.module.storage.Storage;

import java.nio.file.Path;

public class Module {
	public final String name;
	public final Path source_path;

	public final Storage storage;
	public final Cloud_api cloud_api;


	public Module(String name, Path source_path, Storage storage, Cloud_api cloud) {
		this.name = name;
		this.source_path = source_path;

		this.storage = storage;
		this.cloud_api = cloud;
	}


}
