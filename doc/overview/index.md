# 短链接平台 - 项目概述

---

## 1. 项目简介

短链接平台是一个用于创建、管理和统计短链接的 Web 应用系统，支持用户注册登录、短链接创建、分组管理、数据统计和回收站功能。

## 2. 技术栈

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | ^3.3.4 | 前端框架 |
| Element Plus | ^2.3.14 | UI组件库 |
| Vite | ^4.4.9 | 构建工具 |
| Vue Router | ^4.2.4 | 路由管理 |
| Vuex | ^4.0.2 | 状态管理 |
| Axios | ^1.5.1 | HTTP客户端 |

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.x | 应用框架 |
| MyBatis Plus | 3.5.x | ORM框架 |
| Redis | 7.x | 缓存/分布式锁 |
| MySQL | 8.0+ | 数据库 |

## 3. 功能模块

| 模块 | 功能 | 说明 |
|------|------|------|
| 用户模块 | 注册、登录、退出 | 用户账户管理 |
| 分组模块 | 创建、查询、更新、删除 | 短链接分组管理 |
| 短链接模块 | 创建、查询、更新、删除 | 核心短链接功能 |
| 回收站模块 | 恢复、彻底删除 | 已删除短链接管理 |
| 统计模块 | 访问统计、趋势分析 | 数据统计展示 |

## 4. 项目结构

```
shortlink/
├── frontend/           # 前端项目
│   ├── src/           # 源代码
│   ├── index.html     # 入口HTML
│   └── package.json   # 依赖配置
├── backend/           # 后端项目（待创建）
│   ├── admin/         # 后台管理模块
│   ├── project/       # 核心业务模块
│   └── gateway/       # API网关
└── doc/              # 项目文档
    ├── overview/      # 项目概述（本文件）
    ├── api/          # API文档
    ├── frontend/     # 前端开发指南
    ├── backend/      # 后端开发指南
    ├── database/     # 数据库设计
    └── standards/    # 编码规范
```

## 5. 快速开始

### 环境要求

- Node.js >= 18.0.0
- Java >= 17
- MySQL >= 8.0
- Redis >= 7.0

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 访问地址

- 前端：http://localhost:3000
- API基础路径：/api/short-link/admin/v1

## 6. 文档导航

| 文档 | 路径 | 说明 |
|------|------|------|
| 项目概述 | `/overview/index.md` | 本文件 |
| API文档 | `/api/index.md` | 接口规范说明 |
| 前端开发指南 | `/frontend/development.md` | 前端开发规范 |
| 后端开发指南 | `/backend/development.md` | 后端开发规范 |
| 数据库设计 | `/database/index.md` | 数据库结构设计 |
| 编码规范 | `/standards/index.md` | 统一编码规范 |

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
