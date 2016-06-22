package org.unified.dev.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.unified.dev.server.Dev_server;

import java.net.InetSocketAddress;

public class Dev_server_websocket_connection extends WebSocketAdapter {


	private final Dev_server server;





	public Dev_server_websocket_connection(Dev_server server) {
		this.server = server;
	}





	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		server.activity.connections.add(this);
	}





	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
	}





	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		server.activity.connections.remove(this);
		super.onWebSocketClose(statusCode, reason);
	}





	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
	}





	@Override
	public String toString() {
		InetSocketAddress address = getSession().getRemoteAddress();
		return address.getHostName();
	}
}
