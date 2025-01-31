<template>
    <div class="flex flex-col gap-4 w-96 m-auto">
        <div class="flex flex-col gap-2">
            <label for="userName">使用者名稱</label>
            <input id="userName" v-model="form.userName" /> robert /isabelle / bjorn
        </div>
        <div class="flex flex-col gap-2">
            <label for="passwd">密碼</label>
            <input id="passwd" v-model="form.passwd" type="password" />
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
     <p id="p2">配套2:使用proxy service-login api</p>
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


const customTenantIdHeader = 'X-Tenant-Id';
let  headerValuse = {
      "Content-Type": "application/json; charset=utf-8",
      Accept: "application/json" 
};

function getOauthToeknUrl() {
    // 依照subdomain取找正確的oauth2 server
    const tenantId = getTenantFromSubdomain();
    if(!tenantId || 'localhost' === tenantId){
        // 如果沒有抓到tenantId，則使用預設oauth2 server
        //headerValuse[customTenantIdHeader]= '' ;
    }else{
        headerValuse[customTenantIdHeader]= tenantId ;
    }
    const host = window.location.origin;     
    return host+`/api/aggregate/auth/v1/auth/verify`;   
}

const initForm = ref({  
    userName: '',
    passwd: 'password'  
});
const form = ref({ ...initForm.value });


const loginUrl = import.meta.env.VITE_BASE_LOGIN_URL; 
console.log('loginUrl:', loginUrl);
const handleLogin = async () => {    
    const request = {
        ...form.value
    }
    const url =  getOauthToeknUrl(); 
    console.log("headerValuse: " ,headerValuse);
    await $api.post(url, request , {
        headers: headerValuse
    }).then((response) => {
        console.log(response);
        userStore.user.token = response.data.access_token;
        userStore.user.name = initForm.value.userName ;
        //router.push('/');
        const tenantId = getTenantFromSubdomain();
        if(!tenantId || 'localhost' === tenantId){
           // 如果沒有抓到tenantId，則使用預設oauth2 server
           userStore.user.tenantId= '' ;
        }else{
            userStore.user.tenantId= tenantId ;
        }
        document.getElementById("p1").innerHTML = userStore.user.name + ' Login Success!';
    }).catch((error) => {
        console.log(error);
        document.getElementById("p1").innerHTML = initForm.value.userName + " Login Failre!";
    });
};

</script>
<style lang="scss" scoped></style>