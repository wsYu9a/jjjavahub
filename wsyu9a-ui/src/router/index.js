import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { isPublic: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/register/index.vue'),
      meta: { isPublic: true }
    },
    {
      path: '/resetpwd',
      name: 'ResetPassword',
      component: () => import('@/views/resetpwd/index.vue'),
      meta: { isPublic: true }
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/index.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/dashboard/index.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'user',
          name: 'UserManage',
          component: () => import('@/views/admin/user/index.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'role',
          name: 'RoleManage',
          component: () => import('@/views/admin/user/role.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'problem',
          name: 'ProblemManage',
          component: () => import('@/views/admin/problem/index.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'category',
          name: 'CategoryManage',
          component: () => import('@/views/admin/problem/category.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'announcement',
          name: 'AnnouncementList',
          component: () => import('@/views/admin/announcement/index.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'announcement/create',
          name: 'AnnouncementCreate',
          component: () => import('@/views/admin/announcement/create.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'system/config',
          name: 'SystemConfig',
          component: () => import('@/views/admin/system/config.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'system/log',
          name: 'SystemLog',
          component: () => import('@/views/admin/system/log.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        },
        {
          path: 'system/backup',
          name: 'SystemBackup',
          component: () => import('@/views/admin/system/backup.vue'),
          meta: { requiresAuth: true, roles: ['ADMIN'] }
        }
      ]
    },
    {
      path: '/dashboard',
      component: () => import('@/views/dashboard/index.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/',
      redirect: (to) => {
        const token = localStorage.getItem('token')
        const role = localStorage.getItem('role')
        
        if (!token) {
          return '/login'
        }
        return role === 'ADMIN' ? '/admin' : '/dashboard'
      }
    },
    {
      path: '/problem/:id',
      component: () => import('@/views/dashboard/problem/detail.vue'),
      meta: { title: '题目详情' }
    },
    {
      path: '/problem/solve/:id',
      component: () => import('@/views/dashboard/problem/solve.vue'),
      meta: { title: '开始挑战' }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  console.log('路由守卫: from=', from.path, 'to=', to.path) // 添加调试日志
  
  // 如果是公共页面，直接放行
  if (to.meta.isPublic) {
    next()
    return
  }

  // 如果已登录要访问登录页，重定向到对应的首页
  if (['/login', '/register', '/resetpwd'].includes(to.path) && token) {
    console.log('已登录，准备重定向')
    next(role === 'ADMIN' ? '/admin' : '/dashboard')
    return
  }

  // 验证需要登录的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      console.log('未登录，重定向到登录页')
      next('/login')
    } else if (to.meta.roles && !to.meta.roles.includes(role?.toUpperCase())) {
      console.log('无权限访问:', role, '需要角色:', to.meta.roles)
      next('/')
    } else {
      console.log('验证通过，允许访问')
      next()
    }
  } else {
    next()
  }
})

export default router 