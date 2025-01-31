import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/pdfParser',
      name: 'PdfParser', 
      component: () => import('../views/PdfParser.vue')
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/login',
      name: 'login', 
      component: () => import('../views/TestLogin1View.vue')
    },
    {
      path: '/login2',
      name: 'login2', 
      component: () => import('../views/TestLogin2View.vue')
    },
    {
      path: '/instruments',
      name: 'instruments', 
      component: () => import('../views/instruments-v1.vue')
    },
    {
      path: '/instruments-v2',
      name: 'instruments-v2', 
      component: () => import('../views/instruments-v2.vue')
    }
  ]
})

export default router
