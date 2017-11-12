package com.jeesuite.passport.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.exception.ForbiddenAccessException;
import com.jeesuite.passport.service.AccountService;
import com.jeesuite.passport.snslogin.OauthUser;
import com.jeesuite.springweb.annotation.CorsEnabled;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.jeesuite.springweb.utils.IpUtils;

import io.swagger.annotations.ApiOperation;

@Controller  
@RequestMapping(value = "/")
public class RegisterController {

	@Autowired
	protected AccountService accountService;
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String toRegister(){
		return "register";
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public WrapperResponseEntity register(){
		
		return new WrapperResponseEntity();
	}
	
	@CorsEnabled
	@RequestMapping(value = "register_check", method = RequestMethod.POST)
	@ApiOperation(value = "注册账号可用性检查",notes="### 调用范围 \n - 匿名 \n - 可跨域", httpMethod = "POST")
	public @ResponseBody WrapperResponseEntity registerCheck(@RequestParam("account") String name){
		
		Account account = accountService.findAcctountByLoginName(name);
		if(account != null)throw new JeesuiteBaseException(4001, "已注册");
		
		return new WrapperResponseEntity();
	}
	
	@CorsEnabled
	@RequestMapping(value = "register/bind", method = RequestMethod.POST)
	@ApiOperation(value = "第三方账号绑定创建账号", notes = "### 调用范围 \n - 匿名 \n - 可跨域", httpMethod = "POST")
	public @ResponseBody WrapperResponseEntity createUserThreepartBind(HttpServletRequest request,@RequestBody AccountBindParam param){
		if(StringUtils.isBlank(param.getAuthTicket()))throw new ForbiddenAccessException();
		RedisObject redis = new RedisObject(param.getAuthTicket());
		OauthUser oauthUser = redis.get();
		if(oauthUser == null){
			throw new JeesuiteBaseException(4001, "ticket过期或者不存在");
		}
		
		param.setAppId(oauthUser.getFromClientId());
		param.setIpAddr(IpUtils.getIpAddr(request));
		accountService.createAccountByOauthInfo(oauthUser, param);
		//删除ticket
		redis.remove();
		return new WrapperResponseEntity();
	}
}
