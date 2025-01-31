<template>
  <div class="ocr-interface">
    <input type="file" @change="handleFileUpload" accept="image/*" />
    <button @click="cropImage" :disabled="!selection">執行OCR</button>
    <div v-if="recognizedText" class="result">
      <h3>前端預覽辨識結果：</h3>
      <!--<p>{{ recognizedText }}</p> -->
      <li v-for="(item, key, index) in recognizedText.split('\n')">
         {{ item }}  
     </li>
      
    </div>
    <div v-if="imageUrl" class="image-container">
      <img :src="imageUrl" ref="image" @mousedown="startSelection" @mousemove="updateSelection" @mouseup="endSelection" />
      <div v-if="selection" class="selection" :style="selectionStyle"></div>
    </div>
    
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import Tesseract from 'tesseract.js';

export default {
  setup() {
    const imageUrl = ref(null);
    const image = ref(null);
    const selection = ref(null);
    const isSelecting = ref(false);
    const recognizedText = ref('');

    const handleFileUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        imageUrl.value = URL.createObjectURL(file);
        recognizedText.value = ''; // 清除先前的辨識結果
      }
    };

    const startSelection = (event) => {
      isSelecting.value = true;
      selection.value = {
        startX: event.offsetX,
        startY: event.offsetY,
        endX: event.offsetX,
        endY: event.offsetY
      };
    };

    const updateSelection = (event) => {
      if (isSelecting.value) {
        selection.value.endX = event.offsetX;
        selection.value.endY = event.offsetY;
      }
    };

    const endSelection = () => {
      isSelecting.value = false;
    };

    const selectionStyle = computed(() => {
      if (!selection.value) return {};
      const { startX, startY, endX, endY } = selection.value;
      return {
        left: `${Math.min(startX, endX)}px`,
        top: `${Math.min(startY, endY)}px`,
        width: `${Math.abs(endX - startX)}px`,
        height: `${Math.abs(endY - startY)}px`
      };
    });

    const cropImage = async() => {
      if (!selection.value || !image.value) return;

      recognizedText.value = ''; // 清除先前的辨識結果
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const { startX, startY, endX, endY } = selection.value;

      const width = Math.abs(endX - startX);
      const height = Math.abs(endY - startY);
      canvas.width = width;
      canvas.height = height;

      ctx.drawImage(
        image.value,
        Math.min(startX, endX),
        Math.min(startY, endY),
        width,
        height,
        0,
        0,
        width,
        height
      );

      const croppedImageBase64 = canvas.toDataURL('image/jpeg');
      console.log('Cropped image (base64):', croppedImageBase64);

      // Here you would typically send the cropped image to your backend for OCR processing
      // For example:
      // sendToBackendForOCR(croppedImageBase64);
      try {
        const result = await Tesseract.recognize(croppedImageBase64, 'chi_tra', {
          logger: m => console.log(m),
          tessedit_pageseg_mode: Tesseract.PSM.SINGLE_BLOCK,
          tessedit_ocr_engine_mode: Tesseract.OEM.LSTM_ONLY,
          preserve_interword_spaces: '1',
          tessedit_char_whitelist: '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,!?@#$%^&*()-_+=[]{}|:;<>',
        });
        recognizedText.value = result.data.text;
        console.log("preview ocr: " ,result.data.text);
      } catch (error) {
        console.error('OCR error:', error);
        recognizedText.value = '辨識失敗，請重試。';
      } 
    };

    return {
      imageUrl,
      image,
      selection,
      recognizedText,
      handleFileUpload,
      startSelection,
      updateSelection,
      endSelection,
      selectionStyle,
      cropImage
    };
  }
};
</script>

<style scoped>
.ocr-interface {
  max-width: 800px;
  margin: 0 auto;
}

.image-container {
  position: relative;
  margin-top: 20px;
}

.image-container img {
  max-width: 100%;
  height: auto;
}

.selection {
  position: absolute;
  border: 2px solid red;
  pointer-events: none;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
.result {
  margin-top: 20px;
  border: 1px solid #ccc;
  padding: 10px;
  background-color: #f9f9f9;
}

.loading {
  margin-top: 10px;
  font-style: italic;
  color: #666;
}
</style>