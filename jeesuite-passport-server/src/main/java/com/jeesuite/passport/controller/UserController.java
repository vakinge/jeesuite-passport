package com.jeesuite.passport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.exception.UnauthorizedException;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.springweb.annotation.CorsEnabled;
import com.jeesuite.springweb.model.WrapperResponse;
import com.jeesuite.springweb.model.WrapperResponseEntity;

@Controller  
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	protected UserService userService;
	
	@CorsEnabled
	@RequestMapping(value = "profile", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<UserInfo> getMyInfo(){
		LoginSession session = LoginContext.getRequireLoginSession();
		UserInfo account = userService.findAcctountById(session.getUserId());
		return new WrapperResponse<>(account);
	}
	
	@CorsEnabled
	@RequestMapping(value = "profile/{accesstoken}", method = RequestMethod.GET)
	public @ResponseBody WrapperResponse<UserInfo> getMyInfoByAccesstoken(@PathVariable("accesstoken") String accessToken){
		LoginSession session = AuthSessionHelper.getLoginSession(accessToken);
		if(session == null)throw new UnauthorizedException();
		UserInfo account = userService.findAcctountById(session.getUserId());
		return new WrapperResponse<>(account);
	}
	
	@CorsEnabled
	@RequestMapping(value = "login_status", method = RequestMethod.GET)
	public @ResponseBody WrapperResponseEntity loginStatus(){
		LoginSession session = LoginContext.getRequireLoginSession();
		return new WrapperResponseEntity(session.getSessionId());
	}
	
	@CorsEnabled
	@RequestMapping(value = "login_status/{accesstoken}", method = RequestMethod.GET)
	public @ResponseBody WrapperResponseEntity loginStatusWithToken(@PathVariable("accesstoken") String accessToken){
		LoginSession session = AuthSessionHelper.getLoginSession(accessToken);
		if(session == null)throw new UnauthorizedException();
		return new WrapperResponseEntity(session.getSessionId());
	}	
}
