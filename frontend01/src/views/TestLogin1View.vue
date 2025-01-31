<template>
    <div class="flex flex-col gap-4 w-96 m-auto">
        <div class="flex flex-col gap-2">
            <label for="username">使用者名稱</label>
            <input id="username" v-model="form.username" /> robert /isabelle / bjorn
        </div>
        <div class="flex flex-col gap-2">
            <label for="password">密碼</label>
            <input id="password" v-model="form.password" type="password" />
        </div>
        <div class="flex justify-end">
            <button @click="handleLogin">
                <span class="material-symbols-outlined">
                    login
                </span>
                <span>登入</span>
            </button>
        </div>
    </div>
    <div class="flex flex-col gap-4 w-96 m-auto">
     登入狀況:<p id="p1"></p>
     <p id="p2">配套1:使用oath2 password flow</p>
    </div>
   
</template>


<script setup>
import { ref, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getTenantFromSubdomain } from '@/utils/tenant'
const router = useRouter()
const route = useRoute();
const $api = inject('$api');
const userStore = useUserStore()

function getOauthToeknUrl() {
    // 依照subdomain取找正確的oauth2 server
    const tenantId = getTenantFromSubdomain();
    const host = window.location.origin;
    if(!tenantId || 'localhost' === tenantId){
        // 如果沒有抓到tenantId，則使用預設oauth2 server
        const url = host+`/auth/realms/default/protocol/openid-connect/token`;
        // const url = `http://localhost:8001/auth/realms/default/protocol/openid-connect/token`;   
        return url ;
    }
    return host+`/auth/realms/${tenantId}/protocol/openid-connect/token`;
}

const initForm = ref({ 
    grant_type: 'password',
    username: '',
    password: 'password',
    client_id: 'edge-service',
    client_secret: 'rocking-secret'
});
const form = ref({ ...initForm.value });


const loginUrl = import.meta.env.VITE_BASE_LOGIN_URL; 
console.log('loginUrl:', loginUrl);

// 在 Tab 中創建一個 BroadcastChannel
const channel = new BroadcastChannel('user-channel');

// Step 1: 在新 Tab 中發送請求，向已存在的 Tab 請求 JWT
if (!userStore.user.token) {
    // 如果 sessionStorage 中沒有 JWT，請求其他 Tab 傳送 JWT
    channel.postMessage({ type: 'request-jwt' });
}

// Step 2: 當已存在的 Tab 收到請求時，傳送 JWT
channel.onmessage = (event) => {
    if (event.data.type === 'request-jwt') {
        // 傳送 JWT 給新的 Tab
        //const jwt = userStore.user.token ;
        const user =  JSON.stringify (userStore.user ) ;        
        if (user) { 
            channel.postMessage({ type: 'response-jwt', user });
        }
    } else if (event.data.type === 'response-jwt') {
        // Step 3: 當新的 Tab 收到 JWT 時，儲存在 sessionStorage 中
        //sessionStorage.setItem('jwt', event.data.jwt);
        console.log("----------------------");
        console.log(event.data);
        const user = event.data.user ; 
        if(user){
           userStore.user= JSON.parse(event.data.user);
        }
    }
 };

const handleLogin = async () => {    
    const request = {
        ...form.value
    }
    const formData = new URLSearchParams();
    for (const key in request){  
       let value=request[key] ;
       formData.append(key, value);
    }
     
    const url =  getOauthToeknUrl(); 
    await $api.post(url, formData, {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded" 
        }
    }).then((response) => {
        console.log(response);
        userStore.user.token = response.data.access_token;
        userStore.user.name = initForm.value.username ;
        //router.push('/');
        const tenantId = getTenantFromSubdomain();
        if(!tenantId || 'localhost' === tenantId){
           // 如果沒有抓到tenantId，則使用預設oauth2 server
            //userStore.user.tenantId= '' ;
        }else{
            userStore.user.tenantId= tenantId ;
        }
        document.getElementById("p1").innerHTML = userStore.user.name + ' Login Success!';
        const serialization =  JSON.stringify (userStore.user ) ;
        channel.postMessage({ user: serialization });
        
    }).catch((error) => {
        console.log(error);
        document.getElementById("p1").innerHTML = initForm.value.username + " Login Failre!";
    });
};

</script>
<style lang="scss" scoped></style>