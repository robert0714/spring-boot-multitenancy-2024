<template>
    <Toast ref="toastRef" position="bottom-left" :pt="{ container: 'text-black bg-white border-0 shadow-lg' }"
        @life-end="(data) => handleDelete(data)">
        <template #container="{ message, closeCallback }">
            <div class="w-96 h-16 p-2 flex items-center ">
                <div class="w-16 h-full flex items-center place-content-center shrink-0">
                    <span v-if="message.severity === 'success'"
                        class="material-symbols-outlined text-[var(--third-key)]">
                        check_circle
                    </span>
                    <span v-else class="material-symbols-outlined text-[var(--warning-key)]">
                        cancel
                    </span>
                </div>

                <span class="line-clamp-2">
                    {{ message.detail }}
                </span>

                <div class="grow" />

                <div v-if="message.type === 'delete'" class="flex items-center shrink-0">
                    <Divider layout="vertical" />
                    <Button label="復原" link @click="handleRecovery(message,closeCallback)" />
                </div>

            </div>
        </template>
    </Toast>
</template>




<script setup>
import Toast from 'primevue/toast';
import { useCounterStore } from '@/stores/counter'
import { useGeneralStore } from '@/stores/general'

const counterStore = useCounterStore()
const generalStore = useGeneralStore()


// 刪除
const handleDelete = async (data) => {
    if (data.message.type !== 'delete') return
    await data.message.function(data.message.id)
    // 刷新table，這邊是用counterStore來刷新，table部分要根據自己的需求綁定counterStore.count來刷新
    counterStore.increment()
    await new Promise((resolve) => setTimeout(resolve, 1000))
    generalStore.deleteItems = generalStore.deleteItems.filter((id) => id !== data.message.id)
};

// 復原
const handleRecovery = async (message,closeCallback) => {
    closeCallback()
    generalStore.deleteItems = generalStore.deleteItems.filter((id) => id !== message.id)
    counterStore.increment()
};


</script>




<style lang="scss" scoped></style>