/*
 Navicat Premium Data Transfer

 Source Server         : local-mysql001
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : ruoyi-vue-pro

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 03/05/2021 12:02:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dors_room
-- ----------------------------
DROP TABLE IF EXISTS `dors_room`;
CREATE TABLE IF NOT EXISTS `dors_room` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `type` VARCHAR(64) NOT NULL COMMENT '类型（手术室/会议室）',
  `name` VARCHAR(255) NOT NULL COMMENT '房间名称',
  `creator` VARCHAR(64) NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remarks` VARCHAR(512) NULL COMMENT '备注',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
COMMENT = '房间'

-- ----------------------------
-- Records of dors_room
-- ----------------------------
BEGIN;
COMMIT;


-- ----------------------------
-- Table structure for dors_device
-- ----------------------------
DROP TABLE IF EXISTS `dors_device`;
CREATE TABLE IF NOT EXISTS `dors_device` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `room` INT NULL COMMENT '所属房间',
  `type` VARCHAR(32) NOT NULL COMMENT '设备类型（PAD/ENCODER/DECODER）',
  `serial_no` VARCHAR(64) NULL COMMENT '设备序列号',
  `device_mac` VARCHAR(64) NULL COMMENT '设备MAC地址',
  `app_version` VARCHAR(32) NULL COMMENT '应用程序版本',
  `sdk_version` VARCHAR(32) NULL COMMENT 'SDK版本',
  `sys_version` VARCHAR(32) NULL COMMENT '系统版本',
  `last_online_ip` VARCHAR(64) NULL COMMENT '最近一次上线IP地址',
  `last_online_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近一次上线时间',
  `creator` VARCHAR(64) NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remarks` VARCHAR(512) NULL COMMENT '备注',
  INDEX `fk_dors_device_dors_room_idx` (`room` ASC) VISIBLE,
  PRIMARY KEY (`id`),
  INDEX `room_device_type` (`room` ASC) VISIBLE,
  UNIQUE INDEX `device_mac_UNIQUE` (`device_mac` ASC) VISIBLE,
  CONSTRAINT `fk_dors_device_dors_room`
    FOREIGN KEY (`room`)
    REFERENCES `dors_room` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '设备'

-- ----------------------------
-- Records of dors_device
-- ----------------------------
BEGIN;
COMMIT;


-- ----------------------------
-- Table structure for dors_channel
-- ----------------------------
DROP TABLE IF EXISTS `dors_channel`;
CREATE TABLE IF NOT EXISTS `dors_channel` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  `device` INT NOT NULL COMMENT '所属设备',
  `channel_id` INT NOT NULL COMMENT '频道ID',
  `type` VARCHAR(32) NOT NULL COMMENT '频道类型（vi/usb/net/ndi/file/mix)',
  `name` VARCHAR(64) NOT NULL COMMENT '频道名称',
  `json_info` VARCHAR(5120) NULL COMMENT '频道JSON数据信息',
  `creator` VARCHAR(64) NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remarks` VARCHAR(512) NULL COMMENT '备注',
  INDEX `fk_dors_channel_dors_device1_idx` (`device` ASC) VISIBLE,
  PRIMARY KEY (`id`),
  INDEX `device_channel_type` (`device` ASC, `type` ASC) VISIBLE,
  INDEX `device_channel_id` (`device` ASC, `channel_id` ASC) VISIBLE,
  CONSTRAINT `fk_dors_channel_dors_device1`
    FOREIGN KEY (`device`)
    REFERENCES `dors_device` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '频道'

-- ----------------------------
-- Records of dors_channel
-- ----------------------------
BEGIN;
COMMIT;


-- ----------------------------
-- Table structure for dors_version
-- ----------------------------
DROP TABLE IF EXISTS `dors_version`;
CREATE TABLE IF NOT EXISTS `dors_version` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '编号（自增）',
  `name` VARCHAR(255) NULL COMMENT '名称',
  `package_name` VARCHAR(512) NULL COMMENT '包名',
  `version_num` INT NOT NULL DEFAULT 0 COMMENT '版本号',
  `version_code` VARCHAR(64) NULL COMMENT '版本代码',
  `force_update` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否强制升级',
  `main_activity` VARCHAR(128) NULL COMMENT '主入口Activity',
  `url` VARCHAR(512) NULL COMMENT '下载地址',
  `checksum` VARCHAR(256) NULL COMMENT '校验码',
  `size` BIGINT NULL COMMENT '大小',
  `author` VARCHAR(128) NULL COMMENT '开发者',
  `author_contact` VARCHAR(128) NULL COMMENT '开发者联系方式',
  `change_log` VARCHAR(1024) NULL COMMENT '更新日志',
  `description` VARCHAR(1024) NULL COMMENT '描述',
  `hits` INT NULL DEFAULT 0 COMMENT '浏览次数',
  `dl_count` INT NULL DEFAULT 0 COMMENT '下载次数',
  `creator` VARCHAR(64) NULL COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) NULL COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `remarks` VARCHAR(512) NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  INDEX `lastest_version` (`package_name` ASC, `version_num` DESC) VISIBLE)
ENGINE = InnoDB
COMMENT = '应用版本'

-- ----------------------------
-- Records of dors_version
-- ----------------------------
BEGIN;
COMMIT;


