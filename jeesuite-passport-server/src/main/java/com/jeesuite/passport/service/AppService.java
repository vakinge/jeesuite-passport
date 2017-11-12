/**
 * 
 */
package com.jeesuite.passport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeesuite.passport.dao.entity.ClientConfigEntity;
import com.jeesuite.passport.dao.mapper.ClientConfigEntityMapper;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年3月19日
 */
@Service
public class AppService  {

	@Autowired
	private ClientConfigEntityMapper clientConfigMapper;

	public ClientConfigEntity getApp(int id) {
		ClientConfigEntity entity = clientConfigMapper.selectByPrimaryKey(id);
		return entity;
	}


	public ClientConfigEntity findByClientId(String clientId) {
		ClientConfigEntity entity = clientConfigMapper.findByClientId(clientId);
		return entity;
	}

}
