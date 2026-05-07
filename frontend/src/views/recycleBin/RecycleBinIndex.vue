<template>
  <div class="recycle-bin">
    <div class="page-header">
      <div class="header-left">
        <h2>回收站</h2>
        <p class="subtitle">已删除的短链接可在此恢复或彻底删除</p>
      </div>
    </div>

    <div class="search-section">
      <el-card class="search-card">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryPage">
              🔍 查询
            </el-button>
            <el-button @click="resetQuery">
              🔄 重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="table-section">
      <el-card class="table-card">
        <div class="card-header">
          <span class="card-title">回收站列表</span>
          <span class="record-count">共 {{ total }} 条记录</span>
        </div>
        
        <el-table 
          :data="tableData" 
          border 
          stripe
          :loading="loading"
          class="recycle-table"
        >
          <el-table-column prop="shortLink" label="短链接" min-width="180" />
          <el-table-column prop="originUrl" label="原始链接" min-width="250" show-overflow-tooltip />
          <el-table-column prop="groupName" label="分组" width="120" />
          <el-table-column prop="deleteTime" label="删除时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="success" link size="small" @click="recoverLink(row)">
                🔄 恢复
              </el-button>
              <el-button type="danger" link size="small" @click="removeLink(row)">
                🗑️ 彻底删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="queryPage"
            @current-change="queryPage"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const queryParams = reactive({
  dateRange: [],
  pageNum: 1,
  pageSize: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const queryPage = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }
    if (queryParams.dateRange?.length === 2) {
      params.startDate = queryParams.dateRange[0]
      params.endDate = queryParams.dateRange[1]
    }
    const res = await API.smallLinkPage.queryRecycleBin(params)
    if (res.data.code === '0') {
      tableData.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (error) {
    console.error('查询失败', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.dateRange = []
  queryParams.pageNum = 1
  queryPage()
}

const recoverLink = async (row) => {
  try {
    await ElMessageBox.confirm('确定恢复该短链接吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await API.smallLinkPage.recoverLink({ id: row.id })
    if (res.data.code === '0') {
      ElMessage.success('恢复成功')
      queryPage()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
    }
  }
}

const removeLink = async (row) => {
  try {
    await ElMessageBox.confirm('确定彻底删除该短链接吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await API.smallLinkPage.removeLink({ id: row.id })
    if (res.data.code === '0') {
      ElMessage.success('删除成功')
      queryPage()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  queryPage()
})
</script>

<style lang="scss" scoped>
.recycle-bin {
  .page-header {
    margin-bottom: 20px;

    .header-left {
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
  }

  .search-section {
    margin-bottom: 20px;

    .search-card {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }

    .search-form {
      margin: 0;
    }
  }

  .table-section {
    .table-card {
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid #eee;

        .card-title {
          font-size: 15px;
          font-weight: 600;
          color: #333;
        }

        .record-count {
          font-size: 13px;
          color: #999;
        }
      }

      .recycle-table {
        --el-table-row-hover-bg-color: rgba(245, 108, 108, 0.05);
      }

      .pagination-container {
        display: flex;
        justify-content: flex-end;
        margin-top: 20px;
        padding-top: 15px;
        border-top: 1px solid #eee;
      }
    }
  }
}
</style>
