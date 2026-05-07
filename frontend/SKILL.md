---
name: shortlink-vue
description: Vue3 短链接平台前端开发规范。当开发短链接创建、分组管理、数据统计、回收站功能时使用此 skill。
---

# Vue3 短链接平台前端开发规范

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
| **Vanta** | ^0.5.24 | 动态背景效果 |

---

## Part 2: 目录结构

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
│   ├── png/                # PNG图片
│   ├── svg/                # SVG图标
│   ├── base.css            # 基础样式
│   ├── main.css            # 入口样式
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
│   │   └── components/
│   │       ├── chartsInfo/ # 图表组件
│   │       ├── createLink/ # 创建链接
│   │       ├── editLink/   # 编辑链接
│   │       └── qrCode/     # 二维码
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
| 常量 | UPPER_SNAKE_CASE | 在代码中使用 |

### 变量命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 普通变量 | camelCase | `loginForm` |
| 响应式引用 | camelCase + ref | `loginFormRef` |
| 布尔值 | is/has/can前缀 | `isLogin`, `hasToken` |
| 状态对象 | camelCase | `queryParams` |

### 方法命名

| 动作 | 前缀 | 示例 |
|------|------|------|
| 登录/注册 | login/addUser | `login()`, `addUser()` |
| 获取数据 | query | `queryGroup()`, `queryPage()` |
| 创建链接 | addSmallLink | `addSmallLink()` |
| 编辑链接 | editSmallLink | `editSmallLink()` |
| 删除/恢复 | remove/recover | `removeLink()`, `recoverLink()` |
| 跳转页面 | to | `toMySpace()`, `toMine()` |
| 退出登录 | logout | `logout()` |

---

## Part 4: 组件规范

### Element Plus 优先原则

| 需求 | 使用 | 避免 |
|------|------|------|
| 按钮 | `<el-button>` | 自定义 `.btn` |
| 输入框 | `<el-input>` | 自定义 `.input` |
| 表格 | `<el-table>` | 自定义表格 |
| 弹窗 | `<el-dialog>` | 自定义弹窗 |
| 表单 | `<el-form>` | 自定义表单 |
| 下拉菜单 | `<el-dropdown>` | 自定义下拉 |

### 登录页面模板

```vue
<template>
  <div class="login-page">
    <div class="login-box">
      <div class="logon" :class="{ hidden: !isLogin }">
        <h2>用户登录</h2>
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
      <!-- 注册表单类似结构 -->
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginFormRule = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const login = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      const res = await API.user.login(loginForm)
      if (res.data.code === '0') {
        // 保存token和username
        setToken(res.data.data.token)
        setUsername(loginForm.username)
        ElMessage.success('登录成功！')
        router.push('/home')
      }
    }
  })
}
</script>
```

### 短链接创建组件

```vue
<template>
  <div class="create-link">
    <el-form ref="formRef" :model="formData">
      <el-form-item label="原始链接">
        <el-input v-model="formData.originUrl" placeholder="请输入原始链接" />
      </el-form-item>
      <el-form-item label="分组">
        <el-select v-model="formData.groupId">
          <el-option v-for="group in groups" :key="group.id" :label="group.name" :value="group.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="自定义短链">
        <el-input v-model="formData.customSuffix" placeholder="可选，自定义短链后缀" />
      </el-form-item>
      <el-button type="primary" @click="handleCreate">创建短链接</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const formRef = ref()
const formData = reactive({
  originUrl: '',
  groupId: '',
  customSuffix: ''
})

const groups = ref([])

onMounted(async () => {
  const res = await API.group.queryGroup()
  groups.value = res.data.data
})

const handleCreate = async () => {
  const res = await API.smallLinkPage.addSmallLink(formData)
  if (res.data.success) {
    ElMessage.success('创建成功')
  }
}
</script>
```

---

## Part 5: API 规范

### 基础配置

```javascript
// axios.js
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

http.interceptors.response.use(
  (res) => {
    if (res.status === 200) {
      return Promise.resolve(res)
    }
    return Promise.reject(res)
  },
  (err) => {
    if (err.response.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default http
```

### API 模块结构

```javascript
// api/modules/smallLinkPage.js
import http from '../axios'

export default {
  queryPage(data) {
    return http({ url: '/page', method: 'get', params: data })
  },
  addSmallLink(data) {
    return http({ url: '/create', method: 'post', data })
  },
  editSmallLink(data) {
    return http({ url: '/update', method: 'post', data })
  },
  toRecycleBin(data) {
    return http({ url: '/recycle-bin/save', method: 'post', data })
  },
  queryRecycleBin(data) {
    return http({ url: '/recycle-bin/page', method: 'get', params: data })
  },
  recoverLink(data) {
    return http({ url: '/recycle-bin/recover', method: 'post', data })
  },
  removeLink(data) {
    return http({ url: '/recycle-bin/remove', method: 'post', data })
  },
  queryLinkStats(data) {
    return http({ url: 'stats', method: 'get', params: data })
  }
}
```

---

## Part 6: 认证与权限

### Auth 工具函数

```javascript
// core/auth.js
import Cookies from 'js-cookie'

const TokenKey = 'token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function getUsername() {
  return Cookies.get('username')
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function setUsername(username) {
  return Cookies.set('username', username)
}

export function removeKey() {
  return Cookies.remove(TokenKey)
}

export function removeUsername() {
  return Cookies.remove('username')
}
```

### 路由守卫

```javascript
// router/index.js
router.beforeEach(async (to, from, next) => {
  setToken(localStorage.getItem('token'))
  setUsername(localStorage.getItem('username'))
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

## Part 7: 样式规范

### 全局样式

```scss
// style.scss
@import 'element-plus/dist/index.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
}

#app {
  width: 100%;
  min-height: 100vh;
}
```

### 组件样式

```scss
// HomeIndex.vue
.header {
  background-color: #252b30;
  padding: 0 20px;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  .logo {
    color: #e8e8e8;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    
    &:hover {
      color: #fff;
    }
  }
}

.content-box {
  height: calc(100vh - 54px);
  background-color: #fff;
}
```

---

## Part 8: 状态管理

### Vuex Store

```javascript
// store/index.js
import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      domain: 's.nageoffer.com'
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

## Part 9: 图表组件规范

### ECharts 封装示例

```vue
<template>
  <div ref="chartRef" class="chart"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  chartData: {
    type: Object,
    default: () => ({})
  }
})

const chartRef = ref()
let chartInstance = null

const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  const option = {
    title: { text: '访问统计' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: props.chartData.xData || []
    },
    yAxis: { type: 'value' },
    series: [{
      data: props.chartData.seriesData || [],
      type: 'line'
    }]
  }
  
  chartInstance.setOption(option)
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => chartInstance?.resize())
})

watch(() => props.chartData, () => updateChart(), { deep: true })
</script>

<style scoped>
.chart {
  width: 100%;
  height: 300px;
}
</style>
```

---

## Part 10: 二维码生成

### QRCode 组件

```vue
<template>
  <div class="qr-code">
    <div ref="qrRef"></div>
    <p class="link-text">{{ shortLink }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import QRCode from 'qrcode'

const props = defineProps({
  shortLink: {
    type: String,
    required: true
  }
})

const qrRef = ref()

onMounted(() => {
  QRCode.toCanvas(qrRef.value, props.shortLink, {
    width: 150,
    margin: 2
  })
})
</script>

<style scoped>
.qr-code {
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .link-text {
    margin-top: 10px;
    font-size: 12px;
    color: #666;
    word-break: break-all;
    text-align: center;
    max-width: 150px;
  }
}
</style>
```
