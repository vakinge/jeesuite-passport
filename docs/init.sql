SET NAMES utf8;

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` varchar(32)  NOT NULL,
  `user_id` varchar(32)  NOT NULL, 
  `name` varchar(32) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `mobile` char(11) DEFAULT NULL,
  `password` char(128) DEFAULT NULL,
  `source_client_id` VARCHAR(32) DEFAULT NULL COMMENT '用户来源（业务系统）',
  `enabled` bit(1) DEFAULT b'1',
  `deleted` bit(1) DEFAULT b'0',
  `reg_ip` varchar(15) DEFAULT NULL COMMENT '注册ip',
  `reg_at` datetime DEFAULT NULL,
  `last_login_ip` varchar(15) DEFAULT NULL COMMENT '最后登录ip',
  `last_login_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
   UNIQUE INDEX `name_uq_index` (`name`),
   UNIQUE INDEX `email_uq_index` (`email`),
   UNIQUE INDEX `mobile_uq_index` (`mobile`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户账号表';


DROP TABLE IF EXISTS `user_principal`;
CREATE TABLE `user_principal` (
  `id` varchar(32)  NOT NULL,
  `email` varchar(32) DEFAULT NULL,
  `mobile` char(11) DEFAULT NULL,
  `realname` varchar(32) DEFAULT NULL,
  `avatar` varchar(200) DEFAULT NULL,
  `age` int(3)  DEFAULT 0,
  `gender` ENUM('male', 'female') DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `id_type` int(1) DEFAULT 1 COMMENT '身份证件类型',
  `id_number` varchar(20) DEFAULT NULL COMMENT '身份证件号码',
  `employee_id` varchar(64) DEFAULT NULL COMMENT '员工id',
  `department_id` varchar(100) DEFAULT NULL COMMENT '部门id',
  `department_name` varchar(100) DEFAULT NULL COMMENT '部门id',
  `post_name` varchar(100) DEFAULT NULL COMMENT '职位名称',
  `nickname` varchar(32) DEFAULT NULL,
  `verify_status` int(3) DEFAULT b'0' COMMENT '验证状态(手机、邮箱、身份证bitmap)',
  `enabled` bit(1) DEFAULT b'1',
  `deleted` bit(1) DEFAULT b'0',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(32) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
   UNIQUE INDEX `email_uq_index` (`email`),
   UNIQUE INDEX `mobile_uq_index` (`mobile`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户主体表';

DROP TABLE IF EXISTS `user_system_scopes`;
CREATE TABLE `user_system_scopes` (
  `user_id` varchar(32)  NOT NULL ,
  `system_id` varchar(32) NOT NULL ,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`system_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='用户系统范围';


DROP TABLE IF EXISTS `open_account_binding`;
CREATE TABLE `open_account_binding` (
  `id` int(10)  NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32)  NOT NULL, 
  `open_type` ENUM('wechat', 'weibo','qq','taobao','alipay') NOT NULL,
  `sub_type` ENUM('gzh','xcx','oauth') DEFAULT 'oauth',
  `union_id` varchar(32) DEFAULT NULL,
  `open_id` varchar(32) DEFAULT NULL,
  `source_client_id` VARCHAR(32) DEFAULT NULL COMMENT '用户来源（业务系统）',
  `enabled` bit(1) DEFAULT b'1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
   UNIQUE INDEX `uo_uq_index` (`user_id`,`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='第三方账号绑定';


DROP TABLE IF EXISTS `open_oauth_config`;
CREATE TABLE `open_oauth_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `open_type` ENUM('wechat', 'weibo','qq','taobao','alipay') NOT NULL,
  `sub_type` ENUM('gzh','xcx','oauth') DEFAULT 'oauth',
  `app_id` varchar(32) DEFAULT NULL,
  `app_secret` varchar(64) DEFAULT NULL,
  `bind_client_ids` varchar(200) DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1',
  `deleted` bit(1) DEFAULT b'0',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(32) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_config`;
CREATE TABLE `client_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `client_id` varchar(32) DEFAULT NULL,
  `client_secret` varchar(64) DEFAULT NULL,
  `is_inner_app` bit(1) DEFAULT b'0',
  `domains` varchar(200) DEFAULT NULL,
  `callback_uri` varchar(100) DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1',
  `deleted` bit(1) DEFAULT b'0',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(32) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `updated_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `client_id_uq_index` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
