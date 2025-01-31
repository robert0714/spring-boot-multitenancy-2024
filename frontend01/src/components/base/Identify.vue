<template>
    <div class="s-canvas">
        <canvas id="s-canvas" :width="props.contentWidth" :height="props.contentHeight"></canvas>
    </div>
</template>




<script setup>
import { onMounted, watch } from 'vue'

// eslint-disable-next-line no-undef
const props = defineProps({
    identifyCode: {
        type: String,
        default: '1234'
    },
    fontSizeMin: {
        type: Number,
        default: 25
    },
    fontSizeMax: {
        type: Number,
        default: 35
    },
    backgroundColorMin: {
        type: Number,
        default: 255
    },
    backgroundColorMax: {
        type: Number,
        default: 255
    },
    colorMin: {
        type: Number,
        default: 0
    },
    colorMax: {
        type: Number,
        default: 160
    },
    lineColorMin: {
        type: Number,
        default: 40
    },
    lineColorMax: {
        type: Number,
        default: 180
    },
    dotColorMin: {
        type: Number,
        default: 0
    },
    dotColorMax: {
        type: Number,
        default: 255
    },
    contentWidth: {
        type: Number,
        default: 112
    },
    contentHeight: {
        type: Number,
        default: 40
    }
})
// 生成一個隨機數
const randomNum = (min, max) => {
    return Math.floor(Math.random() * (max - min) + min)
}

// 生成一個隨機的顏色
const randomColor = (min, max) => {
    let r = randomNum(min, max)
    let g = randomNum(min, max)
    let b = randomNum(min, max)
    return 'rgb(' + r + ',' + g + ',' + b + ')'
}

// 繪製干擾線
const drawLine = (ctx) => {
    for (let i = 0; i < 5; i++) {
        ctx.strokeStyle = randomColor(props.lineColorMin, props.lineColorMax)
        ctx.beginPath()
        ctx.moveTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
        ctx.lineTo(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight))
        ctx.stroke()
    }
}
//在畫布上顯示資料
const drawText = (ctx, txt, i) => {
    ctx.fillStyle = randomColor(props.colorMin, props.colorMax)
    ctx.font = randomNum(props.fontSizeMin, props.fontSizeMax) + 'px SimHei'
    let x = (i + 1) * (props.contentWidth / (props.identifyCode.length + 1))
    let y = randomNum(props.fontSizeMax, props.contentHeight - 5)
    var deg = randomNum(-45, 45)
    // 修改坐標原點和旋轉角度
    ctx.translate(x, y)
    ctx.rotate((deg * Math.PI) / 180)
    ctx.fillText(txt, 0, 0)
    // 恢復坐標原點和旋轉角度
    ctx.rotate((-deg * Math.PI) / 180)
    ctx.translate(-x, -y)
}
// 繪製干擾點
const drawDot = (ctx) => {
    for (let i = 0; i < 80; i++) {
        ctx.fillStyle = randomColor(0, 255)
        ctx.beginPath()
        ctx.arc(randomNum(0, props.contentWidth), randomNum(0, props.contentHeight), 1, 0, 2 * Math.PI)
        ctx.fill()
    }
}
//畫圖
const drawPic = () => {
    let canvas = document.getElementById('s-canvas')
    let ctx = canvas.getContext('2d')
    ctx.textBaseline = 'bottom'
    // 繪製背景
    ctx.fillStyle = randomColor(props.backgroundColorMin, props.backgroundColorMax)
    ctx.fillRect(0, 0, props.contentWidth, props.contentHeight)
    // 繪製文字
    for (let i = 0; i < props.identifyCode.length; i++) {
        drawText(ctx, props.identifyCode[i], i)
    }
    drawLine(ctx)
    drawDot(ctx)
}
//元件掛載
onMounted(() => {
    drawPic()
})
// 監聽
watch(
    () => props.identifyCode,
    () => {
        drawPic()
    }
);

</script>




<style lang="scss" scoped>
.s-canvas {
    cursor: pointer;
}
</style>