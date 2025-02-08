<template>
  <div class="admin-container">
    <el-container class="admin-layout">
      <!-- 侧边栏 -->
      <el-aside width="260px" class="sidebar">
        <div class="logo-container">
          <span class="logo-text">管理控制台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <!-- 使用配置文件中的菜单项 -->
          <template v-for="item in menuItems" :key="item.path">
            <!-- 没有子菜单的项目 -->
            <el-menu-item v-if="!item.children" :index="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
            
            <!-- 有子菜单的项目 -->
            <el-sub-menu v-else :index="item.path">
              <template #title>
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item 
                v-for="child in item.children" 
                :key="child.path"
                :index="child.path"
              >
                <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
                <span>{{ child.title }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="breadcrumb">
            <el-breadcrumb>
              <el-breadcrumb-item>管理控制台</el-breadcrumb-item>
              <el-breadcrumb-item>{{ activeMenuLabel }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="admin-info">
            <el-dropdown trigger="click">
              <div class="admin-profile">
                
                <span class="username">{{ username }}</span>
                <span class="role-tag">管理员</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/dashboard')">
                    <el-icon><House /></el-icon>返回前台
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/resetpwd')">
                    <el-icon><Lock /></el-icon>修改密码
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import adminMenu from '@/config/adminMenu'
import {
  User,
  Document,
  Lock,
  SwitchButton,
  House,
  Collection,
  List,
  Bell,
  Setting,
  Upload,
  TrendCharts,
  Monitor,
  Odometer
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const username = ref(localStorage.getItem('username') || '')
const menuItems = ref(adminMenu)

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 菜单标签映射
const menuLabels = {
  '/admin': '数据统计',
  '/admin/user': '用户列表',
  '/admin/role': '角色管理',
  '/admin/category': '分类管理',
  '/admin/problem/list': '题目列表',
  '/admin/problem/add': '添加题目',
  '/admin/announcement': '公告列表',
  '/admin/system/config': '系统配置',
  '/admin/system/container': '容器管理'
}

// 计算当前菜单标签
const activeMenuLabel = computed(() => menuLabels[route.path] || '首页')

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  
  ElMessage.success('退出成功')
  router.push('/login')
}

// 添加组件卸载前的清理
onBeforeUnmount(() => {
  // 清理可能的定时器或事件监听器
})
</script>

<style lang="scss" scoped>
.admin-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.admin-layout {
  min-height: 100vh;
}

.sidebar {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid #1f2d3d;
  
  .logo-text {
    color: #fff;
    font-size: 20px;
    font-weight: bold;
  }
}

.sidebar-menu {
  border: none;
}

.main-container {
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.admin-info {
  display: flex;
  align-items: center;
  
  .admin-profile {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    
    .username {
      font-size: 14px;
      color: #606266;
    }
    
    .role-tag {
      background-color: #ecf5ff;
      color: #409eff;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 12px;
    }
  }
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 