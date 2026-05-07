---
name: shortlink-frontend
description: Vue3短链接平台前端开发规范。当开发短链接创建、分组管理、数据统计、回收站功能时使用此skill。
---

# Vue3短链接平台前端开发规范

## 触发条件

- 开发短链接平台前端项目
- 实现短链接创建与管理
- 实现分组管理功能
- 实现数据统计图表
- 实现回收站功能

---

## Part 1: 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Vue** | ^3.3.4 | 前端框架 |
| **Element Plus** | ^2.3.14 | UI组件库 |
| **Vite** | ^4.4.9 | 构建工具 |
| **Vue Router** | ^4.2.4 | 路由管理 |
| **Vuex** | ^4.0.2 | 状态管理 |
| **Axios** | ^1.5.1 | HTTP客户端 |
| **js-cookie** | ^3.0.5 | Cookie管理 |
| **ECharts** | 4.8 | 数据图表 |
| **qrcode** | ^1.5.3 | 二维码生成 |

---

## Part 2: 目录结构

```
src/
├── api/                    # API请求模块
│   ├── modules/            # 业务模块API
│   │   ├── user.js         # 用户模块API
│   │   ├── group.js        # 分组模块API
│   │   └── smallLinkPage.js # 短链接模块API
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

## Part 3: 命名规范

### 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `CTable.vue` |
| 页面 | kebab-case + Index | `LoginIndex.vue` |
| API模块 | camelCase | `smallLinkPage.js` |
| 工具函数 | camelCase | `plugins.js` |

### 变量命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 普通变量 | camelCase | `loginForm` |
| 响应式引用 | camelCase + Ref | `loginFormRef` |
| 布尔值 | is/has/can前缀 | `isLogin`, `hasToken` |
| 状态对象 | camelCase | `queryParams` |

### 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 获取数据 | query/fetch | `queryGroup()`, `queryPage()` |
| 创建 | create/add | `createShortLink()` |
| 更新 | update/edit | `updateShortLink()` |
| 删除 | delete/remove | `deleteLink()` |
| 跳转 | to | `toMySpace()` |

---

## Part 4: API接口规范

### 基础配置

```javascript
const baseURL = '/api/short-link/admin/v1'

http.interceptors.request.use(
  (config) => {
    config.headers.Token = getToken() || ''
    config.headers.Username = getUsername() || ''
    return config
  }
)
```

### 用户模块API

| 方法 | 路径 | 说明 | 前端方法名 |
|------|------|------|-----------|
| POST | `/user/login` | 用户登录 | `login(data)` |
| POST | `/user/register` | 用户注册 | `register(data)` |

### 分组模块API

| 方法 | 路径 | 说明 | 前端方法名 |
|------|------|------|-----------|
| GET | `/group/page` | 查询分组列表 | `queryGroup()` |
| POST | `/group/create` | 创建分组 | `addGroup(data)` |
| POST | `/group/update` | 更新分组 | `editGroup(data)` |
| POST | `/group/delete` | 删除分组 | `removeGroup(data)` |

### 短链接模块API

| 方法 | 路径 | 说明 | 前端方法名 |
|------|------|------|-----------|
| GET | `/page` | 查询短链接列表 | `queryPage(data)` |
| POST | `/create` | 创建短链接 | `addSmallLink(data)` |
| POST | `/update` | 更新短链接 | `editSmallLink(data)` |
| POST | `/recycle-bin/save` | 移至回收站 | `toRecycleBin(data)` |
| GET | `/recycle-bin/page` | 查询回收站 | `queryRecycleBin(data)` |
| POST | `/recycle-bin/recover` | 恢复短链接 | `recoverLink(data)` |
| POST | `/recycle-bin/remove` | 彻底删除 | `removeLink(data)` |
| GET | `/stats` | 查询访问统计 | `queryLinkStats(data)` |

### 响应格式

```json
{
  "code": "0",
  "message": "success",
  "data": {}
}
```

---

## Part 5: 组件规范

### Element Plus优先原则

| 需求 | 使用组件 |
|------|----------|
| 按钮 | `<el-button>` |
| 输入框 | `<el-input>` |
| 表格 | `<el-table>` |
| 弹窗 | `<el-dialog>` |
| 表单 | `<el-form>` |

### 登录页面模板

```vue
<template>
  <div class="login-page">
    <div class="login-box">
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRule">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" @click="login(loginFormRef)">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const login = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      const res = await API.user.login(loginForm)
      if (res.data.code === '0') {
        setToken(res.data.data.token)
        setUsername(loginForm.username)
        router.push('/home')
      }
    }
  })
}
</script>
```

---

## Part 6: 认证与权限

### Auth工具函数

```javascript
import Cookies from 'js-cookie'

const TokenKey = 'token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function getUsername() {
  return Cookies.get('username')
}

export function setUsername(username) {
  return Cookies.set('username', username)
}
```

### 路由守卫

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

## Part 7: 状态管理

### Vuex Store

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

export default store
```

---

## Part 8: 样式规范

### 全局样式

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

---

## Part 9: 开发流程

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

---

**文档版本**: v1.0  
**适用版本**: 短链接平台 v1.0
