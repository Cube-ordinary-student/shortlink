<template>
  <div class="login-page">
    <div class="login-box">
      <div class="logo-container">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="60" height="60">
          <circle cx="50" cy="50" r="45" fill="#667eea" />
          <text x="50" y="55" text-anchor="middle" fill="white" font-size="24" font-weight="bold">短</text>
        </svg>
        <h1>短链接平台</h1>
      </div>
      
      <div class="form-container">
        <div class="tabs">
          <span 
            :class="{ active: isLogin }" 
            @click="switchToLogin"
          >登录</span>
          <span 
            :class="{ active: !isLogin }" 
            @click="switchToRegister"
          >注册</span>
        </div>
        
        <div class="logon" :class="{ hidden: !isLogin }">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRule">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
              />
            </el-form-item>
            <el-button type="primary" class="submit-btn" @click="login(loginFormRef)">登录</el-button>
          </el-form>
        </div>

        <div class="register" :class="{ hidden: isLogin }">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerFormRule">
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请确认密码"
              />
            </el-form-item>
            <el-button type="primary" class="submit-btn" @click="register(registerFormRef)">注册</el-button>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { setToken, setUsername } from '@/core/auth'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const router = useRouter()
const isLogin = ref(true)

const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginFormRule = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const registerFormRule = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const switchToRegister = () => {
  isLogin.value = false
}

const switchToLogin = () => {
  isLogin.value = true
}

const login = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      try {
        const res = await API.user.login(loginForm)
        if (res.data.code === '0') {
          setToken(res.data.data.token)
          setUsername(loginForm.username)
          localStorage.setItem('token', res.data.data.token)
          localStorage.setItem('username', loginForm.username)
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.error(res.data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请检查用户名和密码')
      }
    }
  })
}

const register = async (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      try {
        const res = await API.user.register({
          username: registerForm.username,
          password: registerForm.password
        })
        if (res.data.code === '0') {
          ElMessage.success('注册成功！')
          isLogin.value = true
        } else {
          ElMessage.error(res.data.message || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后重试')
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: rotate 30s linear infinite;
  }

  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }

  .login-box {
    position: relative;
    z-index: 1;
    width: 380px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);

    .logo-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 30px;

      h1 {
        margin-top: 15px;
        font-size: 24px;
        font-weight: 600;
        color: #333;
      }
    }

    .form-container {
      .tabs {
        display: flex;
        margin-bottom: 25px;
        border-bottom: 1px solid #eee;

        span {
          flex: 1;
          text-align: center;
          padding: 10px 0;
          cursor: pointer;
          font-size: 14px;
          color: #666;
          position: relative;
          transition: color 0.3s;

          &.active {
            color: #667eea;
            font-weight: 600;

            &::after {
              content: '';
              position: absolute;
              bottom: -1px;
              left: 50%;
              transform: translateX(-50%);
              width: 40px;
              height: 2px;
              background: #667eea;
              border-radius: 1px;
            }
          }

          &:hover {
            color: #667eea;
          }
        }
      }

      .hidden {
        display: none;
      }

      .el-form-item {
        margin-bottom: 20px;
      }

      .submit-btn {
        width: 100%;
        height: 44px;
        font-size: 15px;
        font-weight: 500;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 8px;
        transition: transform 0.2s, box-shadow 0.2s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }

        &:active {
          transform: translateY(0);
        }
      }
    }
  }
}
</style>
