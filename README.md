# 短链接平台（short-link）

一个基于 Spring Cloud 微服务架构的企业级短链接平台，提供短链接生成、跳转、分组管理、回收站、访问统计等完整能力，配套 Vue 3 管理控制台。采用分库分表、布隆过滤器、分布式锁、缓存多级防护等手段应对高并发场景。

## ✨ 功能特性

- **用户体系**：注册、登录、信息查询（脱敏/明文）、修改、退出；JWT Token + Redis 会话管理
- **分组管理**：短链接分组的新增、查询、重命名、删除、排序
- **短链接管理**：创建、修改、分页查询、批量创建（Excel 导出）
- **短链接跳转**：访问短链 302 重定向至原始地址，缓存 + 布隆过滤器 + 分布式锁回源
- **回收站**：移入回收站、分页查询、恢复、彻底删除
- **访问统计**：PV / UV / IP / 浏览器 / 操作系统 / 设备 / 网络 / 地区分布、访问记录、日维度聚合
- **网页标题抓取**：基于 Jsoup 自动解析目标网址标题
- **网关鉴权**：Spring Cloud Gateway 统一 Token 校验、登录注册白名单
- **高可用设计**：ShardingSphere 分库分表、Redisson 分布式锁、布隆过滤器防穿透、Sentinel 限流熔断

## 🛠 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| Java 17 | 基础运行环境 |
| Spring Boot 3.0.7 | 核心框架 |
| Spring Cloud 2022.0.3 | 微服务框架 |
| Spring Cloud Alibaba 2022.0.0.0-RC2 | Nacos / Sentinel |
| Spring Cloud Gateway | 网关与路由 |
| Nacos | 服务注册与发现 |
| OpenFeign + LoadBalancer | 服务远程调用与负载均衡 |
| Sentinel | 流量控制、熔断降级 |
| MySQL 8 + MyBatis-Plus 3.5.3.1 | 数据持久化 |
| ShardingSphere 5.3.2 | 分库分表 |
| Redis + Redisson | 缓存、分布式锁、布隆过滤器 |
| fastjson2 / Hutool / Guava | 工具集 |
| Jsoup | 网页标题抓取 |
| EasyExcel | 批量数据导出 |
| Transmittable-Thread-Local | 跨线程上下文传递 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3.3 + TypeScript | 前端框架 |
| Vite 4 | 构建工具 |
| Vue Router 4 / Vuex 4 | 路由与状态管理 |
| Element Plus | UI 组件库 |
| Axios | HTTP 请求 |
| ECharts | 图表可视化 |
| Sass / Less | 样式预处理 |

## 🏗 系统架构

```
                         ┌─────────────────────────────┐
        /api/**          │         Gateway :8000        │
   Frontend ───────────▶ │  Token 鉴权 / 路由转发        │
   (Vue 3 :5173)         └──────────┬──────────────────┘
                                    │ lb 负载均衡
                 ┌──────────────────┴──────────────────┐
                 ▼                                     ▼
        ┌────────────────┐                   ┌────────────────┐
        │  admin  :8002   │──Feign 远程调用──▶│  project :8001  │
        │  用户/分组/短链  │                   │ 短链核心/跳转/统计│
        └────────────────┘                   └────────────────┘
                 │                                     │
                 └─────────────┬───────────────────────┘
                               ▼
              Nacos / Redis / MySQL(分库分表) / Sentinel
```

### 服务端口与路由

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway | 8000 | 统一入口，Token 校验 |
| project | 8001 | 短链接核心业务（含跳转） |
| admin | 8002 | 管理端业务 |

网关路由规则：

| 路径 | 目标服务 | 鉴权 |
|------|----------|------|
| `/api/short-link/admin/**` | short-link-admin | 需 Token（login/has-username/注册为白名单） |
| `/api/short-link/**` | short-link-project | 需 Token |
| `/{short-uri}` | 直连 project:8001 | 无需鉴权（302 跳转） |

## 📁 项目结构

```
shortlink
├── gateway        # 网关服务（Token 鉴权、路由转发）
├── admin          # 管理端服务（用户/分组/短链管理，Feign 调 project）
├── project        # 短链接核心服务（创建/跳转/回收站/统计）
├── console-vue    # 前端管理控制台（Vue 3 + TypeScript）
├── doc/api        # 接口文档（Markdown）
└── skills         # AI 辅助技能
```

## ⚡ 核心亮点

- **缓存穿透防护**：短链接存在性校验先过 Redisson 布隆过滤器，无效短链直接拦截
- **缓存击穿防护**：回源查询使用 Redisson 分布式锁 + 双重校验，避免瞬时并发穿透数据库
- **分库分表**：短链接跳转表通过 ShardingSphere 按 hash 分片，支撑海量数据
- **幂等与风控**：注册/登录幂等 Token、用户流量风控过滤器
- **跨线程上下文**：Transmittable-Thread-Local 解决异步场景下用户上下文丢失问题

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+（前端）
- MySQL 8
- Redis
- Nacos Server

### 后端启动

1. 分别在 `admin/src/main/resources`、`project/src/main/resources`、`gateway/src/main/resources` 的 `application.yaml` 中配置好 Nacos、Redis、MySQL 连接信息。
2. 初始化数据库建表脚本后，依次启动：

```bash
# 在项目根目录执行，或分别进入各模块执行 mvn spring-boot:run
mvn -pl gateway -am spring-boot:run    # 网关 8000
mvn -pl project -am spring-boot:run    # 短链核心 8001
mvn -pl admin -am spring-boot:run      # 管理端 8002
```

### 前端启动

```bash
cd console-vue
npm install
npm run dev
```

前端默认运行于 http://localhost:5173 ，`/api` 请求自动代理至网关 http://127.0.0.1:8000 。

## 📖 接口文档

完整的接口说明见 [doc/api](./doc/api)：

- [README - 总览](./doc/api/README.md)
- [common - 通用约定与错误码](./doc/api/common.md)
- [admin - 管理端接口](./doc/api/admin.md)
- [project - 短链接核心接口](./doc/api/project.md)

## 📝 License

[MIT](./LICENSE)