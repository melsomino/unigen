package org.unigen.generator;

import org.unigen.generator.android.Android_cloud_generator;
import org.unigen.generator.ios.cloud.Ios_cloud_generator;
import org.unigen.generator.android.Android_storage_generator;
import org.unigen.generator.ios.storage.Ios_storage_generator;
import org.unigen.model.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Uni_generator {


	private static void add(List<Generator> generators, String path, Object component, Callable<Generator> generator) throws Exception {
		if (path != null && component != null) {
			generators.add(generator.call());
		}
	}

	public static void generate(Uni uni) throws Exception {
		ArrayList<Generator> generators = new ArrayList<>();

		add(generators, uni.out.ios, uni.storage, () -> new Ios_storage_generator(uni));
		add(generators, uni.out.ios, uni.cloud, () -> new Ios_cloud_generator(uni));
		add(generators, uni.out.android, uni.storage, () -> new Android_storage_generator(uni));
		add(generators, uni.out.android, uni.cloud, () -> new Android_cloud_generator(uni));

		for (Generator generator : generators) {
			generator.generate();
		}
	}
}
