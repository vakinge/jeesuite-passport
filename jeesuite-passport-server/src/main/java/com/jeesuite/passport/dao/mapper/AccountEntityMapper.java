package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.mybatis.core.BaseMapper;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.entity.AccountEntity;

public interface AccountEntityMapper extends BaseMapper<AccountEntity,String> {
	
	@Cache
	@Select("select * from account where name = #{name} or email = #{email} or mobile = #{mobile} limit 1")
	@ResultMap("BaseResultMap")
	AccountEntity findByLoginName(String name);
	
	@Cache
	@Select("select * from account where mobile = #{mobile} limit 1")
	@ResultMap("BaseResultMap")
	AccountEntity findByMobile(@Param("mobile") String mobile);
	
	@Select("select * from account where user_id = (select user_id from sns_account_binding where union_id = #{unionId}  and enabled = 1 limit 1) limit 1")
	@ResultMap("BaseResultMap")
	AccountEntity findByWxUnionId(@Param("unionId") String unionId);
}