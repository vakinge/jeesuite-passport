package com.jeesuite.passport.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Update;

import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.CustomBaseMapper;
import com.jeesuite.passport.dao.entity.SnsAccountBindingEntity;

public interface SnsAccountBindingEntityMapper extends CustomBaseMapper<SnsAccountBindingEntity> {
	
	@Cache
	SnsAccountBindingEntity findBySnsOpenId(@Param("snsType") String snsType,@Param("openId") String openId);
	
	@Cache
	SnsAccountBindingEntity findByWxUnionIdAndOpenId(@Param("unionId") String unionId,@Param("openId") String openId);
	
	@Cache
	List<SnsAccountBindingEntity> findByUnionId(@Param("unionId") String unionId);
	
	@Cache
	String findWxUnionId(@Param("userId") int userId);
	
	@Update("UPDATE sns_account_binding SET enabled=0 WHERE user_id=#{userId} AND sns_type=#{snsType} AND sub_sns_type IS NULL")
	@ResultType(Integer.class)
	Integer unbindSnsAccount(@Param("userId") int userId,@Param("snsType") String snsType);
}