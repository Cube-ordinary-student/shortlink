# 短链接平台 - 编码规范

---

## 1. 概述

本规范旨在统一短链接平台的编码风格，确保代码的可读性、可维护性和一致性。

---

## 2. Java编码规范

### 2.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | PascalCase | `UserController` |
| 方法名 | camelCase | `getUserByUsername` |
| 变量名 | camelCase | `username`, `userDO` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | lowercase | `com.lanyue.shortlink` |

### 2.2 代码格式

- 使用 4 个空格缩进
- 每行最多 120 个字符
- 大括号 `{` 单独占一行
- 方法之间空一行
- 类成员之间空一行

### 2.3 注释规范

- 类注释使用 `/** ... */`
- 方法注释使用 `/** ... */`
- 单行注释使用 `//`
- 注释应清晰说明代码的意图，而非简单重复代码

### 2.4 异常处理

- 使用统一的异常类型
- 在 GlobalExceptionHandler 中统一处理异常
- 避免捕获 `Exception` 后不处理

### 2.5 依赖注入

- 使用 `@RequiredArgsConstructor` 代替 `@Autowired`
- 构造器注入优先于字段注入

---

## 3. JavaScript/TypeScript编码规范

### 3.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 变量 | camelCase | `loginForm` |
| 函数 | camelCase | `queryUser` |
| 类 | PascalCase | `UserService` |
| 常量 | UPPER_SNAKE_CASE | `BASE_URL` |
| 文件 | kebab-case | `small-link-page.js` |

### 3.2 代码格式

- 使用 2 个空格缩进
- 字符串使用单引号 `'`
- 箭头函数优先
- 使用 `const` 和 `let`，避免 `var`

### 3.3 Vue组件规范

- 组件名使用 PascalCase
- Props 定义完整类型
- 事件命名使用 kebab-case
- 避免在模板中使用复杂表达式

### 3.4 API调用规范

- 统一封装 HTTP 请求
- 使用 async/await
- 统一错误处理

---

## 4. SQL编码规范

### 4.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 表名 | snake_case，前缀 t_ | `t_user` |
| 字段名 | snake_case | `user_name` |
| 索引名 | idx_ + 字段名 | `idx_username` |
| 唯一索引 | uk_ + 字段名 | `uk_username` |

### 4.2 SQL格式

- 关键字大写
- 每行一个关键字
- 适当缩进

### 4.3 安全规范

- 使用参数化查询，防止SQL注入
- 避免拼接SQL
- 限制查询返回行数

---

## 5. Git规范

### 5.1 分支管理

| 分支 | 用途 |
|------|------|
| main | 主分支，稳定版本 |
| develop | 开发分支 |
| feature/* | 功能开发分支 |
| bugfix/* | Bug修复分支 |
| hotfix/* | 紧急修复分支 |

### 5.2 提交规范

```
<类型>(<模块>): <描述>

[可选的详细说明]
```

**类型**:
- feat: 新功能
- fix: Bug修复
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试

**示例**:
```
feat(user): 添加用户注册功能

- 实现注册接口
- 添加数据验证
- 更新数据库表结构
```

### 5.3 PR规范

- PR标题清晰描述改动内容
- 包含改动说明
- 关联相关Issue
- 代码审查通过后合并

---

## 6. 日志规范

### 6.1 日志级别

| 级别 | 使用场景 |
|------|----------|
| DEBUG | 详细调试信息，生产环境关闭 |
| INFO | 重要业务操作记录 |
| WARN | 警告信息 |
| ERROR | 错误信息 |

### 6.2 日志格式

```java
log.info("用户登录成功，用户名：{}", username);
log.error("用户登录失败，用户名：{}，错误：{}", username, e.getMessage());
```

---

## 7. 安全规范

### 7.1 密码处理

- 使用 BCrypt 加密密码
- 禁止明文存储密码
- 定期更换密钥

### 7.2 输入验证

- 使用 `@Valid` 注解进行参数校验
- 自定义校验注解处理复杂校验
- 防止XSS攻击

### 7.3 权限控制

- 使用 Token 进行身份验证
- 实现细粒度权限控制
- 日志记录敏感操作

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
