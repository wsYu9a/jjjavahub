<template>
  <el-menu>
    <!-- ... 其他菜单项 -->
    <el-sub-menu index="/admin/problem">
      <template #title>
        <el-icon><document /></el-icon>
        <span>题目管理</span>
      </template>
      <el-menu-item index="/admin/problem/list">题目列表</el-menu-item>
      <el-menu-item index="/admin/problem/add">添加题目</el-menu-item>
    </el-sub-menu>
    <!-- ... 其他菜单项 -->
  </el-menu>
</template> 