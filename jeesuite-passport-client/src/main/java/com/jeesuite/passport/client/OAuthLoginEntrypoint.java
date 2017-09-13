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

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/oauth2/login", description = "oauth2.0登录入口")
public class OAuthLoginEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String referer = req.getHeader("Referer");
		
		String redirectUrl = ClientConfig.redirctUri();
		if(StringUtils.isBlank(redirectUrl)){
			redirectUrl = req.getRequestURL().toString().replace("login", "redirct_callback");
		}
		String state = AuthSessionHelper.createOauthState(referer);
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
