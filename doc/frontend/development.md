# 短链接平台 - 前端开发指南

---

## 1. 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | ^3.3.4 | 前端框架 |
| Element Plus | ^2.3.14 | UI组件库 |
| Vite | ^4.4.9 | 构建工具 |
| Vue Router | ^4.2.4 | 路由管理 |
| Vuex | ^4.0.2 | 状态管理 |
| Axios | ^1.5.1 | HTTP客户端 |
| js-cookie | ^3.0.5 | Cookie管理 |
| ECharts | 4.8 | 数据图表 |
| qrcode | ^1.5.3 | 二维码生成 |

---

## 2. 目录结构

```
src/
├── api/                    # API 请求模块
│   ├── modules/            # 业务模块API
│   │   ├── user.js         # 用户模块
│   │   ├── group.js        # 分组模块
│   │   └── smallLinkPage.js # 短链接模块
│   ├── axios.js            # Axios封装
│   └── index.js            # API统一导出
├── assets/                 # 静态资源
│   └── logo.svg            # Logo
├── components/             # 通用组件
│   ├── CTable.vue          # 表格组件
│   ├── LabelSelect.vue     # 标签选择
│   └── emptyList.vue       # 空列表
├── core/                   # 核心模块
│   └── auth.js             # 认证管理
├── router/                 # 路由配置
│   └── index.js            # 路由定义
├── store/                  # Vuex状态管理
│   └── index.js            # Store配置
├── utils/                  # 工具函数
│   └── plugins.js          # 工具方法
├── views/                  # 页面组件
│   ├── login/              # 登录页面
│   │   └── LoginIndex.vue
│   ├── home/               # 首页布局
│   │   └── HomeIndex.vue
│   ├── mySpace/            # 我的空间
│   │   ├── MySpaceIndex.vue
│   │   └── components/     # 子组件
│   ├── recycleBin/         # 回收站
│   │   └── RecycleBinIndex.vue
│   └── mine/               # 个人中心
│       └── MineIndex.vue
├── App.vue                 # 根组件
├── main.js                 # 入口文件
└── style.scss              # 全局样式
```

---

## 3. 命名规范

### 3.1 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `CTable.vue` |
| 页面 | kebab-case + Index | `LoginIndex.vue` |
| API模块 | camelCase | `smallLinkPage.js` |
| 工具函数 | camelCase | `plugins.js` |

### 3.2 变量命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 普通变量 | camelCase | `loginForm` |
| 响应式引用 | camelCase + Ref | `loginFormRef` |
| 布尔值 | is/has/can前缀 | `isLogin`, `hasToken` |
| 状态对象 | camelCase | `queryParams` |

### 3.3 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 获取数据 | query/fetch | `queryGroup()`, `queryPage()` |
| 创建 | create/add | `createShortLink()` |
| 更新 | update/edit | `updateShortLink()` |
| 删除 | delete/remove | `deleteLink()` |
| 跳转 | to | `toMySpace()` |

---

## 4. 组件规范

### 4.1 Element Plus 优先原则

| 需求 | 使用 |
|------|------|
| 按钮 | `<el-button>` |
| 输入框 | `<el-input>` |
| 表格 | `<el-table>` |
| 弹窗 | `<el-dialog>` |
| 表单 | `<el-form>` |

### 4.2 组件开发规范

- 组件应单一职责，避免巨型组件
- 使用 `defineProps` 和 `defineEmits` 进行组件通信
- 避免直接操作父组件状态
- 组件命名应清晰描述其功能

---

## 5. API规范

### 5.1 Axios封装

```javascript
import axios from 'axios'
import { getToken, getUsername } from '@/core/auth.js'

const baseURL = '/api/short-link/admin/v1'

const http = axios.create({
  baseURL: baseURL,
  timeout: 15000
})

http.interceptors.request.use(
  (config) => {
    config.headers.Token = getToken() || ''
    config.headers.Username = getUsername() || ''
    return config
  },
  (error) => Promise.reject(error)
)
```

### 5.2 API模块结构

```javascript
export default {
  queryPage(data) {
    return http({ url: '/page', method: 'get', params: data })
  },
  addSmallLink(data) {
    return http({ url: '/create', method: 'post', data })
  }
}
```

---

## 6. 认证与权限

### 6.1 Auth工具函数

```javascript
import Cookies from 'js-cookie'

const TokenKey = 'token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}
```

### 6.2 路由守卫

```javascript
router.beforeEach(async (to, from, next) => {
  const token = getToken()
  
  if (to.path === '/login') {
    next()
    return
  }
  
  if (token) {
    next()
  } else {
    next('/login')
  }
})
```

---

## 7. 样式规范

### 7.1 全局样式

```scss
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
}
```

### 7.2 组件样式

- 使用 scoped 样式隔离
- 避免全局样式污染
- 使用 BEM 命名规范

---

## 8. 状态管理

### 8.1 Vuex Store

```javascript
import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      domain: 's.lanyue.com'
    }
  },
  mutations: {
    SET_DOMAIN(state, domain) {
      state.domain = domain
    }
  },
  actions: {
    setDomain({ commit }, domain) {
      commit('SET_DOMAIN', domain)
    }
  },
  getters: {
    getDomain: (state) => state.domain
  }
})
```

---

## 9. 开发流程

### 9.1 启动开发服务器

```bash
cd frontend
npm install
npm run dev
```

### 9.2 构建生产版本

```bash
npm run build
```

### 9.3 代码检查

```bash
npm run lint
```

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用版本**: 短链接平台 v1.0
