package org.unified.dev.server;


import org.unified.templates.Template;
import org.unified.templates.Template_engine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Dev_web_servlet extends org.eclipse.jetty.servlet.NoJspServlet {


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		Dev_server server = (Dev_server) getServletContext().getAttribute("dev_server");
		try {
			String content = server.dev_web_path != null ? Template_engine.read_text_file(server.dev_web_path.resolve("index.html")) : Template_engine.read_text_resource(
				Dev_web_servlet.class, "web/index.html");
			response.getWriter().print(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
