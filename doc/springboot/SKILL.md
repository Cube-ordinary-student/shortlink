---
name: shortlink-backend
description: 短链接平台后端开发规范。当开发短链接创建、分组管理、用户管理、数据统计、回收站功能时使用此 skill。
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
|------|----|------|
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
│   ├── config/         # 配置类
│   └── common/         # 通用组件
├── gateway/            # API网关
└── aggregation/        # 聚合服务
```

---

## Part 3: 目录结构

### admin 模块结构

```
admin/src/main/java/com/lanyue/shortlink/admin/
├── common/                     # 通用组件
│   ├── biz/user/               # 用户上下文
│   │   ├── UserContext.java
│   │   ├── UserInfoDTO.java
│   │   └── UserTransmitFilter.java
│   ├── constant/               # 常量定义
│   │   └── RedisCacheConstant.java
│   ├── convention/             # 约定规范
│   │   ├── errorcode/          # 错误码
│   │   ├── exception/          # 异常类
│   │   └── result/             # 返回结果
│   ├── database/               # 数据库基础
│   │   └── BaseDO.java
│   ├── enums/                  # 枚举类
│   │   └── UserErrorCodeEnum.java
│   └── web/                    # Web相关
│       └── GlobalExceptionHandler.java
├── config/                     # 配置类
│   ├── DataBaseConfiguration.java
│   ├── RBloomFilterConfiguration.java
│   └── UserConfiguration.java
├── controller/                 # 控制层
│   ├── UserController.java
│   ├── GroupController.java
│   └── ShortLinkController.java
├── dao/                        # 数据访问层
│   ├── entity/                 # 实体类
│   │   ├── UserDO.java
│   │   └── GroupDO.java
│   └── mapper/                 # Mapper接口
│       ├── UserMapper.java
│       └── GroupMapper.java
├── dto/                        # 数据传输对象
│   ├── req/                    # 请求DTO
│   │   ├── UserRegisterReqDTO.java
│   │   └── UserLoginReqDTO.java
│   └── resp/                   # 响应DTO
│       ├── UserRespDTO.java
│       └── UserLoginRespDTO.java
├── remote/                     # 远程调用
│   ├── dto/                    # 远程DTO
│   └── ShortLinkActualRemoteService.java
├── service/                    # 业务层
│   ├── UserService.java
│   └── impl/
│       └── UserServiceImpl.java
└── ShortLinkAdminApplication.java
```

### project 模块结构

```
project/src/main/java/com/lanyue/shortlink/project/
├── common/                     # 通用组件
├── config/                     # 配置类
├── controller/                 # 控制层
├── dao/                        # 数据访问层
│   ├── entity/                 # 实体类（含统计DO）
│   │   ├── ShortLinkDO.java
│   │   ├── LinkAccessStatsDO.java
│   │   └── LinkOsStatsDO.java
│   └── mapper/                 # Mapper接口
├── dto/                        # 数据传输对象
├── mq/                         # 消息队列
│   ├── consumer/               # 消费者
│   │   ├── ShortLinkStatsSaveConsumer.java
│   │   └── DelayShortLinkStatsConsumer.java
│   └── producer/               # 生产者
│       ├── ShortLinkStatsSaveProducer.java
│       └── DelayShortLinkStatsProducer.java
├── service/                    # 业务层
│   ├── ShortLinkService.java
│   ├── ShortLinkStatsService.java
│   └── impl/
├── toolkit/                    # 工具类
│   ├── HashUtil.java           # Hash算法
│   └── LinkUtil.java           # 短链接工具
└── ShortLinkApplication.java
```

---

## Part 4: 命名规范

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
| 配置类 | 驼峰 + Configuration | `DataBaseConfiguration.java` |
| 常量类 | 驼峰 + Constant | `RedisCacheConstant.java` |
| 枚举类 | 驼峰 + Enum | `UserErrorCodeEnum.java` |

### 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 查询单个 | get/find/query | `getUserByUsername()` |
| 查询列表 | list/page | `pageShortLink()` |
| 新增 | save/create/add | `register()` |
| 更新 | update/edit | `update()` |
| 删除 | delete/remove | `logout()` |
| 检查/验证 | check/has/is | `hasUsername()` |

### 变量命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 普通变量 | 小驼峰 | `username`, `userDO` |
| 常量 | 大写下划线 | `LOCK_USER_REGISTER_KEY` |
| 集合 | 复数形式 | `userList`, `gidList` |
| 布尔值 | is/has/can前缀 | `isSuccess`, `hasLogin` |

---

## Part 5: 代码规范

### Controller 层

```java
@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }
    
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }
}
```

### Service 层

```java
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
    Boolean hasUsername(String username);
    void register(UserRegisterReqDTO requestParam);
    void update(UserUpdateReqDTO requestParam);
    UserLoginRespDTO login(UserLoginReqDTO requestParam);
}
```

### ServiceImpl 层

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if (!lock.tryLock()) {
            throw new ClientException(USER_NAME_EXIST);
        }
        try {
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if (inserted < 1) {
                throw new ClientException(USER_SAVE_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        } finally {
            lock.unlock();
        }
    }
}
```

### DTO 定义

```java
@Data
public class UserRegisterReqDTO {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String mail;
}
```

---

## Part 6: 统一响应规范

### Result 封装

```java
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {
    
    public static final String SUCCESS_CODE = "0";
    
    private String code;
    private String message;
    private T data;
    private String requestId;
    
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
```

### Results 工具类

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

## Part 7: 异常处理规范

### 异常类型

| 异常类型 | 使用场景 |
|----------|----------|
| `ClientException` | 客户端请求错误（参数错误、用户不存在等） |
| `ServiceException` | 服务端业务错误（数据库操作失败等） |
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

## Part 8: 用户上下文传递

### UserContext

```java
public final class UserContext {
    
    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();
    
    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }
    
    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUserId).orElse(null);
    }
    
    public static String getUsername() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUsername).orElse(null);
    }
    
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}
```

### 拦截器配置

```java
public class UserTransmitFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String username = httpRequest.getHeader("Username");
        String token = httpRequest.getHeader("Token");
        
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(token)) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setUsername(username);
            UserContext.setUser(userInfoDTO);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.removeUser();
        }
    }
}
```

---

## Part 9: 远程调用规范

### Feign 接口

```java
@FeignClient(
    value = "short-link-project",
    url = "${aggregation.remote-url:}",
    configuration = OpenFeignConfiguration.class
)
public interface ShortLinkActualRemoteService {
    
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);
    
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(
        @RequestParam("gid") String gid,
        @RequestParam("current") Long current,
        @RequestParam("size") Long size
    );
}
```

### OpenFeign 配置

```java
public class OpenFeignConfiguration {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Username", UserContext.getUsername());
            requestTemplate.header("Token", UserContext.getToken());
        };
    }
}
```

---

## Part 10: 消息队列规范

### Producer

```java
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {
    
    private final StringRedisTemplate stringRedisTemplate;
    
    public void send(String topic, Object message) {
        stringRedisTemplate.convertAndSend(topic, JSON.toJSONString(message));
    }
}
```

### Consumer

```java
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveConsumer {
    
    private final ShortLinkStatsService shortLinkStatsService;
    
    @StreamListener(ShortLinkStatsStreamInitializeTask.SHORT_LINK_STATS_TOPIC)
    public void onMessage(String message) {
        ShortLinkStatsRecordDTO recordDTO = JSON.parseObject(message, ShortLinkStatsRecordDTO.class);
        shortLinkStatsService.recordStats(recordDTO);
    }
}
```

---

## Part 11: 数据库规范

### 实体类定义

```java
@Data
@TableName("t_user")
public class UserDO extends BaseDO {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String mail;
    
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
```

### Mapper 接口

```java
public interface UserMapper extends BaseMapper<UserDO> {
    
    @Select("SELECT * FROM t_user WHERE username = #{username} AND del_flag = 0")
    UserDO selectByUsername(@Param("username") String username);
}
```

### 分页查询

```java
LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
    .eq(UserDO::getDelFlag, 0)
    .orderByDesc(UserDO::getCreateTime);

Page<UserDO> page = baseMapper.selectPage(new Page<>(current, size), queryWrapper);
```

---

## Part 12: 缓存规范

### Redis Key 命名

```java
public final class RedisCacheConstant {
    
    public static final String USER_LOGIN_KEY = "short-link:login:";
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock:register:";
    public static final String GROUP_COUNT_KEY = "short-link:group:count:";
}
```

### 分布式锁使用

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

### Bloom Filter 使用

```java
@Configuration
public class RBloomFilterConfiguration {
    
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        return redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
    }
}
```

---

## Part 13: 工具类规范

### Hash 工具类

```java
public final class HashUtil {
    
    private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int SCALE = CHARS.length;
    
    public static String hashToBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(CHARS[(int) (num % SCALE)]);
            num /= SCALE;
        }
        return sb.reverse().toString();
    }
}
```

### 短链接工具类

```java
public final class LinkUtil {
    
    public static String generateShortLink(String originUrl) {
        long hash = Math.abs(originUrl.hashCode());
        return HashUtil.hashToBase62(hash);
    }
    
    public static String buildFullShortUrl(String domain, String shortCode) {
        return String.format("https://%s/%s", domain, shortCode);
    }
}
```

---

## Part 14: API 接口规范

### 用户模块

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 用户注册 | POST | `/api/short-link/admin/v1/user` | 注册新用户 |
| 用户登录 | POST | `/api/short-link/admin/v1/user/login` | 用户登录 |
| 查询用户 | GET | `/api/short-link/admin/v1/user/{username}` | 根据用户名查询 |
| 修改用户 | PUT | `/api/short-link/admin/v1/user` | 修改用户信息 |
| 用户退出 | DELETE | `/api/short-link/admin/v1/user/logout` | 退出登录 |

### 短链接模块

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 创建短链接 | POST | `/api/short-link/v1/create` | 创建单个短链接 |
| 批量创建 | POST | `/api/short-link/v1/create/batch` | 批量创建短链接 |
| 修改短链接 | POST | `/api/short-link/v1/update` | 修改短链接 |
| 分页查询 | GET | `/api/short-link/v1/page` | 分页查询短链接 |
| 访问统计 | GET | `/api/short-link/v1/stats` | 获取访问统计 |

### 分组模块

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 创建分组 | POST | `/api/short-link/admin/v1/group` | 创建分组 |
| 查询分组 | GET | `/api/short-link/admin/v1/group` | 查询分组列表 |
| 修改分组 | PUT | `/api/short-link/admin/v1/group` | 修改分组 |
| 删除分组 | DELETE | `/api/short-link/admin/v1/group` | 删除分组 |

---

## Part 15: 安全规范

### 密码处理

```java
// 使用 BCrypt 加密
String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

// 验证密码
if (BCrypt.checkpw(password, storedPassword)) {
    // 密码正确
}
```

### Token 管理

```java
// 登录时生成 Token
String token = UUID.randomUUID().toString();
stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + username, token, userInfoJson);
stringRedisTemplate.expire(USER_LOGIN_KEY + username, 30L, TimeUnit.MINUTES);

// 验证登录
Boolean isLogin = stringRedisTemplate.opsForHash().hasKey(USER_LOGIN_KEY + username, token);
```

### 请求限流

```java
// 使用 Sentinel 进行限流
@SentinelResource(value = "userLogin", blockHandler = "loginBlockHandler")
public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
    // 登录逻辑
}

public UserLoginRespDTO loginBlockHandler(UserLoginReqDTO requestParam, BlockException ex) {
    throw new ClientException("登录过于频繁，请稍后重试");
}
```

---

## Part 16: 日志规范

### 日志使用

```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public UserRespDTO getUserByUsername(String username) {
        log.info("查询用户信息，用户名：{}", username);
        try {
            // 业务逻辑
            log.debug("查询用户成功，用户ID：{}", userDO.getId());
            return userRespDTO;
        } catch (Exception e) {
            log.error("查询用户失败，用户名：{}，错误信息：{}", username, e.getMessage());
            throw e;
        }
    }
}
```

### 日志级别

| 级别 | 使用场景 |
|------|----------|
| `DEBUG` | 详细的调试信息，生产环境关闭 |
| `INFO` | 重要的业务操作记录 |
| `WARN` | 警告信息，需要关注但不影响业务 |
| `ERROR` | 错误信息，业务异常 |

---

## Part 17: 事务规范

### 事务注解

```java
@Transactional(rollbackFor = Exception.class)
public void register(UserRegisterReqDTO requestParam) {
    // 用户注册
    baseMapper.insert(userDO);
    // 创建默认分组
    groupService.saveGroup(username, "默认分组");
}
```

### 事务传播

| 传播类型 | 说明 |
|----------|------|
| `REQUIRED` | 默认，需要事务，无则创建 |
| `REQUIRES_NEW` | 总是创建新事务 |
| `NESTED` | 嵌套事务 |
| `SUPPORTS` | 支持事务，无则非事务运行 |
| `NOT_SUPPORTED` | 不支持事务，以非事务运行 |

---

## Part 18: 最佳实践

### 1. 代码复用
- 将通用逻辑抽取为工具类或公共方法
- 使用 MyBatis Plus 的通用 Service

### 2. 异常处理
- 统一异常类型，避免抛出原始异常
- 在 GlobalExceptionHandler 中统一处理

### 3. 参数校验
- 使用 `@Valid` 注解进行参数校验
- 自定义校验注解处理复杂校验

### 4. 性能优化
- 使用 Bloom Filter 防止缓存穿透
- 使用 Redis 缓存热点数据
- 使用分布式锁处理并发场景

### 5. 安全防护
- 使用 BCrypt 加密密码
- 使用 Token 进行身份验证
- 使用 Sentinel 进行限流降级

### 6. 代码规范
- 使用 Lombok 减少样板代码
- 遵循阿里巴巴 Java 开发规范
- 使用 `@RequiredArgsConstructor` 代替 `@Autowired`

---

## Part 19: 代码模板

### Controller 模板

```java
@RestController
@RequiredArgsConstructor
public class XxxController {
    
    private final XxxService xxxService;
    
    @GetMapping("/api/short-link/admin/v1/xxx/{id}")
    public Result<XxxRespDTO> getXxxById(@PathVariable("id") Long id) {
        return Results.success(xxxService.getXxxById(id));
    }
    
    @PostMapping("/api/short-link/admin/v1/xxx")
    public Result<Void> saveXxx(@RequestBody XxxReqDTO requestParam) {
        xxxService.saveXxx(requestParam);
        return Results.success();
    }
}
```

### Service 模板

```java
public interface XxxService extends IService<XxxDO> {
    XxxRespDTO getXxxById(Long id);
    void saveXxx(XxxReqDTO requestParam);
}
```

### ServiceImpl 模板

```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxDO> implements XxxService {
    
    @Override
    public XxxRespDTO getXxxById(Long id) {
        XxxDO xxxDO = baseMapper.selectById(id);
        if (xxxDO == null) {
            throw new ClientException("数据不存在");
        }
        return BeanUtil.toBean(xxxDO, XxxRespDTO.class);
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveXxx(XxxReqDTO requestParam) {
        XxxDO xxxDO = BeanUtil.toBean(requestParam, XxxDO.class);
        baseMapper.insert(xxxDO);
    }
}
```

### DTO 模板

```java
@Data
public class XxxReqDTO {
    @NotBlank(message = "名称不能为空")
    private String name;
    
    @Size(max = 200, message = "描述不能超过200个字符")
    private String description;
}
```

### DO 模板

```java
@Data
@TableName("t_xxx")
public class XxxDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
}
```
