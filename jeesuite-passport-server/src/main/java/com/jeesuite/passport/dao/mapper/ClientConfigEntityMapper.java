package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;

import tk.mybatis.mapper.common.BaseMapper;

public interface ClientConfigEntityMapper extends BaseMapper<ClientConfigEntity> {
	
	@Cache
	@Select("select * from client_config where client_id = #{clientId} ")
	@ResultMap("BaseResultMap")
	ClientConfigEntity findByClientId(String clientId);
}