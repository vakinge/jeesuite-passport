package com.jeesuite.passport.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.jeesuite.mybatis.core.BaseMapper;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.passport.dao.entity.OpenAccountBindingEntity;

public interface OpenAccountBindingEntityMapper extends BaseMapper<OpenAccountBindingEntity, Integer> {
	
	@Cache
	@Select("SELECT * FROM open_account_binding WHERE open_type = #{openType} and open_id = #{openId}")
	@ResultMap("BaseResultMap")
	OpenAccountBindingEntity findByOpenId(@Param("openType") String openType,@Param("openId") String openId);
	
	@Cache
	@Select("SELECT * FROM open_account_binding WHERE open_type = #{openType} and union_id = #{unionId}")
	@ResultMap("BaseResultMap")
	List<OpenAccountBindingEntity> findByUnionId(@Param("openType") String openType,@Param("union_id") String unionId);
	
	@Update("UPDATE open_account_binding SET enabled=0 WHERE user_id=#{userId} AND open_type=#{openType} AND app_type IS NULL")
	@ResultType(Integer.class)
	Integer unbindSnsAccount(@Param("userId") int userId,@Param("openType") String openType);
}