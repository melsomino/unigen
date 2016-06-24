package org.unified.dev.server;

import com.oracle.javafx.jmx.json.JSONDocument;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.unified.dev.Dev_configuration;
import org.unified.templates.Template_engine;

import java.io.IOException;
import java.net.InetSocketAddress;

class Dev_websocket_connection extends WebSocketAdapter {


	private final Dev_server server;
	private String client_info;





	Dev_websocket_connection(Dev_server server) {
		this.server = server;
	}





	synchronized void send(String message) throws IOException {
		RemoteEndpoint remote = getRemote();
		remote.sendString(message);
	}





	private void send(String command, Object... args) throws IOException {
		send(make_message(command, args));
	}





	static String make_message(String command, Object... args) {
		if (args.length == 0) {
			return command;
		}
		StringBuilder message = new StringBuilder(command);
		for (Object arg : args) {
			message.append("`");
			if (arg != null) {
				message.append(arg.toString());
			}
		}
		return message.toString();
	}





	interface To_json<T> {
		JSONDocument convert(T item);
	}





	private static <T> JSONDocument json_array(T[] items, To_json<T> to_json) {
		JSONDocument array = JSONDocument.createArray(items.length);
		int index = 0;
		for (T item : items) {
			array.set(index++, to_json.convert(item));
		}
		return array;
	}





	private static JSONDocument json_object(Object... name_value) {
		JSONDocument obj = JSONDocument.createObject();
		for (int i = 0; i < name_value.length - 1; i += 2) {
			String name = name_value[i].toString();
			Object value = name_value[i + 1];
			if (value == null) {
				obj.setNull(name);
			} else if (value instanceof Boolean) {
				obj.setBoolean(name, (Boolean) value);
			} else if (value instanceof Integer) {
				obj.setNumber(name, (Integer) value);
			} else if (value instanceof JSONDocument) {
				obj.set(name, (JSONDocument) value);
			} else {
				obj.setString(name, value.toString());
			}
		}
		return obj;
	}





	private static JSONDocument source_json(Dev_configuration.Source source) {
		return json_object("is_module", source.is_module, "name", source.name, "path", source.path, "ios_out", source.ios_out, "android_out", source.android_out,
			"status", source.status, "status_class", source.status_class, "status_details", source.status_details, "status_time", source.status_time);
	}





	private static JSONDocument connection_json(Dev_websocket_connection connection) {
		return json_object("client_info", connection.toString());
	}





	private static JSONDocument state_json(Dev_server server) {
		return json_object("sources", json_array(server.configuration.sources, Dev_websocket_connection::source_json), "connections",
			json_array(server.active_connections(), Dev_websocket_connection::connection_json));
	}





	private void send_state() throws IOException {
		send("state", state_json(server).toJSON());
	}





	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		server.activity.connection_opened(this);
	}





	@Override
	public void onWebSocketText(String message) {
		try {
			super.onWebSocketText(message);
			String[] parts = message.split("`");
			switch (parts[0].toLowerCase()) {
				case "get-state":
					send_state();
					break;
				case "get-repository":
					Dev_configuration.Source repository = server.configuration.find_repository(parts[1]);
					if (repository != null) {
						send("repository", Template_engine.read_text_file(repository.path));
					}
					break;
				case "client-info":
					client_info = parts[1];
					server.activity.connection_changed(this);
					break;
			}
		} catch (IOException e) {
			Logger.getRootLogger().trace("Failed to handle dev request: " + message, e);
		}
	}





	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		server.activity.connection_closed(this);
	}





	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
	}





	@Override
	public String toString() {
		InetSocketAddress address = getSession().getRemoteAddress();
		return client_info != null ? client_info + " (" + address.getHostName() + ")" : address.getHostName();
	}
}
