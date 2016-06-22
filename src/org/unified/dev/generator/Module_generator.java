package org.unified.dev.generator;

import org.unified.dev.generator.android.Android_cloud_generator;
import org.unified.dev.generator.ios.cloud.Ios_cloud_generator;
import org.unified.dev.generator.android.Android_storage_generator;
import org.unified.dev.generator.ios.storage.Ios_storage_generator;
import org.unified.module.Module;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class Module_generator {


	private static void add(List<Generator> generators, Path path, Object component, Callable<Generator> generator) throws Exception {
		if (path != null && component != null) {
			generators.add(generator.call());
		}
	}

	public static void generate(Module module, Path ios_out_path, Path android_out_path) throws Exception {
		ArrayList<Generator> generators = new ArrayList<>();

		Date time = new Date();

		add(generators, ios_out_path, module.storage, () -> new Ios_storage_generator(module, time, ios_out_path));
		add(generators, ios_out_path, module.cloud_api, () -> new Ios_cloud_generator(module, time, ios_out_path));
		add(generators, android_out_path, module.storage, () -> new Android_storage_generator(module, time, android_out_path));
		add(generators, android_out_path, module.cloud_api, () -> new Android_cloud_generator(module, time, android_out_path));

		for (Generator generator : generators) {
			generator.generate();
		}
	}
}
