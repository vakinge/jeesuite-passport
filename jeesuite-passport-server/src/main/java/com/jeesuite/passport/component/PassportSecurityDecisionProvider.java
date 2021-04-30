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

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.passport.AppConstants;
import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.passport.dto.AuthUserDetails;
import com.jeesuite.passport.service.AccountService;
import com.jeesuite.security.SecurityConfigurerProvider;
import com.jeesuite.security.exception.UserNotFoundException;
import com.jeesuite.security.exception.UserPasswordWrongException;
import com.jeesuite.security.model.UserSession;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2018年12月4日
 */
@Component
public class PassportSecurityDecisionProvider extends SecurityConfigurerProvider<AuthUserDetails> {

	private @Autowired AccountService userService;

	@Override
	public String contextPath() {
		return ResourceUtils.getProperty("server.servlet.context-path", "");
	}
	
	

	@Override
	public AuthUserDetails validateUser(String name, String password) throws UserNotFoundException, UserPasswordWrongException {
		AccountEntity account = userService.findAcctountByLoginName(name);
		if(account == null)throw new UserNotFoundException();
		if(!BCrypt.checkpw(password, account.getPassword())){
			throw new UserPasswordWrongException();
		}
		return null;
	}

	@Override
	public List<String> findAllUriPermissionCodes() {
		List<String> result = new ArrayList<>();
		return result;
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



	@Override
	public List<String> anonymousUrlPatterns() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<String> getUserPermissionCodes(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
