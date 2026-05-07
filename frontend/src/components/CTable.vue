<template>
  <el-table
    v-bind="$attrs"
    :data="data"
    :loading="loading"
    :border="border"
    :stripe="stripe"
    :highlight-current-row="highlightCurrentRow"
    @selection-change="handleSelectionChange"
  >
    <template v-if="showSelection" #default>
      <el-table-column type="selection" width="55" />
    </template>
    <slot></slot>
    <template v-if="showIndex" #default>
      <el-table-column type="index" label="序号" width="60" />
    </template>
  </el-table>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  border: {
    type: Boolean,
    default: true
  },
  stripe: {
    type: Boolean,
    default: true
  },
  highlightCurrentRow: {
    type: Boolean,
    default: false
  },
  showSelection: {
    type: Boolean,
    default: false
  },
  showIndex: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['selection-change'])

const handleSelectionChange = (val) => {
  emit('selection-change', val)
}
</script>

<style lang="scss" scoped>
</style>
