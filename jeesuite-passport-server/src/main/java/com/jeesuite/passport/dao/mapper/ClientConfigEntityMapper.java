package com.jeesuite.passport.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.CustomBaseMapper;
import com.jeesuite.passport.dao.entity.ClientConfigEntity;

public interface ClientConfigEntityMapper extends CustomBaseMapper<ClientConfigEntity> {
	
	@Cache
	@Select("select * from client_config where client_id = #{clientId} ")
	@ResultMap("BaseResultMap")
	ClientConfigEntity findByClientId(@Param("clientId") String clientId);
}