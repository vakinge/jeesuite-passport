package com.jeesuite.passport.component.saml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeesuite.common.util.ResourceUtils;

/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @website <a href="http://www.jeesuite.com">vakin</a>
 * @date 2018年5月22日
 */
public class SAMLPrincipal {

	public static String IDP_ENTITY_ID = ResourceUtils.getProperty("saml.idp.entity-id");

	private String serviceProviderEntityID;
	private String requestID;
	private String assertionConsumerServiceURL;
	private String relayState;
	private String nameID;
	private String nameIDType = SAMLBuilder.NAMEID_FORMAT;
	private final Map<String,List<String>> attributes = new HashMap<>();


	public String getServiceProviderEntityID() {
		return serviceProviderEntityID;
	}

	public void setServiceProviderEntityID(String serviceProviderEntityID) {
		this.serviceProviderEntityID = serviceProviderEntityID;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public String getAssertionConsumerServiceURL() {
		return assertionConsumerServiceURL;
	}

	public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
		this.assertionConsumerServiceURL = assertionConsumerServiceURL;
	}

	public String getRelayState() {
		return relayState;
	}

	public void setRelayState(String relayState) {
		this.relayState = relayState;
	}

	public String getNameID() {
		return nameID;
	}

	public void setNameID(String nameID) {
		this.nameID = nameID;
	}

	public String getNameIDType() {
		return nameIDType;
	}

	public void setNameIDType(String nameIDType) {
		this.nameIDType = nameIDType;
	}

	public Map<String, List<String>> getAttributes() {
		return attributes;
	}

	
}
