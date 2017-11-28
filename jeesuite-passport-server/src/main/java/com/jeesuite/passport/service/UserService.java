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
import com.jeesuite.passport.dao.entity.SnsAccounyBindingEntity;
import com.jeesuite.passport.dao.entity.SnsAccounyBindingEntity.SnsType;
import com.jeesuite.passport.dao.entity.UserEntity;
import com.jeesuite.passport.dao.mapper.SnsAccounyBindingEntityMapper;
import com.jeesuite.passport.dao.mapper.UserEntityMapper;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.RequestMetadata;
import com.jeesuite.passport.dto.UserInfo;
import com.jeesuite.passport.helper.SecurityCryptUtils;
import com.jeesuite.passport.snslogin.OauthUser;
import com.jeesuite.passport.snslogin.connector.WeixinGzhConnector;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Service
public class UserService {

	
	@Autowired
	private UserEntityMapper userMapper;
	@Autowired
	private SnsAccounyBindingEntityMapper snsAccounyBindingMapper;


	public UserInfo findAcctountById(int id) {
		UserEntity entity = userMapper.selectByPrimaryKey(id);
		return buildUserInfo(entity);
	}
	
	public UserInfo findByWxUnionId(String unionId) {
		UserEntity entity = userMapper.findByWxUnionId(unionId);
		return buildUserInfo(entity);
	}
	
	public UserInfo findAcctountByLoginName(String loginName) {
		UserEntity entity = userMapper.findByLoginName(loginName);
		return buildUserInfo(entity);
	}

	private UserInfo buildUserInfo(UserEntity entity) {
		UserInfo userInfo = BeanCopyUtils.copy(entity, UserInfo.class);
		return userInfo;
	}
	
	public UserInfo findAcctountBySnsOpenId(String type,String openId) {
		type = WeixinGzhConnector.SNS_TYPE.equals(type) ? SnsType.weixin.name() : type;
		SnsAccounyBindingEntity bindingEntity = snsAccounyBindingMapper.findBySnsOpenId(type, openId);
		if(bindingEntity != null){
			UserEntity accountEntity = userMapper.selectByPrimaryKey(bindingEntity.getUserId());
			return buildUserInfo(accountEntity);
		}
		return null;
	}
	
	public UserInfo checkAndGetAccount(String loginName,String password){
		UserEntity entity = userMapper.findByLoginName(loginName);
		if(entity == null || !entity.getPassword().equals(cryptPassword(password, entity.getRegAt()))){
		   return null;
		}
		return buildUserInfo(entity);
	}
	
	@Transactional
	public UserInfo createUser(UserInfo userInfo,RequestMetadata metadata){
		UserEntity accountEntity = null;
		if(FormatValidateUtils.isMobile(userInfo.getMobile())){
			accountEntity = userMapper.findByMobile(userInfo.getMobile());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4001, "该手机已注册");
			}
		}else{
			throw new JeesuiteBaseException(4003, "手机号不能为空");
		}
		
		if(StringUtils.isNotBlank(userInfo.getEmail())){
			accountEntity = userMapper.findByLoginName(userInfo.getEmail());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
		}
		
		if(StringUtils.isNotBlank(userInfo.getUsername())){
			accountEntity = userMapper.findByLoginName(userInfo.getUsername());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该用户名已注册");
			}
		}
		
		accountEntity = BeanCopyUtils.copy(userInfo, UserEntity.class);
		accountEntity.setRegAt(metadata.getTime());
		accountEntity.setRegIp(metadata.getIpAddr());
		accountEntity.setSourceAppId(metadata.getAppId());
		if(StringUtils.isNotBlank(accountEntity.getPassword())){
			accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
		}
		userMapper.insertSelective(accountEntity);
		
		userInfo.setId(accountEntity.getId());
		return userInfo;
	}
	
	@Transactional
	public UserInfo createUserByOauthInfo(OauthUser oauthUser,AccountBindParam bindParam){
		
		String snsType = WeixinGzhConnector.SNS_TYPE.equals(oauthUser.getSnsType()) ? SnsType.weixin.name() : oauthUser.getSnsType();
		UserInfo account = findAcctountBySnsOpenId(snsType, oauthUser.getOpenId());
		if(account == null){
			
			UserEntity accountEntity = null;
			//先按unionId查找是否有已存在的用户
			if(StringUtils.isNotBlank(oauthUser.getUnionId())){
				List<SnsAccounyBindingEntity> sameAccounyBinds = snsAccounyBindingMapper.findByUnionId(oauthUser.getUnionId());
				if(sameAccounyBinds != null && sameAccounyBinds.size() > 0){
					accountEntity = userMapper.selectByPrimaryKey(sameAccounyBinds.get(0).getUserId());
					if(accountEntity == null){
						throw new JeesuiteBaseException(501, String.format("该账号绑定用户异常，UserId:%s", sameAccounyBinds.get(0).getUserId()));
					}
				}
			}
			
			if(accountEntity == null){
				if(bindParam == null){					
					accountEntity = new UserEntity();
					accountEntity.setAvatar(oauthUser.getAvatar());
					accountEntity.setNickname(oauthUser.getNickname());
					accountEntity.setGender(oauthUser.getGender());
				}else{
					accountEntity = BeanCopyUtils.copy(bindParam, UserEntity.class);
				}
				accountEntity.setSourceAppId(bindParam.getAppId());
				accountEntity.setRegIp(bindParam.getIpAddr());
				accountEntity.setRegAt(bindParam.getTime());
				if(StringUtils.isNotBlank(accountEntity.getPassword())){
					accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
				}
				userMapper.insertSelective(accountEntity);
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
		
		UserEntity accountEntity = userMapper.selectByPrimaryKey(userInfo.getId());
		if(accountEntity == null)throw new JeesuiteBaseException(4001, "账号不存在");
		
		UserEntity existAccount = null;
		if(StringUtils.isNotBlank(userInfo.getMobile()) && !userInfo.getMobile().equals(accountEntity.getMobile())){
			existAccount = userMapper.findByMobile(userInfo.getMobile());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该手机号码已注册");
			}
			accountEntity.setMobile(userInfo.getMobile());
		}
		if(StringUtils.isNotBlank(userInfo.getEmail()) && !userInfo.getEmail().equals(accountEntity.getEmail())){
			existAccount = userMapper.findByLoginName(userInfo.getEmail());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
			accountEntity.setEmail(userInfo.getEmail());
		}
		if(StringUtils.isNotBlank(userInfo.getPassword())){
			accountEntity.setPassword(cryptPassword(userInfo.getPassword(), accountEntity.getRegAt()));
		}
		if(StringUtils.isNotBlank(userInfo.getAvatar())){
			accountEntity.setAvatar(userInfo.getAvatar());
		}
        if(StringUtils.isNotBlank(userInfo.getGender())){
        	accountEntity.setGender(userInfo.getGender());
		}
        if(StringUtils.isNotBlank(userInfo.getNickname())){
        	accountEntity.setNickname(userInfo.getNickname());
		}
        if(StringUtils.isNotBlank(userInfo.getRealname())){
        	accountEntity.setRealname(userInfo.getRealname());
		} 
        if(userInfo.getAge() != null){
        	accountEntity.setAge(userInfo.getAge());
		}
        if(userInfo.getBirthday() != null){
        	accountEntity.setBirthday(userInfo.getBirthday());
        }
		accountEntity.setUpdatedAt(new Date());
		
		userMapper.updateByPrimaryKeySelective(accountEntity);
	}


	private static String cryptPassword(String password,Date regAt){
		String salt = String.valueOf(regAt.getTime() / 1000);
		return SecurityCryptUtils.cryptPassword(password, salt);
	}
}
