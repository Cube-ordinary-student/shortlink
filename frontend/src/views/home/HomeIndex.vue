<template>
  <div class="home-page">
    <div class="header">
      <div class="logo" @click="toMySpace">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 40" width="30" height="30">
          <circle cx="20" cy="20" r="18" fill="#667eea" />
          <text x="20" y="24" text-anchor="middle" fill="white" font-size="12" font-weight="bold">短</text>
        </svg>
        <span class="logo-text">短链接平台</span>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click">
          <span class="user-info">
            <span class="user-icon">👤</span>
            <span class="username">{{ username }}</span>
            <span class="arrow-icon">▼</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="toMine">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="content-box">
      <div class="sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#252b30"
          text-color="#e8e8e8"
          active-text-color="#667eea"
          unique-opened
        >
          <el-menu-item index="/home/mySpace">
            <span>📦 我的空间</span>
          </el-menu-item>
          <el-menu-item index="/home/recycleBin">
            <span>🗑️ 回收站</span>
          </el-menu-item>
          <el-menu-item index="/home/mine">
            <span>👤 个人中心</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUsername, removeToken, removeUsername } from '@/core/auth'

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()

const username = ref(getUsername() || '')

const activeMenu = computed(() => route.path)

const toMySpace = () => {
  router.push('/home/mySpace')
}

const toMine = () => {
  router.push('/home/mine')
}

const logout = () => {
  removeToken()
  removeUsername()
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.home-page {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;

  .header {
    background: linear-gradient(135deg, #252b30 0%, #1a1f24 100%);
    padding: 0 20px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);

    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;

      .logo-text {
        color: #e8e8e8;
        font-size: 16px;
        font-weight: 600;
      }

      &:hover .logo-text {
        color: #fff;
      }
    }

    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 15px;
        border-radius: 20px;
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        .user-icon {
          width: 20px;
          height: 20px;
          color: #667eea;
        }

        .username {
          color: #e8e8e8;
          font-size: 14px;
        }

        .arrow-icon {
          width: 14px;
          height: 14px;
          color: #999;
        }
      }
    }
  }

  .content-box {
    display: flex;
    height: calc(100vh - 60px);

    .sidebar {
      width: 200px;
      background: #252b30;
      border-right: 1px solid #1a1f24;

      .el-menu {
        border: none;
        height: 100%;

        .el-menu-item {
          margin: 5px 10px;
          border-radius: 8px;
          transition: all 0.3s;

          &:hover {
            background: rgba(102, 126, 234, 0.2);
          }

          &.is-active {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%);
            color: #667eea;
          }
        }
      }
    }

    .main-content {
      flex: 1;
      background: #f5f5f5;
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>
