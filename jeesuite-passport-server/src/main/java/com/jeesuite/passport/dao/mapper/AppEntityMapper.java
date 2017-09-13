package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.passport.dao.entity.AppEntity;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;

import tk.mybatis.mapper.common.BaseMapper;

public interface AppEntityMapper extends BaseMapper<AppEntity> {
	
	@Cache
	@Select("select * from app where client_id = #{clientId} ")
	@ResultMap("BaseResultMap")
	AppEntity findByClientId(String clientId);
}