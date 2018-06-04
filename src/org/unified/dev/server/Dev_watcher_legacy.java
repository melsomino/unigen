package org.unified.dev.server;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class Dev_watcher_legacy {
	interface Notify {
		void on_modified(Set<Path> file_paths) throws Exception;
	}





	static void watch(Set<Path> folder_paths, Notify notify) throws Exception {
		//noinspection InfiniteLoopStatement
		while (true) {
			var watcher = FileSystems.getDefault().newWatchService();
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
				notify.on_modified(file_paths);
			} while (watch_key.reset());
		}
	}
}
