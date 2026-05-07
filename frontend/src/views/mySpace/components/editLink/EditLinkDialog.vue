<template>
  <el-dialog 
    v-model="visible" 
    title="编辑短链接" 
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
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleEdit">保存</el-button>
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
  linkData: {
    type: Object,
    default: () => ({})
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
  id: '',
  originUrl: '',
  groupId: ''
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
  if (val && props.linkData) {
    formData.id = props.linkData.id || ''
    formData.originUrl = props.linkData.originUrl || ''
    formData.groupId = props.linkData.groupId || ''
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
  formRef.value?.resetFields()
}

const handleEdit = async () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await API.smallLinkPage.editSmallLink(formData)
        if (res.data.code === '0') {
          ElMessage.success('保存成功')
          emit('success')
          handleClose()
        } else {
          ElMessage.error(res.data.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      }
    }
  })
}
</script>
