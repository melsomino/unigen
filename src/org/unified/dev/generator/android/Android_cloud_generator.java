package org.unified.dev.generator.android;

import org.unified.dev.generator.Generator;
import org.unified.module.Module;

import java.nio.file.Path;
import java.util.Date;

public class Android_cloud_generator extends Generator {

	private final String out_path;





	public Android_cloud_generator(Module module, Date time, Path out_path) throws Exception {
		super(module, time);
		this.out_path = out_path.toString();
	}


	@Override
	public void generate() throws Exception {
		new_location(out_path);
		close_location();
	}
}
