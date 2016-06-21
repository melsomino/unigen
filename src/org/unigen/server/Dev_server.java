package org.unigen.server;

import org.unified.declaration.DeclarationError;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class Dev_server {

	public Dev_server_configuration configuration = new Dev_server_configuration();





	public void configure(Path configuration_file_path) throws DeclarationError {
		org.apache.log4j.BasicConfigurator.configure();

		configuration.load(configuration_file_path);
		http.configure(configuration);
		watcher.configure(configuration);
	}



	public Dev_connection[] active_connections() {
		return activity.connections.toArray(new Dev_connection[activity.connections.size()]);
	}


	void on_configuration_file_changed() throws DeclarationError {
		configure(configuration.configuration_file_path);
	}





	void on_source_file_changed(Dev_server_configuration.Source source) {

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

	private Dev_http http = new Dev_http(this);
	private Dev_watcher watcher = new Dev_watcher(this);
	Dev_server_activity activity = new Dev_server_activity(this);
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
