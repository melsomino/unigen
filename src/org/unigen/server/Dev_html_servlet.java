package org.unigen.server;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public class Dev_html_servlet extends org.eclipse.jetty.servlet.NoJspServlet {



	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		List<Path> repository_paths = (List<Path>) getServletContext().getAttribute("repository_paths");
		PrintWriter html = response.getWriter();
		html.print("<body style='margin: 0'>");
		html.print("<div style='background: orange; padding: 8pt; font-family: Helvetica Neue, Helvetica; font-size: 24pt; color: white'>Unified Dev Server</div>");
		html.print("<div style='padding: 18pt; font-family: Helvetica Neue, Helvetica; font-size: 14pt; color: orange'>Monitored repositories</div>");
		for (Path repository_path : repository_paths) {
			html.print("<div style='padding: 18pt; font-family: Helvetica Neue, Helvetica; font-size: 12pt; color: gray'>" + repository_path.toAbsolutePath() + "</div>");
		}
		html.print("</body>");
	}
}
