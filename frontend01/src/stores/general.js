import { ref, inject } from 'vue'
import { defineStore } from 'pinia'
import instance from '@/utils/interceptors'

export const useGeneralStore = defineStore('general', () => {
  const deleteItems = ref([])

  const permissionVisible = ref(false)
  const notFoundVisible = ref(false)
  const logoutVisible = ref(false)

  const logout = async () => {
    const loginUrl = import.meta.env.VITE_BASE_LOGIN_URL
    const { default: router } = await import('@/router/index')

    await instance
      .post(`${loginUrl}/auth/logout`, {
        refreshToken: JSON.parse(sessionStorage.getItem('user')).refreshToken
      })
      .then(() => {
        sessionStorage.clear()
        router.push('/login')
      })
      .catch(() => {
        sessionStorage.clear()
        router.push('/login')
      })
  }

  return { deleteItems, permissionVisible, logout, logoutVisible, notFoundVisible }
})
