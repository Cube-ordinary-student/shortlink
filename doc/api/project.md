# 短链接平台 project 模块接口文档

## 1. 概述

| 项目 | 说明 |
| --- | --- |
| 服务名称 | short-link-project |
| 服务实际端口 | 8001 |
| 网关 Base URL | http://localhost:8000 |
| 网关路由 | `/api/short-link/**` → project 服务（前缀保留） |
| 短链接跳转入口 | http://localhost:8001（直连，不经网关） |

说明：

- 除短链接跳转接口（`/{short-uri}`）与「短链接不存在跳转页面」（`/page/notfound`）外，其余接口均以 `/api/short-link/v1/...` 为前缀，经网关访问（网关 Base URL 为 http://localhost:8000，前缀保留），实际转发到 project 服务（端口 8001）。
- 认证：经网关的 `/api/short-link/**` 接口均需认证，请求头需携带 `username`、`token`；project 侧无白名单，不在本服务内做强制鉴权。
  - project 侧通过 `UserTransmitInterceptor` 拦截器读取请求头 `username`、`userId`、`realName` 写入用户上下文（`userId`、`realName` 为可选上下文透传字段）。
- 短链接跳转接口 `/{short-uri}` 位于根路径（无 `/api` 前缀），直连 http://localhost:8001/{short-uri}，返回 302 重定向，无需认证。
- 统一响应结构为 `Result<T>`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| code | string | 返回码，成功为 `"0"` |
| message | string | 返回消息 |
| data | T | 响应数据 |
| requestId | string | 请求 ID |

- 分页接口 `data` 为 `IPage`（MyBatis-Plus Page）结构，核心字段如下：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| current | long | 当前页码 |
| size | long | 每页条数 |
| total | long | 总记录数 |
| records | array | 当前页记录列表 |

- 本模块请求体 DTO 均**未**添加 `@NotBlank / @NotNull / @NotEmpty` 等校验注解，因此请求体字段的「是否必填」在框架校验层面均为「否」（由业务逻辑自行判断，缺少关键字段时由服务返回业务错误或异常）。
- 认证请求头（`username`、`token`）在各 API 接口中统一要求，下文各接口的「请求头」部分不再重复列出完整说明，仅列出与本接口强相关的说明。

## 2. 错误码说明

统一取自 `BaseErrorCode`：

| 错误码 | 说明 | 异常类型 |
| --- | --- | --- |
| A000001 | 用户端错误（客户端错误） | ClientException（默认） |
| A000100 | 用户注册错误 | - |
| A000110 | 用户名校验失败 | - |
| A000111 | 用户名已存在 | - |
| A000112 | 用户名包含敏感词 | - |
| A000113 | 用户名包含特殊字符 | - |
| A000120 | 密码校验失败 | - |
| A000121 | 密码长度不够 | - |
| A000151 | 手机格式校验失败 | - |
| A000200 | 幂等 Token 为空 | - |
| A000201 | 幂等 Token 已被使用或失效 | - |
| B000001 | 系统执行出错 | ServiceException（默认） |
| B000100 | 系统执行超时 | - |
| C000001 | 调用第三方服务出错 | RemoteException（默认） |

## 3. 接口清单

| # | 方法 | 路径 | 接口名称 |
| --- | --- | --- | --- |
| 1 | GET | `/{short-uri}` | 短链接跳转 |
| 2 | POST | `/api/short-link/v1/create` | 创建短链接 |
| 3 | POST | `/api/short-link/v1/create/by-lock` | 通过分布式锁创建短链接 |
| 4 | POST | `/api/short-link/v1/create/batch` | 批量创建短链接 |
| 5 | POST | `/api/short-link/v1/update` | 修改短链接 |
| 6 | GET | `/api/short-link/v1/page` | 分页查询短链接 |
| 7 | GET | `/api/short-link/v1/count` | 查询分组内短链接数量 |
| 8 | POST | `/api/short-link/v1/recycle-bin/save` | 移入回收站 |
| 9 | GET | `/api/short-link/v1/recycle-bin/page` | 分页查询回收站短链接 |
| 10 | POST | `/api/short-link/v1/recycle-bin/recover` | 恢复短链接 |
| 11 | POST | `/api/short-link/v1/recycle-bin/remove` | 移除短链接 |
| 12 | GET | `/api/short-link/v1/stats` | 单条短链接监控数据 |
| 13 | GET | `/api/short-link/v1/stats/group` | 分组短链接监控数据 |
| 14 | GET | `/api/short-link/v1/stats/access-record` | 单条短链接访问记录 |
| 15 | GET | `/api/short-link/v1/stats/access-record/group` | 分组短链接访问记录 |
| 16 | GET | `/api/short-link/v1/title` | 获取网页标题 |
| 17 | GET | `/page/notfound` | 短链接不存在跳转页面 |

---

## 4. 接口详情

### 4.1 短链接跳转

- **接口名称**：短链接跳转
- **功能描述**：根据短链接后缀（`short-uri`）还原原始链接。服务先查 Redis 缓存获取原始链接，命中则记录访问统计并返回 302 重定向到原始链接；缓存未命中且布隆过滤器不包含该短链接或已被标记为空时，重定向到 `/page/notfound`；否则加分布式锁回源查询数据库，命中后写回缓存、记录统计并 302 重定向。
- **请求 URL**：`http://localhost:8001/{short-uri}`
- **请求方法**：GET
- **请求参数**：

| 参数名称 | 类型 | 位置 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- | --- |
| short-uri | string | 路径参数 | 是 | - | 短链接后缀（短链 URI） |
| uv | string | Cookie（可选，由服务端下发） | 否 | - | UV 访客标识 Cookie |

- **请求头**：无需认证。
- **响应数据结构**：无响应体（302 重定向）。

| 响应 | 说明 |
| --- | --- |
| 302 Location: 原始链接 | 命中有效短链接，Location 为原始链接 |
| 302 Location: /page/notfound | 短链接不存在 / 已失效 / 已删除 |

- **错误码说明**：无（不返回 `Result` 结构，直接 302）。
- **请求示例**：

```
GET http://localhost:8001/abC123 HTTP/1.1
Host: localhost:8001
Cookie: uv=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

- **响应示例（命中）**：

```
HTTP/1.1 302 Found
Location: https://www.example.com/some/very/long/original/url
```

- **响应示例（未命中）**：

```
HTTP/1.1 302 Found
Location: /page/notfound
```

---

### 4.2 创建短链接

- **接口名称**：创建短链接
- **功能描述**：创建一条短链接。会校验原始链接域名白名单（默认关闭），生成短链后缀，写入短链接与跳转映射并写入 Redis 缓存，返回完整短链接。
- **请求 URL**：`/api/short-link/v1/create`
- **请求方法**：POST（接口同时受 Sentinel 资源 `create_short-link` 流控保护）
- **请求参数**：

请求头：

| 参数名称 | 数据类型 | 是否必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 认证用户名（由网关校验） |
| token | string | 是 | 认证令牌（由网关校验） |
| userId | string | 否 | 用户上下文透传字段（project 侧拦截器读取） |
| realName | string | 否 | 用户上下文透传字段（project 侧拦截器读取） |

请求体（JSON，字段均未加校验注解，框架层面均非必填）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| domain | string | 否 | - | 域名 |
| originUrl | string | 否 | - | 原始链接 |
| gid | string | 否 | - | 分组标识 |
| createdType | int | 否 | - | 创建类型：0 接口创建；1 控制台创建 |
| validDateType | int | 否 | - | 有效期类型：0 永久有效；1 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 否 | - | 描述 |

- **响应数据结构**：`Result<ShortLinkCreateRespDTO>`

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| gid | string | 分组标识 |
| originUrl | string | 原始链接 |
| fullShortUrl | string | 完整短链接（服务端拼接 `http://` 前缀返回） |

- **错误码说明**：`A000001`（原始链接填写错误 / 白名单校验失败）；`B000001`（短链接生成重复、频繁生成等）。
- **请求示例**：

```json
POST /api/short-link/v1/create
Headers:
  username: zhangsan
  token: xxxxxxxx

{
  "domain": "localhost:8001",
  "originUrl": "https://www.example.com",
  "gid": "group1",
  "createdType": 0,
  "validDateType": 0,
  "validDate": null,
  "describe": "示例链接"
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": {
    "gid": "group1",
    "originUrl": "https://www.example.com",
    "fullShortUrl": "http://localhost:8001/abC123"
  },
  "requestId": null
}
```

---

### 4.3 通过分布式锁创建短链接

- **接口名称**：通过分布式锁创建短链接
- **功能描述**：通过 Redisson 分布式锁创建短链接。在加锁区间内先查库校验短链后缀是否重复，再插入数据库与跳转映射并写入 Redis 缓存。
- **请求 URL**：`/api/short-link/v1/create/by-lock`
- **请求方法**：POST
- **请求参数**：同「4.2 创建短链接」的请求头与请求体（`ShortLinkCreateReqDTO` 字段一致）。
- **响应数据结构**：`Result<ShortLinkCreateRespDTO>`，字段同「4.2 创建短链接」。
- **错误码说明**：`A000001`（原始链接填写错误 / 白名单校验失败）；`B000001`（短链接生成重复、频繁生成等）。
- **请求示例**：同「4.2 创建短链接」，URL 换为 `/api/short-link/v1/create/by-lock`。
- **响应示例**：同「4.2 创建短链接」。

---

### 4.4 批量创建短链接

- **接口名称**：批量创建短链接
- **功能描述**：批量创建短链接。对 `originUrls` 逐条调用创建逻辑，单条失败记录错误日志并跳过，返回成功创建的短链接基础信息列表。
- **请求 URL**：`/api/short-link/v1/create/batch`
- **请求方法**：POST
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

请求体（JSON，字段均未加校验注解）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| originUrls | array[string] | 否 | - | 原始链接集合 |
| describes | array[string] | 否 | - | 描述集合（与 originUrls 按下标一一对应） |
| gid | string | 否 | - | 分组标识 |
| createdType | int | 否 | - | 创建类型：0 接口创建；1 控制台创建 |
| validDateType | int | 否 | - | 有效期类型：0 永久有效；1 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |

- **响应数据结构**：`Result<ShortLinkBatchCreateRespDTO>`

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| total | int | 成功数量 |
| baseLinkInfos | array[object] | 批量创建返回参数，元素结构见 `ShortLinkBaseInfoRespDTO` |

`baseLinkInfos[]`（ShortLinkBaseInfoRespDTO）：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| describe | string | 描述信息 |
| originUrl | string | 原始链接 |
| fullShortUrl | string | 完整短链接 |

- **错误码说明**：`A000001`（白名单校验失败）；单条失败不影响整体返回。
- **请求示例**：

```json
POST /api/short-link/v1/create/batch
{
  "originUrls": ["https://www.example.com", "https://www.zhihu.com"],
  "describes": ["示例一", "示例二"],
  "gid": "group1",
  "createdType": 0,
  "validDateType": 0,
  "validDate": null
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": {
    "total": 2,
    "baseLinkInfos": [
      {
        "describe": "示例一",
        "originUrl": "https://www.example.com",
        "fullShortUrl": "http://localhost:8001/abC123"
      },
      {
        "describe": "示例二",
        "originUrl": "https://www.zhihu.com",
        "fullShortUrl": "http://localhost:8001/deF456"
      }
    ]
  },
  "requestId": null
}
```

---

### 4.5 修改短链接

- **接口名称**：修改短链接
- **功能描述**：修改短链接的原始链接、分组、有效期、描述等。原分组与新分组一致时直接更新；跨分组时删除旧记录并迁移到新分组。变更原始链接或有效期后删除对应 Redis 缓存。
- **请求 URL**：`/api/short-link/v1/update`
- **请求方法**：POST
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

请求体（JSON，字段均未加校验注解）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| originUrl | string | 否 | - | 原始链接 |
| fullShortUrl | string | 否 | - | 完整短链接 |
| originGid | string | 否 | - | 原始分组标识 |
| gid | string | 否 | - | 目标分组标识 |
| validDateType | int | 否 | - | 有效期类型：0 永久有效；1 自定义 |
| validDate | string | 否 | - | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 否 | - | 描述 |

- **响应数据结构**：`Result<Void>`（`data` 为 null）。

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| code | string | 返回码，成功为 `"0"` |
| message | string | 返回消息 |
| data | null | 无业务数据 |
| requestId | string | 请求 ID |

- **错误码说明**：`A000001`（短链接记录不存在、原始链接填写错误等）。
- **请求示例**：

```json
POST /api/short-link/v1/update
{
  "originUrl": "https://www.example.com/new",
  "fullShortUrl": "localhost:8001/abC123",
  "originGid": "group1",
  "gid": "group2",
  "validDateType": 1,
  "validDate": "2026-12-31 23:59:59",
  "describe": "更新后的描述"
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": null
}
```

---

### 4.6 分页查询短链接

- **接口名称**：分页查询短链接
- **功能描述**：分页查询当前分组下的短链接列表（数据来自数据库分页查询），每条记录附带 PV/UV/UIP 等统计信息。返回结果中 `domain` 会由服务端拼接 `http://` 前缀。
- **请求 URL**：`/api/short-link/v1/page`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| orderTag | string | 否 | - | 排序标识 |
| current | long | 否 | 1 | 当前页码（继承自 MyBatis-Plus `Page`） |
| size | long | 否 | 10 | 每页条数（继承自 MyBatis-Plus `Page`） |

- **响应数据结构**：`Result<IPage<ShortLinkPageRespDTO>>`，`data` 为分页对象。

`data`（IPage）：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| current | long | 当前页码 |
| size | long | 每页条数 |
| total | long | 总记录数 |
| records | array[object] | 短链接记录列表，元素结构见 `ShortLinkPageRespDTO` |

`records[]`（ShortLinkPageRespDTO）：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| id | long | id |
| domain | string | 域名（返回时已拼接 `http://` 前缀） |
| shortUri | string | 短链接 |
| fullShortUrl | string | 完整短链接 |
| originUrl | string | 原始链接 |
| gid | string | 分组标识 |
| validDateType | int | 有效期类型：0 永久有效；1 自定义 |
| enableStatus | int | 启用标识：0 启用；1 未启用 |
| validDate | string | 有效期，格式 `yyyy-MM-dd HH:mm:ss` |
| createTime | string | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |
| describe | string | 描述 |
| favicon | string | 网站标识（favicon） |
| totalPv | int | 历史 PV |
| todayPv | int | 今日 PV |
| totalUv | int | 历史 UV |
| todayUv | int | 今日 UV |
| totalUip | int | 历史 UIP |
| todayUip | int | 今日 UIP |

- **错误码说明**：`B000001`（系统异常由全局异常处理器兜底）。
- **请求示例**：

```
GET /api/short-link/v1/page?gid=group1&orderTag=todayPv&current=1&size=10
```

- **响应示例**：

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
        "domain": "http://localhost:8001",
        "shortUri": "abC123",
        "fullShortUrl": "localhost:8001/abC123",
        "originUrl": "https://www.example.com",
        "gid": "group1",
        "validDateType": 0,
        "enableStatus": 0,
        "validDate": null,
        "createTime": "2026-09-07 00:00:00",
        "describe": "示例链接",
        "favicon": "https://www.example.com/favicon.ico",
        "totalPv": 10,
        "todayPv": 2,
        "totalUv": 5,
        "todayUv": 1,
        "totalUip": 4,
        "todayUip": 1
      }
    ]
  },
  "requestId": null
}
```

---

### 4.7 查询分组内短链接数量

- **接口名称**：查询分组内短链接数量
- **功能描述**：查询指定分组集合中各分组的短链接数量，仅统计启用且未删除的短链接。
- **请求 URL**：`/api/short-link/v1/count`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| requestParam | array[string] | 否 | - | 分组标识集合，可重复（如 `requestParam=g1&requestParam=g2`） |

- **响应数据结构**：`Result<List<ShortLinkGroupCountQueryRespDTO>>`

`data[]`（ShortLinkGroupCountQueryRespDTO）：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| gid | string | 分组标识 |
| shortLinkCount | int | 短链接数量 |

- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```
GET /api/short-link/v1/count?requestParam=group1&requestParam=group2
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": [
    { "gid": "group1", "shortLinkCount": 3 },
    { "gid": "group2", "shortLinkCount": 5 }
  ],
  "requestId": null
}
```

---

### 4.8 移入回收站

- **接口名称**：移入回收站
- **功能描述**：将指定分组的短链接移入回收站（`enable_status` 置为 1），并删除对应跳转缓存。
- **请求 URL**：`/api/short-link/v1/recycle-bin/save`
- **请求方法**：POST
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

请求体（JSON，字段均未加校验注解）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

- **响应数据结构**：`Result<Void>`（`data` 为 null），结构同「4.5 修改短链接」。
- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```json
POST /api/short-link/v1/recycle-bin/save
{
  "gid": "group1",
  "fullShortUrl": "localhost:8001/abC123"
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": null
}
```

---

### 4.9 分页查询回收站短链接

- **接口名称**：分页查询回收站短链接
- **功能描述**：分页查询回收站中的短链接（已移入回收站的记录）。返回结构中 `domain` 同样拼接 `http://` 前缀。
- **请求 URL**：`/api/short-link/v1/recycle-bin/page`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gidList | array[string] | 否 | - | 分组标识集合，可重复（如 `gidList=g1&gidList=g2`） |
| current | long | 否 | 1 | 当前页码（继承自 MyBatis-Plus `Page`） |
| size | long | 否 | 10 | 每页条数（继承自 MyBatis-Plus `Page`） |

- **响应数据结构**：`Result<IPage<ShortLinkPageRespDTO>>`，`data` 分页结构与 `records[]` 字段同「4.6 分页查询短链接」。
- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```
GET /api/short-link/v1/recycle-bin/page?gidList=group1&current=1&size=10
```

- **响应示例**：结构同「4.6 分页查询短链接」的响应示例。

---

### 4.10 恢复短链接

- **接口名称**：恢复短链接
- **功能描述**：将回收站中的短链接恢复为启用状态（`enable_status` 置为 0），并删除「短链接不存在」的空缓存标记。
- **请求 URL**：`/api/short-link/v1/recycle-bin/recover`
- **请求方法**：POST
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

请求体（JSON，字段均未加校验注解，与 `RecycleBinRecoverReqDTO`）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

- **响应数据结构**：`Result<Void>`（`data` 为 null），结构同「4.5 修改短链接」。
- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```json
POST /api/short-link/v1/recycle-bin/recover
{
  "gid": "group1",
  "fullShortUrl": "localhost:8001/abC123"
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": null
}
```

---

### 4.11 移除短链接

- **接口名称**：移除短链接
- **功能描述**：彻底移除回收站中的短链接（`del_flag` 置为 1，并写入删除时间 `del_time`）。
- **请求 URL**：`/api/short-link/v1/recycle-bin/remove`
- **请求方法**：POST
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

请求体（JSON，字段均未加校验注解，与 `RecycleBinRemoveReqDTO`）：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| fullShortUrl | string | 否 | - | 完整短链接 |

- **响应数据结构**：`Result<Void>`（`data` 为 null），结构同「4.5 修改短链接」。
- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```json
POST /api/short-link/v1/recycle-bin/remove
{
  "gid": "group1",
  "fullShortUrl": "localhost:8001/abC123"
}
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": null,
  "requestId": null
}
```

---

### 4.12 单条短链接监控数据

- **接口名称**：单条短链接监控数据
- **功能描述**：访问单个短链接在指定时间内的监控数据，返回 PV/UV/UIP、基础访问详情、地区、小时、高频 IP、一周、浏览器、操作系统、访客类型、设备、网络等统计。
- **请求 URL**：`/api/short-link/v1/stats`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| fullShortUrl | string | 否 | - | 完整短链接 |
| gid | string | 否 | - | 分组标识 |
| startDate | string | 否 | - | 开始日期 |
| endDate | string | 否 | - | 结束日期 |
| enableStatus | int | 否 | - | 启用标识：0 启用；1 未启用 |

- **响应数据结构**：`Result<ShortLinkStatsRespDTO>`

`data`（ShortLinkStatsRespDTO）：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| pv | int | 访问量（PV） |
| uv | int | 独立访客数（UV） |
| uip | int | 独立 IP 数（UIP） |
| daily | array[object] | 基础访问详情，元素结构见 `ShortLinkStatsAccessDailyRespDTO` |
| localeCnStats | array[object] | 地区访问详情（仅国内），元素结构见 `ShortLinkStatsLocaleCNRespDTO` |
| hourStats | array[int] | 小时访问详情 |
| topIpStats | array[object] | 高频访问 IP 详情，元素结构见 `ShortLinkStatsTopIpRespDTO` |
| weekdayStats | array[int] | 一周访问详情 |
| browserStats | array[object] | 浏览器访问详情，元素结构见 `ShortLinkStatsBrowserRespDTO` |
| osStats | array[object] | 操作系统访问详情，元素结构见 `ShortLinkStatsOsRespDTO` |
| uvTypeStats | array[object] | 访客访问类型详情，元素结构见 `ShortLinkStatsUvRespDTO` |
| deviceStats | array[object] | 访问设备类型详情，元素结构见 `ShortLinkStatsDeviceRespDTO` |
| networkStats | array[object] | 访问网络类型详情，元素结构见 `ShortLinkStatsNetworkRespDTO` |

各元素结构：

`ShortLinkStatsAccessDailyRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| date | string | 日期 |
| pv | int | 访问量 |
| uv | int | 独立访客数 |
| uip | int | 独立 IP 数 |

`ShortLinkStatsLocaleCNRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| locale | string | 地区 |
| ratio | double | 占比 |

`ShortLinkStatsTopIpRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| ip | string | IP |

`ShortLinkStatsBrowserRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| browser | string | 浏览器 |
| ratio | double | 占比 |

`ShortLinkStatsOsRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| os | string | 操作系统 |
| ratio | double | 占比 |

`ShortLinkStatsUvRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| uvType | string | 访客类型 |
| ratio | double | 占比 |

`ShortLinkStatsDeviceRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| device | string | 设备类型 |
| ratio | double | 占比 |

`ShortLinkStatsNetworkRespDTO`：

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| cnt | int | 统计 |
| network | string | 访问网络 |
| ratio | double | 占比 |

- **错误码说明**：`B000001`（系统异常兜底）；`C000001`（调用第三方服务出错，如高德 IP 定位等）。
- **请求示例**：

```
GET /api/short-link/v1/stats?fullShortUrl=localhost%3A8001%2FabC123&gid=group1&startDate=2026-09-01&endDate=2026-09-07&enableStatus=0
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": {
    "pv": 100,
    "uv": 50,
    "uip": 40,
    "daily": [
      { "date": "2026-09-07", "pv": 20, "uv": 10, "uip": 8 }
    ],
    "localeCnStats": [
      { "cnt": 30, "locale": "广东", "ratio": 0.6 }
    ],
    "hourStats": [0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    "topIpStats": [
      { "cnt": 10, "ip": "192.168.1.1" }
    ],
    "weekdayStats": [1, 2, 3, 4, 5, 6, 7],
    "browserStats": [
      { "cnt": 60, "browser": "Chrome", "ratio": 0.6 }
    ],
    "osStats": [
      { "cnt": 70, "os": "Windows", "ratio": 0.7 }
    ],
    "uvTypeStats": [
      { "cnt": 50, "uvType": "new", "ratio": 1.0 }
    ],
    "deviceStats": [
      { "cnt": 80, "device": "PC", "ratio": 0.8 }
    ],
    "networkStats": [
      { "cnt": 90, "network": "wifi", "ratio": 0.9 }
    ]
  },
  "requestId": null
}
```

---

### 4.13 分组短链接监控数据

- **接口名称**：分组短链接监控数据
- **功能描述**：访问分组下短链接在指定时间内的监控数据，返回结构同单条短链接监控。
- **请求 URL**：`/api/short-link/v1/stats/group`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| startDate | string | 否 | - | 开始日期 |
| endDate | string | 否 | - | 结束日期 |

- **响应数据结构**：`Result<ShortLinkStatsRespDTO>`，字段同「4.12 单条短链接监控数据」。
- **错误码说明**：`B000001`（系统异常兜底）；`C000001`（调用第三方服务出错）。
- **请求示例**：

```
GET /api/short-link/v1/stats/group?gid=group1&startDate=2026-09-01&endDate=2026-09-07
```

- **响应示例**：结构同「4.12 单条短链接监控数据」的响应示例。

---

### 4.14 单条短链接访问记录

- **接口名称**：单条短链接访问记录
- **功能描述**：分页查询单个短链接在指定时间内的访问记录监控数据（访客类型、浏览器、操作系统、IP、网络、设备、地区、用户、访问时间）。
- **请求 URL**：`/api/short-link/v1/stats/access-record`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| fullShortUrl | string | 否 | - | 完整短链接 |
| gid | string | 否 | - | 分组标识 |
| startDate | string | 否 | - | 开始日期 |
| endDate | string | 否 | - | 结束日期 |
| enableStatus | int | 否 | - | 启用标识：0 启用；1 未启用 |
| current | long | 否 | 1 | 当前页码（继承自 MyBatis-Plus `Page`） |
| size | long | 否 | 10 | 每页条数（继承自 MyBatis-Plus `Page`） |

- **响应数据结构**：`Result<IPage<ShortLinkStatsAccessRecordRespDTO>>`，`data` 为分页对象。

`data.records[]`（ShortLinkStatsAccessRecordRespDTO）：

| 字段 | 数据类型 | 说明 |
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

- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```
GET /api/short-link/v1/stats/access-record?fullShortUrl=localhost%3A8001%2FabC123&gid=group1&startDate=2026-09-01&endDate=2026-09-07&enableStatus=0&current=1&size=10
```

- **响应示例**：

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
        "uvType": "new",
        "browser": "Chrome",
        "os": "Windows",
        "ip": "192.168.1.1",
        "network": "wifi",
        "device": "PC",
        "locale": "广东",
        "user": "new",
        "createTime": "2026-09-07 10:00:00"
      }
    ]
  },
  "requestId": null
}
```

---

### 4.15 分组短链接访问记录

- **接口名称**：分组短链接访问记录
- **功能描述**：分页查询分组下短链接在指定时间内的访问记录监控数据。
- **请求 URL**：`/api/short-link/v1/stats/access-record/group`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| gid | string | 否 | - | 分组标识 |
| startDate | string | 否 | - | 开始日期 |
| endDate | string | 否 | - | 结束日期 |
| current | long | 否 | 1 | 当前页码（继承自 MyBatis-Plus `Page`） |
| size | long | 否 | 10 | 每页条数（继承自 MyBatis-Plus `Page`） |

- **响应数据结构**：`Result<IPage<ShortLinkStatsAccessRecordRespDTO>>`，分页结构与 `records[]` 字段同「4.14 单条短链接访问记录」。
- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```
GET /api/short-link/v1/stats/access-record/group?gid=group1&startDate=2026-09-01&endDate=2026-09-07&current=1&size=10
```

- **响应示例**：结构同「4.14 单条短链接访问记录」的响应示例。

---

### 4.16 获取网页标题

- **接口名称**：获取网页标题
- **功能描述**：根据 URL 抓取对应网站页面并返回其标题（`<title>`），请求失败时返回字符串 `"Error while fetching title."`。
- **请求 URL**：`/api/short-link/v1/title`
- **请求方法**：GET
- **请求参数**：

请求头：同「4.2 创建短链接」（`username`、`token` 必填）。

查询参数：

| 参数名称 | 数据类型 | 是否必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| url | string | 否 | - | 目标网页 URL |

- **响应数据结构**：`Result<String>`（`data` 为网页标题字符串）。

| 字段 | 数据类型 | 说明 |
| --- | --- | --- |
| code | string | 返回码，成功为 `"0"` |
| message | string | 返回消息 |
| data | string | 网页标题 |
| requestId | string | 请求 ID |

- **错误码说明**：`B000001`（系统异常兜底）。
- **请求示例**：

```
GET /api/short-link/v1/title?url=https%3A%2F%2Fwww.example.com
```

- **响应示例**：

```json
{
  "code": "0",
  "message": null,
  "data": "Example Domain",
  "requestId": null
}
```

---

### 4.17 短链接不存在跳转页面

- **接口名称**：短链接不存在跳转页面
- **功能描述**：Spring MVC 视图控制器，返回 `notfound` 视图（对应模板 `templates/notfound.html`）。主要在短链接跳转未命中时作为 302 重定向目标页面使用。
- **请求 URL**：`/page/notfound`
- **请求方法**：GET（该接口同时接受 POST 等其它 HTTP 方法，`@RequestMapping` 未限定请求方式）
- **请求参数**：无。
- **请求头**：无需认证。
- **响应数据结构**：HTML 页面（`notfound` 视图，非 `Result` 结构）。
- **错误码说明**：无。
- **请求示例**：

```
GET http://localhost:8001/page/notfound HTTP/1.1
Host: localhost:8001
```

- **响应示例**：返回 `notfound.html` 页面内容（HTML）。
```
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8

<!DOCTYPE html>
<html>...notfound 页面内容...</html>
```