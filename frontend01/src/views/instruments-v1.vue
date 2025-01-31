<template>
  <div>
    <h1>Musical Instruments</h1>
    <button @click="fetchInstruments">Fetch Instruments</button>
    <p v-if="loading">Loading...</p>
    <table v-else>
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Type</th>
        </tr>
      </thead>
      <tbody >
        <tr v-for="instrument in instruments" :key="instrument.id">
          <td style="border: 1px solid;">{{ instrument.id }}</td>
          <td style="border: 1px solid;">{{ instrument.name }}</td>
          <td style="border: 1px solid;">{{ instrument.type }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';  
import { getInstruments } from '@/service/instruments';
 
    const instruments = ref([]) ;
    const loading = ref(false)  ;

    const fetchInstruments =  () => {
      getInstruments()
      .then((res) => {
           console.log(res);
           instruments.value = res;
      })
    }

    onMounted(() => {
      fetchInstruments();
    });
</script>
 

