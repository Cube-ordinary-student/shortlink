<template>
  <div class="label-select">
    <el-select
      v-model="selectedLabels"
      :multiple="multiple"
      :placeholder="placeholder"
      :disabled="disabled"
      :size="size"
    >
      <el-option
        v-for="label in labels"
        :key="label.value"
        :label="label.label"
        :value="label.value"
      />
    </el-select>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  labels: {
    type: Array,
    default: () => []
  },
  multiple: {
    type: Boolean,
    default: false
  },
  placeholder: {
    type: String,
    default: '请选择'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'default'
  }
})

defineEmits(['update:modelValue'])
</script>

<style lang="scss" scoped>
.label-select {
  .el-select {
    width: 100%;
  }
}
</style>
