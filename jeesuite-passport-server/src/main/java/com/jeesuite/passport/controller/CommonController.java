package com.jeesuite.passport.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.command.RedisString;
import com.jeesuite.common.util.FormatValidateUtils;
import com.jeesuite.springweb.model.WrapperResponseEntity;

@Controller  
@RequestMapping(value = "/common")
public class CommonController {

	@RequestMapping(value = "send_code", method = RequestMethod.POST)
	public @ResponseBody WrapperResponseEntity register(@RequestParam("to") String sendTo){
		RedisString cache = new RedisString("send_code:"+ sendTo);
		String code = cache.get();
		if(StringUtils.isNotBlank(code)){
			return new WrapperResponseEntity(200,"send ok");
		}
		
		code = String.valueOf(RandomUtils.nextInt(1000, 9999));
		//验证码
		if(FormatValidateUtils.isMobile(sendTo)){
			//TODO 
		}else if(FormatValidateUtils.isEmail(sendTo)){
			//TODO 
		}
		
		System.out.println("code:"+code);
		cache.set(code, 180);
		return new WrapperResponseEntity(200,"send ok");
	}
}
