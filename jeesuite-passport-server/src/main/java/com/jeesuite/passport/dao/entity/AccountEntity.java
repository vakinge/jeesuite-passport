package com.jeesuite.passport.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.jeesuite.mybatis.core.BaseEntity;
import com.jeesuite.passport.dto.AuthUserDetails;

@Table(name = "users")
public class AccountEntity extends BaseEntity {
	
	@Id
    private String id;

    @Column(name = "username",updatable=false)
    private String username;
    private String email;
    private String mobile;
    private String password;
    private String realname;
    private String nickname;
    private String avatar;
    private Integer age;
    private String gender;
    private Date birthday;
    @Column(name = "id_type")
    private Integer idType;
    @Column(name = "id_number")
    private String idNumber;
    @Column(name = "verify_status")
    private Integer verifyStatus;
    @Column(name = "source_app_id",updatable=false)
    private String sourceAppId;

    private Boolean enabled;

    private Boolean deleted;

    @Column(name = "updated_at")
    private Date updatedAt;
    
    /**
     * 注册ip
     */
    @Column(name = "reg_ip",updatable = false )
    private String regIp;

    @Column(name = "reg_at",updatable = false )
    private Date regAt;

    /**
     * 最后登录ip
     */
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "last_login_at")
    private Date lastLoginAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getIdType() {
		return idType;
	}

	public void setIdType(Integer idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getSourceAppId() {
		return sourceAppId;
	}

	public void setSourceAppId(String sourceAppId) {
		this.sourceAppId = sourceAppId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getRegIp() {
		return regIp;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
	}

	public Date getRegAt() {
		return regAt;
	}

	public void setRegAt(Date regAt) {
		this.regAt = regAt;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Date getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(Date lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

    public AuthUserDetails toAuthUser() {
    	AuthUserDetails authUser = new AuthUserDetails();
        authUser.setId(this.getId());
        authUser.setName(this.getUsername());
        authUser.setNickname(this.getNickname());
        authUser.setMobile(this.getMobile());
        authUser.setEmail(this.getEmail());
        authUser.setAvatar(this.getAvatar());
        
        return authUser;
    }
    
    public static void main(String[] args) {
		System.out.println(BCrypt.hashpw("123456", BCrypt.gensalt(4)));
	}
}