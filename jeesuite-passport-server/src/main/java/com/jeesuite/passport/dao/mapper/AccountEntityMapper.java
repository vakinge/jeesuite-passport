package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.passport.dao.entity.AccountEntity;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;

import tk.mybatis.mapper.common.BaseMapper;

public interface AccountEntityMapper extends BaseMapper<AccountEntity> {
	
	@Cache
	@Select("select * from account where username = #{name} or email = #{email} or mobile = #{mobile}")
	@ResultMap("BaseResultMap")
	AccountEntity findByLoginName(String name);
}