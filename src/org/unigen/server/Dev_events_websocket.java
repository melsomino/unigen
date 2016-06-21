package org.unigen.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class Dev_events_websocket extends WebSocketAdapter {
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
	}


	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
	}


	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
	}


	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
	}
}
