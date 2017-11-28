package com.jeesuite.passport.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.CustomBaseMapper;
import com.jeesuite.passport.dao.entity.SnsAccounyBindingEntity;

public interface SnsAccounyBindingEntityMapper extends CustomBaseMapper<SnsAccounyBindingEntity> {
	
	@Cache
	SnsAccounyBindingEntity findBySnsOpenId(@Param("snsType") String snsType,@Param("openId") String openId);
	
	@Cache
	SnsAccounyBindingEntity findByWxUnionIdAndOpenId(@Param("unionId") String unionId,@Param("openId") String openId);
	
	@Cache
	List<SnsAccounyBindingEntity> findByUnionId(@Param("unionId") String unionId);
	
	@Cache
	String findWxUnionId(@Param("userId") int userId);
}