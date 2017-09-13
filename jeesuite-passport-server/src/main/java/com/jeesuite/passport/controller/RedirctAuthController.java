package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jeesuite.passport.dto.Account;

@Controller  
@RequestMapping(value = "/auth")
public class RedirctAuthController extends BaseAuthController{

	
	
	@RequestMapping(value = "login")
	public Object login(Model model, HttpServletRequest request ,HttpServletResponse response){
		
		if(StringUtils.equals(request.getMethod(), HttpMethod.GET)){
			String referer = request.getHeader("Referer");
			if(StringUtils.isBlank(referer))referer = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
			model.addAttribute(OAuth.OAUTH_REDIRECT_URI, referer);
			return "login";
		}		
		String redirctUrl = request.getParameter(OAuth.OAUTH_REDIRECT_URI);
		if(StringUtils.isBlank(redirctUrl) || !redirctUrl.contains(safeDomain)){
			model.addAttribute("error", "非法来源");
			return "error";
		}
		//验证用户
		Account account = validateUser(request,model);
		if(account == null){
			return "error";
		}
		//
		createLoginSesion(response,account);
		return "redirect:" + redirctUrl;
	}

}
