import axios from 'axios'
import { getTenantFromSubdomain } from '@/utils/tenant'

const baseURL = import.meta.env.VITE_APP_API_BASE_URL;

function getBaseURL() {
  // 依照subdomain取找正確的oauth2 server    
  const host = window.location.origin;  
  const tenantId = getTenantFromSubdomain();
    if(!tenantId || 'localhost' === tenantId){
        // 如果沒有抓到tenantId，則使用預設oauth2 server
        return `${baseURL}`;
    }else{
      return host ;  
    }  
}
const baseNewURL = getBaseURL() ;


// 創建axios instance
const instance = axios.create({ 
  baseURL:  `${baseNewURL}`
})

// 新增請求攔截器
instance.interceptors.request.use(
  (config) => {
    const token = JSON.parse(sessionStorage.getItem('user'))?.token;
    const tenantId = JSON.parse(sessionStorage.getItem('user'))?.tenantId;
    const customTenantIdHeader = 'X-Tenant-Id';
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;      
    }
    if(tenantId){
      config.headers[`${customTenantIdHeader}`] = tenantId; 
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 新增回應攔截器
// 異步載入router避免開發時HMR失效的問題
instance.interceptors.response.use(
  async (response) => {
    const { default: router } = await import('@/router/index')
    switch (response?.data?.body?.responseCode) {
      // 使用者沒有權限時強制登出
      case 1307:
        router.push('/login')
        break
      default:
        break
    }

    return response
  },
  async (error) => {
    console.log(error)
    const { default: router } = await import('@/router/index');
    switch (error?.response?.status) {
      // UNAUTHORIZED
      case 401:
        router.push('/login')
        break
      default:
        break
    }

    return Promise.reject(error)
  }
)

export default instance
