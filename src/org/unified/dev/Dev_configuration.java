package org.unified.dev;

import org.unified.declaration.Declaration_element;
import org.unified.declaration.Declaration_error;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Dev_configuration {
	public Path configuration_file_path;
	public Declaration_element[] declarations;
	public Source[] sources;





	public void load(Path configuration_file_path) throws Declaration_error {
		this.configuration_file_path = configuration_file_path;
		declarations = Declaration_element.load_from(configuration_file_path);
		Path base_path = configuration_file_path.getParent();

		List<Source> repositories = new ArrayList<>();
		for (Declaration_element element : declarations) {
			if (element.name_is("source")) {
				repositories.add(new Source(base_path, element));
			}
		}
		this.sources = repositories.toArray(new Source[repositories.size()]);
	}





	public Source find_source_with_path(Path path) {
		for (Source source : sources) {
			if (source.path.equals(path)) {
				return source;
			}
		}
		return null;
	}





	public Source find_repository(String name) {
		for (Source source : sources) {
			if (!source.is_module && source.name.equalsIgnoreCase(name)) {
				return source;
			}
		}
		return null;
	}





	public class Source {

		public final Declaration_element declaration;
		public final Path path;
		public final boolean is_module;
		public final String name;
		public final Path ios_out;
		public final Path android_out;
		public String status;
		public String status_class;
		public String status_details;
		public String status_time;





		public Source(Path base_path, Declaration_element source_element) throws Declaration_error {
			this.declaration = source_element;
			path = base_path.resolve(source_element.value()).normalize();

			Declaration_element[] declarations = Declaration_element.load_from(path);
			Declaration_element module_element = Declaration_element.first_element_with_name("module", declarations);
			is_module = module_element != null;
			String source_name = null;
			if (is_module) {
				source_name = module_element.value();
				ios_out = platform_out("ios", source_element);
				android_out = platform_out("android", source_element);
			}
			else {
				Declaration_element repository_element = Declaration_element.first_element_with_name("repository", declarations);
				if (repository_element != null) {
					source_name = repository_element.value();
				}
				ios_out = null;
				android_out = null;
			}
			name = source_name != null ? source_name : path.getFileName().toString();
		}

		private Path platform_out(String platform, Declaration_element source_element) {
			Declaration_element platform_element = source_element.first_child_with_name(platform);
			return platform_element != null ? path.getParent().resolve(platform_element.value()).normalize() : null;
		}
	}

}
