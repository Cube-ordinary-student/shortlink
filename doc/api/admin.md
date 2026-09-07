# 短链接平台（shortlink）admin 模块接口文档

## 1. 概述

| 项目 | 说明 |
| --- | --- |
| 网关 Base URL | `http://localhost:8000` |
| admin 服务实际端口 | `8002` |
| 网关路由 | `/api/short-link/admin/**` → admin 服务（前缀保留） |
| 统一响应结构 | `Result<T>`：`{ "code", "message", "data", "requestId" }`，成功时 `code = "0"` |
| 分页数据结构 | `data` 为 `Page` 对象：`{ "current", "size", "total", "records" }` |

### 1.1 统一响应结构 Result&lt;T&gt;

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| code | string | 返回码，成功为 `"0"`，失败为具体错误码 |
| message | string | 返回消息（成功时为 `null`，失败时为错误信息） |
| data | T / object / null | 响应数据，接口返回类型见各接口定义 |
| requestId | string | 请求 ID（链路追踪标识） |

成功响应示例（无返回数据）：

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

### 1.2 认证方式

- 认证信息通过请求头传递：`username`（用户名，必填）、`token`（登录令牌）。另有 `userId`、`realName` 两个可选请求头由前端透传，供服务端将用户信息写入上下文。
- 白名单接口（无需认证）：`POST /api/short-link/admin/v1/user`、`POST /api/short-link/admin/v1/user/login`、`GET /api/short-link/admin/v1/user/has-username`。
- 除白名单接口外，其余接口均需携带 `username` 请求头。
- 所有接口均经过「用户操作流量风控过滤器」，超出访问频率时返回错误码 `A000300`。

### 1.3 请求体校验说明

本模块所有请求 DTO 均未使用 JSR-303 校验注解（`@NotBlank` / `@NotNull` / `@NotEmpty` 等），Controller 层也未使用 `@Valid`，因此框架层面不做必填校验。下文「请求体字段」表中的「是否必填」均标注为「否」；字段是否为业务必需，见「说明」列。

### 1.4 数据类型约定

| Java 类型 | 文档数据类型 |
| --- | --- |
| String | string |
| Integer / int | int |
| Long / long | long |
| Boolean | boolean |
| Double | double |
| Date / LocalDateTime | string（格式在说明中注明） |
| List&lt;T&gt; | array |
| Page&lt;T&gt; | object（`{current, size, total, records}`） |

### 1.5 错误码说明

| 错误码 | 枚举常量 | 说明 |
| --- | --- | --- |
| A000001 | CLIENT_ERROR | 客户端错误（含通过 `new ClientException("${message}")` 抛出的业务异常，错误信息为自定义 message） |
| A000100 | USER_REGISTER_ERROR | 用户注册错误 |
| A000110 | USER_NAME_VERIFY_ERROR | 用户名校验失败 |
| A000111 | USER_NAME_EXIST_ERROR | 用户名已存在 |
| A000112 | USER_NAME_SENSITIVE_ERROR | 用户名包含敏感词 |
| A000113 | USER_NAME_SPECIAL_CHARACTER_ERROR | 用户名包含特殊字符 |
| A000120 | PASSWORD_VERIFY_ERROR | 密码校验失败 |
| A000121 | PASSWORD_SHORT_ERROR | 密码长度不够 |
| A000151 | PHONE_VERIFY_ERROR | 手机格式校验失败 |
| A000200 | IDEMPOTENT_TOKEN_NULL_ERROR | 幂等 Token 为空 |
| A000201 | IDEMPOTENT_TOKEN_DELETE_ERROR | 幂等 Token 已被使用或失效 |
| A000300 | FLOW_LIMIT_ERROR | 当前系统繁忙，请稍后再试（流量风控） |
| B000001 | SERVICE_ERROR | 系统执行出错（含通过 `new ServiceException("${message}")` 抛出的服务端异常） |
| B000100 | SERVICE_TIMEOUT_ERROR | 系统执行超时 |
| B000200 | USER_NULL | 用户记录不存在 |
| B000201 | USER_NAME_EXIST | 用户名已存在 |
| B000202 | USER_EXIST | 用户记录已存在 |
| B000203 | USER_SAVE_ERROR | 用户记录新增失败 |
| C000001 | REMOTE_ERROR | 调用第三方服务出错（Feign 调用 short-link-project 异常） |

---

## 2. 接口清单

| 序号 | 方法 | 路径 | 功能 | 认证 |
| --- | --- | --- | --- | --- |
| 1 | GET | /api/short-link/admin/v1/user/{username} | 根据用户名查询用户信息（脱敏） | 是 |
| 2 | GET | /api/short-link/admin/v1/actual/user/{username} | 根据用户名查询无脱敏用户信息 | 是 |
| 3 | GET | /api/short-link/admin/v1/user/has-username | 查询用户名是否存在 | 白名单 |
| 4 | POST | /api/short-link/admin/v1/user | 注册用户 | 白名单 |
| 5 | PUT | /api/short-link/admin/v1/user | 修改用户 | 是 |
| 6 | POST | /api/short-link/admin/v1/user/login | 用户登录 | 白名单 |
| 7 | GET | /api/short-link/admin/v1/user/check-login | 检查用户是否登录 | 是 |
| 8 | DELETE | /api/short-link/admin/v1/user/logout | 用户退出登录 | 是 |
| 9 | POST | /api/short-link/admin/v1/group | 新增短链接分组 | 是 |
| 10 | GET | /api/short-link/admin/v1/group | 查询短链接分组集合 | 是 |
| 11 | PUT | /api/short-link/admin/v1/group | 修改短链接分组名称 | 是 |
| 12 | DELETE | /api/short-link/admin/v1/group | 删除短链接分组 | 是 |
| 13 | POST | /api/short-link/admin/v1/group/sort | 排序短链接分组 | 是 |
| 14 | POST | /api/short-link/admin/v1/create | 创建短链接 | 是 |
| 15 | POST | /api/short-link/admin/v1/create/batch | 批量创建短链接（下载 Excel） | 是 |
| 16 | POST | /api/short-link/admin/v1/update | 修改短链接 | 是 |
| 17 | GET | /api/short-link/admin/v1/page | 分页查询短链接 | 是 |
| 18 | POST | /api/short-link/admin/v1/recycle-bin/save | 保存回收站 | 是 |
| 19 | GET | /api/short-link/admin/v1/recycle-bin/page | 分页查询回收站短链接 | 是 |
| 20 | POST | /api/short-link/admin/v1/recycle-bin/recover | 恢复短链接 | 是 |
| 21 | POST | /api/short-link/admin/v1/recycle-bin/remove | 移除短链接 | 是 |
| 22 | GET | /api/short-link/admin/v1/stats | 单个短链接指定时间监控数据 | 是 |
| 23 | GET | /api/short-link/admin/v1/stats/group | 分组短链接指定时间监控数据 | 是 |
| 24 | GET | /api/short-link/admin/v1/stats/access-record | 单个短链接访问记录监控数据 | 是 |
| 25 | GET | /api/short-link/admin/v1/stats/access-record/group | 分组短链接访问记录监控数据 | 是 |
| 26 | GET | /api/short-link/admin/v1/title | 根据 URL 获取网站标题 | 是 |

---

## 3. 用户管理接口（UserController）

### 3.1 根据用户名查询用户信息（脱敏）

- **功能描述**：根据用户名查询用户信息，手机号返回时脱敏。
- **请求 URL**：`/api/short-link/admin/v1/user/{username}`
- **请求方法**：`GET`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 路径参数 | string | 是 | - | 用户名 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 UserRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| id | long | 用户 ID |
| username | string | 用户名 |
| realName | string | 真实姓名 |
| phone | string | 手机号（脱敏） |
| mail | string | 邮箱 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000200 | 用户记录不存在 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/user/lanyue" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "id": 1,
    "username": "lanyue",
    "realName": "蓝月",
    "phone": "138****8888",
    "mail": "lanyue@example.com"
  },
  "requestId": "..."
}
```

---

### 3.2 根据用户名查询无脱敏用户信息

- **功能描述**：根据用户名查询用户信息，手机号不脱敏（返回原始信息）。
- **请求 URL**：`/api/short-link/admin/v1/actual/user/{username}`
- **请求方法**：`GET`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 路径参数 | string | 是 | - | 用户名 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 UserActualRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| id | long | 用户 ID |
| username | string | 用户名 |
| realName | string | 真实姓名 |
| phone | string | 手机号（不脱敏） |
| mail | string | 邮箱 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000200 | 用户记录不存在 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/actual/user/lanyue" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "id": 1,
    "username": "lanyue",
    "realName": "蓝月",
    "phone": "13812348888",
    "mail": "lanyue@example.com"
  },
  "requestId": "..."
}
```

---

### 3.3 查询用户名是否存在

- **功能描述**：查询用户名是否可用（未注册）。
- **请求 URL**：`/api/short-link/admin/v1/user/has-username`
- **请求方法**：`GET`
- **认证**：白名单（无需认证）

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 查询参数 | string | 是 | - | 用户名 |

**响应数据结构**（`data` 为 Boolean）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| data | boolean | `true` 表示用户名不存在（可用），`false` 表示已存在 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/user/has-username?username=lanyue"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": true,
  "requestId": "..."
}
```

---

### 3.4 注册用户

- **功能描述**：注册新用户，注册成功后自动创建「默认分组」。
- **请求 URL**：`/api/short-link/admin/v1/user`
- **请求方法**：`POST`
- **认证**：白名单（无需认证）

**请求体字段**（UserRegisterReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| username | string | 否 | - | 用户名（业务必需） |
| password | string | 否 | - | 密码（业务必需） |
| realName | string | 否 | - | 真实姓名 |
| phone | string | 否 | - | 手机号 |
| mail | string | 否 | - | 邮箱 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000201 | 用户名已存在 |
| B000202 | 用户记录已存在（并发插入唯一键冲突） |
| B000203 | 用户记录新增失败 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "lanyue",
    "password": "123456",
    "realName": "蓝月",
    "phone": "13812348888",
    "mail": "lanyue@example.com"
  }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 3.5 修改用户

- **功能描述**：修改当前登录用户信息。
- **请求 URL**：`/api/short-link/admin/v1/user`
- **请求方法**：`PUT`
- **认证**：是

**请求体字段**（UserUpdateReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| username | string | 否 | - | 用户名（业务必需，须与当前登录用户一致） |
| password | string | 否 | - | 密码 |
| realName | string | 否 | - | 真实姓名 |
| phone | string | 否 | - | 手机号 |
| mail | string | 否 | - | 邮箱 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| A000001 | 当前登录用户修改请求异常（请求体 username 与当前登录用户不一致） |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X PUT "http://localhost:8000/api/short-link/admin/v1/user" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{
    "username": "lanyue",
    "password": "654321",
    "realName": "蓝月",
    "phone": "13812348888",
    "mail": "lanyue@example.com"
  }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 3.6 用户登录

- **功能描述**：用户登录，校验用户名密码成功后返回登录 Token；已登录时复用已有 Token 并刷新有效期。
- **请求 URL**：`/api/short-link/admin/v1/user/login`
- **请求方法**：`POST`
- **认证**：白名单（无需认证）

**请求体字段**（UserLoginReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| username | string | 否 | - | 用户名（业务必需） |
| password | string | 否 | - | 密码（业务必需） |

**响应数据结构**（`data` 为 UserLoginRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| token | string | 用户登录 Token |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| A000001 | 用户不存在（message：`用户不存在`）、用户登录错误（message：`用户登录错误`） |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "lanyue",
    "password": "123456"
  }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "token": "8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
  },
  "requestId": "..."
}
```

---

### 3.7 检查用户是否登录

- **功能描述**：检查指定用户名与 Token 是否处于登录状态。
- **请求 URL**：`/api/short-link/admin/v1/user/check-login`
- **请求方法**：`GET`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 查询参数 | string | 是 | - | 用户名 |
| token | 查询参数 | string | 是 | - | 登录令牌 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 Boolean）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| data | boolean | `true` 表示已登录，`false` 表示未登录 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/user/check-login?username=lanyue&token=8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": true,
  "requestId": "..."
}
```

---

### 3.8 用户退出登录

- **功能描述**：用户退出登录，删除登录会话。
- **请求 URL**：`/api/short-link/admin/v1/user/logout`
- **请求方法**：`DELETE`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 查询参数 | string | 是 | - | 用户名 |
| token | 查询参数 | string | 是 | - | 登录令牌 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| A000001 | 用户 Token 不存在或用户未登录（message：`用户Token不存在或用户未登录`） |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X DELETE "http://localhost:8000/api/short-link/admin/v1/user/logout?username=lanyue&token=8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

## 4. 分组管理接口（GroupController）

### 4.1 新增短链接分组

- **功能描述**：为当前登录用户新增短链接分组。
- **请求 URL**：`/api/short-link/admin/v1/group`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（ShortLinkGroupSaveReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| name | string | 否 | - | 分组名（业务必需） |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| A000001 | 已超出最大分组数（message：`已超出最大分组数：%d`） |
| B000001 | 生成分组标识频繁（系统执行出错） |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/group" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{ "name": "电商推广" }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 4.2 查询短链接分组集合

- **功能描述**：查询当前登录用户的全部短链接分组，并统计每个分组下的短链接数量。
- **请求 URL**：`/api/short-link/admin/v1/group`
- **请求方法**：`GET`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 `List<ShortLinkGroupRespDTO>`，即 array）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| gid | string | 分组标识 |
| name | string | 分组名称 |
| sortOrder | int | 分组排序 |
| shortLinkCount | int | 分组下短链接数量 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 统计数量服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/group" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": [
    {
      "gid": "61VfBn6P",
      "name": "默认分组",
      "sortOrder": 1,
      "shortLinkCount": 5
    },
    {
      "gid": "8XzY2kLm",
      "name": "电商推广",
      "sortOrder": 0,
      "shortLinkCount": 2
    }
  ],
  "requestId": "..."
}
```

---

### 4.3 修改短链接分组名称

- **功能描述**：修改当前登录用户指定分组的名称。
- **请求 URL**：`/api/short-link/admin/v1/group`
- **请求方法**：`PUT`
- **认证**：是

**请求体字段**（ShortLinkGroupUpdateReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识（业务必需） |
| name | string | 否 | - | 分组名（业务必需） |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X PUT "http://localhost:8000/api/short-link/admin/v1/group" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{ "gid": "8XzY2kLm", "name": "品牌推广" }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 4.4 删除短链接分组

- **功能描述**：删除当前登录用户指定分组（软删除）。
- **请求 URL**：`/api/short-link/admin/v1/group`
- **请求方法**：`DELETE`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| gid | 查询参数 | string | 是 | - | 分组标识 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X DELETE "http://localhost:8000/api/short-link/admin/v1/group?gid=8XzY2kLm" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 4.5 排序短链接分组

- **功能描述**：批量设置短链接分组排序。
- **请求 URL**：`/api/short-link/admin/v1/group/sort`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（`List<ShortLinkGroupSortReqDTO>`，即 array）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| sortOrder | int | 否 | - | 排序值（升序） |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/group/sort" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '[
    { "gid": "61VfBn6P", "sortOrder": 1 },
    { "gid": "8XzY2kLm", "sortOrder": 0 }
  ]'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

## 5. 短链接管理接口（ShortLinkController）

### 5.1 创建短链接

- **功能描述**：创建短链接（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/create`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（ShortLinkCreateReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| domain | string | 否 | - | 域名 |
| originUrl | string | 否 | - | 原始链接 |
| gid | string | 否 | - | 分组标识 |
| createdType | int | 否 | - | 创建类型：`0` 接口创建，`1` 控制台创建 |
| validDateType | int | 否 | - | 有效期类型：`0` 永久有效，`1` 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 否 | - | 描述 |

**响应数据结构**（`data` 为 ShortLinkCreateRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| gid | string | 分组信息 |
| originUrl | string | 原始链接 |
| fullShortUrl | string | 短链接（完整短链接） |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

> 注：short-link-project 返回的业务错误码会通过 `Result.code` 透传。

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/create" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{
    "domain": "s.lanyue.cn",
    "originUrl": "https://www.example.com/article/123",
    "gid": "61VfBn6P",
    "createdType": 1,
    "validDateType": 0,
    "validDate": null,
    "describe": "测试短链接"
  }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "gid": "61VfBn6P",
    "originUrl": "https://www.example.com/article/123",
    "fullShortUrl": "http://s.lanyue.cn/aB3dEf"
  },
  "requestId": "..."
}
```

---

### 5.2 批量创建短链接（下载 Excel）

- **功能描述**：批量创建短链接，成功后直接以 Excel（.xlsx）文件形式下载创建结果。
- **请求 URL**：`/api/short-link/admin/v1/create/batch`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（ShortLinkBatchCreateReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| originUrls | array(string) | 否 | - | 原始链接集合 |
| describes | array(string) | 否 | - | 描述集合 |
| gid | string | 否 | - | 分组标识 |
| createdType | int | 否 | - | 创建类型：`0` 接口创建，`1` 控制台创建 |
| validDateType | int | 否 | - | 有效期类型：`0` 永久有效，`1` 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |

**响应说明**

- 该接口并非返回 JSON，而是直接向浏览器写入 Excel 文件下载流。
- 响应 Content-Type：`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`。
- 文件名：`批量创建短链接-SaaS短链接系统.xlsx`。
- Excel 表头与数据列（对应 ShortLinkBaseInfoRespDTO）：

| 列名 | 字段名 | 说明 |
| --- | --- | --- |
| 标题 | describe | 描述信息 |
| 短链接 | fullShortUrl | 短链接 |
| 原始链接 | originUrl | 原始链接 |

（数据源结构 ShortLinkBatchCreateRespDTO：`total`（int，成功数量）、`baseLinkInfos`（array，批量创建返回参数）。）

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/create/batch" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  --output result.xlsx \
  -d '{
    "originUrls": ["https://www.example.com/a", "https://www.example.com/b"],
    "describes": ["链接A", "链接B"],
    "gid": "61VfBn6P",
    "createdType": 1,
    "validDateType": 0,
    "validDate": null
  }'
```

**响应示例**（文件下载，无 JSON 响应体）

---

### 5.3 修改短链接

- **功能描述**：修改短链接（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/update`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（ShortLinkUpdateReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| originUrl | string | 否 | - | 原始链接 |
| fullShortUrl | string | 否 | - | 完整短链接 |
| originGid | string | 否 | - | 原始分组标识 |
| gid | string | 否 | - | 分组标识 |
| validDateType | int | 否 | - | 有效期类型：`0` 永久有效，`1` 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 否 | - | 描述 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/update" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{
    "originUrl": "https://www.example.com/article/456",
    "fullShortUrl": "http://s.lanyue.cn/aB3dEf",
    "originGid": "61VfBn6P",
    "gid": "8XzY2kLm",
    "validDateType": 0,
    "validDate": null,
    "describe": "修改后的描述"
  }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 5.4 分页查询短链接

- **功能描述**：分页查询指定分组下的短链接列表。
- **请求 URL**：`/api/short-link/admin/v1/page`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkPageReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| gid | 查询参数 | string | 否 | - | 分组标识 |
| orderTag | 查询参数 | string | 否 | - | 排序标识 |
| current | 查询参数 | long | 否 | 1 | 当前页码 |
| size | 查询参数 | long | 否 | 10 | 每页数量 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 `Page<ShortLinkPageRespDTO>`，即 `{current, size, total, records}`）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| current | long | 当前页码 |
| size | long | 每页数量 |
| total | long | 记录总数 |
| records | array | 短链接分页数据，元素结构见下表 |

records 元素（ShortLinkPageRespDTO）：

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| id | long | id |
| domain | string | 域名 |
| shortUri | string | 短链接 |
| fullShortUrl | string | 完整短链接 |
| originUrl | string | 原始链接 |
| gid | string | 分组标识 |
| validDateType | int | 有效期类型：`0` 永久有效，`1` 自定义 |
| enableStatus | int | 启用标识：`0` 启用，`1` 未启用 |
| validDate | string | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| createTime | string | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 描述 |
| favicon | string | 网站标识 |
| totalPv | int | 历史 PV |
| todayPv | int | 今日 PV |
| totalUv | int | 历史 UV |
| todayUv | int | 今日 UV |
| totalUip | int | 历史 UIP |
| todayUip | int | 今日 UIP |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/page?gid=61VfBn6P&orderTag=&current=1&size=10" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "current": 1,
    "size": 10,
    "total": 5,
    "records": [
      {
        "id": 1,
        "domain": "s.lanyue.cn",
        "shortUri": "aB3dEf",
        "fullShortUrl": "http://s.lanyue.cn/aB3dEf",
        "originUrl": "https://www.example.com/article/123",
        "gid": "61VfBn6P",
        "validDateType": 0,
        "enableStatus": 0,
        "validDate": null,
        "createTime": "2026-09-07 10:00:00",
        "describe": "测试短链接",
        "favicon": "https://www.example.com/favicon.ico",
        "totalPv": 100,
        "todayPv": 10,
        "totalUv": 80,
        "todayUv": 8,
        "totalUip": 70,
        "todayUip": 7
      }
    ]
  },
  "requestId": "..."
}
```

---

## 6. 回收站接口（RecycleBinController）

### 6.1 保存回收站

- **功能描述**：将短链接移入回收站（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/recycle-bin/save`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（RecycleBinSaveReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/recycle-bin/save" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{ "gid": "61VfBn6P", "fullShortUrl": "http://s.lanyue.cn/aB3dEf" }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 6.2 分页查询回收站短链接

- **功能描述**：分页查询当前登录用户回收站中的短链接（分组范围由后端根据当前用户自动确定）。
- **请求 URL**：`/api/short-link/admin/v1/recycle-bin/page`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkRecycleBinPageReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| current | 查询参数 | long | 否 | 1 | 当前页码 |
| size | 查询参数 | long | 否 | 10 | 每页数量 |
| gidList | 查询参数 | array(string) | 否 | - | 分组标识集合（后端会根据当前登录用户的分组覆盖，客户端无需传） |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 `Page<ShortLinkPageRespDTO>`，即 `{current, size, total, records}`）

records 元素结构与「5.4 分页查询短链接」的 ShortLinkPageRespDTO 一致（见 5.4）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| B000001 | 用户无分组信息（系统执行出错），或系统执行出错 |
| C000001 | 调用 short-link-project 服务出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/recycle-bin/page?current=1&size=10" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "current": 1,
    "size": 10,
    "total": 1,
    "records": [
      {
        "id": 1,
        "domain": "s.lanyue.cn",
        "shortUri": "aB3dEf",
        "fullShortUrl": "http://s.lanyue.cn/aB3dEf",
        "originUrl": "https://www.example.com/article/123",
        "gid": "61VfBn6P",
        "validDateType": 0,
        "enableStatus": 0,
        "validDate": null,
        "createTime": "2026-09-07 10:00:00",
        "describe": "测试短链接",
        "favicon": "https://www.example.com/favicon.ico",
        "totalPv": 100,
        "todayPv": 10,
        "totalUv": 80,
        "todayUv": 8,
        "totalUip": 70,
        "todayUip": 7
      }
    ]
  },
  "requestId": "..."
}
```

---

### 6.3 恢复短链接

- **功能描述**：从回收站恢复短链接（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/recycle-bin/recover`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（RecycleBinRecoverReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/recycle-bin/recover" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{ "gid": "61VfBn6P", "fullShortUrl": "http://s.lanyue.cn/aB3dEf" }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

### 6.4 移除短链接

- **功能描述**：从回收站彻底移除短链接（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/recycle-bin/remove`
- **请求方法**：`POST`
- **认证**：是

**请求体字段**（RecycleBinRemoveReqDTO）

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

**响应数据结构**

无返回数据（`data` 为 `null`）。

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/recycle-bin/remove" \
  -H "Content-Type: application/json" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c" \
  -d '{ "gid": "61VfBn6P", "fullShortUrl": "http://s.lanyue.cn/aB3dEf" }'
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": "..."
}
```

---

## 7. 监控统计接口（ShortLinkStatsController）

### 7.1 访问单个短链接指定时间内监控数据

- **功能描述**：查询单个短链接在指定时间内的监控统计数据（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/stats`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkStatsReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| fullShortUrl | 查询参数 | string | 否 | - | 完整短链接 |
| gid | 查询参数 | string | 否 | - | 分组标识 |
| startDate | 查询参数 | string | 否 | - | 开始日期 |
| endDate | 查询参数 | string | 否 | - | 结束日期 |
| enableStatus | 查询参数 | int | 否 | - | 启用标识：`0` 启用，`1` 未启用 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 ShortLinkStatsRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| pv | int | 访问量 |
| uv | int | 独立访客数 |
| uip | int | 独立 IP 数 |
| daily | array | 基础访问详情（元素结构见 7.5 表） |
| localeCnStats | array | 地区访问详情（仅国内，元素：`cnt` int、`locale` string、`ratio` double） |
| hourStats | array(int) | 小时访问详情 |
| topIpStats | array | 高频访问 IP 详情（元素：`cnt` int、`ip` string） |
| weekdayStats | array(int) | 一周访问详情 |
| browserStats | array | 浏览器访问详情（元素：`cnt` int、`browser` string、`ratio` double） |
| osStats | array | 操作系统访问详情（元素：`cnt` int、`os` string、`ratio` double） |
| uvTypeStats | array | 访客访问类型详情（元素：`cnt` int、`uvType` string、`ratio` double） |
| deviceStats | array | 访问设备类型详情（元素：`cnt` int、`device` string、`ratio` double） |
| networkStats | array | 访问网络类型详情（元素：`cnt` int、`network` string、`ratio` double） |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/stats?fullShortUrl=http%3A%2F%2Fs.lanyue.cn%2FaB3dEf&gid=61VfBn6P&enableStatus=0&startDate=2026-09-01&endDate=2026-09-07" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "pv": 100,
    "uv": 80,
    "uip": 70,
    "daily": [
      { "date": "2026-09-01", "pv": 20, "uv": 16, "uip": 14 }
    ],
    "localeCnStats": [
      { "cnt": 50, "locale": "北京", "ratio": 0.5 }
    ],
    "hourStats": [1, 2, 3],
    "topIpStats": [
      { "cnt": 10, "ip": "1.2.3.4" }
    ],
    "weekdayStats": [10, 11, 12, 13, 14, 15, 16],
    "browserStats": [
      { "cnt": 60, "browser": "Chrome", "ratio": 0.6 }
    ],
    "osStats": [
      { "cnt": 55, "os": "Windows", "ratio": 0.55 }
    ],
    "uvTypeStats": [
      { "cnt": 70, "uvType": "新访客", "ratio": 0.7 }
    ],
    "deviceStats": [
      { "cnt": 40, "device": "PC", "ratio": 0.4 }
    ],
    "networkStats": [
      { "cnt": 30, "network": "4G", "ratio": 0.3 }
    ]
  },
  "requestId": "..."
}
```

---

### 7.2 访问分组短链接指定时间内监控数据

- **功能描述**：查询指定分组短链接在指定时间内的监控统计数据（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/stats/group`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkGroupStatsReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| gid | 查询参数 | string | 否 | - | 分组标识 |
| startDate | 查询参数 | string | 否 | - | 开始日期 |
| endDate | 查询参数 | string | 否 | - | 结束日期 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 ShortLinkStatsRespDTO，结构同 7.1）

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/stats/group?gid=61VfBn6P&startDate=2026-09-01&endDate=2026-09-07" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**（结构同 7.1 响应示例，略）

---

### 7.3 访问单个短链接指定时间内访问记录监控数据

- **功能描述**：分页查询单个短链接指定时间内的访问记录监控数据（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/stats/access-record`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkStatsAccessRecordReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| fullShortUrl | 查询参数 | string | 否 | - | 完整短链接 |
| gid | 查询参数 | string | 否 | - | 分组标识 |
| startDate | 查询参数 | string | 否 | - | 开始日期 |
| endDate | 查询参数 | string | 否 | - | 结束日期 |
| enableStatus | 查询参数 | int | 否 | - | 启用标识：`0` 启用，`1` 未启用 |
| current | 查询参数 | long | 否 | 1 | 当前页码 |
| size | 查询参数 | long | 否 | 10 | 每页数量 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 `Page<ShortLinkStatsAccessRecordRespDTO>`，即 `{current, size, total, records}`）

records 元素（ShortLinkStatsAccessRecordRespDTO）：

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| uvType | string | 访客类型 |
| browser | string | 浏览器 |
| os | string | 操作系统 |
| ip | string | ip |
| network | string | 访问网络 |
| device | string | 访问设备 |
| locale | string | 地区 |
| user | string | 用户信息 |
| createTime | string | 访问时间，格式 `yyyy-MM-dd HH:mm:ss` |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/stats/access-record?fullShortUrl=http%3A%2F%2Fs.lanyue.cn%2FaB3dEf&gid=61VfBn6P&startDate=2026-09-01&endDate=2026-09-07&enableStatus=0&current=1&size=10" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "records": [
      {
        "uvType": "新访客",
        "browser": "Chrome",
        "os": "Windows",
        "ip": "1.2.3.4",
        "network": "4G",
        "device": "PC",
        "locale": "北京",
        "user": "lanyue",
        "createTime": "2026-09-07 10:00:00"
      }
    ]
  },
  "requestId": "..."
}
```

---

### 7.4 访问分组短链接指定时间内访问记录监控数据

- **功能描述**：分页查询指定分组短链接在指定时间内的访问记录监控数据（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/stats/access-record/group`
- **请求方法**：`GET`
- **认证**：是

**请求参数**（查询参数，绑定 ShortLinkGroupStatsAccessRecordReqDTO）

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| gid | 查询参数 | string | 否 | - | 分组标识 |
| startDate | 查询参数 | string | 否 | - | 开始日期 |
| endDate | 查询参数 | string | 否 | - | 结束日期 |
| current | 查询参数 | long | 否 | 1 | 当前页码 |
| size | 查询参数 | long | 否 | 10 | 每页数量 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 `Page<ShortLinkStatsAccessRecordRespDTO>`，元素结构同 7.3）

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/stats/access-record/group?gid=61VfBn6P&startDate=2026-09-01&endDate=2026-09-07&current=1&size=10" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**（结构同 7.3 响应示例，略）

---

### 7.5 daily 元素结构（ShortLinkStatsAccessDailyRespDTO）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| date | string | 日期，格式 `yyyy-MM-dd` |
| pv | int | 访问量 |
| uv | int | 独立访客数 |
| uip | int | 独立 IP 数 |

---

## 8. URL 标题接口（UrlTitleController）

### 8.1 根据 URL 获取对应网站的标题

- **功能描述**：根据 URL 获取对应网站的标题（Feign 透传至 short-link-project）。
- **请求 URL**：`/api/short-link/admin/v1/title`
- **请求方法**：`GET`
- **认证**：是

**请求参数**

| 参数名称 | 位置 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| url | 查询参数 | string | 是 | - | 目标网站地址 |
| username | 请求头 | string | 是 | - | 当前登录用户名 |
| token | 请求头 | string | 是 | - | 登录令牌 |

**响应数据结构**（`data` 为 String）

| 字段名 | 数据类型 | 说明 |
| --- | --- | --- |
| data | string | 网站标题 |

**错误码说明**

| 错误码 | 说明 |
| --- | --- |
| C000001 | 调用 short-link-project 服务出错 |
| B000001 | 系统执行出错 |
| A000300 | 请求过于频繁（流量风控） |

**请求示例**

```bash
curl -X GET "http://localhost:8000/api/short-link/admin/v1/title?url=https%3A%2F%2Fwww.example.com" \
  -H "username: lanyue" \
  -H "token: 8f9b6e1c-4a2d-4b7e-9c3f-1d2e3f4a5b6c"
```

**响应示例**

```json
{
  "code": "0",
  "message": null,
  "data": "Example Domain",
  "requestId": "..."
}
```