package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.PassportConstants;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.springweb.utils.WebUtils;

@Controller  
@RequestMapping(value = "/auth")
public class RedirctAuthController extends BaseAuthController{

	
	
	@RequestMapping(value = "login")
	public Object login(Model model, HttpServletRequest request ,HttpServletResponse response){
		
		
		if(StringUtils.equals(request.getMethod(), HttpMethod.GET)){
			String referer = request.getHeader(HttpHeaders.REFERER);
			if(StringUtils.isBlank(referer)){
				model.addAttribute("error", "未知来源");
				return "error";
			}
			
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
			
			model.addAttribute("origin_url", referer);
			model.addAttribute(OAuth.OAUTH_REDIRECT_URI, returnUrl);
			return "login";
		}
		
		String redirctUri = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(redirctUri)){
			model.addAttribute("error", "Parameter ["+OAuth.OAUTH_REDIRECT_URI+"] is required");
			return "error";
		}
		
		String orignUrl = request.getParameter("origin_url");
		//验证用户
		Account account = validateUser(request,model);
		if(account == null){
			return "error";
		}
		//
		return createSessionAndSetResponse(request, response, account, redirctUri,orignUrl);
	}

}
