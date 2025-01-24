import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/admin/index.vue'

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
          redirect: '/admin/problem/list',
          meta: { 
            title: '题目管理', 
            icon: 'el-icon-document', 
            requiresAuth: true, 
            roles: ['ADMIN'],
            showInMenu: true
          },
          children: [
            {
              path: 'list',
              name: 'ProblemList',
              component: () => import('../views/admin/problem/list.vue'),
              meta: { 
                title: '题目列表', 
                requiresAuth: true, 
                roles: ['ADMIN'],
                showInMenu: true
              }
            },
            {
              path: 'add',
              name: 'ProblemAdd',
              component: () => import('../views/admin/problem/add.vue'),
              meta: { 
                title: '添加题目', 
                requiresAuth: true, 
                roles: ['ADMIN'],
                showInMenu: true
              }
            }
          ]
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
          path: 'system',
          meta: { title: '系统管理', icon: 'Setting' },
          children: [
            {
              path: 'config',
              name: 'SystemConfig',
              component: () => import('@/views/admin/system/config.vue'),
              meta: { title: '系统配置', requiresAuth: true, roles: ['ADMIN'] }
            },
            {
              path: 'container',
              name: 'ContainerManagement',
              component: () => import('@/views/admin/system/container.vue'),
              meta: { 
                title: '容器管理', 
                icon: 'Monitor', 
                requiresAuth: true, 
                roles: ['ADMIN']
              }
            }
          ]
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

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  try {
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')

    // 处理公共页面
    if (to.meta.isPublic) {
      next()
      return
    }

    // 处理已登录用户访问登录页
    if (['/login', '/register', '/resetpwd'].includes(to.path) && token) {
      next(role === 'ADMIN' ? '/admin' : '/dashboard')
      return
    }

    // 处理需要认证的页面
    if (to.meta.requiresAuth) {
      if (!token) {
        next('/login')
        return
      }
      if (to.meta.roles && !to.meta.roles.includes(role?.toUpperCase())) {
        next('/')
        return
      }
    }

    // 清除可能的路由缓存
    if (to.matched.some(record => record.meta.clearCache)) {
      to.matched.forEach(record => {
        const component = record.components.default
        if (component.name) {
          const cache = router.options.routes.__vueCache
          if (cache && cache[component.name]) {
            delete cache[component.name]
          }
        }
      })
    }
    
    next()
  } catch (error) {
    console.error('Navigation error:', error)
    next(false)
  }
})

export default router 