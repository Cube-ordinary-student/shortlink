<template>
  <div class="mine">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="subtitle">管理您的账户设置</p>
    </div>

    <div class="content-grid">
      <el-card class="info-card">
        <template #header>
          <span class="card-title">账户信息</span>
        </template>
        <el-form :model="userInfo" label-width="100px" class="info-form">
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          <el-form-item label="注册时间">
            <el-input v-model="userInfo.registerTime" disabled />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="domain-card">
        <template #header>
          <span class="card-title">域名设置</span>
        </template>
        <el-form :model="domainForm" label-width="100px" class="domain-form">
          <el-form-item label="默认域名">
            <el-input 
              v-model="domainForm.domain" 
              placeholder="请输入域名"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveDomain">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="stats-card">
        <template #header>
          <span class="card-title">访问统计</span>
        </template>
        <div ref="chartRef" class="chart-container"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const { proxy } = getCurrentInstance()

const userInfo = reactive({
  username: proxy.$store.getters.getUsername || '用户名',
  registerTime: '2024-01-01 10:00:00'
})

const domainForm = reactive({
  domain: proxy.$store.state.domain
})

const chartRef = ref()
let chartInstance = null

const saveDomain = () => {
  proxy.$store.dispatch('setDomain', domainForm.domain)
  ElMessage.success('保存成功')
}

const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  const option = {
    title: { 
      text: '访问趋势', 
      left: 'center',
      textStyle: {
        fontSize: 14,
        fontWeight: 500,
        color: '#333'
      }
    },
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      textStyle: {
        color: '#333'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: {
          color: '#eee'
        }
      },
      axisLabel: {
        color: '#999'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      },
      axisLabel: {
        color: '#999'
      }
    },
    series: [{
      name: '访问量',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        color: '#667eea',
        width: 3
      },
      itemStyle: {
        color: '#667eea',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
        ])
      },
      data: [120, 200, 150, 80, 70, 110, 130]
    }]
  }
  
  chartInstance.setOption(option)
}

watch(() => proxy.$store.state.domain, () => {
  domainForm.domain = proxy.$store.state.domain
})

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => chartInstance?.resize())
})
</script>

<style lang="scss" scoped>
.mine {
  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      margin: 0;
    }

    .subtitle {
      font-size: 13px;
      color: #999;
      margin-top: 5px;
    }
  }

  .content-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;

    .info-card, .domain-card {
      grid-column: span 1;
    }

    .stats-card {
      grid-column: span 2;
    }

    .el-card {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .card-title {
        font-size: 15px;
        font-weight: 600;
        color: #333;
      }

      .info-form, .domain-form {
        padding-top: 10px;
      }
    }

    .chart-container {
      width: 100%;
      height: 350px;
    }
  }
}
</style>
