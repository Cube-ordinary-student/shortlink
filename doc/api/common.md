# 接口通用约定

## 1. 统一响应结构

所有接口统一返回 `Result<T>` 结构，序列化为 JSON：

```json
{
  "code": "0",
  "message": "success",
  "data": {},
  "requestId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | string | 返回码，成功时为 `"0"`，失败时为具体错误码 |
| message | string | 返回消息 |
| data | object | 响应数据，可为对象、数组或空 |
| requestId | string | 请求 ID，用于链路追踪与问题定位 |

- 成功判定：`code` 等于 `"0"` 即为成功。
- 失败时 `data` 可为空，`code` 为具体错误码，`message` 为对应错误描述。

## 2. 分页约定

分页接口的 `data` 字段为 `Page` 对象：

```json
{
  "code": "0",
  "message": "success",
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "records": []
  },
  "requestId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| current | long | 当前页码（从 1 开始） |
| size | long | 每页条数 |
| total | long | 数据总数 |
| records | array | 当前页数据列表 |

## 3. 错误码表

### 3.1 基础错误码（admin 与 project 通用）

| 错误码 | 枚举 | 说明 | 适用范围 |
| --- | --- | --- | --- |
| A000001 | CLIENT_ERROR | 用户端错误 | admin / project |
| A000100 | USER_REGISTER_ERROR | 用户注册错误 | admin / project |
| A000110 | USER_NAME_VERIFY_ERROR | 用户名校验失败 | admin / project |
| A000111 | USER_NAME_EXIST_ERROR | 用户名已存在 | admin / project |
| A000112 | USER_NAME_SENSITIVE_ERROR | 用户名包含敏感词 | admin / project |
| A000113 | USER_NAME_SPECIAL_CHARACTER_ERROR | 用户名包含特殊字符 | admin / project |
| A000120 | PASSWORD_VERIFY_ERROR | 密码校验失败 | admin / project |
| A000121 | PASSWORD_SHORT_ERROR | 密码长度不够 | admin / project |
| A000151 | PHONE_VERIFY_ERROR | 手机格式校验失败 | admin / project |
| A000200 | IDEMPOTENT_TOKEN_NULL_ERROR | 幂等Token为空 | admin / project |
| A000201 | IDEMPOTENT_TOKEN_DELETE_ERROR | 幂等Token已被使用或失效 | admin / project |
| A000300 | FLOW_LIMIT_ERROR | 当前系统繁忙，请稍后再试 | 仅 admin |
| B000001 | SERVICE_ERROR | 系统执行出错 | admin / project |
| B000100 | SERVICE_TIMEOUT_ERROR | 系统执行超时 | admin / project |
| C000001 | REMOTE_ERROR | 调用第三方服务出错 | admin / project |

### 3.2 admin 专属错误码（用户相关）

| 错误码 | 枚举 | 说明 |
| --- | --- | --- |
| B000200 | USER_NULL | 用户记录不存在 |
| B000201 | USER_NAME_EXIST | 用户名已存在 |
| B000202 | USER_EXIST | 用户记录已存在 |
| B000203 | USER_SAVE_ERROR | 用户记录新增失败 |

## 4. 认证与请求头约定

除白名单接口外，所有经网关的接口需通过 `TokenValidate` 过滤器鉴权。

### 4.1 请求头

| 请求头 | 必填 | 说明 |
| --- | --- | --- |
| username | 白名单外必填 | 用户名 |
| token | 白名单外必填 | 登录令牌 |

### 4.2 鉴权结果

- **鉴权通过**：网关向请求头注入 `userId`（用户 ID）、`realName`（真实姓名，URL 编码）后转发至下游服务。
- **鉴权失败**：返回 HTTP 401，响应体为：

```json
{ "status": 401, "message": "Token validation error" }
```

### 4.3 白名单（免鉴权）

| 接口 | 说明 |
| --- | --- |
| `/api/short-link/admin/v1/user/login` | 用户登录 |
| `/api/short-link/admin/v1/user/has-username` | 校验用户名是否存在 |
| `POST /api/short-link/admin/v1/user` | 用户注册 |

## 5. 通用请求/响应格式约定

1. **数据格式**：请求与响应均使用 JSON；`Content-Type` 为 `application/json`。
2. **字符编码**：统一使用 UTF-8。
3. **统一响应**：所有接口响应体均遵循第 1 节的 `Result<T>` 结构。
4. **成功码**：`code` 为 `"0"` 表示请求成功。
5. **错误码**：失败时 `code` 为第 3 节错误码表中的具体错误码，`message` 为对应错误描述。
6. **分页**：分页接口的 `data` 遵循第 2 节的 `Page` 结构；获取指定页需传 `current`（页码）与 `size`（每页条数）。
7. **短链接跳转**：跳转接口 `/{short-uri}` 直连 project 服务（8001），不经网关、无需鉴权，返回为重定向响应。