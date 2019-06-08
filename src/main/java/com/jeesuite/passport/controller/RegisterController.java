package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.BeanUtils;
import com.jeesuite.passport.component.openauth.OauthUser;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.RegisterParam;
import com.jeesuite.passport.dto.RequestMetadata;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.springweb.exception.ForbiddenAccessException;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.IpUtils;

@Controller  
@RequestMapping(value = "/")
public class RegisterController {

	@Autowired
	protected UserService userService;
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String toRegister(){
		return "register";
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public @ResponseBody WrapperResponseEntity register(HttpServletRequest request,@RequestBody RegisterParam param){
		//验证码
		
		userService.createUser(BeanUtils.copy(param, UserInfo.class),RequestMetadata.build(request));
		return new WrapperResponseEntity();
	}
	
	
	@CrossOrigin(origins = "*", maxAge = 3600) 
	@RequestMapping(value = "register/bind", method = RequestMethod.POST)
	public @ResponseBody WrapperResponseEntity createUserThreepartBind(HttpServletRequest request,@RequestBody AccountBindParam param){
		if(StringUtils.isBlank(param.getAuthTicket()))throw new ForbiddenAccessException();
		RedisObject redis = new RedisObject(param.getAuthTicket());
		OauthUser oauthUser = redis.get();
		if(oauthUser == null){
			throw new JeesuiteBaseException(4001, "ticket过期或者不存在");
		}
		
		param.setAppId(oauthUser.getFromClientId());
		param.setIpAddr(IpUtils.getIpAddr(request));
		userService.createUserByOauthInfo(oauthUser, param);
		//删除ticket
		redis.remove();
		return new WrapperResponseEntity();
	}
}
