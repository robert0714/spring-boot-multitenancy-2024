import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// const hostname = window.location.hostname;
// console.log('hostname:', hostname);

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  // base: '/ocarbon14067/',
  server: {
    host: '0.0.0.0',
    port: '4000' ,
    open: true,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/api/aggregate/auth/v1': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api^\/aggregate^\/auth^\/v1/, ''),
      },      
       '/instrumentsEdge': {
        // target: `http://${hostname}`,
        target: 'http://localhost',
        // target: 'http://beans.rock',
        secure: false,
        ws: true,
        changeOrigin: true,  
      },
      '/instruments': {
        target: 'http://localhost:8181',
        // target: 'http://beans.rock',
        changeOrigin: true, 
      },
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
