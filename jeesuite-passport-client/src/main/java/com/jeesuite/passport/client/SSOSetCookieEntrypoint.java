/**
 * 
 */
package com.jeesuite.passport.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.helper.TokenGenerator;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/sso/setcookie", description = "跨域登录设置cookies")
public class SSOSetCookieEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String sessionId = req.getParameter(PassportConstants.PARAM_SESSION_ID);
		if(StringUtils.isBlank(sessionId)){
			WebUtils.responseOutJsonp(resp, PassportConstants.JSONP_SETCOOKIE_CALLBACK_FUN_NAME, new WrapperResponseEntity(4001,"非法请求"));
			return;
		}
		
		String ticket = req.getParameter(PassportConstants.PARAM_TICKET);
		//验证合法性
		try {
			TokenGenerator.validate(ticket, true);
		} catch (JeesuiteBaseException e) {
			WebUtils.responseOutJsonp(resp, PassportConstants.JSONP_SETCOOKIE_CALLBACK_FUN_NAME, new WrapperResponseEntity(e.getCode(),e.getMessage()));
			return;
		}
		
		String expiresIn = req.getParameter(PassportConstants.PARAM_EXPIRE_IN);
		if(StringUtils.isBlank(expiresIn))expiresIn = String.valueOf(LoginSession.SESSION_EXPIRE_SECONDS);
		
		String domain = WebUtils.getRootDomain(req);
		Cookie loginCookie = AuthSessionHelper.createSessionCookies(sessionId, domain, Integer.parseInt(expiresIn));
		resp.addCookie(loginCookie);
		
		WebUtils.responseOutJsonp(resp, PassportConstants.JSONP_SETCOOKIE_CALLBACK_FUN_NAME, new WrapperResponseEntity());
	
	}
	

	@Override
	public void destroy() {
		super.destroy();
	}

}
