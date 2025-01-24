<template>
  <router-view v-slot="{ Component }">
    <suspense>
      <component :is="Component" />
      <template #fallback>
        <div>Loading...</div>
      </template>
    </suspense>
  </router-view>
</template>

<script setup>
import { onBeforeUnmount, onErrorCaptured } from 'vue'

onBeforeUnmount(() => {
  // 清理全局资源
})

onErrorCaptured((err, instance, info) => {
  console.error('Captured error:', err)
  console.log('Error instance:', instance)
  console.log('Error info:', info)
  // 如果是动态导入错误，尝试重新加载页面
  if (err.message.includes('Failed to fetch dynamically imported module')) {
    window.location.reload()
  }
  return false
})
</script>

<style>
#app {
  height: 100vh;
}
</style>
