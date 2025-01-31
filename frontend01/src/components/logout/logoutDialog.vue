<template>
    <Dialog v-model:visible="generalStore.logoutVisible" :closable="false">
        <template #header>
            <div class="mx-auto font-bold">
                因您已閒置一段時間，已將您登出
            </div>
        </template>
        <div>因您已閒置一段時間，已將您登出，如需繼續執行，請重新登入</div>
        <template #footer>
            <Button @click="generalStore.logoutVisible = false">
                <span class="material-symbols-outlined">
                    check
                </span>
                確認
            </Button>
        </template>
    </Dialog>
</template>




<script setup>
import Dialog from 'primevue/dialog';
import { ref, watch,computed,onMounted } from 'vue'
import { useGeneralStore } from '@/stores/general'
const generalStore = useGeneralStore()
// const emit = defineEmits(['update:visible']);
// const props = defineProps({
//     visible: {
//         type: Boolean,
//         required: true,
//     }
// })


</script>




<style lang="scss" scoped></style>