# 短链接平台接口文档

## 1. 项目简介

短链接平台（shortlink）是一个基于微服务架构的短链接生成、管理与跳转平台，提供以下核心能力：

- 短链接的创建、查询、分页管理（短链接核心业务）
- 用户注册、登录、登录态校验（管理端）
- 短链接跳转（将短链 `/{short-uri}` 重定向到原始长链）

## 2. 架构与模块

| 模块 | 端口 | 职责 | 是否对外暴露 |
| --- | --- | --- | --- |
| gateway | 8000 | 统一网关入口、路由转发、登录鉴权 | 是（统一入口） |
| project | 8001 | 短链接核心业务（创建、分页查询、跳转等） | 经网关（跳转接口直连） |
| admin | 8002 | 管理端（用户注册、登录等） | 经网关 |

### 2.1 Base URL

- **统一对外入口（经网关）**：`http://localhost:8000`
- **短链接跳转接口（不经网关，直连 project）**：`http://localhost:8001/{short-uri}`

> 除短链接跳转接口 `/{short-uri}` 直连 project（8001）外，其余对外接口均通过网关（8000）访问。

## 3. 网关路由

| 网关路由规则 | 转发目标 |
| --- | --- |
| `/api/short-link/admin/**` | admin 服务（8002） |
| `/api/short-link/**` | project 服务（8001） |

## 4. 认证机制概述

网关通过 `TokenValidate` 过滤器进行登录鉴权：

- 请求需携带请求头 `username`（用户名）与 `token`（登录令牌）。
- 鉴权通过后，网关会向请求头注入 `userId`、`realName`（URL 编码）两个头，并转发至下游服务。
- 鉴权失败时返回 HTTP 401，响应体为：

```json
{ "status": 401, "message": "Token validation error" }
```

### 4.1 白名单（免鉴权）

| 接口 | 说明 |
| --- | --- |
| `/api/short-link/admin/v1/user/login` | 用户登录 |
| `/api/short-link/admin/v1/user/has-username` | 校验用户名是否存在 |
| `POST /api/short-link/admin/v1/user` | 用户注册 |

## 5. 文档导航

| 文档 | 说明 |
| --- | --- |
| [通用约定](./common.md) | 统一响应结构、错误码表、分页约定、认证与请求头约定 |
| [管理端接口](./admin.md) | 用户注册、登录等管理端接口 |
| [短链接核心接口](./project.md) | 短链接创建、分页查询、跳转等接口 |

## 6. 快速开始

以下示例统一使用网关入口 `http://localhost:8000`。

### 6.1 用户登录

```bash
curl -X POST "http://localhost:8000/api/short-link/admin/v1/user/login" \
  -H "Content-Type: application/json" \
  -H "username: admin" \
  -H "token: 登录令牌" \
  -d '{"username":"admin","password":"123456"}'
```

### 6.2 带鉴权访问业务接口

访问需要通过 `TokenValidate` 过滤器的接口时，需携带 `username` 与 `token` 请求头：

```bash
curl "http://localhost:8000/api/short-link/v1/page" \
  -H "username: admin" \
  -H "token: 登录令牌"
```

### 6.3 短链接跳转（直连 project，不经网关）

```bash
curl -i "http://localhost:8001/{short-uri}"
```

> 具体接口的请求参数、响应结构以对应模块文档为准。