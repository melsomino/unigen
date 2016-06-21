package org.unigen.server;

import org.unified.declaration.DeclarationElement;
import org.unified.declaration.DeclarationError;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Dev_server_configuration {
	public Path configuration_file_path;
	public DeclarationElement[] declarations;
	public Source[] sources;





	void load(Path configuration_file_path) throws DeclarationError {
		this.configuration_file_path = configuration_file_path;
		declarations = DeclarationElement.load(configuration_file_path);
		Path base_path = configuration_file_path.getParent();

		List<Source> repositories = new ArrayList<>();
		for (DeclarationElement element : declarations) {
			if (element.name_is("source")) {
				repositories.add(new Source(base_path, element));
			}
		}
		this.sources = repositories.toArray(new Source[repositories.size()]);
	}





	Source find_source_with_path(Path path) {
		for (Source source : sources) {
			if (source.path.equals(path)) {
				return source;
			}
		}
		return null;
	}





	public class Source {

		public final DeclarationElement declaration;
		public final Path path;
		public final boolean is_module;
		public final String name;





		public Source(Path base_path, DeclarationElement declaration) throws DeclarationError {
			this.declaration = declaration;
			path = base_path.resolve(declaration.attributes[1].name).normalize();

			DeclarationElement[] declarations = DeclarationElement.load(path);
			DeclarationElement module_element = DeclarationElement.find_first_element_with_name("module", declarations);
			is_module = module_element != null;
			String source_name = null;
			if (is_module) {
				source_name = module_element.attributes[1].name;
			}
			else {
				DeclarationElement repository_element = DeclarationElement.find_first_element_with_name("repository", declarations);
				if (repository_element != null) {
					source_name = repository_element.attributes[1].name;
				}
			}
			name = source_name != null ? source_name : path.getFileName().toString();
		}
	}

}
