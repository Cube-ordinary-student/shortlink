# 短链接平台 - 数据库设计文档

---

## 1. 数据库概述

| 项目 | 说明 |
|------|------|
| 数据库类型 | MySQL 8.0+ |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |
| 分库分表 | 采用 ShardingSphere 进行分库分表 |

---

## 2. 核心数据表

### 2.1 用户表 (t_user)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 用户唯一标识 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | - | 用户名 |
| password | VARCHAR(255) | NOT NULL | - | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | NULL | - | 真实姓名 |
| phone | VARCHAR(20) | NULL | - | 手机号码 |
| mail | VARCHAR(100) | NULL | - | 邮箱地址 |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.2 分组表 (t_group)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 分组唯一标识 |
| gid | VARCHAR(32) | NOT NULL, UNIQUE | - | 分组全局唯一标识 |
| username | VARCHAR(50) | NOT NULL | - | 所属用户名 |
| name | VARCHAR(100) | NOT NULL | - | 分组名称 |
| description | VARCHAR(500) | NULL | - | 分组描述 |
| sort_order | INT | NOT NULL | 0 | 排序顺序 |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.3 短链接表 (t_short_link)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 短链接唯一标识 |
| gid | VARCHAR(32) | NOT NULL | - | 所属分组ID |
| short_link_suffix | VARCHAR(12) | NOT NULL, UNIQUE | - | 短链接后缀 |
| origin_url | TEXT | NOT NULL | - | 原始长链接 |
| domain | VARCHAR(100) | NOT NULL | s.lanyue.com | 短链接域名 |
| pv | BIGINT | NOT NULL | 0 | 访问次数 |
| uv | BIGINT | NOT NULL | 0 | 独立访客数 |
| status | TINYINT | NOT NULL | 0 | 状态 |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| expire_time | DATETIME | NULL | - | 过期时间 |

### 2.4 访问统计表 (t_link_access_stats)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 统计记录唯一标识 |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | 短链接后缀 |
| date | DATE | NOT NULL | - | 统计日期 |
| pv | BIGINT | NOT NULL | 0 | 当日访问次数 |
| uv | BIGINT | NOT NULL | 0 | 当日独立访客数 |
| ip_count | BIGINT | NOT NULL | 0 | 当日独立IP数 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

### 2.5 回收站表 (t_short_link_recycle)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 记录唯一标识 |
| short_link_id | BIGINT | NOT NULL | - | 原短链接ID |
| gid | VARCHAR(32) | NOT NULL | - | 所属分组ID |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | 短链接后缀 |
| origin_url | TEXT | NOT NULL | - | 原始长链接 |
| delete_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 删除时间 |
| expire_time | DATETIME | NULL | - | 过期时间 |

---

## 3. 索引设计

### 3.1 用户表索引

| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_username | username | 唯一索引 |
| idx_del_flag | del_flag | 普通索引 |

### 3.2 短链接表索引

| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_short_link_suffix | short_link_suffix | 唯一索引 |
| idx_gid | gid | 普通索引 |
| idx_del_flag | del_flag | 普通索引 |

---

## 4. 逻辑外键关系

> **注意**: 本项目不使用数据库级别的物理外键约束，所有关联关系通过应用程序层面的逻辑外键实现。

| 表名 | 字段 | 关联表 | 关联字段 |
|------|------|--------|----------|
| t_group | username | t_user | username |
| t_short_link | gid | t_group | gid |
| t_link_access_stats | short_link_suffix | t_short_link | short_link_suffix |
| t_short_link_recycle | short_link_id | t_short_link | id |

---

## 5. 数据字典

### 5.1 删除标记 (del_flag)

| 值 | 说明 |
|----|------|
| 0 | 正常状态 |
| 1 | 已删除（回收站） |
| 2 | 彻底删除 |

### 5.2 短链接状态 (status)

| 值 | 说明 |
|----|------|
| 0 | 正常 |
| 1 | 禁用 |

---

## 6. 数据库初始化脚本

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    mail VARCHAR(100) COMMENT '邮箱地址',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建分组表
CREATE TABLE IF NOT EXISTS t_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分组唯一标识',
    gid VARCHAR(32) NOT NULL UNIQUE COMMENT '分组全局唯一标识',
    username VARCHAR(50) NOT NULL COMMENT '所属用户名',
    name VARCHAR(100) NOT NULL COMMENT '分组名称',
    description VARCHAR(500) COMMENT '分组描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分组表';

-- 创建短链接表
CREATE TABLE IF NOT EXISTS t_short_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '短链接唯一标识',
    gid VARCHAR(32) NOT NULL COMMENT '所属分组ID',
    short_link_suffix VARCHAR(12) NOT NULL UNIQUE COMMENT '短链接后缀',
    origin_url TEXT NOT NULL COMMENT '原始长链接',
    domain VARCHAR(100) NOT NULL DEFAULT 's.lanyue.com' COMMENT '短链接域名',
    pv BIGINT NOT NULL DEFAULT 0 COMMENT '访问次数',
    uv BIGINT NOT NULL DEFAULT 0 COMMENT '独立访客数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    expire_time DATETIME COMMENT '过期时间',
    INDEX idx_gid (gid),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短链接表';
```

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
