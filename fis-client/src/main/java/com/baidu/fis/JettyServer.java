package com.baidu.fis;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import php.java.servlet.ContextLoaderListener;
import php.java.servlet.fastcgi.FastCGIServlet;

public class JettyServer {

	public static void main(String[] args) throws Exception {

		HandlerCollection hc = new HandlerCollection();
		WebAppContext context = new WebAppContext("D:/workspace/oak/fis", "/") {
			@Override
			public void doScope(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				super.doScope("/index.php", baseRequest, request, response);
			}
		};
		context.setDefaultsDescriptor(Thread.currentThread().getClass()
				.getResource("/jetty/webdefault.xml").toString());
		context.addServlet(FastCGIServlet.class, "*.php");
		context.addEventListener(new ContextLoaderListener());
		hc.addHandler(context);
		Server server = new Server(80);
		server.setHandler(hc);
		server.start();
	}
}
