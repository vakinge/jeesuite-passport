package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.Constants;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.springweb.utils.WebUtils;

@Controller  
@RequestMapping(value = "/")
public class LoginController extends BaseLoginController{

	
	@RequestMapping(value = "login",method = RequestMethod.GET)
	public String toLoginpage(Model model, HttpServletRequest request ,HttpServletResponse response){
		String referer = request.getHeader(HttpHeaders.REFERER);
		//从其他站点进入
		if(StringUtils.isNotBlank(referer) && !referer.startsWith(WebUtils.getBaseUrl(request))){
			String clientId = request.getParameter(PassportConstants.PARAM_CLIENT_ID);
			String orignDomain = WebUtils.getDomain(referer);
			try {				
				validateOrignDomain(clientId,orignDomain);
			} catch (JeesuiteBaseException e) {
				model.addAttribute("error", e.getMessage());
				return "error";
			}
			String returnUrl;
			//同域
			if(StringUtils.contains(orignDomain, authCookiesDomain)){
				returnUrl = referer;
			}else{
				returnUrl = request.getParameter(PassportConstants.PARAM_RETURN_URL);
				if(StringUtils.isBlank(returnUrl)){
					model.addAttribute("error", "Parameter [return_url] is required");
					return "error";
				}
				
				if(!returnUrl.startsWith("http")){
					returnUrl = WebUtils.getBaseUrl(referer) + returnUrl;
				}
			}
			model.addAttribute(PassportConstants.PARAM_ORIGIN_URL, referer);
			model.addAttribute(OAuth.OAUTH_REDIRECT_URI, returnUrl);
			model.addAttribute(PassportConstants.PARAM_CLIENT_ID, clientId);
		}
		
		
		return "login";
	}
	
	@RequestMapping(value = "login",method = RequestMethod.POST)
	public String login(Model model, HttpServletRequest request ,HttpServletResponse response){
		String redirctUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(redirctUri)){
			redirctUri = WebUtils.getBaseUrl(request) + "/ucenter/index";
		}
		
		String orignUrl = request.getParameter(PassportConstants.PARAM_ORIGIN_URL);
		//验证用户
		UserInfo account = validateUser(request,model);
		if(account == null)return Constants.ERROR;
		//
		return createSessionAndSetResponse(request, response, account, redirctUri,orignUrl);
	}

}
