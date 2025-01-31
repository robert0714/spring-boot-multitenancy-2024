import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'

import { getTenantFromSubdomain } from './utils/tenant';
import { getTenantConfig } from './config/tenantConfig';
import instance from '@/utils/interceptors';



const tenantId = getTenantFromSubdomain();
const config = getTenantConfig(tenantId);

document.documentElement.className = tenantId;
console.log('tenant:', tenantId);
console.log('config:', config);
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue)

app.provide('config', config); // Provide tenant-specific config globally
app.provide('$api', instance)
app.mount('#app')
