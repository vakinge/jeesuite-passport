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
import com.jeesuite.passport.dao.mapper.AccountEntityMapper;
import com.jeesuite.passport.dao.mapper.SnsAccounyBindingEntityMapper;
import com.jeesuite.passport.dto.Account;
import com.jeesuite.passport.dto.AccountBindParam;
import com.jeesuite.passport.dto.AccountParam;
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
	private SnsAccounyBindingEntityMapper snsAccounyBindingMapper;


	public Account findAcctountById(int id) {
		AccountEntity entity = accountMapper.selectByPrimaryKey(id);
		return BeanCopyUtils.copy(entity, Account.class);
	}
	
	public Account findByWxUnionId(String unionId) {
		AccountEntity entity = accountMapper.findByWxUnionId(unionId);
		return BeanCopyUtils.copy(entity, Account.class);
	}
	
	public Account findAcctountByLoginName(String loginName) {
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		return BeanCopyUtils.copy(entity, Account.class);
	}
	
	public Account findAcctountBySnsOpenId(String type,String openId) {
		type = WeixinGzhConnector.SNS_TYPE.equals(type) ? SnsType.weixin.name() : type;
		SnsAccounyBindingEntity bindingEntity = snsAccounyBindingMapper.findBySnsOpenId(type, openId);
		if(bindingEntity != null){
			AccountEntity accountEntity = accountMapper.selectByPrimaryKey(bindingEntity.getUserId());
			return BeanCopyUtils.copy(accountEntity, Account.class);
		}
		return null;
	}
	
	public Account checkAndGetAccount(String loginName,String password){
		AccountEntity entity = accountMapper.findByLoginName(loginName);
		if(entity == null || !entity.getPassword().equals(cryptPassword(password, entity.getRegAt()))){
		   return null;
		}
		return BeanCopyUtils.copy(entity, Account.class);
	}
	
	@Transactional
	public Account createAccount(Account account,RequestMetadata metadata){
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
		
		accountEntity = BeanCopyUtils.copy(account, AccountEntity.class);
		accountEntity.setRegAt(metadata.getTime());
		if(StringUtils.isNotBlank(accountEntity.getPassword())){
			accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
		}
		accountMapper.insertSelective(accountEntity);
		
		account.setId(accountEntity.getId());
		return account;
	}
	
	@Transactional
	public Account createAccountByOauthInfo(OauthUser oauthUser,AccountBindParam bindParam){
		
		String snsType = WeixinGzhConnector.SNS_TYPE.equals(oauthUser.getSnsType()) ? SnsType.weixin.name() : oauthUser.getSnsType();
		Account account = findAcctountBySnsOpenId(snsType, oauthUser.getOpenId());
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
				
				if(StringUtils.isBlank(accountEntity.getNickname()))accountEntity.setNickname(oauthUser.getNickname());
				if(StringUtils.isBlank(accountEntity.getAvatar()))accountEntity.setAvatar(oauthUser.getAvatar());
				
				if(StringUtils.isBlank(accountEntity.getPassword())){
					accountEntity.setPassword(cryptPassword(accountEntity.getPassword(), accountEntity.getRegAt()));
				}
				
				accountEntity.setRegAt(bindParam.getTime());
				
				accountMapper.insertSelective(accountEntity);
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
			
			account = BeanCopyUtils.copy(accountEntity, Account.class);
			
		}
		return account;
	}
	
	@Transactional
	public void updateAccount(AccountParam account){
		
		
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
		
		if(StringUtils.isNotBlank(account.getAvatar())){
			accountEntity.setAvatar(account.getAvatar());
		}
		
		if(StringUtils.isNotBlank(account.getNickname())){
			accountEntity.setNickname(account.getNickname());
		}
		
		if(StringUtils.isNotBlank(account.getPassword())){
			accountEntity.setPassword(cryptPassword(account.getPassword(), accountEntity.getRegAt()));
		}
		
		accountEntity.setUpdatedAt(new Date());
		
		accountMapper.updateByPrimaryKeySelective(accountEntity);
	}


	private static String cryptPassword(String password,Date regAt){
		String salt = String.valueOf(regAt.getTime() / 1000);
		return SecurityCryptUtils.cryptPassword(password, salt);
	}
}
