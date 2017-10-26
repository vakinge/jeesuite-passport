/**
 * 
 */
package com.jeesuite.passport.client;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeesuite.passport.exception.UnauthorizedException;
import com.jeesuite.passport.helper.AuthSessionHelper;
import com.jeesuite.passport.model.LoginSession;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月20日
 */
public class AuthChecker {
	
	private static Logger log = LoggerFactory.getLogger(AuthChecker.class);
	
	private Pattern anonUriPattern;

	public AuthChecker(String ignoreCheckUris) {
		String regex = "/oauth2/.*";
		if(StringUtils.isNotBlank(ignoreCheckUris)){
			regex = regex + "|" + ignoreCheckUris.replaceAll(";", "|").replaceAll("\\*+", ".*");
		}
		anonUriPattern = Pattern.compile(regex);
	}
	
	public AuthChecker() {
		this(StringUtils.trimToEmpty(ClientConfig.get(ClientConstants.AUTH_IGNORE_URIS)));
	}
	
	

	public LoginSession process(HttpServletRequest request,HttpServletResponse response) {
		
		// 是否需要鉴权
		boolean requered = !anonUriPattern.matcher(request.getRequestURI()).matches();

		String sessionId = AuthSessionHelper.getSessionId(request);
		LoginSession sesion = AuthSessionHelper.validateSessionIfNotCreateAnonymous(sessionId);
		
		//如果是首次生成session
		if(StringUtils.isBlank(sessionId) && sesion.isAnonymous()){
			String domain = request.getServerName();
			AuthSessionHelper.createSessionCookies(sesion.getSessionId(), domain, sesion.getExpiresIn());
			log.debug("createSessionCookie:{}",sesion.getSessionId());
		}
				
		if (requered && sesion.isAnonymous()) {
			throw new UnauthorizedException();
		}

		return sesion;
	}

}
