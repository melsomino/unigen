package org.unified.dev.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dev_activity {
	public List<Dev_websocket_connection> connections = new ArrayList<>();





	public Dev_activity(Dev_server server) {
		this.server = server;
	}





	public void broadcast(String command, Object... args) {
		broadcast(Dev_websocket_connection.make_message(command, args));
	}





	public void broadcast(String message) {
		for (Dev_websocket_connection connection : server.activity.connections) {
			try {
				connection.send(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Internals

	private final Dev_server server;





	public void connection_opened(Dev_websocket_connection connection) {
		connections.add(connection);
		broadcast("state-changed");
	}





	public void connection_closed(Dev_websocket_connection connection) {
		connections.remove(connection);
		broadcast("state-changed");

	}





	public void connection_changed(Dev_websocket_connection connection) {
		broadcast("state-changed");
	}
}
