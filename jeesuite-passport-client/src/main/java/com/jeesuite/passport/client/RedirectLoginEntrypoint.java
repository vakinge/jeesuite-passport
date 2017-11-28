/**
 * 
 */
package com.jeesuite.passport.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/redirect/login", description = "跳转去认证中心入口")
public class RedirectLoginEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String returnUrl = req.getHeader("Referer");
		if(StringUtils.isBlank(returnUrl)){
			returnUrl = WebUtils.getBaseUrl(req) + ClientConfig.defaultLoginSuccessRedirctUri();
		}
		
		String url = String.format(ClientConfig.authServerBasePath() + "/login?client_id=%s&return_url=%s", 
				                  ClientConfig.clientId(),returnUrl);
		resp.sendRedirect(url);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
