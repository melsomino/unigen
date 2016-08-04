package org.unified.dev.generator;

import org.unified.module.Module;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class Generator {
	public final Module module;
	public final String source;
	public final Date time;





	public Generator(Module module, Date time) {
		this.module = module;
		this.source = module.source_path.toString();
		this.time = time;
	}





	public abstract void generate() throws Exception;





	public static String uppercase_first_letter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}





	public static String lowercase_first_letter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toLowerCase() + original.substring(1);
	}



	public static String normalize_identifier(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		int non_lowercase_index = 0;
		while (non_lowercase_index < original.length() && Character.isAlphabetic(original.charAt(non_lowercase_index)) && Character.isUpperCase(original.charAt(non_lowercase_index))) {
			++non_lowercase_index;
		}
		if (non_lowercase_index > 0) {
			if (non_lowercase_index < original.length()) {
				original = original.substring(0, non_lowercase_index).toLowerCase() + original.substring(non_lowercase_index);
			}
			else {
				original = original.toLowerCase();
			}
		}
		StringBuilder normalized = new StringBuilder(original.length());
		for (int i = 0; i < original.length(); ++i) {
			char c = original.charAt(i);
			if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
				normalized.append(c);
			}
			else {
				normalized.append('_');
			}
		}
		return normalized.toString();
	}





	private static void deleteFolderContent(Path folder) throws IOException {
		File[] files = folder.toFile().listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolderContent(f.toPath());
				}
				Files.deleteIfExists(f.toPath());
			}
		}
	}





	private List<Path> placement_stack = new ArrayList<>();
	public Path current_placement;
	private PrintStream current_file;





	protected void gen_line(String line) {
		current_file.println(line);
	}





	protected void gen_line() {
		current_file.println();
	}





	protected String quoted(String name) {
		return "\"" + name + "\"";
	}





	protected void new_file(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		close_file();
		current_file = new PrintStream(current_placement.resolve(fileName).toFile(), "utf8");
	}





	protected void close_file() {
		if (current_file != null) {
			current_file.close();
			current_file = null;
		}
	}





	protected void new_location(String... relativePaths) throws IOException {
		Path relativePath = null;
		for (String path : relativePaths) {
			if (relativePath == null) {
				relativePath = Paths.get(path);
			} else {
				relativePath = relativePath.resolve(path);
			}
		}
		relativePath = relativePath.normalize();

		if (current_placement != null) {
			placement_stack.add(current_placement);
			current_placement = current_placement.resolve(relativePath);
		} else {
			current_placement = module.source_path.resolve(relativePath);
		}
		Files.createDirectories(current_placement);
//TODO:		Generator.deleteFolderContent(current_placement);
	}





	protected void close_location() {
		current_placement = !placement_stack.isEmpty() ? placement_stack.remove(placement_stack.size() - 1) : null;
	}





	protected void gen(String format, Object... args) {
		current_file.print(format(format, args));
	}





	public static String format(String format, Object... args) {
		return internal_format(format, args);
	}





	private static String internal_format(String format, Object args[]) {
		Map<String, String> values = new HashMap<>();
		for (int i = 0; i < args.length - 1; i += 2) {
			String name = args[i].toString();
			values.put(name, args[i + 1] != null ? args[i + 1].toString() : "");
		}

		int pos = 0;

		StringBuilder result = new StringBuilder();

		while (pos < format.length()) {
			int openBracePos = format.indexOf('{', pos);
			if (openBracePos < 0) {
				break;
			}
			int closeBracePos = format.indexOf('}', openBracePos + 1);
			if (closeBracePos < 0) {
				break;
			}
			if (openBracePos > pos) {
				result.append(format.substring(pos, openBracePos));
			}
			String value = values.get(format.substring(openBracePos + 1, closeBracePos));
			if (value != null) {
				result.append(value);
				pos = closeBracePos + 1;
			} else {
				result.append('{');
				pos = openBracePos + 1;
			}
		}
		if (pos < format.length()) {
			result.append(format.substring(pos));
		}
		return result.toString();
	}





	protected void gen_file_header(String header_format) {
		gen(header_format, "source", source, "time", time);
	}
}
