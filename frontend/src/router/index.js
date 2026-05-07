import { createRouter, createWebHistory } from 'vue-router'
import { getToken, setToken, setUsername } from '@/core/auth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginIndex.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/HomeIndex.vue'),
    children: [
      {
        path: '',
        redirect: '/home/mySpace'
      },
      {
        path: 'mySpace',
        name: 'MySpace',
        component: () => import('@/views/mySpace/MySpaceIndex.vue')
      },
      {
        path: 'recycleBin',
        name: 'RecycleBin',
        component: () => import('@/views/recycleBin/RecycleBinIndex.vue')
      },
      {
        path: 'mine',
        name: 'Mine',
        component: () => import('@/views/mine/MineIndex.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

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

export default router
