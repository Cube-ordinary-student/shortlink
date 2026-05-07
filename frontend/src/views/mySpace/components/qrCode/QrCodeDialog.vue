<template>
  <el-dialog 
    v-model="visible" 
    title="二维码" 
    width="350px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="qr-content">
      <div ref="qrRef" class="qr-canvas"></div>
      <p class="link-text">{{ shortLink }}</p>
      <el-button type="primary" size="small" class="copy-btn" @click="copyLink">
        📋 复制链接
      </el-button>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
import QRCode from 'qrcode'

const { proxy } = getCurrentInstance()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  linkData: {
    type: Object,
    default: () => ({})
  },
  domain: {
    type: String,
    default: 's.lanyue.com'
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(props.modelValue)
const qrRef = ref()

const shortLink = computed(() => {
  return `http://${props.domain}/${props.linkData.shortLinkSuffix || ''}`
})

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

watch(visible, (val) => {
  if (val) {
    setTimeout(() => {
      generateQrCode()
    }, 100)
  }
})

const generateQrCode = () => {
  if (!qrRef.value) return
  QRCode.toCanvas(qrRef.value, shortLink.value, {
    width: 180,
    margin: 2,
    color: {
      dark: '#333',
      light: '#fff'
    }
  })
}

const handleClose = () => {
  visible.value = false
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shortLink.value)
    ElMessage.success('复制成功')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}
</script>

<style lang="scss" scoped>
.qr-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;

  .qr-canvas {
    margin-bottom: 20px;
    padding: 10px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }

  .link-text {
    font-size: 13px;
    color: #666;
    word-break: break-all;
    text-align: center;
    margin-bottom: 20px;
    max-width: 250px;
    line-height: 1.5;
  }

  .copy-btn {
    padding: 8px 24px;
    border-radius: 20px;
    font-size: 13px;
  }
}
</style>
