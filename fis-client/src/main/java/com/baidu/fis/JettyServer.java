package com.baidu.fis;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.Rule;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CGI;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		final Field privateStringField = CGI.class.getDeclaredField("_env");
		privateStringField.setAccessible(true);
		@SuppressWarnings("serial")
		final ServletHolder holder = new ServletHolder() {
			@Override
			public void handle(Request baseRequest, ServletRequest request,
					ServletResponse response) throws ServletException,
					UnavailableException, IOException {
				try {
					Object env = privateStringField.get(this.getServlet());
					Method set = env.getClass().getMethod("set", String.class,
							String.class);
					set.setAccessible(true);
					set.invoke(env, "REQUEST_URI",
							request.getAttribute("EVN_URI"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.handle(baseRequest, request, response);
			}
		};
		holder.setInitParameter("commandPrefix",
				"D:\\workspace\\oak\\fis\\libs\\php\\php-cgi.exe");
		holder.setHeldClass(CGI.class);

		Server server = new Server(80);
		HandlerCollection hc = new HandlerCollection();
		WebAppContext context = new WebAppContext("D:/workspace/oak/fis", "*") {
			@Override
			public void doScope(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				request.setAttribute("EVN_URI", target);
				super.doScope("/index.php", baseRequest, request, response);
			}
		};
		context.setDefaultsDescriptor(Thread.currentThread().getClass()
				.getResource("/jetty/webdefault.xml").toString());
		context.addServlet(holder, "*.php");
		hc.addHandler(context);
		server.setHandler(hc);
		server.start();
	}
}
