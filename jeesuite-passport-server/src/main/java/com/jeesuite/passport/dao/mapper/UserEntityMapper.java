package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.CustomBaseMapper;
import com.jeesuite.passport.dao.entity.UserEntity;

public interface UserEntityMapper extends CustomBaseMapper<UserEntity> {
	
	@Cache
	@Select("select * from users where username = #{name} or email = #{email} or mobile = #{mobile} limit 1")
	@ResultMap("BaseResultMap")
	UserEntity findByLoginName(String name);
	
	@Cache
	@Select("select * from users where mobile = #{mobile} limit 1")
	@ResultMap("BaseResultMap")
	UserEntity findByMobile(@Param("mobile") String mobile);
	
	@Select("select * from users where id = (select user_id from sns_account_binding where union_id = #{unionId}  and enabled = 1 limit 1) limit 1")
	@ResultMap("BaseResultMap")
	UserEntity findByWxUnionId(@Param("unionId") String unionId);
}