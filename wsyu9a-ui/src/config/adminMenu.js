export default [
  {
    title: '仪表盘',
    icon: 'Odometer',
    path: '/admin'
  },
  {
    title: '用户管理',
    icon: 'User',
    children: [
      {
        title: '用户列表',
        path: '/admin/user'
      },
      {
        title: '角色管理',
        path: '/admin/role'
      }
    ]
  },
  {
    title: '题目管理',
    icon: 'Document',
    children: [
      {
        title: '题目列表',
        path: '/admin/problem/list'
      },
      {
        title: '添加题目',
        path: '/admin/problem/add'
      },
      {
        title: '分类管理',
        path: '/admin/category'
      }
    ]
  },
  {
    title: '系统管理',
    icon: 'Setting',
    children: [
      {
        title: '系统配置',
        path: '/admin/system/config'
      },
      {
        title: '容器管理',
        icon: 'Monitor',
        path: '/admin/system/container'
      }
    ]
  },
  {
    title: '公告管理',
    icon: 'Bell',
    path: '/admin/announcement'
  }
] 