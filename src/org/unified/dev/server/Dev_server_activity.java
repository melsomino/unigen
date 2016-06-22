package org.unified.dev.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dev_server_activity {
	public List<Dev_server_websocket_connection> connections = new ArrayList<>();





	public Dev_server_activity(Dev_server server) {
		this.server = server;
	}





	public void broadcast(String message) throws IOException {
		for (Dev_server_websocket_connection connection : server.activity.connections) {
			connection.getRemote().sendString(message);
		}
	}

	// Internals

	private final Dev_server server;


}
