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

import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/oauth2/login", description = "oauth2.0登录入口")
public class OAuth2LoginEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String loginSuccessRedirctUri = req.getHeader("Referer");
		if(StringUtils.isBlank(loginSuccessRedirctUri)){
			loginSuccessRedirctUri = WebUtils.getBaseUrl(req) + ClientConfig.defaultLoginSuccessRedirctUri();
		}
		
		String redirectUrl = WebUtils.getBaseUrl(req) + ClientConfig.redirctUri();
		String state = AuthSessionHelper.createOauthState(loginSuccessRedirctUri);
		String url = String.format(ClientConfig.authServerBasePath() + "/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s", 
				                  ClientConfig.clientId(),
				                  redirectUrl,
				                  state);
		resp.sendRedirect(url);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
