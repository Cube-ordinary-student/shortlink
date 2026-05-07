<template>
  <div class="my-space">
    <div class="page-header">
      <div class="header-left">
        <h2>我的空间</h2>
        <p class="subtitle">管理您的短链接</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showCreateDialog" class="create-btn">
          + 创建短链接
        </el-button>
      </div>
    </div>

    <div class="search-section">
      <el-card class="search-card">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="分组">
            <el-select 
              v-model="queryParams.groupId" 
              placeholder="请选择分组" 
              clearable
              style="width: 180px;"
            >
              <el-option 
                v-for="group in groups" 
                :key="group.id" 
                :label="group.name" 
                :value="group.id" 
              />
            </el-select>
          </el-form-item>
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
          <span class="card-title">短链接列表</span>
          <span class="record-count">共 {{ total }} 条记录</span>
        </div>
        
        <el-table 
          :data="tableData" 
          border 
          stripe
          :loading="loading"
          class="link-table"
        >
          <el-table-column prop="shortLink" label="短链接" min-width="180">
            <template #default="{ row }">
              <a 
                :href="`http://${domain}/${row.shortLinkSuffix}`" 
                target="_blank" 
                class="link-url"
              >
                {{ `http://${domain}/${row.shortLinkSuffix}` }}
              </a>
            </template>
          </el-table-column>
          <el-table-column prop="originUrl" label="原始链接" min-width="250" show-overflow-tooltip />
          <el-table-column prop="groupName" label="分组" width="120" />
          <el-table-column prop="pv" label="访问量" width="90" align="center">
            <template #default="{ row }">
              <span class="pv-count">{{ row.pv || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="showEditDialog(row)">
                ✏️ 编辑
              </el-button>
              <el-button type="warning" link size="small" @click="showQrCode(row)">
                📷 二维码
              </el-button>
              <el-button type="danger" link size="small" @click="toRecycleBin(row)">
                🗑️ 删除
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

    <CreateLinkDialog 
      v-model="createDialogVisible" 
      :groups="groups"
      @success="handleCreateSuccess" 
    />
    <EditLinkDialog 
      v-model="editDialogVisible" 
      :link-data="currentLink"
      :groups="groups"
      @success="handleEditSuccess" 
    />
    <QrCodeDialog 
      v-model="qrDialogVisible" 
      :link-data="currentLink"
      :domain="domain"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CreateLinkDialog from './components/createLink/CreateLinkDialog.vue'
import EditLinkDialog from './components/editLink/EditLinkDialog.vue'
import QrCodeDialog from './components/qrCode/QrCodeDialog.vue'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const queryParams = reactive({
  groupId: '',
  dateRange: [],
  pageNum: 1,
  pageSize: 10
})

const groups = ref([])
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const qrDialogVisible = ref(false)
const currentLink = ref({})

const domain = computed(() => {
  return proxy.$store.state.domain
})

const queryGroup = async () => {
  try {
    const res = await API.group.queryGroup()
    if (res.data.code === '0') {
      groups.value = res.data.data || []
    }
  } catch (error) {
    console.error('查询分组失败', error)
  }
}

const queryPage = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      groupId: queryParams.groupId
    }
    if (queryParams.dateRange?.length === 2) {
      params.startDate = queryParams.dateRange[0]
      params.endDate = queryParams.dateRange[1]
    }
    const res = await API.smallLinkPage.queryPage(params)
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
  queryParams.groupId = ''
  queryParams.dateRange = []
  queryParams.pageNum = 1
  queryPage()
}

const showCreateDialog = () => {
  createDialogVisible.value = true
}

const showEditDialog = (row) => {
  currentLink.value = { ...row }
  editDialogVisible.value = true
}

const showQrCode = (row) => {
  currentLink.value = { ...row }
  qrDialogVisible.value = true
}

const toRecycleBin = async (row) => {
  try {
    await ElMessageBox.confirm('确定将该短链接移至回收站吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await API.smallLinkPage.toRecycleBin({ id: row.id })
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

const handleCreateSuccess = () => {
  queryPage()
}

const handleEditSuccess = () => {
  queryPage()
}

onMounted(() => {
  queryGroup()
  queryPage()
})
</script>

<style lang="scss" scoped>
.my-space {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

    .create-btn {
      padding: 10px 20px;
      font-size: 14px;
      border-radius: 8px;
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

      .link-table {
        --el-table-row-hover-bg-color: rgba(102, 126, 234, 0.05);
      }

      .link-url {
        color: #667eea;
        text-decoration: none;
        font-size: 13px;

        &:hover {
          text-decoration: underline;
        }
      }

      .pv-count {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-width: 36px;
        height: 24px;
        padding: 0 8px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        color: #667eea;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;
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
