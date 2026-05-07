# 短链接平台数据库设计文档（逻辑外键版本）

---

## 文档说明

本文档描述了短链接平台的数据库设计，**不使用任何数据库级别的物理外键约束**，所有关联关系通过应用程序层面的逻辑外键实现。

---

## 核心设计原则

### 1. 逻辑外键设计原则

| 原则 | 说明 |
|------|------|
| **无物理外键** | 数据库层面不创建 FOREIGN KEY 约束 |
| **逻辑参照** | 通过业务代码实现参照完整性检查 |
| **级联操作** | 在应用层实现级联删除、级联更新逻辑 |
| **数据一致性** | 通过事务和验证确保关联数据的有效性 |

### 2. 逻辑外键实现策略

| 策略 | 实现方式 |
|------|----------|
| **插入前验证** | 插入数据前检查关联数据是否存在 |
| **更新时同步** | 更新主键时同步更新所有关联表 |
| **删除时校验** | 删除数据前检查是否存在子数据依赖 |
| **事务保障** | 使用事务确保多表操作的原子性 |

---

## 数据表结构

### 1. 用户表 (t_user)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 用户唯一标识 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | - | 用户名（登录账号） |
| password | VARCHAR(255) | NOT NULL | - | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | NULL | - | 真实姓名 |
| phone | VARCHAR(20) | NULL | - | 手机号码 |
| mail | VARCHAR(100) | NULL | - | 邮箱地址 |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记（0-正常，1-删除） |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_username | username | 唯一索引 |
| idx_del_flag | del_flag | 普通索引 |

---

### 2. 分组表 (t_group)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 分组唯一标识 |
| gid | VARCHAR(32) | NOT NULL, UNIQUE | - | 分组全局唯一标识（UUID） |
| username | VARCHAR(50) | NOT NULL | - | **逻辑外键**：所属用户名 |
| name | VARCHAR(100) | NOT NULL | - | 分组名称 |
| description | VARCHAR(500) | NULL | - | 分组描述 |
| sort_order | INT | NOT NULL | 0 | 排序顺序 |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记（0-正常，1-删除） |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引设计**：
| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_gid | gid | 唯一索引 |
| idx_username | username | 普通索引 |
| idx_del_flag | del_flag | 普通索引 |

**逻辑外键关系**：`username` → `t_user.username`

---

### 3. 短链接表 (t_short_link)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 短链接唯一标识 |
| gid | VARCHAR(32) | NOT NULL | - | **逻辑外键**：所属分组ID |
| short_link_suffix | VARCHAR(12) | NOT NULL, UNIQUE | - | 短链接后缀（6-8位字符） |
| origin_url | TEXT | NOT NULL | - | 原始长链接 |
| domain | VARCHAR(100) | NOT NULL | s.lanyue.com | 短链接域名 |
| group_name | VARCHAR(100) | NULL | - | 分组名称（冗余字段） |
| pv | BIGINT | NOT NULL | 0 | 访问次数 |
| uv | BIGINT | NOT NULL | 0 | 独立访客数 |
| status | TINYINT | NOT NULL | 0 | 状态（0-正常，1-禁用） |
| del_flag | TINYINT | NOT NULL | 0 | 删除标记（0-正常，1-回收站，2-彻底删除） |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| expire_time | DATETIME | NULL | - | 过期时间 |

**索引设计**：
| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_short_link_suffix | short_link_suffix | 唯一索引 |
| idx_gid | gid | 普通索引 |
| idx_del_flag | del_flag | 普通索引 |
| idx_status | status | 普通索引 |

**逻辑外键关系**：`gid` → `t_group.gid`

---

### 4. 访问统计表 (t_link_access_stats)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 统计记录唯一标识 |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | **逻辑外键**：短链接后缀 |
| date | DATE | NOT NULL | - | 统计日期 |
| pv | BIGINT | NOT NULL | 0 | 当日访问次数 |
| uv | BIGINT | NOT NULL | 0 | 当日独立访客数 |
| ip_count | BIGINT | NOT NULL | 0 | 当日独立IP数 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**索引设计**：
| 索引名 | 字段 | 类型 |
|--------|------|------|
| PRIMARY | id | 主键索引 |
| uk_short_link_date | short_link_suffix, date | 复合唯一索引 |
| idx_short_link_suffix | short_link_suffix | 普通索引 |

**逻辑外键关系**：`short_link_suffix` → `t_short_link.short_link_suffix`

---

### 5. 操作系统统计表 (t_link_os_stats)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 统计记录唯一标识 |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | **逻辑外键**：短链接后缀 |
| date | DATE | NOT NULL | - | 统计日期 |
| os_type | VARCHAR(50) | NOT NULL | - | 操作系统类型 |
| count | BIGINT | NOT NULL | 0 | 访问次数 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**逻辑外键关系**：`short_link_suffix` → `t_short_link.short_link_suffix`

---

### 6. 浏览器统计表 (t_link_browser_stats)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 统计记录唯一标识 |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | **逻辑外键**：短链接后缀 |
| date | DATE | NOT NULL | - | 统计日期 |
| browser_type | VARCHAR(50) | NOT NULL | - | 浏览器类型 |
| count | BIGINT | NOT NULL | 0 | 访问次数 |
| create_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |

**逻辑外键关系**：`short_link_suffix` → `t_short_link.short_link_suffix`

---

### 7. 回收站表 (t_short_link_recycle)

| 字段名 | 数据类型 | 约束 | 默认值 | 说明 |
|--------|----------|------|--------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | 记录唯一标识 |
| short_link_id | BIGINT | NOT NULL | - | **逻辑外键**：原短链接ID |
| gid | VARCHAR(32) | NOT NULL | - | 所属分组ID |
| short_link_suffix | VARCHAR(12) | NOT NULL | - | 短链接后缀 |
| origin_url | TEXT | NOT NULL | - | 原始长链接 |
| group_name | VARCHAR(100) | NULL | - | 分组名称 |
| delete_time | DATETIME | NOT NULL | CURRENT_TIMESTAMP | 删除时间 |
| expire_time | DATETIME | NULL | - | 过期时间（默认删除后30天） |

**逻辑外键关系**：`short_link_id` → `t_short_link.id`

---

## 逻辑外键关系图

```
t_user                    t_group                    t_short_link
┌─────────┐              ┌─────────┐              ┌───────────────┐
│ id (PK) │              │ id (PK) │              │ id (PK)       │
│ username│◄─────────────│ username│              │ gid ◄────┐    │
│ ...     │   逻辑外键    │ gid     │◄─────────────┤ gid      │    │
└─────────┘              │ ...     │   逻辑外键    │ short_link│    │
                         └─────────┘              │ _suffix  │──┐ │
                                                  │ ...      │  │ │
                                                  └──────────┴──┼─┘
                                                                │
        t_link_access_stats      t_link_os_stats      t_link_browser_stats
        ┌─────────────────┐      ┌──────────────┐      ┌─────────────────┐
        │ short_link      │◄─────┤ short_link   │◄─────┤ short_link      │
        │ _suffix        │ 逻辑外键│ _suffix     │ 逻辑外键│ _suffix        │
        │ ...            │      │ ...          │      │ ...             │
        └─────────────────┘      └──────────────┘      └─────────────────┘
```

---

## 逻辑外键实现方案

### 1. 数据模型设计（Java实体类）

#### BaseDO - 基础实体类

```java
@Data
public class BaseDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

#### UserDO - 用户实体

```java
@Data
@TableName("t_user")
public class UserDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String mail;
}
```

#### GroupDO - 分组实体

```java
@Data
@TableName("t_group")
public class GroupDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String gid;
    
    // 逻辑外键：关联 t_user.username
    private String username;
    
    private String name;
    
    private String description;
    
    private Integer sortOrder;
}
```

#### ShortLinkDO - 短链接实体

```java
@Data
@TableName("t_short_link")
public class ShortLinkDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 逻辑外键：关联 t_group.gid
    private String gid;
    
    private String shortLinkSuffix;
    
    private String originUrl;
    
    private String domain;
    
    private String groupName;
    
    private Long pv;
    
    private Long uv;
    
    private Integer status;
    
    private LocalDateTime expireTime;
}
```

---

### 2. 逻辑外键验证服务

#### ForeignKeyValidator - 逻辑外键验证器

```java
@Component
@RequiredArgsConstructor
public class ForeignKeyValidator {
    
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final ShortLinkMapper shortLinkMapper;
    
    /**
     * 验证用户名是否存在
     */
    public boolean validateUserExists(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        return userMapper.exists(queryWrapper);
    }
    
    /**
     * 验证分组ID是否存在
     */
    public boolean validateGroupExists(String gid) {
        if (StringUtils.isBlank(gid)) {
            return false;
        }
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        return groupMapper.exists(queryWrapper);
    }
    
    /**
     * 验证短链接是否存在
     */
    public boolean validateShortLinkExists(String shortLinkSuffix) {
        if (StringUtils.isBlank(shortLinkSuffix)) {
            return false;
        }
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getShortLinkSuffix, shortLinkSuffix)
                .eq(ShortLinkDO::getDelFlag, 0);
        return shortLinkMapper.exists(queryWrapper);
    }
    
    /**
     * 检查分组是否存在子短链接
     */
    public boolean hasChildShortLinks(String gid) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0);
        return shortLinkMapper.exists(queryWrapper);
    }
    
    /**
     * 检查用户是否存在子分组
     */
    public boolean hasChildGroups(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        return groupMapper.exists(queryWrapper);
    }
    
    /**
     * 检查短链接是否存在统计数据
     */
    public boolean hasStatsData(String shortLinkSuffix) {
        // 检查访问统计表
        LambdaQueryWrapper<LinkAccessStatsDO> accessQuery = Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                .eq(LinkAccessStatsDO::getShortLinkSuffix, shortLinkSuffix);
        if (accessQuery.exists()) {
            return true;
        }
        // 检查操作系统统计表
        LambdaQueryWrapper<LinkOsStatsDO> osQuery = Wrappers.lambdaQuery(LinkOsStatsDO.class)
                .eq(LinkOsStatsDO::getShortLinkSuffix, shortLinkSuffix);
        if (osQuery.exists()) {
            return true;
        }
        // 检查浏览器统计表
        LambdaQueryWrapper<LinkBrowserStatsDO> browserQuery = Wrappers.lambdaQuery(LinkBrowserStatsDO.class)
                .eq(LinkBrowserStatsDO::getShortLinkSuffix, shortLinkSuffix);
        return browserQuery.exists();
    }
}
```

---

### 3. 级联操作实现

#### GroupServiceImpl - 分组服务级联操作

```java
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    
    private final ForeignKeyValidator foreignKeyValidator;
    private final ShortLinkMapper shortLinkMapper;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGroup(String gid) {
        // 1. 检查分组是否存在
        if (!foreignKeyValidator.validateGroupExists(gid)) {
            throw new ClientException("分组不存在");
        }
        
        // 2. 检查是否存在子短链接（逻辑外键约束）
        if (foreignKeyValidator.hasChildShortLinks(gid)) {
            throw new ClientException("该分组下存在短链接，无法删除");
        }
        
        // 3. 执行删除
        GroupDO groupDO = baseMapper.selectOne(Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid));
        if (groupDO != null) {
            groupDO.setDelFlag(1);
            baseMapper.updateById(groupDO);
        }
    }
}
```

#### ShortLinkServiceImpl - 短链接服务级联操作

```java
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    
    private final ForeignKeyValidator foreignKeyValidator;
    private final LinkAccessStatsMapper accessStatsMapper;
    private final LinkOsStatsMapper osStatsMapper;
    private final LinkBrowserStatsMapper browserStatsMapper;
    private final ShortLinkRecycleMapper recycleMapper;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteShortLink(Long id) {
        // 1. 查询短链接信息
        ShortLinkDO shortLinkDO = baseMapper.selectById(id);
        if (shortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }
        
        // 2. 移动到回收站
        ShortLinkRecycleDO recycleDO = new ShortLinkRecycleDO();
        recycleDO.setShortLinkId(id);
        recycleDO.setGid(shortLinkDO.getGid());
        recycleDO.setShortLinkSuffix(shortLinkDO.getShortLinkSuffix());
        recycleDO.setOriginUrl(shortLinkDO.getOriginUrl());
        recycleDO.setGroupName(shortLinkDO.getGroupName());
        recycleDO.setExpireTime(LocalDateTime.now().plusDays(30));
        recycleMapper.insert(recycleDO);
        
        // 3. 标记短链接为已删除
        shortLinkDO.setDelFlag(1);
        baseMapper.updateById(shortLinkDO);
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completelyDelete(Long id) {
        // 1. 查询短链接信息
        ShortLinkDO shortLinkDO = baseMapper.selectById(id);
        if (shortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }
        
        String shortLinkSuffix = shortLinkDO.getShortLinkSuffix();
        
        // 2. 删除相关统计数据（级联删除）
        accessStatsMapper.delete(Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                .eq(LinkAccessStatsDO::getShortLinkSuffix, shortLinkSuffix));
        
        osStatsMapper.delete(Wrappers.lambdaQuery(LinkOsStatsDO.class)
                .eq(LinkOsStatsDO::getShortLinkSuffix, shortLinkSuffix));
        
        browserStatsMapper.delete(Wrappers.lambdaQuery(LinkBrowserStatsDO.class)
                .eq(LinkBrowserStatsDO::getShortLinkSuffix, shortLinkSuffix));
        
        // 3. 删除回收站记录
        recycleMapper.delete(Wrappers.lambdaQuery(ShortLinkRecycleDO.class)
                .eq(ShortLinkRecycleDO::getShortLinkId, id));
        
        // 4. 彻底删除短链接
        baseMapper.deleteById(id);
    }
}
```

---

### 4. 数据插入验证

#### GroupServiceImpl - 创建分组验证

```java
@Transactional(rollbackFor = Exception.class)
@Override
public void createGroup(GroupCreateReqDTO requestParam) {
    String username = requestParam.getUsername();
    
    // 验证用户是否存在（逻辑外键检查）
    if (!foreignKeyValidator.validateUserExists(username)) {
        throw new ClientException("用户不存在");
    }
    
    // 检查分组名称是否重复
    boolean exists = baseMapper.exists(Wrappers.lambdaQuery(GroupDO.class)
            .eq(GroupDO::getUsername, username)
            .eq(GroupDO::getName, requestParam.getName())
            .eq(GroupDO::getDelFlag, 0));
    if (exists) {
        throw new ClientException("分组名称已存在");
    }
    
    // 创建分组
    GroupDO groupDO = BeanUtil.toBean(requestParam, GroupDO.class);
    groupDO.setGid(UUID.randomUUID().toString().replace("-", ""));
    baseMapper.insert(groupDO);
}
```

#### ShortLinkServiceImpl - 创建短链接验证

```java
@Transactional(rollbackFor = Exception.class)
@Override
public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
    String gid = requestParam.getGid();
    
    // 验证分组是否存在（逻辑外键检查）
    if (!foreignKeyValidator.validateGroupExists(gid)) {
        throw new ClientException("分组不存在");
    }
    
    // 检查短链后缀是否重复
    if (StringUtils.isNotBlank(requestParam.getCustomSuffix())) {
        boolean exists = baseMapper.exists(Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getShortLinkSuffix, requestParam.getCustomSuffix())
                .eq(ShortLinkDO::getDelFlag, 0));
        if (exists) {
            throw new ClientException("短链接已存在");
        }
    }
    
    // 生成短链后缀
    String shortLinkSuffix = generateShortLinkSuffix(requestParam.getCustomSuffix());
    
    // 创建短链接
    ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
    shortLinkDO.setShortLinkSuffix(shortLinkSuffix);
    baseMapper.insert(shortLinkDO);
    
    return buildResponse(shortLinkDO);
}
```

---

### 5. 异常处理机制

#### ClientException - 客户端异常

```java
public class ClientException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    
    public ClientException(String message) {
        super(message);
        this.errorCode = "400";
        this.errorMessage = message;
    }
    
    public ClientException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    // Getters
}
```

#### GlobalExceptionHandler - 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ClientException.class)
    public Result<Void> handleClientException(ClientException ex) {
        log.warn("客户端错误: {}", ex.getMessage());
        return Results.failure(ex.getErrorCode(), ex.getErrorMessage());
    }
    
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException ex) {
        log.error("服务端错误: {}", ex.getMessage(), ex);
        return Results.failure(ex.getErrorCode(), ex.getErrorMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("系统错误: {}", ex.getMessage(), ex);
        return Results.failure("500", "系统内部错误");
    }
}
```

---

### 6. 数据库初始化脚本（无物理外键）

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

-- 创建分组表（无物理外键）
CREATE TABLE IF NOT EXISTS t_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分组唯一标识',
    gid VARCHAR(32) NOT NULL UNIQUE COMMENT '分组全局唯一标识',
    username VARCHAR(50) NOT NULL COMMENT '所属用户名（逻辑外键）',
    name VARCHAR(100) NOT NULL COMMENT '分组名称',
    description VARCHAR(500) COMMENT '分组描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分组表';

-- 创建短链接表（无物理外键）
CREATE TABLE IF NOT EXISTS t_short_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '短链接唯一标识',
    gid VARCHAR(32) NOT NULL COMMENT '所属分组ID（逻辑外键）',
    short_link_suffix VARCHAR(12) NOT NULL UNIQUE COMMENT '短链接后缀',
    origin_url TEXT NOT NULL COMMENT '原始长链接',
    domain VARCHAR(100) NOT NULL DEFAULT 's.lanyue.com' COMMENT '短链接域名',
    group_name VARCHAR(100) COMMENT '分组名称',
    pv BIGINT NOT NULL DEFAULT 0 COMMENT '访问次数',
    uv BIGINT NOT NULL DEFAULT 0 COMMENT '独立访客数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    expire_time DATETIME COMMENT '过期时间',
    INDEX idx_gid (gid),
    INDEX idx_del_flag (del_flag),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短链接表';
```

---

## 测试用例

### 1. 逻辑外键验证测试

```java
@SpringBootTest
class ForeignKeyValidatorTest {
    
    @Autowired
    private ForeignKeyValidator foreignKeyValidator;
    
    @Autowired
    private UserMapper userMapper;
    
    @Test
    void testValidateUserExists() {
        // 创建测试用户
        UserDO userDO = new UserDO();
        userDO.setUsername("test_user");
        userDO.setPassword("password");
        userMapper.insert(userDO);
        
        // 验证用户存在
        assertTrue(foreignKeyValidator.validateUserExists("test_user"));
        assertFalse(foreignKeyValidator.validateUserExists("not_exists"));
        
        // 清理
        userMapper.deleteById(userDO.getId());
    }
    
    @Test
    void testValidateGroupExists() {
        // 创建测试分组
        GroupDO groupDO = new GroupDO();
        groupDO.setGid("test_gid");
        groupDO.setUsername("test_user");
        groupDO.setName("test_group");
        groupMapper.insert(groupDO);
        
        // 验证分组存在
        assertTrue(foreignKeyValidator.validateGroupExists("test_gid"));
        assertFalse(foreignKeyValidator.validateGroupExists("not_exists"));
        
        // 清理
        groupMapper.deleteById(groupDO.getId());
    }
}
```

### 2. 级联删除测试

```java
@SpringBootTest
class GroupServiceTest {
    
    @Autowired
    private GroupService groupService;
    
    @Autowired
    private ShortLinkService shortLinkService;
    
    @Test
    void testDeleteGroupWithChildLinks() {
        // 创建测试用户和分组
        UserDO userDO = createTestUser();
        GroupDO groupDO = createTestGroup(userDO.getUsername());
        
        // 创建子短链接
        ShortLinkCreateReqDTO req = new ShortLinkCreateReqDTO();
        req.setGid(groupDO.getGid());
        req.setOriginUrl("https://example.com");
        shortLinkService.createShortLink(req);
        
        // 尝试删除分组（应失败）
        assertThrows(ClientException.class, () -> {
            groupService.deleteGroup(groupDO.getGid());
        });
        
        // 清理
        cleanUp();
    }
    
    @Test
    void testDeleteGroupWithoutChildLinks() {
        // 创建测试用户和分组
        UserDO userDO = createTestUser();
        GroupDO groupDO = createTestGroup(userDO.getUsername());
        
        // 删除分组（应成功）
        assertDoesNotThrow(() -> {
            groupService.deleteGroup(groupDO.getGid());
        });
        
        // 验证分组已被标记为删除
        GroupDO deletedGroup = groupMapper.selectById(groupDO.getId());
        assertEquals(1, deletedGroup.getDelFlag());
        
        // 清理
        cleanUp();
    }
}
```

### 3. 数据一致性测试

```java
@SpringBootTest
class ShortLinkServiceTest {
    
    @Autowired
    private ShortLinkService shortLinkService;
    
    @Autowired
    private LinkAccessStatsMapper accessStatsMapper;
    
    @Test
    void testCompletelyDeleteWithStats() {
        // 创建测试数据
        ShortLinkDO shortLinkDO = createTestShortLink();
        String suffix = shortLinkDO.getShortLinkSuffix();
        
        // 添加统计数据
        LinkAccessStatsDO statsDO = new LinkAccessStatsDO();
        statsDO.setShortLinkSuffix(suffix);
        statsDO.setDate(LocalDate.now());
        statsDO.setPv(100L);
        accessStatsMapper.insert(statsDO);
        
        // 彻底删除短链接
        shortLinkService.completelyDelete(shortLinkDO.getId());
        
        // 验证短链接已删除
        assertNull(shortLinkMapper.selectById(shortLinkDO.getId()));
        
        // 验证统计数据已级联删除
        assertEquals(0, accessStatsMapper.selectCount(Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                .eq(LinkAccessStatsDO::getShortLinkSuffix, suffix)));
    }
}
```

---

## 优势与注意事项

### 优势

| 优势 | 说明 |
|------|------|
| **性能提升** | 移除物理外键约束，减少数据库锁竞争，提升写入性能 |
| **灵活性** | 关联关系逻辑完全由应用控制，便于实现复杂业务逻辑 |
| **分库分表友好** | 逻辑外键不受分库分表限制，便于水平扩展 |
| **跨数据库支持** | 可轻松实现跨不同类型数据库的关联 |

### 注意事项

| 事项 | 说明 |
|------|------|
| **数据一致性** | 需要在应用层严格控制，确保事务正确使用 |
| **代码复杂度** | 增加了业务代码的复杂度，需要良好的架构设计 |
| **测试覆盖** | 需要充分测试逻辑外键验证和级联操作 |
| **批量操作** | 批量操作时需要特别注意数据一致性 |

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
