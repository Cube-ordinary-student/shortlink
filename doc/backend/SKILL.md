---
name: shortlink-backend
description: 短链接平台后端开发规范。当开发短链接创建、分组管理、用户管理、数据统计、回收站功能时使用此skill。
---

# 短链接平台后端开发规范

## 触发条件

- 开发短链接平台后端项目
- 实现用户注册登录功能
- 实现短链接创建与管理
- 实现分组管理功能
- 实现数据统计功能
- 实现回收站功能

---

## Part 1: 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 编程语言 |
| **Spring Boot** | 3.2.x | 应用框架 |
| **Spring Cloud** | 2023.0.x | 微服务框架 |
| **MyBatis Plus** | 3.5.x | ORM框架 |
| **Redis** | 7.x | 缓存/分布式锁 |
| **Redisson** | 3.25.x | Redis客户端 |
| **MySQL** | 8.0+ | 数据库 |
| **ShardingSphere** | 5.4.x | 分库分表 |
| **OpenFeign** | 3.2.x | 远程调用 |
| **Lombok** | 1.18.x | 简化代码 |
| **Hutool** | 5.8.x | 工具库 |

---

## Part 2: 模块划分

```
shortlink/
├── admin/              # 后台管理模块
│   ├── controller/     # 控制层
│   ├── service/        # 业务层
│   ├── dao/            # 数据访问层
│   ├── dto/            # 数据传输对象
│   ├── remote/         # 远程调用
│   ├── config/         # 配置类
│   └── common/         # 通用组件
├── project/            # 核心业务模块
│   ├── controller/     # 控制层
│   ├── service/        # 业务层
│   ├── dao/            # 数据访问层
│   ├── dto/            # 数据传输对象
│   ├── mq/             # 消息队列
│   └── config/         # 配置类
├── gateway/            # API网关
```

---

## Part 3: API接口规范

### 基础配置

- **基础路径**: `/api/short-link/admin/v1`
- **请求头**: `Token`, `Username`
- **响应格式**: `{"code": "0", "message": "...", "data": {...}}`

### 用户模块API

| 方法 | 路径 | 说明 | Controller |
|------|------|------|------------|
| POST | `/user/login` | 用户登录 | `UserController` |
| POST | `/user/register` | 用户注册 | `UserController` |

### 分组模块API

| 方法 | 路径 | 说明 | Controller |
|------|------|------|------------|
| GET | `/group/page` | 查询分组列表 | `GroupController` |
| POST | `/group/create` | 创建分组 | `GroupController` |
| POST | `/group/update` | 更新分组 | `GroupController` |
| POST | `/group/delete` | 删除分组 | `GroupController` |

### 短链接模块API

| 方法 | 路径 | 说明 | Controller |
|------|------|------|------------|
| GET | `/page` | 查询短链接列表 | `ShortLinkController` |
| POST | `/create` | 创建短链接 | `ShortLinkController` |
| POST | `/update` | 更新短链接 | `ShortLinkController` |
| POST | `/recycle-bin/save` | 移至回收站 | `ShortLinkController` |
| GET | `/recycle-bin/page` | 查询回收站 | `ShortLinkController` |
| POST | `/recycle-bin/recover` | 恢复短链接 | `ShortLinkController` |
| POST | `/recycle-bin/remove` | 彻底删除 | `ShortLinkController` |
| GET | `/stats` | 查询访问统计 | `ShortLinkController` |

---

## Part 4: 目录结构

### admin模块结构

```
admin/src/main/java/com/lanyue/shortlink/admin/
├── common/                     # 通用组件
│   ├── biz/user/               # 用户上下文
│   ├── constant/               # 常量定义
│   ├── convention/             # 约定规范
│   ├── database/               # 数据库基础
│   ├── enums/                  # 枚举类
│   └── web/                    # Web相关
├── config/                     # 配置类
├── controller/                 # 控制层
├── dao/                        # 数据访问层
│   ├── entity/                 # 实体类
│   └── mapper/                 # Mapper接口
├── dto/                        # 数据传输对象
│   ├── req/                    # 请求DTO
│   └── resp/                   # 响应DTO
├── remote/                     # 远程调用
├── service/                    # 业务层
└── ShortLinkAdminApplication.java
```

---

## Part 5: 命名规范

### 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| Controller | 驼峰 + Controller | `UserController.java` |
| Service接口 | 驼峰 + Service | `UserService.java` |
| Service实现 | 驼峰 + ServiceImpl | `UserServiceImpl.java` |
| Mapper接口 | 驼峰 + Mapper | `UserMapper.java` |
| 实体类 | 驼峰 + DO | `UserDO.java` |
| 请求DTO | 驼峰 + ReqDTO | `UserRegisterReqDTO.java` |
| 响应DTO | 驼峰 + RespDTO | `UserLoginRespDTO.java` |

### 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 查询单个 | get/find/query | `getUserByUsername()` |
| 查询列表 | list/page | `pageShortLink()` |
| 新增 | save/create/add | `register()` |
| 更新 | update/edit | `update()` |
| 删除 | delete/remove | `delete()` |

---

## Part 6: 代码规范

### Controller层

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }
    
    @PostMapping("/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }
}
```

### Service层

```java
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
    UserLoginRespDTO login(UserLoginReqDTO requestParam);
    void register(UserRegisterReqDTO requestParam);
}
```

### ServiceImpl层

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 业务逻辑
    }
}
```

---

## Part 7: 统一响应规范

### Result封装

```java
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {
    
    public static final String SUCCESS_CODE = "0";
    
    private String code;
    private String message;
    private T data;
}
```

### Results工具类

```java
public final class Results {
    
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setMessage("操作成功")
                .setData(data);
    }
    
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE)
                .setMessage("操作成功");
    }
    
    public static <T> Result<T> failure(String code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message);
    }
}
```

---

## Part 8: 异常处理

### 异常类型

| 异常类型 | 使用场景 |
|----------|----------|
| `ClientException` | 客户端请求错误 |
| `ServiceException` | 服务端业务错误 |
| `RemoteException` | 远程调用失败 |

### 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ClientException.class)
    public Result<Void> clientExceptionHandler(ClientException ex) {
        return Results.failure(ex.getErrorCode(), ex.getErrorMessage());
    }
    
    @ExceptionHandler(ServiceException.class)
    public Result<Void> serviceExceptionHandler(ServiceException ex) {
        return Results.failure(ex.getErrorCode(), ex.getErrorMessage());
    }
}
```

---

## Part 9: 数据库规范

### 核心数据表

| 表名 | 说明 |
|------|------|
| `t_user` | 用户表 |
| `t_group` | 分组表 |
| `t_short_link` | 短链接表 |
| `t_link_access_stats` | 访问统计表 |
| `t_short_link_recycle` | 回收站表 |

### 实体类定义

```java
@Data
@TableName("t_short_link")
public class ShortLinkDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String gid;
    
    private String shortLinkSuffix;
    
    private String originUrl;
    
    private String domain;
    
    private Long pv;
    
    private Long uv;
    
    private Integer status;
    
    private LocalDateTime expireTime;
}
```

---

## Part 10: 安全规范

### 密码处理

```java
// 使用 BCrypt 加密
String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
```

### Token管理

```java
String token = UUID.randomUUID().toString();
stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + username, token, userInfoJson);
```

---

**文档版本**: v1.0  
**适用版本**: 短链接平台 v1.0
