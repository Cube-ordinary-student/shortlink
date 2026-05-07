<template>
  <div class="empty-list">
    <div class="empty-icon">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="64" height="64">
        <circle cx="32" cy="32" r="30" fill="#f5f5f5" stroke="#ddd" stroke-width="2" />
        <path d="M20 28h24M20 36h16" stroke="#ccc" stroke-width="2" stroke-linecap="round" />
      </svg>
    </div>
    <p class="empty-text">{{ text }}</p>
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style lang="scss" scoped>
.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;

  .empty-icon {
    margin-bottom: 16px;
    opacity: 0.6;
  }

  .empty-text {
    color: #999;
    font-size: 14px;
    margin-bottom: 16px;
  }
}
</style>
