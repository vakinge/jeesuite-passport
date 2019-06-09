/*
 * Copyright 2016-2018 www.jeesuite.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeesuite.passport.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.dao.entity.UserEntity;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.service.UserService;
import com.jeesuite.security.SecurityDecisionProvider;
import com.jeesuite.security.exception.UserNotFoundException;
import com.jeesuite.security.exception.UserPasswordWrongException;
import com.jeesuite.security.model.BaseUserInfo;
import com.jeesuite.security.model.UserSession;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2018年12月4日
 */
@Component
public class PassportSecurityDecisionProvider extends SecurityDecisionProvider {

	private @Autowired UserService userService;

	@Override
	public String contextPath() {
		return ResourceUtils.getProperty("server.servlet.context-path", "");
	}
	
	@Override
	public String[] anonymousUrlPatterns() {
		return null;
	}
	
	@Override
	public String[] protectedUrlPatterns() {
		return new String[]{"/ucenter/*"};
	}

	@Override
	public BaseUserInfo validateUser(String name, String password) throws UserNotFoundException, UserPasswordWrongException {
		UserInfo userInfo = userService.findAcctountByLoginName(name);
		if(userInfo == null)throw new UserNotFoundException();
		password = UserEntity.encryptPassword(password);
		if(!password.equals(userInfo.getPassword()))throw new UserPasswordWrongException();
		return userInfo;
	}

	@Override
	public List<String> findAllUriPermissionCodes() {
		List<String> result = new ArrayList<>();
		return result;
	}


	@Override
	public List<String> getUserPermissionCodes(Serializable userId) {
		return new ArrayList<String>();
	}

	@Override
	public void authorizedPostHandle(UserSession session) {
		
	}

	@Override
	public String superAdminName() {
		return AppConstants.SUPER_ADMIN_NAME;
	}

	@Override
	public String _401_Error_Page() {
		return "/login";
	}

	@Override
	public String _403_Error_Page() {
		return null;
	}

	
}
