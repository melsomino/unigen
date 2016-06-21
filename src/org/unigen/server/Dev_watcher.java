package org.unigen.server;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Dev_watcher {


	Dev_watcher(Dev_server server) {
		this.server = server;
	}





	void configure(Dev_server_configuration configuration) {
		folder_paths.clear();
		source_by_path.clear();
		watch_path_for_source(configuration.configuration_file_path, configuration);
		for (Dev_server_configuration.Source source : configuration.sources) {
			watch_path_for_source(source.path, source);
		}
	}





	void start() {
		thread = new Thread(() -> {
			try {
				if (System.getProperty("os.name").toLowerCase().contains("mac")) {
					Mac_os_watcher.watch(folder_paths, this::on_modified);
				}
				else {
					Legacy_watcher.watch(folder_paths, this::on_modified);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}





	void stop() {
		thread.interrupt();
		thread = null;
	}





	private void on_modified(Set<Path> modified) throws Exception {
		for (Path path : modified) {
			Object source = source_by_path.get(path);
			if (source == null) {
				continue;
			}
			if (source instanceof Dev_server_configuration) {
				server.on_configuration_file_changed();
			}
			else if (source instanceof Dev_server_configuration.Source) {
				server.on_source_file_changed((Dev_server_configuration.Source) source);
			}
		}
	}


	// Internals


	private final Dev_server server;
	private Thread thread;
	private Map<Path, Object> source_by_path = new HashMap<>();
	private Set<Path> folder_paths = new HashSet<>();





	private void watch_path_for_source(Path path, Object source) {
		folder_paths.add(path.getParent());
		source_by_path.put(path, source);
	}
}
