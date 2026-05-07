---
name: shortlink-api
description: 短链接平台API接口文档。定义前后端交互的接口规范。
---

# 短链接平台 - API接口文档

## 基础配置

### 基础路径

```
/api/short-link/admin/v1
```

### 请求头

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Token | String | 是 | 用户登录令牌 |
| Username | String | 是 | 用户名 |

### 响应格式

```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```

---

## 用户模块

### 用户登录

- **路径**: `POST /user/login`
- **作用**: 用户登录系统
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "登录成功",
  "data": {
    "token": "string",
    "username": "string"
  }
}
```

### 用户注册

- **路径**: `POST /user/register`
- **作用**: 用户注册账号
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "注册成功",
  "data": {}
}
```

---

## 分组模块

### 查询分组列表

- **路径**: `GET /group/page`
- **作用**: 获取用户的分组列表
- **请求参数**: 无

- **成功响应**:
```json
{
  "code": "0",
  "message": "success",
  "data": [
    {
      "id": "string",
      "name": "string",
      "createTime": "string"
    }
  ]
}
```

### 创建分组

- **路径**: `POST /group/create`
- **作用**: 创建新分组
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 分组名称 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "创建成功",
  "data": {}
}
```

### 更新分组

- **路径**: `POST /group/update`
- **作用**: 更新分组信息
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 分组ID |
| name | String | 是 | 新分组名称 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "更新成功",
  "data": {}
}
```

### 删除分组

- **路径**: `POST /group/delete`
- **作用**: 删除分组
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 分组ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "删除成功",
  "data": {}
}
```

---

## 短链接模块

### 查询短链接列表

- **路径**: `GET /page`
- **作用**: 分页查询短链接列表
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Number | 是 | 页码，从1开始 |
| pageSize | Number | 是 | 每页数量 |
| groupId | String | 否 | 分组ID，用于筛选 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "records": [
      {
        "id": "string",
        "shortLink": "string",
        "shortLinkSuffix": "string",
        "originUrl": "string",
        "groupId": "string",
        "groupName": "string",
        "pv": "number",
        "createTime": "string"
      }
    ],
    "total": "number"
  }
}
```

### 创建短链接

- **路径**: `POST /create`
- **作用**: 创建新的短链接
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| originUrl | String | 是 | 原始链接 |
| groupId | String | 是 | 分组ID |
| customSuffix | String | 否 | 自定义短链后缀 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "创建成功",
  "data": {}
}
```

### 更新短链接

- **路径**: `POST /update`
- **作用**: 更新短链接信息
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 短链接ID |
| originUrl | String | 是 | 原始链接 |
| groupId | String | 是 | 分组ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "更新成功",
  "data": {}
}
```

### 移至回收站

- **路径**: `POST /recycle-bin/save`
- **作用**: 将短链接移至回收站
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 短链接ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "删除成功",
  "data": {}
}
```

### 查询回收站列表

- **路径**: `GET /recycle-bin/page`
- **作用**: 分页查询回收站中的短链接
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Number | 是 | 页码，从1开始 |
| pageSize | Number | 是 | 每页数量 |

- **成功响应**:
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "records": [
      {
        "id": "string",
        "shortLink": "string",
        "originUrl": "string",
        "groupName": "string",
        "deleteTime": "string"
      }
    ],
    "total": "number"
  }
}
```

### 恢复短链接

- **路径**: `POST /recycle-bin/recover`
- **作用**: 从回收站恢复短链接
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 短链接ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "恢复成功",
  "data": {}
}
```

### 彻底删除

- **路径**: `POST /recycle-bin/remove`
- **作用**: 从回收站彻底删除短链接（不可恢复）
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 短链接ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "删除成功",
  "data": {}
}
```

### 查询访问统计

- **路径**: `GET /stats`
- **作用**: 获取短链接访问统计数据
- **请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 短链接ID |

- **成功响应**:
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "pv": "number",
    "uv": "number",
    "trend": [
      {
        "date": "string",
        "count": "number"
      }
    ]
  }
}
```

---

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 0 | 成功 |
| 其他 | 失败（具体错误信息在 message 字段） |

---

**文档版本**: v1.0  
**适用版本**: 短链接平台 v1.0
