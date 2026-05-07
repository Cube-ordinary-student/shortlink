# 短链接平台 - 后端开发指南

---

## 1. 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.x | 应用框架 |
| Spring Cloud | 2023.0.x | 微服务框架 |
| MyBatis Plus | 3.5.x | ORM框架 |
| Redis | 7.x | 缓存/分布式锁 |
| Redisson | 3.25.x | Redis客户端 |
| MySQL | 8.0+ | 数据库 |
| ShardingSphere | 5.4.x | 分库分表 |
| OpenFeign | 3.2.x | 远程调用 |
| Lombok | 1.18.x | 简化代码 |
| Hutool | 5.8.x | 工具库 |

---

## 2. 模块划分

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
└── aggregation/        # 聚合服务
```

---

## 3. 目录结构

### 3.1 admin模块结构

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

## 4. 命名规范

### 4.1 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| Controller | 驼峰 + Controller | `UserController.java` |
| Service接口 | 驼峰 + Service | `UserService.java` |
| Service实现 | 驼峰 + ServiceImpl | `UserServiceImpl.java` |
| Mapper接口 | 驼峰 + Mapper | `UserMapper.java` |
| 实体类 | 驼峰 + DO | `UserDO.java` |
| 请求DTO | 驼峰 + ReqDTO | `UserRegisterReqDTO.java` |
| 响应DTO | 驼峰 + RespDTO | `UserLoginRespDTO.java` |
| 配置类 | 驼峰 + Configuration | `DataBaseConfiguration.java` |

### 4.2 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 查询单个 | get/find/query | `getUserByUsername()` |
| 查询列表 | list/page | `pageShortLink()` |
| 新增 | save/create/add | `register()` |
| 更新 | update/edit | `update()` |
| 删除 | delete/remove | `delete()` |

---

## 5. 代码规范

### 5.1 Controller层

```java
@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }
}
```

### 5.2 Service层

```java
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
    void register(UserRegisterReqDTO requestParam);
}
```

### 5.3 ServiceImpl层

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

## 6. 统一响应规范

### 6.1 Result封装

```java
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {
    
    public static final String SUCCESS_CODE = "0";
    
    private String code;
    private String message;
    private T data;
    
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
```

### 6.2 Results工具类

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

## 7. 异常处理

### 7.1 异常类型

| 异常类型 | 使用场景 |
|----------|----------|
| `ClientException` | 客户端请求错误 |
| `ServiceException` | 服务端业务错误 |
| `RemoteException` | 远程调用失败 |

### 7.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ClientException.class)
    public Result<Void> clientExceptionHandler(ClientException ex) {
        return Results.failure(ex.getErrorCode(), ex.getErrorMessage());
    }
}
```

---

## 8. 数据库规范

### 8.1 实体类定义

```java
@Data
@TableName("t_user")
public class UserDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
}
```

### 8.2 Mapper接口

```java
public interface UserMapper extends BaseMapper<UserDO> {
    
    @Select("SELECT * FROM t_user WHERE username = #{username} AND del_flag = 0")
    UserDO selectByUsername(@Param("username") String username);
}
```

---

## 9. 缓存规范

### 9.1 Redis Key命名

```java
public final class RedisCacheConstant {
    public static final String USER_LOGIN_KEY = "short-link:login:";
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock:register:";
}
```

### 9.2 分布式锁使用

```java
RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + username);
if (!lock.tryLock()) {
    throw new ClientException("用户正在注册中");
}
try {
    // 业务逻辑
} finally {
    lock.unlock();
}
```

---

## 10. 安全规范

### 10.1 密码处理

```java
// 使用 BCrypt 加密
String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

// 验证密码
if (BCrypt.checkpw(password, storedPassword)) {
    // 密码正确
}
```

### 10.2 Token管理

```java
// 登录时生成 Token
String token = UUID.randomUUID().toString();
stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + username, token, userInfoJson);
```

---

## 11. 开发流程

### 11.1 启动服务

```bash
cd backend
mvn spring-boot:run
```

### 11.2 打包部署

```bash
mvn clean package
java -jar target/shortlink-admin.jar
```

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
