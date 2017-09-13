/**
 * 
 */
package com.jeesuite.passport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeesuite.passport.dao.entity.AppEntity;
import com.jeesuite.passport.dao.mapper.AppEntityMapper;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Service
public class AppService  {

	@Autowired
	private AppEntityMapper appMapper;

	public AppEntity getApp(int id) {
		AppEntity entity = appMapper.selectByPrimaryKey(id);
		return entity;
	}


	public AppEntity findByClientId(String clientId) {
		AppEntity entity = appMapper.findByClientId(clientId);
		return entity;
	}

}
