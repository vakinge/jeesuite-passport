package com.jeesuite.passport.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.jeesuite.mybatis.core.BaseEntity;

@Table(name = "account")
public class AccountEntity extends BaseEntity {
	
	@Id
	private String id;

    @Column(name = "user_id")
    private String userId;

    private String name;

    private String email;

    private String mobile;

    private String password;

    /**
     * 用户来源（业务系统）
     */
    @Column(name = "source_client_id")
    private String sourceClientId;

    private Boolean enabled;

    private Boolean deleted;

    /**
     * 注册ip
     */
    @Column(name = "reg_ip")
    private String regIp;

    @Column(name = "reg_at")
    private Date regAt;

    /**
     * 最后登录ip
     */
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "last_login_at")
    private Date lastLoginAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
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
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 获取注册ip
     *
     * @return reg_ip - 注册ip
     */
    public String getRegIp() {
        return regIp;
    }

    /**
     * 设置注册ip
     *
     * @param regIp 注册ip
     */
    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    /**
     * @return reg_at
     */
    public Date getRegAt() {
        return regAt;
    }

    /**
     * @param regAt
     */
    public void setRegAt(Date regAt) {
        this.regAt = regAt;
    }

    /**
     * 获取最后登录ip
     *
     * @return last_login_ip - 最后登录ip
     */
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    /**
     * 设置最后登录ip
     *
     * @param lastLoginIp 最后登录ip
     */
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    /**
     * @return last_login_at
     */
    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    /**
     * @param lastLoginAt
     */
    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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

    
    public static void main(String[] args) {
		System.out.println(BCrypt.hashpw("123456", BCrypt.gensalt(4)));
	}
}