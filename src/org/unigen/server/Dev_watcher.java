package org.unigen.server;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class Dev_watcher {
	private final Dev_server server;
	private Thread thread;





	Dev_watcher(Dev_server server) {
		this.server = server;
	}





	void start() {
		thread = new Thread(() -> {
			try {
				Set<Path> folder_paths = new HashSet<>();
				for (Dev_server_configuration.Source source : server.configuration.sources) {
					folder_paths.add(source.path.getParent());
				}
				if (System.getProperty("os.name").toLowerCase().contains("mac")) {
					Mac_os_watcher.watch(folder_paths, this::onModified);
				} else {
					legacyWatch(folder_paths);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}





	void stop() {
		thread.interrupt();
	}





	private void onModified(Set<Path> file_paths) throws IOException {
		StringBuilder message = new StringBuilder();
		for (Path path : file_paths) {
			Dev_server_configuration.Source source = server.configuration.find_source_with_path(path);
			if (source != null) {
				message.append("modified:").append(source.is_module ? "module" : "repository").append("|").append(source.name).append('|').append(source.path.toAbsolutePath()).append('\n');
			}
		}
		for (Dev_events_websocket connection : server.activity.connections) {
			connection.getRemote().sendString(message.toString());
		}
	}





	private void legacyWatch(Set<Path> folder_paths) throws IOException, InterruptedException {
		//noinspection InfiniteLoopStatement
		while (true) {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			for (Path folder_path : folder_paths) {
				folder_path.register(watcher, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
			}
			WatchKey watch_key;
			do {
				watch_key = watcher.take();
				Set<Path> file_paths = new HashSet<>();
				for (WatchEvent<?> event : watch_key.pollEvents()) {
					@SuppressWarnings({"unchecked"}) WatchEvent<Path> path_event = (WatchEvent<Path>) event;
					if (path_event.kind() == ENTRY_MODIFY) {
						Path folder_path = (Path) watch_key.watchable();
						file_paths.add(folder_path.resolve(path_event.context()));
					}
				}
				onModified(file_paths);
			} while (watch_key.reset());
		}
	}
}
