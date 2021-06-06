package com.jeesuite.passport.controller;

import java.awt.Font;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesuite.cache.command.RedisString;
import com.jeesuite.common.util.FormatValidateUtils;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.springweb.model.WrapperResponseEntity;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;

@Controller  
@RequestMapping(value = "/api/common")
public class CommonController {

	@RequestMapping(value = "/send_code", method = RequestMethod.POST)
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
	
	@RequestMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        SecurityDelegating.setSessionAttribute(AppConstants.CAPTCHA, specCaptcha.text(), 60);
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
}
