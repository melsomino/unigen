package org.unigen.model;

import org.unigen.model.cloud.Cloud;
import org.unigen.model.storage.Storage;

import java.nio.file.Path;
import java.util.Date;

public class Uni {
	public final Path source_path;
	public final String generation_source;
	public final Date generation_time;

	public final Out out;
	public final Storage storage;
	public final Cloud cloud;


	public Uni(Path generation_source, Date generation_time, Out out, Storage storage, Cloud cloud) {
		source_path = generation_source.getParent();
		this.generation_source = generation_source.toString();
		this.generation_time = generation_time;

		this.out = out;
		this.storage = storage;
		this.cloud = cloud;
	}


}
