package com.jeesuite.passport.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jeesuite.common.util.BeanUtils;
import com.jeesuite.mybatis.core.BaseEntity;
import com.jeesuite.passport.dto.AuthUserDetails;

@Table(name = "user_principal")
public class UserPrincipalEntity extends BaseEntity {
	
	@Id
    private String id;

    private String email;

    private String mobile;

    private String realname;
    
    private String nickname;

    private String avatar;

    private Integer age;

    private String gender;

    private Date birthday;

    /**
     * 身份证件类型
     */
    @Column(name = "id_type")
    private Integer idType;

    /**
     * 身份证件号码
     */
    @Column(name = "id_number")
    private String idNumber;

    /**
     * 员工id
     */
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 部门id
     */
    @Column(name = "department_id")
    private String departmentId;

    /**
     * 部门id
     */
    @Column(name = "department_name")
    private String departmentName;

    /**
     * 职位名称
     */
    @Column(name = "post_name")
    private String postName;
    
    /**
     * 验证状态(手机、邮箱、身份证bitmap)
     */
    @Column(name = "verify_status")
    private Integer verifyStatus;

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
    
    @Transient
    private AccountEntity account;

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
     * @return realname
     */
    public String getRealname() {
        return realname;
    }

    /**
     * @param realname
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取身份证件类型
     *
     * @return id_type - 身份证件类型
     */
    public Integer getIdType() {
        return idType;
    }

    /**
     * 设置身份证件类型
     *
     * @param idType 身份证件类型
     */
    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    /**
     * 获取身份证件号码
     *
     * @return id_number - 身份证件号码
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * 设置身份证件号码
     *
     * @param idNumber 身份证件号码
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * 获取员工id
     *
     * @return employee_id - 员工id
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * 设置员工id
     *
     * @param employeeId 员工id
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * 获取部门id
     *
     * @return department_id - 部门id
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门id
     *
     * @param departmentId 部门id
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取部门id
     *
     * @return department_name - 部门id
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * 设置部门id
     *
     * @param departmentName 部门id
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * 获取职位名称
     *
     * @return post_name - 职位名称
     */
    public String getPostName() {
        return postName;
    }

    /**
     * 设置职位名称
     *
     * @param postName 职位名称
     */
    public void setPostName(String postName) {
        this.postName = postName;
    }
    

    public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
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
    
    
    public AccountEntity getAccount() {
		return account;
	}

	public void setAccount(AccountEntity account) {
		this.account = account;
	}

	public AuthUserDetails toAuthUser() {
    	AuthUserDetails authUser = BeanUtils.copy(this, AuthUserDetails.class);
    	if(this.account != null) {
    		authUser.setUsername(account.getName());
    	}
        return authUser;
    }
}