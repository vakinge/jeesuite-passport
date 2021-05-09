package com.jeesuite.passport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jeesuite.common.util.BeanUtils;
import com.jeesuite.passport.dao.entity.UserPrincipalEntity;
import com.jeesuite.passport.dto.UserPrincipal;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;
import com.jeesuite.springweb.model.WrapperResponse;

@RestController  
@RequestMapping(value = "/auth/user")
public class UserController {
	
	@Autowired
	protected UserService userService;
	
	@RequestMapping(value = "baseInfo", method = RequestMethod.GET)
	public WrapperResponse<UserPrincipal> getCurrentUserBaseInfo(){
		UserSession session = SecurityDelegating.getAndValidateCurrentSession();
		UserPrincipalEntity entity = userService.findUserById(session.getUserId());
		UserPrincipal userPrincipal = BeanUtils.copy(entity, UserPrincipal.class);
		return new WrapperResponse<>(userPrincipal);
	}
}
