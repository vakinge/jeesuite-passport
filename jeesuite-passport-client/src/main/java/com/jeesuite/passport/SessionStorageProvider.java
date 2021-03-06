package com.jeesuite.passport;

import com.jeesuite.common.model.AuthUser;

/**
 * session存储
 * 
 * <br>
 * Class Name   : SessionStorageProvider
 *
 * @author jiangwei
 * @version 1.0.0
 * @date Apr 11, 2021
 */
public interface SessionStorageProvider {

	void set(String sessionId,Object o);
	
	void remove(String sessionId);
	
	AuthUser get(String sessionId);
}
