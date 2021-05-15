package com.jeesuite.passport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.passport.AppConstants.OpenSubType;
import com.jeesuite.passport.component.openauth.OauthConnector;
import com.jeesuite.passport.component.openauth.OauthUser;
import com.jeesuite.passport.component.openauth.connector.OSChinaConnector;
import com.jeesuite.passport.component.openauth.connector.QQConnector;
import com.jeesuite.passport.component.openauth.connector.WeiboConnector;
import com.jeesuite.passport.component.openauth.connector.WeixinMpConnector;
import com.jeesuite.passport.component.openauth.connector.WinxinConnector;
import com.jeesuite.passport.dao.entity.OpenOauthConfigEntity;
import com.jeesuite.passport.dao.entity.UserPrincipalEntity;
import com.jeesuite.passport.dao.mapper.OpenOauthConfigEntityMapper;
import com.jeesuite.passport.dto.LoginClientInfo;
import com.jeesuite.security.SecurityConstants;
import com.jeesuite.security.SecurityDelegating;
import com.jeesuite.security.model.UserSession;


/**
 * 第三方开放平台登录
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2016年5月23日
 */
@Controller
@RequestMapping("/auth/openlogin")
public class OpenOAuthLoginController extends BaseLoginController implements CommandLineRunner{
	

	@Autowired
	private OpenOauthConfigEntityMapper openOauthConfigMapper;
	private Map<String, OauthConnector> oauthConnectors = new HashMap<>();
	private WeixinMpConnector weixinGzhConnector = new WeixinMpConnector();
	
	@RequestMapping(value = "{type}",method = RequestMethod.GET)
	public String loginRedirect(HttpServletRequest request,@PathVariable("type") String type
			,@RequestParam(value="client_id",required=false) String clientId
			,@RequestParam(value="return_url",required=false) String returnUrl){
		
		boolean isWxGzh = WeixinMpConnector.TYPE.equals(type);

		OauthConnector connector = null;
		if(!isWxGzh){
			connector = oauthConnectors.get(type);
			if(connector == null)throw new JeesuiteBaseException(1001,"未找到登录类型["+type+"]配置");
		}
		
		validateAndGetClientConfig(clientId,returnUrl);
		//
		LoginClientInfo clientInfo = new LoginClientInfo(clientId, type, returnUrl);
		String state = SecurityDelegating.getSessionManager().setTemporaryObject(clientId, clientInfo, 60);
		
		String callBackUri = request.getRequestURL().toString().split("/" + type)[0] + "/callback";
		String redirectUrl;
		if(isWxGzh){
			String scope = request.getParameter("scope");
			scope = WeixinMpConnector.SNSAPI_USERINFO.equalsIgnoreCase(scope) ? WeixinMpConnector.SNSAPI_USERINFO : WeixinMpConnector.SNSAPI_BASE;
			redirectUrl = weixinGzhConnector.getAuthorizeUrl(clientId, scope, callBackUri, state);
		}else{
			redirectUrl = connector.getAuthorizeUrl(state, callBackUri);			
		}

		return redirectTo(redirectUrl); 
	}
	
	
	@RequestMapping(value = "callback", method = {RequestMethod.GET,RequestMethod.POST})
	public String callback(HttpServletRequest request,HttpServletResponse response,Model model) {
		String code = request.getParameter(SecurityConstants.PARAM_CODE);
		String state = request.getParameter("state");
		if(StringUtils.isBlank(state)){
			return redirectError("Parameter[state] is required");
		}
		LoginClientInfo clientInfo = SecurityDelegating.getSessionManager().getTemporaryObjectByEncodeKey(state);
		if(clientInfo == null){
			return redirectError("Request is expired");
		}

		OauthUser oauthUser;
		if(WeixinMpConnector.TYPE.equals(clientInfo.getOpenType())){
			oauthUser = weixinGzhConnector.getUser(clientInfo.getClientId(), code);
		}else{
			oauthUser = oauthConnectors.get(clientInfo.getOpenType()).getUser(code);
		}
		if(StringUtils.isBlank(oauthUser.getOpenId())){
			return redirectError("openId is null");
		}
		
		oauthUser.setOpenType(clientInfo.getOpenType());
		oauthUser.setFromClientId(clientInfo.getClientId());

		//根据openid 找用户
		UserPrincipalEntity userPrincipal = userService.findUserByOpenId(oauthUser.getOpenType(), oauthUser.getOpenId());
		if(userPrincipal == null){
			userPrincipal = userService.createUserIfAbent(oauthUser);
		}
		
		UserSession session = SecurityDelegating.updateSession(userPrincipal.toAuthUser());
		if(clientInfo.getClientId() == null){
			return redirectTo(frontLandingUrl);
		}else{
			return loginSuccessRedirect(session, clientInfo.getClientId() , clientInfo.getReturnUrl());
		}
	}


	@Override
	public void run(String... args) throws Exception {
		List<OpenOauthConfigEntity> list = openOauthConfigMapper.selectAll();
		for (OpenOauthConfigEntity entity : list) {
			if(!entity.getEnabled())continue;
			if(QQConnector.TYPE.equals(entity.getOpenType())){
				oauthConnectors.put(QQConnector.TYPE, new QQConnector(entity.getAppId(), entity.getAppSecret()));
			}else if(WeiboConnector.TYPE.equals(entity.getOpenType())){
				oauthConnectors.put(WeiboConnector.TYPE, new WeiboConnector(entity.getAppId(), entity.getAppSecret()));
			}else if(WinxinConnector.TYPE.equals(entity.getOpenType())){
				if(OpenSubType.gzh.name().equals(entity.getSubType())){
					weixinGzhConnector.addConfig(entity.getBindClientIds(), entity.getAppId(), entity.getAppSecret());
				}else if(OpenSubType.xcx.name().equals(entity.getSubType())){
					
				}else{
					oauthConnectors.put(WinxinConnector.TYPE, new WinxinConnector(entity.getAppId(), entity.getAppSecret()));
				}
			}else if(OSChinaConnector.TYPE.equals(entity.getOpenType())){
				oauthConnectors.put(OSChinaConnector.TYPE, new OSChinaConnector(entity.getAppId(), entity.getAppSecret()));
			}
		}
	}

	
}
