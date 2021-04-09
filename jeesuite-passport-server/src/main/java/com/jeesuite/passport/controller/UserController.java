package com.jeesuite.passport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.passport.dto.AuthUserDetails;
import com.jeesuite.passport.service.AccountService;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.model.WrapperResponse;
import com.jeesuite.springweb.model.WrapperResponseEntity;

@Controller  
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	protected AccountService userService;
	
	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<AuthUserDetails> getMyInfo(){
		UserSession session = SecurityDelegating.getCurrentSession();
		AuthUserDetails account = userService.findAcctountById(session.getUserId()).toAuthUser();
		return new WrapperResponse<>(account);
	}
	

	@RequestMapping(value = "login_status", method = RequestMethod.GET)
	public @ResponseBody WrapperResponseEntity loginStatus(){
		UserSession session = SecurityDelegating.getCurrentSession();
		return new WrapperResponseEntity(session.getSessionId());
	}

}
