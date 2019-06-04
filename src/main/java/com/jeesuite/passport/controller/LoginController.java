package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.dto.LoginParam;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.utils.WebUtils;

/**
 * 常规登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年3月26日
 */
@Controller  
@RequestMapping(value = "/")
public class LoginController extends BaseLoginController{

	
	@RequestMapping(value = "login",method = RequestMethod.GET)
	public String toLoginpage(Model model, HttpServletRequest request){
		String referer = request.getHeader(HttpHeaders.REFERER);
		//从其他站点进入
		if(StringUtils.isNotBlank(referer) && !referer.startsWith(WebUtils.getBaseUrl(request))){
			String clientId = request.getParameter(SecurityConstants.PARAM_CLIENT_ID);
			String orignDomain = WebUtils.getDomain(referer);
			try {				
				validateOrignDomain(clientId,orignDomain);
			} catch (JeesuiteBaseException e) {
				model.addAttribute("error", e.getMessage());
				return "error";
			}
			String returnUrl;
			//同域
			if(StringUtils.contains(orignDomain, getCookiesDomain(request))){
				returnUrl = referer;
			}else{
				returnUrl = request.getParameter(SecurityConstants.PARAM_RETURN_URL);
				if(StringUtils.isBlank(returnUrl)){
					model.addAttribute("error", "Parameter [return_url] is required");
					return "error";
				}
				
				if(!returnUrl.startsWith("http")){
					returnUrl = WebUtils.getBaseUrl(referer) + returnUrl;
				}
			}
			model.addAttribute(SecurityConstants.PARAM_ORIGIN_URL, referer);
			model.addAttribute(OAuth.OAUTH_REDIRECT_URI, returnUrl);
			model.addAttribute(SecurityConstants.PARAM_CLIENT_ID, clientId);
		}
		
		
		return "login";
	}
	
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public String login(HttpServletRequest request,@RequestBody LoginParam param){
		String redirctUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(redirctUri)){
			redirctUri = WebUtils.getBaseUrl(request) + "/ucenter/index";
		}
		
		if (StringUtils.isAnyBlank(param.getLoginName(), param.getPassword())) {
			throw new JeesuiteBaseException(4001, "用户名或密码不能为空");
		}
		
		UserSession session = SecurityDelegating.doAuthentication(param.getLoginName(), param.getPassword());
		
		return loginSuccessRedirect(session, redirctUri);
	}

}
