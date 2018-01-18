package com.jeesuite.passport.dao.entity;

import com.jeesuite.mybatis.core.BaseEntity;
import javax.persistence.*;

@Table(name = "resources")
public class ResourceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 父ID，顶级为0
     */
    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "app_id")
    private Integer appId;

    /**
     * 菜单名称
     */
    private String name;


    private String code;

    /**
     * 是否菜单
     */
    @Column(name = "is_menu")
    private Boolean isMenu;

    /**
     * 是否叶节点
     */
    @Column(name = "is_leaf")
    private Boolean isLeaf;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    @Column(name = "order_id")
    private Integer orderId;

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
     * 获取父ID，顶级为0
     *
     * @return parent_id - 父ID，顶级为0
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父ID，顶级为0
     *
     * @param parentId 父ID，顶级为0
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * @return app_id
     */
    public Integer getAppId() {
        return appId;
    }

    /**
     * @param appId
     */
    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    /**
     * 获取菜单名称
     *
     * @return name - 菜单名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置菜单名称
     *
     * @param name 菜单名称
     */
    public void setName(String name) {
        this.name = name;
    }    

    /**
     * 获取菜单图标
     *
     * @return icon - 菜单图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置菜单图标
     *
     * @param icon 菜单图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取排序
     *
     * @return order_id - 排序
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置排序
     *
     * @param orderId 排序
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsMenu() {
		return isMenu;
	}

	public void setIsMenu(Boolean isMenu) {
		this.isMenu = isMenu;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
    
}