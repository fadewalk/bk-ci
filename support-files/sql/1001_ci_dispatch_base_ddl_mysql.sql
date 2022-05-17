USE devops_ci_dispatch_base;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for T_BASE_BUILD
-- ----------------------------
CREATE TABLE IF NOT EXISTS `T_BASE_BUILD` (
  `DISPATCH_TYPE` varchar(32) NOT NULL COMMENT 'DISPATCH类型',
  `PIPELINE_ID` varchar(34) CHARACTER SET utf8mb4 NOT NULL,
  `VM_SEQ_ID` varchar(34) CHARACTER SET utf8mb4 NOT NULL,
  `POOL_NO` int(11) NOT NULL,
  `PROJECT_ID` varchar(64) CHARACTER SET utf8mb4 NOT NULL,
  `BUILDER_NAME` varchar(128) CHARACTER SET utf8mb4 NOT NULL,
  `IMAGES` varchar(1024) CHARACTER SET utf8mb4 NOT NULL,
  `STATUS` int(11) NOT NULL,
  `CREATED_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `USER_ID` varchar(34) CHARACTER SET utf8mb4 NOT NULL,
  `DEBUG_STATUS` bit(1) DEFAULT b'0' COMMENT '是否处于debug状态',
  `DEBUG_TIME` timestamp NULL DEFAULT NULL COMMENT 'debug时间',
  `CPU` float COMMENT 'CPU',
  `MEMORY` varchar(64) COMMENT '内存 单位M',
  `DISK` varchar(64) COMMENT '磁盘 单位G',
  PRIMARY KEY (`DISPATCH_TYPE`,`PIPELINE_ID`,`VM_SEQ_ID`,`POOL_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for T_BASE_BUILD_BUILDER_POOL_NO
-- ----------------------------
CREATE TABLE IF NOT EXISTS `T_BASE_BUILD_BUILDER_POOL_NO` (
  `DISPATCH_TYPE` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT 'DISPATCH类型',
  `BUILD_ID` varchar(64) NOT NULL COMMENT '构建ID',
  `VM_SEQ_ID` varchar(64) NOT NULL COMMENT 'VmSeqID',
  `BUILDER_NAME` varchar(128) DEFAULT NULL COMMENT '构建机名称',
  `POOL_NO` varchar(128) DEFAULT NULL COMMENT '构建机池编号',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `EXECUTE_COUNT` int(11) DEFAULT '1' COMMENT '流水线重试次数',
  PRIMARY KEY (`DISPATCH_TYPE`,`BUILD_ID`,`VM_SEQ_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='buildId和builderName,poolNo的映射关系';

-- ----------------------------
-- Table structure for T_BASE_BUILD_HIS
-- ----------------------------
CREATE TABLE IF NOT EXISTS `T_BASE_BUILD_HIS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DISPATCH_TYPE` varchar(32) NOT NULL COMMENT 'DISPATCH类型',
  `PIPELINE_ID` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'pipeline id',
  `BUIDLD_ID` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'build id',
  `VM_SEQ_ID` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'vm seq id',
  `BUILDER_NAME` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '构建机名称',
  `GMT_CREATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `SECRET_KEY` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '构建密钥',
  `POOL_NO` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '并发构建池',
  `CPU` floatCOMMENT 'CPU',
  `MEMORY` varchar(64) COMMENT '内存 单位M',
  `DISK` varchar(64) COMMENT '磁盘 单位G',
  `EXECUTE_COUNT` int(11) DEFAULT '1' COMMENT '流水线重试次数',
  `PROJECT_ID` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '蓝盾项目ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for T_BASE_PERFORMANCE_CONFIG
-- ----------------------------
CREATE TABLE IF NOT EXISTS `T_BASE_PERFORMANCE_CONFIG` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `DISPATCH_TYPE` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT 'DISPATCH类型',
  `PROJECT_ID` varchar(64) NOT NULL COMMENT '蓝盾项目ID',
  `OPTION_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '基础配置ID',
  `GMT_CREATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `GMT_MODIFIED` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BASE配额表';

-- ----------------------------
-- Table structure for T_BASE_PERFORMANCE_OPTIONS
-- ----------------------------
CREATE TABLE IF NOT EXISTS `T_BASE_PERFORMANCE_OPTIONS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `DISPATCH_TYPE` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT 'DISPATCH类型',
  `CPU` float DEFAULT '1' COMMENT 'CPU',
  `MEMORY` varchar(64) COMMENT '内存 单位M',
  `DISK` varchar(64) COMMENT '磁盘 单位G',
  `DESCRIPTION` varchar(128) NOT NULL DEFAULT '' COMMENT '描述',
  `GMT_CREATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `GMT_MODIFIED` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BASE基础配额表';

SET FOREIGN_KEY_CHECKS = 1;
