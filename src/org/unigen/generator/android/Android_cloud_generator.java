package org.unigen.generator.android;

import org.unigen.generator.Generator;
import org.unigen.model.Uni;

public class Android_cloud_generator extends Generator {

	public Android_cloud_generator(Uni uni) throws Exception {
		super(uni);
	}


	@Override
	public void generate() throws Exception {
		new_location(uni.out.android);
		close_location();
	}
}
