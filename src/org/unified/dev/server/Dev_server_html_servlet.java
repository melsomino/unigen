package org.unified.dev.server;


import org.unified.templates.Template;
import org.unified.templates.Template_engine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Dev_server_html_servlet extends org.eclipse.jetty.servlet.NoJspServlet {


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		Dev_server server = (Dev_server) getServletContext().getAttribute("dev_server");

		Template_engine templates = new Template_engine("html", Dev_server_html_servlet.class, "templates");
		templates.shared_header = "" +
			"import java.io.*;\n" +
			"import org.unified.declaration.*;\n" +
			"import org.unified.dev.*;\n" +
			"import org.unified.dev.server.*;\n";
		templates.shared_arg_names_and_types = new Object[]{"server", Dev_server.class};

		try {
			Template template = templates.load("index", null);
			template.generate(response.getWriter(), server);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
