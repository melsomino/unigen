package org.unified.dev.server;


import org.unified.templates.Template_engine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Dev_web_servlet extends org.eclipse.jetty.servlet.NoJspServlet {


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		String path = req.getPathInfo();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.isEmpty()) {
			path = "index.html";
		}
		Dev_server server = (Dev_server) getServletContext().getAttribute("dev_server");
		try {
			String content;
			if (server.dev_web_path != null) {
				content = Template_engine.read_text_file(server.dev_web_path.resolve(path));
			}
			else {
				content = Template_engine.read_text_resource(Dev_web_servlet.class, "web/" + path);
			}
			response.getWriter().print(content);
		} catch (Exception e) {
			response.setStatus(404);
			log("Resource [" + path + "] not found.");
		}
	}
}
