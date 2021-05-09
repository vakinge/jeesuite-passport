package com.jeesuite.passport.dao.entity;

import com.jeesuite.mybatis.core.BaseEntity;
import java.util.Date;
import javax.persistence.*;

@Table(name = "open_account_binding")
public class OpenAccountBindingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "open_type")
    private String openType;

    @Column(name = "sub_type")
    private String subType;

    @Column(name = "union_id")
    private String unionId;

    @Column(name = "open_id")
    private String openId;

    /**
     * 用户来源（业务系统）
     */
    @Column(name = "source_client_id")
    private String sourceClientId;

    private Boolean enabled;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

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
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
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

    

    public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
     * @return union_id
     */
    public String getUnionId() {
        return unionId;
    }

    /**
     * @param unionId
     */
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    /**
     * @return open_id
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * @param openId
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取用户来源（业务系统）
     *
     * @return source_client_id - 用户来源（业务系统）
     */
    public String getSourceClientId() {
        return sourceClientId;
    }

    /**
     * 设置用户来源（业务系统）
     *
     * @param sourceClientId 用户来源（业务系统）
     */
    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
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
     * @return updated_at
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}