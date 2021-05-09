package com.jeesuite.passport.dao.entity;

import com.jeesuite.mybatis.core.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "open_oauth2_config")
public class OpenOauth2ConfigEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "open_type")
	private String openType;

	@Column(name = "sub_type")
	private String subType;

	@Column(name = "app_id")
	private String appId;

	@Column(name = "app_secret")
	private String appSecret;

	@Column(name = "bind_client_ids")
	private String bindClientIds;

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
	 * @return open_type
	 */
	public String getOpenType() {
		return openType;
	}

	/**
	 * @param openType
	 */
	public void setOpenType(String openType) {
		this.openType = openType;
	}

	/**
	 * @return sub_type
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @param subType
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * @return app_id
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return app_secret
	 */
	public String getAppSecret() {
		return appSecret;
	}

	/**
	 * @param appSecret
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	/**
	 * @return bind_client_ids
	 */
	public String getBindClientIds() {
		return bindClientIds;
	}

	/**
	 * @param bindClientIds
	 */
	public void setBindClientIds(String bindClientIds) {
		this.bindClientIds = bindClientIds;
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
	 * @param updatedAt
	 *            更新时间
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