export const adminMenus = [
  {
    title: '仪表盘',
    path: '/admin',
    icon: 'el-icon-menu'
  },
  {
    title: '用户管理',
    path: '/admin/user',
    icon: 'el-icon-user'
  },
  {
    title: '题目管理',
    path: '/admin/problem',
    icon: 'el-icon-document',
    children: [
      {
        title: '题目列表',
        path: '/admin/problem/list'
      },
      {
        title: '添加题目',
        path: '/admin/problem/add'
      }
    ]
  },
  // ... 其他菜单项
] 