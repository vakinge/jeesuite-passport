/**
 * 
 */
package com.jeesuite.passport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.passport.dao.mapper.AccountEntityMapper;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.common.util.BeanCopyUtils;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Service
public class AccountService {

	@Autowired
	private AccountEntityMapper accountMapper;

	public Account findAcctountByLoginName(String loginName) {
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		return BeanCopyUtils.copy(entity, Account.class);
	}

}
