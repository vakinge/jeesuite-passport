/**
 * 
 */
package com.jeesuite.passport.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.BeanCopyUtils;
import com.jeesuite.common.util.FormatValidateUtils;
import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.passport.dao.entity.SnsAccounyBindingEntity;
import com.jeesuite.passport.dao.entity.SnsAccounyBindingEntity.SnsType;
import com.jeesuite.passport.dao.entity.UserDetailEntity;
import com.jeesuite.passport.dao.mapper.AccountEntityMapper;
import com.jeesuite.passport.dao.mapper.SnsAccounyBindingEntityMapper;
import com.jeesuite.passport.dao.mapper.UserDetailEntityMapper;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.RequestMetadata;
import com.jeesuite.passport.helper.SecurityCryptUtils;
import com.jeesuite.passport.snslogin.OauthUser;
import com.jeesuite.passport.snslogin.connector.WeixinGzhConnector;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Service
public class AccountService {

	
	@Autowired
	private AccountEntityMapper accountMapper;
	@Autowired
	private UserDetailEntityMapper userDetailMapper;
	@Autowired
	private SnsAccounyBindingEntityMapper snsAccounyBindingMapper;


	public UserInfo findAcctountById(int id) {
		AccountEntity entity = accountMapper.selectByPrimaryKey(id);
		return buildUserInfo(entity);
	}
	
	public UserInfo findByWxUnionId(String unionId) {
		AccountEntity entity = accountMapper.findByWxUnionId(unionId);
		return buildUserInfo(entity);
	}
	
	public UserInfo findAcctountByLoginName(String loginName) {
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		return buildUserInfo(entity);
	}

	private UserInfo buildUserInfo(AccountEntity entity) {
		UserDetailEntity detailEntity = userDetailMapper.selectByPrimaryKey(entity.getId());
		UserInfo userInfo = BeanCopyUtils.copy(detailEntity, UserInfo.class);
		userInfo.setMobile(entity.getMobile());
		userInfo.setEmail(entity.getEmail());
		return userInfo;
	}
	
	public UserInfo findAcctountBySnsOpenId(String type,String openId) {
		type = WeixinGzhConnector.SNS_TYPE.equals(type) ? SnsType.weixin.name() : type;
		SnsAccounyBindingEntity bindingEntity = snsAccounyBindingMapper.findBySnsOpenId(type, openId);
		if(bindingEntity != null){
			AccountEntity accountEntity = accountMapper.selectByPrimaryKey(bindingEntity.getUserId());
			return buildUserInfo(accountEntity);
		}
		return null;
	}
	
	public UserInfo checkAndGetAccount(String loginName,String password){
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		if(entity == null || !entity.getPassword().equals(cryptPassword(password, entity.getRegAt()))){
		   return null;
		}
		return BeanCopyUtils.copy(entity,UserInfo.class);
	}
	
	@Transactional
	public UserInfo createUser(UserInfo userInfo,RequestMetadata metadata){
		AccountEntity accountEntity = null;
		if(FormatValidateUtils.isMobile(userInfo.getMobile())){
			accountEntity = accountMapper.findByMobile(userInfo.getMobile());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4001, "该手机已注册");
			}
		}else{
			throw new JeesuiteBaseException(4003, "手机号不能为空");
		}
		
		if(StringUtils.isNotBlank(userInfo.getEmail())){
			accountEntity = accountMapper.findByLoginName(userInfo.getEmail());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
		}
		
		if(StringUtils.isNotBlank(userInfo.getUsername())){
			accountEntity = accountMapper.findByLoginName(userInfo.getUsername());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该用户名已注册");
			}
		}
		
		accountEntity = BeanCopyUtils.copy(userInfo, AccountEntity.class);
		accountEntity.setRegAt(metadata.getTime());
		if(StringUtils.isNotBlank(accountEntity.getPassword())){
			accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
		}
		accountMapper.insertSelective(accountEntity);
		
		//
		UserDetailEntity userDetail = new UserDetailEntity(); 
		userDetail.setUserId(accountEntity.getId());
		userDetail.setNickname(userInfo.getNickname());
		userDetail.setAvatar(userInfo.getAvatar());
		userDetail.setCreatedAt(accountEntity.getRegAt());
		userDetailMapper.insertSelective(userDetail);
		
		userInfo.setId(accountEntity.getId());
		return userInfo;
	}
	
	@Transactional
	public UserInfo createUserByOauthInfo(OauthUser oauthUser,AccountBindParam bindParam){
		
		String snsType = WeixinGzhConnector.SNS_TYPE.equals(oauthUser.getSnsType()) ? SnsType.weixin.name() : oauthUser.getSnsType();
		UserInfo account = findAcctountBySnsOpenId(snsType, oauthUser.getOpenId());
		if(account == null){
			
			AccountEntity accountEntity = null;
			//先按unionId查找是否有已存在的用户
			if(StringUtils.isNotBlank(oauthUser.getUnionId())){
				List<SnsAccounyBindingEntity> sameAccounyBinds = snsAccounyBindingMapper.findByUnionId(oauthUser.getUnionId());
				if(sameAccounyBinds != null && sameAccounyBinds.size() > 0){
					accountEntity = accountMapper.selectByPrimaryKey(sameAccounyBinds.get(0).getUserId());
					if(accountEntity == null){
						throw new JeesuiteBaseException(501, String.format("该账号绑定用户异常，UserId:%s", sameAccounyBinds.get(0).getUserId()));
					}
				}
			}
			
			if(accountEntity == null){
				if(bindParam == null){					
					accountEntity = new AccountEntity();
				}else{
					accountEntity = BeanCopyUtils.copy(bindParam, AccountEntity.class);
				}
				if(StringUtils.isBlank(accountEntity.getPassword())){
					accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
				}
				accountEntity.setRegAt(bindParam.getTime());
				accountMapper.insertSelective(accountEntity);
				
				UserDetailEntity userDetail = new UserDetailEntity(); 
				userDetail.setUserId(accountEntity.getId());
				userDetail.setNickname(oauthUser.getNickname());
				userDetail.setAvatar(oauthUser.getAvatar());
				if(StringUtils.isNotBlank(oauthUser.getGender())){					
					userDetail.setGender(oauthUser.getGender());
				}
				userDetail.setCreatedAt(accountEntity.getRegAt());
				userDetailMapper.insertSelective(userDetail);
			}
			//
			SnsAccounyBindingEntity bindingEntity = new SnsAccounyBindingEntity();
			bindingEntity.setUserId(accountEntity.getId().intValue());
			bindingEntity.setSnsType(snsType);
			bindingEntity.setOpenId(oauthUser.getOpenId());
			bindingEntity.setUnionId(oauthUser.getUnionId());
			bindingEntity.setEnabled(true);
			bindingEntity.setCreatedAt(bindParam.getTime());
			snsAccounyBindingMapper.insertSelective(bindingEntity);
			
			account = buildUserInfo(accountEntity);
			
		}
		return account;
	}
	
	@Transactional
	public void updateAccount(UserInfo userInfo){
		
		AccountEntity accountEntity = accountMapper.selectByPrimaryKey(userInfo.getId());
		if(accountEntity == null)throw new JeesuiteBaseException(4001, "账号不存在");
		UserDetailEntity detailEntity = userDetailMapper.selectByPrimaryKey(userInfo.getId());
		
		boolean updateAccount = false;
		boolean updateDetail = false;
		AccountEntity existAccount = null;
		if(StringUtils.isNotBlank(userInfo.getMobile()) && !userInfo.getMobile().equals(accountEntity.getMobile())){
			existAccount = accountMapper.findByMobile(userInfo.getMobile());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该手机号码已注册");
			}
			accountEntity.setMobile(userInfo.getMobile());
			updateAccount = true;
		}
		
		if(StringUtils.isNotBlank(userInfo.getEmail()) && !userInfo.getEmail().equals(accountEntity.getEmail())){
			existAccount = accountMapper.findByLoginName(userInfo.getEmail());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
			accountEntity.setEmail(userInfo.getEmail());
			updateAccount = true;
		}
		
		if(StringUtils.isNotBlank(userInfo.getPassword())){
			accountEntity.setPassword(cryptPassword(userInfo.getPassword(), accountEntity.getRegAt()));
			updateAccount = true;
		}
		
		if(StringUtils.isNotBlank(userInfo.getGender())){
			detailEntity.setGender(userInfo.getGender());
			updateDetail = true;
		}
		
		if(StringUtils.isNotBlank(userInfo.getAvatar())){
			detailEntity.setAvatar(userInfo.getAvatar());
			updateDetail = true;
		}
		
		if(StringUtils.isNotBlank(userInfo.getNickname())){
			detailEntity.setNickname(userInfo.getNickname());
			updateDetail = true;
		}
		
		accountEntity.setUpdatedAt(new Date());
		
		if(updateAccount){
			accountMapper.updateByPrimaryKeySelective(accountEntity);
		}
		if(updateDetail){
			userDetailMapper.updateByPrimaryKeySelective(detailEntity);
		}
	}


	private static String cryptPassword(String password,Date regAt){
		String salt = String.valueOf(regAt.getTime() / 1000);
		return SecurityCryptUtils.cryptPassword(password, salt);
	}
}
