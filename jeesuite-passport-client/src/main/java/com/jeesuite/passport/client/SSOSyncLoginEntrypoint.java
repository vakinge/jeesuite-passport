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
import com.jeesuite.springweb.utils.WebUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@WebServlet(urlPatterns = "/sso/jump_login", description = "跨应用传递登录")
public class SSOSyncLoginEntrypoint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String redirectUri = WebUtils.getBaseUrl(req) + ClientConfig.defaultLoginSuccessRedirctUri();
		String sessionId = req.getParameter(PassportConstants.PARAM_SESSION_ID);
		String ticket = req.getParameter(PassportConstants.PARAM_TICKET);
		String act = req.getParameter(PassportConstants.PARAM_ACT);
		if(StringUtils.isAnyBlank(sessionId,ticket,act)){
			WebUtils.responseOutHtml(resp, "非法请求[参数session_id,ticket,act必填]");
			return;
		}
		
		try {
			TokenGenerator.validate(ticket, true);
		} catch (JeesuiteBaseException e) {
			WebUtils.responseOutHtml(resp, e.getMessage());
			return;
		}
		
		LoginSession loginSession = AuthSessionHelper.getLoginSession(sessionId);
		if(loginSession == null)return;
		//生成新的sessionId
		loginSession.setSessionId(AuthSessionHelper.generateSessionId(false));
		//TODO 储存session  需要先解决一个用户多个session的场景 逻辑需要梳理
		
		String domain = WebUtils.getRootDomain(req);
		Cookie loginCookie = AuthSessionHelper.createSessionCookies(loginSession.getSessionId(), domain, loginSession.getExpiresIn());
		resp.addCookie(loginCookie);
		

		resp.sendRedirect(redirectUri);
	}
	

	@Override
	public void destroy() {
		super.destroy();
	}

}
