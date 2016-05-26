package org.unigen.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Loader {

	@FunctionalInterface
	public interface Entry_consumer {
		void apply(String name, Object value);
	}

	@FunctionalInterface
	public interface Array_factory<T> {
		T[] apply(int size);
	}

	@FunctionalInterface
	public interface Default_item_factory<T> {
		T apply(String name, Object source) throws Exception;
	}


	static void enum_default_items(JSONObject parent, Entry_consumer action) {
		parent.forEach((key, value) -> {
			String name = (String) key;
			if (!name.startsWith(".")) {
				action.apply(name, value);
			}
		});
	}


	protected static <T> T[] create_default_items(JSONObject parent, Array_factory<T> array_factory, Default_item_factory<T> item_factory) throws Exception {
		List<T> items = new ArrayList<>();
		if (parent != null) {
			for (Object entry_object : parent.entrySet()) {
				Map.Entry entry = (Map.Entry) entry_object;
				String name = (String) entry.getKey();
				if (!name.startsWith(".")) {
					items.add(item_factory.apply(name, entry.getValue()));
				}

			}
		}
		return items.toArray(array_factory.apply(items.size()));
	}


	private static JSONObject as_object(Object value) {

		return (JSONObject) value;
	}


	protected static JSONObject get_object(String name, JSONObject def) {
		if (def == null) {
			return null;
		}
		Object value = def.get(name);
		return value != null ? (JSONObject) value : null;
	}


	static JSONObject read_json_object_from_utf8_file(Path file_path) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return as_object(parser.parse(Files.newBufferedReader(file_path, Charset.forName("utf8"))));
	}


	protected static String get_string(String name, JSONObject def) {
		return get_string(name, null, def);
	}


	protected static String get_string(String name, String default_value, JSONObject def) {
		if (def == null) {
			return default_value;
		}
		Object value = def.get(name);
		return value != null ? value.toString() : default_value;
	}


	protected static int get_integer(String name, int default_value, JSONObject def) {
		if (def == null) {
			return default_value;
		}
		Object value = def.get(name);
		return value != null ? Integer.parseInt(value.toString()) : default_value;
	}


}
