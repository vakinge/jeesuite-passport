package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * jsonp方式跨域登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2019年5月26日
 */
@Controller
@RequestMapping(value = "/jsonp")
public class JsonpLoginController extends BaseLoginController {


	@RequestMapping(value = "login",method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response,@RequestParam("username") String username,@RequestParam("password") String password) {
		
		String referer = request.getHeader(HttpHeaders.REFERER);
		String[] segs = StringUtils.split(referer,"/");
		String orignBaseUrl = segs[0] + "//" + segs[1];
		//redirect_uri
		String setCookiesUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(setCookiesUri))setCookiesUri = "/login_callback";
		
		String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
		try {
			if (StringUtils.isAnyBlank(username,password)) {
				throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
			}
			validateOrignDomain(clientId,segs[1]);
			//
			UserSession session = SecurityDelegating.doAuthentication(username, password, false);
			StringBuilder urlBuiler = new StringBuilder(orignBaseUrl);
			urlBuiler.append(setCookiesUri);
			urlBuiler.append("?session_id=").append(session.getSessionId());
			urlBuiler.append("&expires_in=").append(session.getExpiresIn());
			urlBuiler.append("&login_type=jsonp");
			urlBuiler.append("&ticket=").append(TokenGenerator.generateWithSign());
			return redirectTo(urlBuiler.toString());
			
		} catch (JeesuiteBaseException e) {
			WrapperResponseEntity entity = new WrapperResponseEntity(e.getCode(), e.getMessage());
			WebUtils.responseOutJsonp(response, SecurityConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
	}

}
