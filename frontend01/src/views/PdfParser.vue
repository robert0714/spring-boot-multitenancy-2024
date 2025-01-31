<template>
  <div>
    <!-- PDF上傳 -->
    <input type="file" @change="onFileChange" accept="application/pdf" />
    
    <!-- PDF預覽 -->
    <div ref="pdfContainer" class="pdf-container" @mousedown="startSelection" @mouseup="endSelection" @mousemove="moveSelection">
      <canvas ref="pdfCanvas"></canvas>
      <!-- 圈選範圍框框 -->
      <div v-if="selection.width && selection.height" 
           class="selection-box"
           :style="selectionStyle"></div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import * as pdfjsLib from 'pdfjs-dist';
import * as pdfWorker from "pdfjs-dist/build/pdf.worker.mjs"; // 引入PDF.js的worker
 
const convertBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const fileReader = new FileReader();
    fileReader.readAsDataURL(file);

    fileReader.onload = () => {
      resolve(fileReader.result);
    };

    fileReader.onerror = (error) => {
      reject(error);
    };
  });
};
// 將 Base64 字符串轉換為 Uint8Array
 function base64ToArrayBuffer(base64) {
  const binaryString = decodeBase64(base64);
  const len = binaryString.length;
  const bytes = new Uint8Array(len);
  for (let i = 0; i < len; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes.buffer;
}

// 手動解碼 Base64 字符串
function decodeBase64(base64) {
  const base64Chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  let str = '';
  let bytes = parseInt((base64.length / 4) * 3, 10);
  
  for (let i = 0; i < base64.length; i += 4) {
    const enc1 = base64Chars.indexOf(base64[i]);
    const enc2 = base64Chars.indexOf(base64[i + 1]);
    const enc3 = base64Chars.indexOf(base64[i + 2]);
    const enc4 = base64Chars.indexOf(base64[i + 3]);

    const chr1 = (enc1 << 2) | (enc2 >> 4);
    const chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
    const chr3 = ((enc3 & 3) << 6) | enc4;

    str += String.fromCharCode(chr1);

    if (enc3 !== 64) {
      str += String.fromCharCode(chr2);
    }
    if (enc4 !== 64) {
      str += String.fromCharCode(chr3);
    }
  }

  return str;
}
export default {
  
  data() {
    return {
      pdfDoc: null,
      pageNum: 1,
      scale: 1, // PDF的縮放比例
      selection: { startX: 0, startY: 0, endX: 0, endY: 0, width: 0, height: 0 },
      isSelecting: false,
      selectedText: ''
    };
  },
  computed: {
    selectionStyle() {
      return {
        top: `${this.selection.startY}px`,
        left: `${this.selection.startX}px`,
        width: `${this.selection.width}px`,
        height: `${this.selection.height}px`,
        border: '2px solid red',
        position: 'absolute'
      };
    }
  },
  methods: {
    // 處理PDF上傳
    onFileChange(event) {
      const file = event.target.files[0];
      if (file && file.type === 'application/pdf') {
        const reader = new FileReader();
        reader.onload = async  (e) => {
          const pdfData = new Uint8Array(e.target.result);
          this.loadPDF(pdfData);
          const base64 = await convertBase64(file); 
          localStorage.setItem('file',base64);          
        };
        reader.readAsArrayBuffer(file);
        
      }
    },

    // 加載PDF並渲染第一頁
    async loadPDF(pdfData) {
      const pdfDoc = await pdfjsLib.getDocument(pdfData ).promise;            
      const page = await pdfDoc.getPage(this.pageNum);
      
      this.renderPage(page);
    },

    // 渲染PDF的某一頁
    
    async renderPage(page) { 
      const viewport = page.getViewport({ scale: this.scale });

      const canvas = this.$refs.pdfCanvas;
      const ctx = canvas.getContext('2d');
      canvas.height = viewport.height;
      canvas.width = viewport.width;

      const renderContext = {
        canvasContext: ctx,
        viewport: viewport
      };

      page.render(renderContext);
    },

    // 開始圈選
    startSelection(event) {
      this.isSelecting = true;
      this.selection.startX = event.offsetX;
      this.selection.startY = event.offsetY;
    },

    // 結束圈選
    endSelection(event) {
      this.isSelecting = false;
      this.selection.endX = event.offsetX;
      this.selection.endY = event.offsetY;
      this.selection.width = Math.abs(this.selection.endX - this.selection.startX);
      this.selection.height = Math.abs(this.selection.endY - this.selection.startY);

      // 提取選取範圍內的文字
      this.extractSelectedText();
    },

    // 在移動時更新選擇範圍
    moveSelection(event) {
      if (this.isSelecting) {
        this.selection.endX = event.offsetX;
        this.selection.endY = event.offsetY;
        this.selection.width = Math.abs(this.selection.endX - this.selection.startX);
        this.selection.height = Math.abs(this.selection.endY - this.selection.startY);
      }
    },

    // 從圈選範圍內提取文字
    async extractSelectedText() {
      const base64Pdf = localStorage.getItem('file');
      // 將 Base64 字符串轉換為 Uint8Array
      const pdfData = base64ToArrayBuffer(base64Pdf);

      const pdfDoc = await pdfjsLib.getDocument(pdfData ).promise;   
      const page = await pdfDoc.getPage(this.pageNum);
      const textContent = await page.getTextContent();
      const canvas = this.$refs.pdfCanvas;
      const scaleFactor = canvas.width / canvas.offsetWidth;

      let selectedText = '';
      textContent.items.forEach((item) => {
        const tx = item.transform;
        const [x, y] = [tx[4], tx[5]];
        const [width, height] = [item.width, item.height];

        const inSelection = 
          x * scaleFactor >= this.selection.startX &&
          y * scaleFactor <= this.selection.startY + this.selection.height &&
          x * scaleFactor + width <= this.selection.startX + this.selection.width &&
          y * scaleFactor - height >= this.selection.startY;

        if (inSelection) {
          selectedText += item.str + ' ';
        }
      });

      this.selectedText = selectedText.trim();
      console.log('選取的文字: ', this.selectedText);
    }
  }
};
</script>

<style scoped>
.pdf-container {
  position: relative;
  display: inline-block;
  border: 1px solid #ccc;
}

.selection-box {
  position: absolute;
  pointer-events: none;
}
</style>