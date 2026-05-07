import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.scss'

import API from './api'

const app = createApp(App)
app.config.globalProperties.$API = API

app.use(router).use(store).use(ElementPlus).mount('#app')
