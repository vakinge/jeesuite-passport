/**
 * 
 */
package com.jeesuite.passport.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/sso/*", description = "SSO客户端入口")
public class SSOEntryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String act = req.getPathInfo().substring(1);
		if("redirect".equals(act)) {
			
		}else if("validate".equals(act)) {
			
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
