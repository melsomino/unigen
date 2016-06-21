package org.unigen.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.net.InetSocketAddress;

public class Dev_connection extends WebSocketAdapter {


	private final Dev_server server;

	public Dev_connection(Dev_server server) {
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
