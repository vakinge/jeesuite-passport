package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.WebUtils;

@Controller
@RequestMapping(value = "/jsonp")
public class JsonpLoginController extends BaseLoginController {


	@RequestMapping(value = "login")
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		String referer = request.getHeader(HttpHeaders.REFERER);
		String[] segs = StringUtils.split(referer,"/");
		String orignBaseUrl = segs[0] + "//" + segs[1];
		//redirect_uri
		String setCookiesUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(setCookiesUri))setCookiesUri = "/login_callback";
		
		String clientId = request.getParameter(PassportConstants.PARAM_CLIENT_ID);
		try {
			validateOrignDomain(clientId,segs[1]);
		} catch (JeesuiteBaseException e) {
			WrapperResponseEntity entity = new WrapperResponseEntity(e.getCode(), e.getMessage());
			WebUtils.responseOutJsonp(response, PassportConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
		
		// 验证用户
		UserInfo account = validateUser(request, model);
		if (account == null) {
			WrapperResponseEntity entity = new WrapperResponseEntity(4001, "账号或密码错误");
			WebUtils.responseOutJsonp(response, PassportConstants.JSONP_LOGIN_CALLBACK_FUN_NAME, entity);
			return null;
		}
		
		LoginSession session = createLoginSesion(request, account);
		
		StringBuilder urlBuiler = new StringBuilder(orignBaseUrl);
		urlBuiler.append(setCookiesUri);
		urlBuiler.append("?session_id=").append(session.getSessionId());
		urlBuiler.append("&expires_in=").append(session.getExpiresIn());
		//TODO JWT 
		//urlBuiler.append("&userinfo=").append(session.toEncodeString());
		urlBuiler.append("&login_type=jsonp");
		urlBuiler.append("&ticket=").append(TokenGenerator.generateWithSign());
		return redirectTo(urlBuiler.toString());
	}

}
