package com.jeesuite.passport.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.jeesuite.mybatis.core.BaseEntity;

@Table(name = "client_config")
public class ClientConfigEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "is_inner_app")
    private Boolean isInnerApp;
    
    private String domains;

    @Column(name = "callback_uri")
    private String callbackUri;

    private Boolean enabled;

    private Boolean deleted;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return client_id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return client_secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return is_inner_app
     */
    public Boolean getIsInnerApp() {
        return isInnerApp;
    }

    /**
     * @param isInnerApp
     */
    public void setIsInnerApp(Boolean isInnerApp) {
        this.isInnerApp = isInnerApp;
    }
    

    public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

	/**
     * @return callback_uri
     */
    public String getCallbackUri() {
        return callbackUri;
    }

    /**
     * @param callbackUri
     */
    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }

    /**
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return created_at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return created_by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取更新时间
     *
     * @return updated_at - 更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return updated_by
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}