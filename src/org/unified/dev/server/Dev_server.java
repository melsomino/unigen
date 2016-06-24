package org.unified.dev.server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.unified.declaration.Declaration_error;
import org.unified.dev.Dev_configuration;
import org.unified.dev.generator.Module_generator;
import org.unified.module.Module;
import org.unified.module.Module_loader;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Dev_server {

	public Dev_configuration configuration = new Dev_configuration();
	public Dev_activity activity = new Dev_activity(this);
	public Path dev_web_path = null;





	public void configure(Path configuration_file_path) throws Declaration_error {
		org.apache.log4j.BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.INFO);

		configuration.load(configuration_file_path);
		http.configure(configuration);
		watcher.configure(configuration);
	}





	public Dev_websocket_connection[] active_connections() {
		return activity.connections.toArray(new Dev_websocket_connection[activity.connections.size()]);
	}





	void on_configuration_file_changed() throws Declaration_error {
		configure(configuration.configuration_file_path);
	}





	void on_source_file_changed(Dev_configuration.Source source) throws Exception {
		source.status_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		if (source.is_module) {
			try {
				Module module = Module_loader.load(source.path);
				Module_generator.generate(module, source.ios_out, source.android_out);
				source.status = "generated";
				source.status_class = "ok";
				source.status_details = null;
			}
			catch(Exception error) {
				source.status = "generation failed";
				source.status_class = "failed";
				source.status_details = error.getMessage();
			}
			activity.broadcast("module-changed", source.name, source.path);
		}
		else {
			source.status = "modified";
			source.status_class = "info";
			activity.broadcast("repository-changed", source.name, source.path);
		}
		activity.broadcast("state-changed");
	}





	public void run() {
		try {
			http.start();
			watcher.start();
			terminated.await();
			http.stop();
			watcher.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	public void terminate() {
		terminated.countDown();
	}

	// Internals

	private Dev_servlet_server http = new Dev_servlet_server(this);
	private Dev_watcher watcher = new Dev_watcher(this);
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
