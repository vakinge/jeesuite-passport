package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.cache.CacheExpires;
import com.jeesuite.cache.command.RedisObject;
import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.snslogin.OauthConnector;
import com.jeesuite.passport.snslogin.OauthUser;
import com.jeesuite.passport.snslogin.SnsLoginState;
import com.jeesuite.passport.snslogin.connector.QQConnector;
import com.jeesuite.passport.snslogin.connector.WechatConnector;
import com.jeesuite.passport.snslogin.connector.WeiboConnector;



@Controller
@RequestMapping("/snslogin")
public class ThreePartAuthController extends BaseAuthController implements EnvironmentAware{
	
	private Map<String, OauthConnector> oauthConnectors = new HashMap<>();
	
	@RequestMapping(value = "{type}/{appId}", method = RequestMethod.GET)
	public String redirect(HttpServletRequest request,@PathVariable("type") String type
			,@RequestParam("appId") String appId
			,@RequestParam("reg") int reg
			,@RequestParam(value="regurl",required=false) String regPageUrl
			,@RequestParam("returnurl") String returnUrl){
		
		OauthConnector connector = oauthConnectors.get(type + "#" + appId);
		if(connector == null)throw new JeesuiteBaseException(1001,"不支持授权类型:"+type);
		
		SnsLoginState loginState = new SnsLoginState(appId, type, reg == 1, regPageUrl, returnUrl);
		new RedisObject(loginState.getState()).set(loginState, CacheExpires.IN_1MIN);
		
		String callBackUrl = request.getRequestURL().toString().split("/" + type)[0] + "/callback";
		String url = connector.getAuthorizeUrl(loginState.getState(), callBackUrl);

		return "redirect:" + url; 
	}
	
	@RequestMapping(value = "callback", method = {RequestMethod.GET,RequestMethod.POST})
	public String callback(HttpServletRequest request,HttpServletResponse response,Model model) {
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		
		if(StringUtils.isBlank(state)){
			return "redirect:/index"; 
		}
		
		SnsLoginState loginState = new RedisObject(state).get();
		if(loginState == null){
			return "redirect:/index"; 
		}
		
		OauthConnector connector = oauthConnectors.get(loginState.getSnsType());
		
		OauthUser oauthUser = connector.getUser(code);
		//TODO 根据openid 找用户
		Account account = null;
		
		if(account != null){			
			createLoginSesion(response,account);
			return "redirect:" + loginState.getSuccessDirectUrl(); 
		}
		
		//跳转去绑定页面
		model.addAttribute("oauthUser", oauthUser);
		model.addAttribute("redirect_uri", loginState.getSuccessDirectUrl());
		//
		return "userbind"; 
	}

	@Override
	public void setEnvironment(Environment environment) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment, "threepart.");
		Map<String, Object> subProperties = resolver.getSubProperties("");
		String type;String appKey;String appSecret;
		for (String key : subProperties.keySet()) {
			type = key.split("\\.")[1];
			if(oauthConnectors.containsKey(type))continue;
			appKey = environment.getProperty("threepart.oauth."+type+".appkey");
			appSecret = environment.getProperty("threepart.oauth."+type+".appSecret");
			
			if(QQConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new QQConnector(appKey, appSecret));
			}else if(WechatConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WechatConnector(appKey, appSecret));
			}else if(WeiboConnector.SNS_TYPE.equals(type)){				
				oauthConnectors.put(type, new WeiboConnector(appKey, appSecret));
			}
			
		}
		
	}

}
