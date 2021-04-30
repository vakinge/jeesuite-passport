/**
 * 
 */
package com.jeesuite.passport.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesuite.common.JeesuiteBaseException;
import com.jeesuite.common.util.BeanUtils;
import com.jeesuite.common.util.FormatValidateUtils;
import com.jeesuite.passport.AppConstants.SnsType;
import com.jeesuite.passport.component.openauth.OauthUser;
import com.jeesuite.passport.component.openauth.connector.WeixinMpConnector;
import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.passport.dao.entity.OpenAccountBindingEntity;
import com.jeesuite.passport.dao.mapper.AccountEntityMapper;
import com.jeesuite.passport.dao.mapper.OpenAccountBindingEntityMapper;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.RequestMetadata;

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
	private OpenAccountBindingEntityMapper openAccounyBindingMapper;


	public AccountEntity findAcctountById(String id) {
		AccountEntity entity = accountMapper.selectByPrimaryKey(id);
		if(entity == null || !entity.getEnabled()) {
			throw new JeesuiteBaseException("账号不存在或已禁用");
		}
		return entity;
	}
	
	public AccountEntity findByWxUnionId(String unionId) {
		AccountEntity entity = accountMapper.findByWxUnionId(unionId);
		return entity;
	}
	
	public AccountEntity findAcctountByLoginName(String loginName) {
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		return entity;
	}

	
	public AccountEntity findAcctountBySnsOpenId(String type,String openId) {
		type = WeixinMpConnector.SNS_TYPE.equals(type) ? SnsType.weixin.name() : type;
		OpenAccountBindingEntity bindingEntity = openAccounyBindingMapper.findByOpenId(type, openId);
		if(bindingEntity != null){
			AccountEntity accountEntity = accountMapper.selectByPrimaryKey(bindingEntity.getAccountId());
			return accountEntity;
		}
		return null;
	}

	@Transactional
	public AccountEntity createUser(AccountEntity account,RequestMetadata metadata){
		AccountEntity accountEntity = null;
		if(FormatValidateUtils.isMobile(account.getMobile())){
			accountEntity = accountMapper.findByMobile(account.getMobile());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4001, "该手机已注册");
			}
		}else{
			throw new JeesuiteBaseException(4003, "手机号不能为空");
		}
		
		if(StringUtils.isNotBlank(account.getEmail())){
			accountEntity = accountMapper.findByLoginName(account.getEmail());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
		}
		
		if(StringUtils.isNotBlank(account.getUsername())){
			accountEntity = accountMapper.findByLoginName(account.getUsername());
			if(accountEntity != null){
				throw new JeesuiteBaseException(4003, "该用户名已注册");
			}
		}
		
		accountEntity = BeanUtils.copy(account, AccountEntity.class);
		accountEntity.setRegAt(metadata.getTime());
		accountEntity.setRegIp(metadata.getIpAddr());
		accountEntity.setSourceAppId(metadata.getAppId());
		if(StringUtils.isNotBlank(accountEntity.getPassword())){
			accountEntity.setPassword(BCrypt.hashpw(accountEntity.getPassword(), BCrypt.gensalt(4)));
		}
		accountMapper.insertSelective(accountEntity);
		
		return accountEntity;
	}
	
	@Transactional
	public AccountEntity createUserByOauthInfo(OauthUser oauthUser,AccountBindParam bindParam){

		String openType = null;String appType = null;
		if(oauthUser.getOpenType().contains(":")){
			openType = StringUtils.split(oauthUser.getOpenType(), ":")[0];
			appType = StringUtils.split(oauthUser.getOpenType(), ":")[1];
		}else{
			openType = oauthUser.getOpenType();
		}
		AccountEntity account = findAcctountBySnsOpenId(openType, oauthUser.getOpenId());
		if(account == null){
			
			AccountEntity accountEntity = null;
			//先按unionId查找是否有已存在的用户
			if(StringUtils.isNotBlank(oauthUser.getUnionId())){
				List<OpenAccountBindingEntity> sameAccounyBinds = openAccounyBindingMapper.findByUnionId(openType,oauthUser.getUnionId());
				if(sameAccounyBinds != null && sameAccounyBinds.size() > 0){
					accountEntity = accountMapper.selectByPrimaryKey(sameAccounyBinds.get(0).getAccountId());
					if(accountEntity == null){
						throw new JeesuiteBaseException(501, String.format("该账号绑定用户异常，UserId:%s", sameAccounyBinds.get(0).getAccountId()));
					}
				}
			}
			
			if(accountEntity == null){
				if(bindParam == null){					
					accountEntity = new AccountEntity();
					accountEntity.setAvatar(oauthUser.getAvatar());
					accountEntity.setNickname(oauthUser.getNickname());
					accountEntity.setGender(oauthUser.getGender());
				}else{
					accountEntity = BeanUtils.copy(bindParam, AccountEntity.class);
				}
				accountEntity.setSourceAppId(bindParam.getAppId());
				accountEntity.setRegIp(bindParam.getIpAddr());
				accountEntity.setRegAt(bindParam.getTime());
				if(StringUtils.isNotBlank(accountEntity.getPassword())){
					accountEntity.setPassword(BCrypt.hashpw(accountEntity.getPassword(), BCrypt.gensalt(4)));
				}
				accountMapper.insertSelective(accountEntity);
			}
			//
			OpenAccountBindingEntity bindingEntity = new OpenAccountBindingEntity();
			bindingEntity.setAccountId(accountEntity.getId());
			bindingEntity.setOpenType(openType);
			bindingEntity.setAppType(appType);
			bindingEntity.setOpenId(oauthUser.getOpenId());
			bindingEntity.setUnionId(oauthUser.getUnionId());
			bindingEntity.setEnabled(true);
			bindingEntity.setCreatedAt(bindParam.getTime());
			openAccounyBindingMapper.insertSelective(bindingEntity);
		}
		return account;
	}
	
	@Transactional
	public void updateAccount(AccountEntity account){
		
		AccountEntity accountEntity = accountMapper.selectByPrimaryKey(account.getId());
		if(accountEntity == null)throw new JeesuiteBaseException(4001, "账号不存在");
		
		AccountEntity existAccount = null;
		if(StringUtils.isNotBlank(account.getMobile()) && !account.getMobile().equals(accountEntity.getMobile())){
			existAccount = accountMapper.findByMobile(account.getMobile());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该手机号码已注册");
			}
			accountEntity.setMobile(account.getMobile());
		}
		if(StringUtils.isNotBlank(account.getEmail()) && !account.getEmail().equals(accountEntity.getEmail())){
			existAccount = accountMapper.findByLoginName(account.getEmail());
			if(existAccount != null){
				throw new JeesuiteBaseException(4003, "该邮箱已注册");
			}
			accountEntity.setEmail(account.getEmail());
		}
		if(StringUtils.isNotBlank(account.getPassword())){
			accountEntity.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt(4)));
		}
		if(StringUtils.isNotBlank(account.getAvatar())){
			accountEntity.setAvatar(account.getAvatar());
		}
        if(StringUtils.isNotBlank(account.getGender())){
        	accountEntity.setGender(account.getGender());
		}
        if(StringUtils.isNotBlank(account.getNickname())){
        	accountEntity.setNickname(account.getNickname());
		}
        if(StringUtils.isNotBlank(account.getRealname())){
        	accountEntity.setRealname(account.getRealname());
		} 
        if(account.getAge() != null){
        	accountEntity.setAge(account.getAge());
		}
        if(account.getBirthday() != null){
        	accountEntity.setBirthday(account.getBirthday());
        }
		accountEntity.setUpdatedAt(new Date());
		
		accountMapper.updateByPrimaryKeySelective(accountEntity);
	}

	public void addSnsAccountBind(String accountId,OauthUser oauthUser){
		OpenAccountBindingEntity bindingEntity = openAccounyBindingMapper.findByOpenId(oauthUser.getOpenType(), oauthUser.getOpenId());
		if(bindingEntity != null){
			if(bindingEntity.getAccountId().equals(accountId)){
				if(!bindingEntity.getEnabled()){
					bindingEntity.setEnabled(true);
					bindingEntity.setUpdatedAt(new Date());
					openAccounyBindingMapper.updateByPrimaryKeySelective(bindingEntity);
				}
				return;
			}
			throw new JeesuiteBaseException(4005, "该账号已经绑定其他账号");
		}
		
		bindingEntity = new OpenAccountBindingEntity();
		bindingEntity.setAccountId(accountId);
		bindingEntity.setOpenType(oauthUser.getOpenType());
		bindingEntity.setOpenId(oauthUser.getOpenId());
		bindingEntity.setUnionId(oauthUser.getUnionId());
		bindingEntity.setEnabled(true);
		bindingEntity.setCreatedAt(new Date());
		openAccounyBindingMapper.insertSelective(bindingEntity);
		
	}
	
    public void cancelSnsAccountBind(int userId,String snsType){
    	openAccounyBindingMapper.unbindSnsAccount(userId, snsType);
	}
}
