package org.unigen.model;

import org.json.simple.JSONObject;
import org.unigen.model.Loader;
import org.unigen.model.Uni;
import org.unigen.model.Out;
import org.unigen.model.cloud.Cloud;
import org.unigen.model.cloud.Cloud_loader;
import org.unigen.model.storage.Storage;
import org.unigen.model.storage.Storage_loader;

import java.nio.file.Path;
import java.util.Date;

public class Uni_loader extends Loader {

	public static Uni load(Path uniFilePath) throws Exception {
		JSONObject def = read_json_object_from_utf8_file(uniFilePath);
		assert def != null;
		return load(def, uniFilePath);
	}





	public static Uni load(JSONObject def, Path source) throws Exception {
		Out out = load_out(get_object("out", def));
		Storage storage = Storage_loader.load(get_object("storage", def));
		Cloud cloud = Cloud_loader.load(get_object("cloud", def));
		return new Uni(source, new Date(), out, storage, cloud);
	}





	private static Out load_out(JSONObject def) {
		if (def == null) {
			return null;
		}
		return new Out(get_string("ios", def), get_string("android", def));
	}


}
