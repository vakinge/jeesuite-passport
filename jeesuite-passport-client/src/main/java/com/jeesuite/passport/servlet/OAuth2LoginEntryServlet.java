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

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.PassportClientContext;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/oauth2/login", description = "oauth2.0登录入口")
public class OAuth2LoginEntryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String loginSuccessRedirctUri = req.getHeader(HttpHeaders.REFERER);
		if(StringUtils.isBlank(loginSuccessRedirctUri)){
			loginSuccessRedirctUri = PassportClientContext.defaultLoginSuccessRedirctUri();
		}
		
		String redirectUrl = PassportClientContext.redirctUri();
		String tmpSessonId = TokenGenerator.generate();
		PassportClientContext.getSessionStorageProvider().set(tmpSessonId, redirectUrl);
		String url = String.format(PassportClientContext.authServerBasePath() + "/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s", 
				                  PassportClientContext.clientId(),
				                  redirectUrl,
				                  tmpSessonId);
		resp.sendRedirect(url);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
