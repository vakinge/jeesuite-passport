package com.jeesuite.passport.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesuite.common.guid.GUID;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.passport.AppConstants.OpenType;
import com.jeesuite.passport.component.openauth.OauthUser;
import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.passport.dao.entity.OpenAccountBindingEntity;
import com.jeesuite.passport.dao.entity.UserPrincipalEntity;
import com.jeesuite.passport.dao.mapper.AccountEntityMapper;
import com.jeesuite.passport.dao.mapper.OpenAccountBindingEntityMapper;
import com.jeesuite.passport.dao.mapper.UserPrincipalEntityMapper;

@Service
public class UserService {

	@Autowired
	private AccountEntityMapper accountMapper;
	@Autowired
	private OpenAccountBindingEntityMapper openAccounyBindingMapper;
	@Autowired
	private UserPrincipalEntityMapper userPrincipalMapper;

	
	public UserPrincipalEntity findUserById(String id) {
		UserPrincipalEntity entity = userPrincipalMapper.selectByPrimaryKey(id);
		return entity;
	}
	
	public UserPrincipalEntity findUserByUnionId(OpenType openType,String unionId) {
		String userId = openAccounyBindingMapper.findUserIdByUnionId(openType.name(), unionId);
		if(userId != null) {
			UserPrincipalEntity entity = userPrincipalMapper.selectByPrimaryKey(userId);
			return entity;
		}
		return null;
	}
	
	public UserPrincipalEntity findUserByOpenId(OpenType openType,String openId) {
		OpenAccountBindingEntity binding = openAccounyBindingMapper.findByOpenId(openType.name(), openId);
		if(binding != null) {
			UserPrincipalEntity entity = userPrincipalMapper.selectByPrimaryKey(binding.getUserId());
			return entity;
		}
		return null;
	}
	
	public UserPrincipalEntity findUserByAccount(String account) {
		AccountEntity accountEntity = accountMapper.findByLoginName(account);
		if(accountEntity != null) {
			UserPrincipalEntity entity = userPrincipalMapper.selectByPrimaryKey(accountEntity.getUserId());
			entity.setAccount(accountEntity);
			return entity;
		}
		return null;
	}
	
	
	@Transactional
	public UserPrincipalEntity createUserIfAbent(OauthUser oauthUser) {
		String userId = null;
		if(StringUtils.isNotBlank(oauthUser.getUnionId())) {
			userId = openAccounyBindingMapper.findUserIdByUnionId(oauthUser.getOpenType().name(), oauthUser.getUnionId());
		}
		if(userId == null) {
			OpenAccountBindingEntity bindingEntity = openAccounyBindingMapper.findByOpenId(oauthUser.getOpenType().name(), oauthUser.getOpenId());
			if(bindingEntity != null) {
				userId = bindingEntity.getUserId();
			}
		}
		
		if(userId != null) {
			return userPrincipalMapper.selectByPrimaryKey(userId);
		}
		
		UserPrincipalEntity entity = new UserPrincipalEntity();
		entity.setId(String.valueOf(GUID.guid()));
		entity.setAvatar(oauthUser.getAvatar());
		entity.setNickname(oauthUser.getNickname());
		entity.setGender(oauthUser.getGender());
		entity.setCreatedAt(new Date());
		userPrincipalMapper.insertSelective(entity);
		//
		OpenAccountBindingEntity bindingEntity = new OpenAccountBindingEntity();
		bindingEntity.setUserId(entity.getId());
		bindingEntity.setOpenType(oauthUser.getOpenType().name());
		bindingEntity.setSubType(oauthUser.getOpenSubType().name());
		bindingEntity.setOpenId(oauthUser.getOpenId());
		bindingEntity.setUnionId(oauthUser.getUnionId());
		bindingEntity.setEnabled(true);
		bindingEntity.setCreatedAt(entity.getCreatedAt());
		openAccounyBindingMapper.insertSelective(bindingEntity);
		
		return entity;
		
	}
	
}
