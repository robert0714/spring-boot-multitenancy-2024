import { ref } from 'vue'
import { defineStore } from 'pinia'
import { useSessionStorage } from '@vueuse/core'

export const useUserStore = defineStore('user', () => {
  const user = useSessionStorage(
    'user',
    ref({
      companyId: '',
      id: '',
      snNumber: 'string',
      userName: '',
      name: '',
      passwd: '',
      catchpa: '',
      token: '',
      tenantId: '',
      role: [],
      logoutTime: '',
      countdownTime: '',
      decimalPoint: ''
    })
  )

  return { user }
})
