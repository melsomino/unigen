package org.unified.dev.server;

import org.unified.declaration.Declaration_error;
import org.unified.dev.Dev_configuration;
import org.unified.dev.generator.Module_generator;
import org.unified.module.Module;
import org.unified.module.Module_loader;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class Dev_server {

	public Dev_configuration configuration = new Dev_configuration();
	public Dev_server_activity activity = new Dev_server_activity(this);





	public void configure(Path configuration_file_path) throws Declaration_error {
		org.apache.log4j.BasicConfigurator.configure();

		configuration.load(configuration_file_path);
		http.configure(configuration);
		watcher.configure(configuration);
	}



	public Dev_server_websocket_connection[] active_connections() {
		return activity.connections.toArray(new Dev_server_websocket_connection[activity.connections.size()]);
	}


	void on_configuration_file_changed() throws Declaration_error {
		configure(configuration.configuration_file_path);
	}





	void on_source_file_changed(Dev_configuration.Source source) throws Exception {
		if (source.is_module) {
			Module module = Module_loader.load(source.path);
			Module_generator.generate(module, source.ios_out, source.android_out);
		}
	}





	public void run() {
		try {
			http.start();
			watcher.start();
			terminated.await();
			http.stop();
			watcher.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}





	public void terminate() {
		terminated.countDown();
	}

	// Internals

	private Dev_server_http_server http = new Dev_server_http_server(this);
	private Dev_server_watcher watcher = new Dev_server_watcher(this);
	private CountDownLatch terminated = new CountDownLatch(1);





	public void start() throws Exception {
		http.start();
		watcher.start();
	}





	public void stop() throws Exception {
		watcher.stop();
		http.stop();
	}


}
