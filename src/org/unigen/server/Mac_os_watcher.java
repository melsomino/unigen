package org.unigen.server;

import com.barbarysoftware.watchservice.WatchEvent;
import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_MODIFY;

class Mac_os_watcher {
	interface Notify {
		void on_modified(Set<Path> file_paths) throws Exception;
	}

	static void watch(Set<Path> folder_paths, Notify notify) throws Exception {
		//noinspection InfiniteLoopStatement
		while (true) {
			WatchService watcher = WatchService.newWatchService();
			for (Path folder_path : folder_paths) {
				WatchableFile watchable = new WatchableFile(folder_path.toFile());
				watchable.register(watcher, ENTRY_MODIFY);
			}
			WatchKey watch_key;
			do {
				watch_key = watcher.take();
				Set<Path> file_paths = new HashSet<>();
				for (WatchEvent<?> event : watch_key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == ENTRY_MODIFY) {
						@SuppressWarnings({"unchecked"}) WatchEvent<File> file_event = (WatchEvent<File>) event;
						file_paths.add(file_event.context().toPath());
					}
				}
				notify.on_modified(file_paths);
			} while (watch_key.reset());
		}

	}


}
