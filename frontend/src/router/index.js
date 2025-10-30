import { createRouter, createWebHistory } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'

import dashboardView from '@/views/dashboardView.vue'
import loginView from '@/views/loginView.vue'
import registerView from '@/views/registerView.vue'
import analyticsView from '@/views/analyticsView.vue'
import BillingsView from '@/views/billingsView.vue'
import settingsView from '@/views/settingsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'login', component: loginView, meta: { isPublic: true } },
    { path: '/register', name: 'register', component: registerView, meta: { isPublic: true } },
    { path: '/', name: 'Dashboard', component: dashboardView},
    { path: '/analytics', name: 'Analytics', component: analyticsView},
    { path: '/billings', name: 'Billings', component: BillingsView},
    { path: '/settings', name: 'Settings', component: settingsView},
  ],
})

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserdataStore()
  const isLoggedIn = !!userStore.token

  // 公开页面直接放行
  if (to.meta.isPublic) {
    // 如果已登录，访问登录/注册页面时重定向到首页
    if (isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 如果是需要鉴权的页面，检查是否已登录
  if (!isLoggedIn) {
    // 未登录，重定向到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath } // 保存原始路径，登录后可以跳转回来
    })
  } else {
    // 已登录
    next()
  }
})

// 全局后置路由守卫，设置页面标题
router.afterEach((to) => {
  if (to.name) {
    document.title = `${to.name} | Personal Finance Tracker`
  } else {
    document.title = 'Personal Finance Tracker'
  }
})

export default router
