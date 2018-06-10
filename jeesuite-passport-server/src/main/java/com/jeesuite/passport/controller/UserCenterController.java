package com.jeesuite.passport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.passport.LoginContext;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.model.LoginSession;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.springweb.model.WrapperResponseEntity;

@Controller  
@RequestMapping(value = "/ucenter")
public class UserCenterController {

	private static final String USER_INFO_ATTR_NAME = "userInfo";
	@Autowired
	protected UserService userService;
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		UserInfo userInfo = userService.findAcctountById(session.getUserId());
		model.addAttribute(USER_INFO_ATTR_NAME, userInfo);
		return "ucenter/index";
	}

	@RequestMapping(value = "snsbinding", method = RequestMethod.GET)
	public String snsbinding(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		model.addAttribute(USER_INFO_ATTR_NAME, session.getUserInfo());
		return "ucenter/snsbinding";
	}
	
	@RequestMapping(value = "setting", method = RequestMethod.GET)
	public String setting(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		model.addAttribute(USER_INFO_ATTR_NAME, session.getUserInfo());
		return "ucenter/setting";
	}
	
	@RequestMapping(value = "setting/password", method = RequestMethod.GET)
	public String passwordsetting(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		model.addAttribute(USER_INFO_ATTR_NAME, session.getUserInfo());
		return "ucenter/passwordsetting";
	}
	
	@RequestMapping(value = "setting/email", method = RequestMethod.GET)
	public String emailsetting(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		model.addAttribute(USER_INFO_ATTR_NAME, session.getUserInfo());
		return "ucenter/emailsetting";
	}
	
	@RequestMapping(value = "setting/mobile", method = RequestMethod.GET)
	public String mobilesetting(Model model){
		LoginSession session = LoginContext.getRequireLoginSession();
		model.addAttribute(USER_INFO_ATTR_NAME, session.getUserInfo());
		return "ucenter/mobilesetting";
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public @ResponseBody WrapperResponseEntity updateMyUser(@RequestBody UserInfo account){
		LoginSession session = LoginContext.getRequireLoginSession();
		account.setId(session.getUserId());
		userService.updateAccount(account);
		return new WrapperResponseEntity();
	}
	
}
