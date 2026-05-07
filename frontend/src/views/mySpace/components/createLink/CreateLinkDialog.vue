<template>
  <el-dialog 
    v-model="visible" 
    title="创建短链接" 
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form 
      ref="formRef" 
      :model="formData" 
      :rules="formRules" 
      label-width="100px"
    >
      <el-form-item label="原始链接" prop="originUrl">
        <el-input 
          v-model="formData.originUrl" 
          placeholder="请输入原始链接"
          type="textarea"
          :rows="3"
        />
      </el-form-item>
      <el-form-item label="分组" prop="groupId">
        <el-select 
          v-model="formData.groupId" 
          placeholder="请选择分组" 
          style="width: 100%"
        >
          <el-option 
            v-for="group in groups" 
            :key="group.id" 
            :label="group.name" 
            :value="group.id" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="自定义短链">
        <el-input 
          v-model="formData.customSuffix" 
          placeholder="可选，自定义短链后缀（6-12位字母或数字）"
        />
        <p class="tip-text">不填写则自动生成</p>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleCreate">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()
const API = proxy.$API

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  groups: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const formRef = ref()
const formData = reactive({
  originUrl: '',
  groupId: '',
  customSuffix: ''
})

const formRules = reactive({
  originUrl: [
    { required: true, message: '请输入原始链接', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const urlRegex = /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w.-]*)*\/?$/
        if (value && !urlRegex.test(value)) {
          callback(new Error('请输入有效的URL地址'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  groupId: [{ required: true, message: '请选择分组', trigger: 'change' }]
})

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
}

const handleCreate = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await API.smallLinkPage.addSmallLink(formData)
        if (res.data.code === '0') {
          ElMessage.success('创建成功')
          emit('success')
          handleClose()
        } else {
          ElMessage.error(res.data.message || '创建失败')
        }
      } catch (error) {
        ElMessage.error('创建失败')
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.tip-text {
  margin: 5px 0 0 0;
  font-size: 12px;
  color: #999;
}
</style>
