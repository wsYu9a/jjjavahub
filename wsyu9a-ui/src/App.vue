<template>
  <router-view />
</template>

<script setup>
import { onBeforeUnmount } from 'vue'

onBeforeUnmount(() => {
  // 清理全局资源
})
</script>

<style>
#app {
  height: 100vh;
}
</style>
