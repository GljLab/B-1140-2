<template>
  <div class="h-full flex flex-col">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-3">
        <button @click="$emit('back')" class="inline-flex items-center space-x-1 text-sm text-gray-600 hover:text-blue-600 transition">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
          <span>返回</span>
        </button>
        <h2 class="text-2xl font-bold text-gray-800">拼图编辑器</h2>
      </div>
      <div class="flex items-center space-x-2">
        <button @click="saveCollage" :disabled="saving" class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg v-if="saving" class="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
          <span>{{ saving ? '保存中...' : '保存拼图' }}</span>
        </button>
        <button @click="generateAndExport" :disabled="exporting" class="px-5 py-2 bg-gradient-to-r from-emerald-500 to-teal-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg v-if="exporting" class="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
          <span>{{ exporting ? '导出中...' : '导出图片' }}</span>
        </button>
      </div>
    </div>

    <div class="flex-1 flex gap-4 min-h-0">
      <div class="w-72 flex-shrink-0 space-y-3 overflow-y-auto pr-1">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">选择模板</h3>
          <div class="grid grid-cols-3 gap-2">
            <button v-for="t in templateTypes" :key="t.value" @click="applyTemplate(t.value)"
              :class="['p-2 rounded-xl border-2 text-center transition text-xs',
                collage.templateType === t.value ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-gray-200 text-gray-600 hover:border-blue-300']">
              <div class="text-lg mb-0.5">{{ t.icon }}</div>
              <div>{{ t.label }}</div>
            </button>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">尺寸预设</h3>
          <div class="grid grid-cols-2 gap-2">
            <button v-for="sp in sizePresets" :key="sp.value" @click="applySizePreset(sp)"
              :class="['px-2 py-1.5 rounded-lg text-xs font-medium transition border',
                collage.sizePreset === sp.value ? 'bg-blue-500 text-white border-blue-500' : 'bg-white text-gray-600 border-gray-200 hover:border-blue-300']">
              {{ sp.label }}
            </button>
          </div>
          <div class="mt-3 grid grid-cols-2 gap-2">
            <div>
              <label class="text-xs text-gray-500">宽度</label>
              <input v-model.number="collage.canvasWidth" type="number" min="100" max="4096"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500">高度</label>
              <input v-model.number="collage.canvasHeight" type="number" min="100" max="4096"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">样式设置</h3>
          <div class="space-y-3">
            <div>
              <label class="text-xs text-gray-500 mb-1 block">背景颜色</label>
              <div class="flex items-center space-x-2">
                <input type="color" v-model="collage.backgroundColor" class="w-8 h-8 rounded border border-gray-200 cursor-pointer" />
                <input v-model="collage.backgroundColor" type="text" class="flex-1 px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
              </div>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">图片间距: {{ collage.gap }}px</label>
              <input v-model.number="collage.gap" type="range" min="0" max="50" class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">圆角: {{ collage.borderRadius }}px</label>
              <input v-model.number="collage.borderRadius" type="range" min="0" max="50" class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">边框宽度: {{ collage.borderWidth }}px</label>
              <input v-model.number="collage.borderWidth" type="range" min="0" max="10" class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
            <div v-if="collage.borderWidth > 0">
              <label class="text-xs text-gray-500 mb-1 block">边框颜色</label>
              <div class="flex items-center space-x-2">
                <input type="color" v-model="collage.borderColor" class="w-8 h-8 rounded border border-gray-200 cursor-pointer" />
                <input v-model="collage.borderColor" type="text" class="flex-1 px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
              </div>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">导出质量</label>
              <div class="flex space-x-1">
                <button v-for="q in qualityOptions" :key="q.value" @click="collage.quality = q.value"
                  :class="['flex-1 px-2 py-1 rounded text-xs font-medium transition',
                    collage.quality === q.value ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
                  {{ q.label }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">添加文字</h3>
          <div class="space-y-2">
            <input v-model="newText.content" type="text" placeholder="输入文字内容"
              class="w-full px-2 py-1.5 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="text-xs text-gray-500">字号</label>
                <input v-model.number="newText.fontSize" type="number" min="8" max="120"
                  class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
              </div>
              <div>
                <label class="text-xs text-gray-500">颜色</label>
                <input type="color" v-model="newText.color" class="w-full h-7 rounded border border-gray-200 cursor-pointer" />
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <label class="flex items-center space-x-1 cursor-pointer text-xs text-gray-600">
                <input type="checkbox" v-model="newText.bold" class="w-3 h-3" />
                <span>粗体</span>
              </label>
              <label class="flex items-center space-x-1 cursor-pointer text-xs text-gray-600">
                <input type="checkbox" v-model="newText.italic" class="w-3 h-3" />
                <span>斜体</span>
              </label>
            </div>
            <button @click="addText" :disabled="!newText.content.trim()"
              :class="['w-full px-3 py-1.5 rounded-lg text-xs font-medium transition',
                newText.content.trim() ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg' : 'bg-gray-100 text-gray-400 cursor-not-allowed']">
              添加文字
            </button>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">添加贴纸</h3>
          <div class="grid grid-cols-5 gap-1.5">
            <button v-for="sticker in stickerOptions" :key="sticker" @click="addSticker(sticker)"
              class="text-2xl p-1 rounded-lg hover:bg-gray-100 transition">
              {{ sticker }}
            </button>
          </div>
        </div>
      </div>

      <div class="flex-1 flex items-center justify-center bg-gray-100 rounded-2xl overflow-hidden relative" ref="canvasContainer">
        <div class="relative bg-white shadow-2xl" ref="canvasWrapper"
          :style="canvasWrapperStyle">
          <div class="absolute inset-0" :style="{ backgroundColor: collage.backgroundColor, borderRadius: '8px' }"></div>

          <div v-for="(item, idx) in collageItems" :key="'item-' + idx"
            :class="['absolute cursor-move overflow-hidden group',
              selectedItemIndex === idx && selectedType === 'item' ? 'ring-2 ring-blue-500 ring-offset-1' : '']"
            :style="getItemStyle(item)"
            @mousedown.stop="startDrag($event, idx, 'item')"
            @click.stop="selectItem(idx, 'item')">
            <img v-if="item.url" :src="item.url" class="w-full h-full object-cover" draggable="false"
              :style="{ borderRadius: collage.borderRadius + 'px' }" />
            <div v-if="collage.borderWidth > 0" class="absolute inset-0 pointer-events-none"
              :style="{ border: collage.borderWidth + 'px solid ' + collage.borderColor, borderRadius: collage.borderRadius + 'px' }"></div>
            <div v-if="selectedItemIndex === idx && selectedType === 'item'" class="absolute -top-1 -right-1 flex space-x-0.5 opacity-0 group-hover:opacity-100 transition">
              <button @click.stop="removeItem(idx)" class="w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center shadow hover:bg-red-600">×</button>
            </div>
          </div>

          <div v-for="(text, idx) in collageTexts" :key="'text-' + idx"
            :class="['absolute cursor-move select-none',
              selectedItemIndex === idx && selectedType === 'text' ? 'ring-2 ring-blue-500 ring-offset-1 ring-offset-transparent' : '']"
            :style="getTextStyle(text)"
            @mousedown.stop="startDrag($event, idx, 'text')"
            @click.stop="selectItem(idx, 'text')">
            {{ text.content }}
            <div v-if="selectedItemIndex === idx && selectedType === 'text'" class="absolute -top-5 right-0 flex space-x-0.5">
              <button @click.stop="removeText(idx)" class="w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center shadow hover:bg-red-600">×</button>
            </div>
          </div>

          <div v-for="(sticker, idx) in collageStickers" :key="'sticker-' + idx"
            :class="['absolute cursor-move select-none',
              selectedItemIndex === idx && selectedType === 'sticker' ? 'ring-2 ring-blue-500 ring-offset-1 ring-offset-transparent' : '']"
            :style="getStickerStyle(sticker)"
            @mousedown.stop="startDrag($event, idx, 'sticker')"
            @click.stop="selectItem(idx, 'sticker')">
            {{ sticker.emoji }}
            <div v-if="selectedItemIndex === idx && selectedType === 'sticker'" class="absolute -top-5 right-0 flex space-x-0.5">
              <button @click.stop="removeSticker(idx)" class="w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center shadow hover:bg-red-600">×</button>
            </div>
          </div>
        </div>
      </div>

      <div class="w-60 flex-shrink-0 space-y-3 overflow-y-auto pl-1">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">已选图片 ({{ pictures.length }})</h3>
          <div v-if="pictures.length === 0" class="text-center text-gray-400 text-xs py-4">
            暂无图片，请从图片列表选择
          </div>
          <div class="space-y-2 max-h-60 overflow-y-auto">
            <div v-for="(pic, idx) in pictures" :key="pic.id"
              class="flex items-center space-x-2 p-1.5 rounded-lg hover:bg-gray-50 transition group">
              <img :src="pic.url" class="w-10 h-10 rounded object-cover flex-shrink-0" />
              <span class="text-xs text-gray-700 truncate flex-1">{{ pic.name }}</span>
              <button @click="removePicture(idx)" class="text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
              </button>
            </div>
          </div>
          <button @click="$emit('add-pictures')" class="w-full mt-2 px-3 py-1.5 border-2 border-dashed border-gray-300 rounded-lg text-xs text-gray-500 hover:border-blue-400 hover:text-blue-600 transition">
            + 添加更多图片
          </button>
        </div>

        <div v-if="selectedType === 'item' && selectedItemIndex !== null && collageItems[selectedItemIndex]" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">调整图片</h3>
          <div class="space-y-2">
            <div>
              <label class="text-xs text-gray-500 mb-1 block">X 位置</label>
              <input v-model.number="collageItems[selectedItemIndex].x" type="number" min="0"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">Y 位置</label>
              <input v-model.number="collageItems[selectedItemIndex].y" type="number" min="0"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">宽度</label>
              <input v-model.number="collageItems[selectedItemIndex].width" type="number" min="10"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">高度</label>
              <input v-model.number="collageItems[selectedItemIndex].height" type="number" min="10"
                class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">缩放: {{ Math.round((collageItems[selectedItemIndex].scale || 1) * 100) }}%</label>
              <input v-model.number="collageItems[selectedItemIndex].scale" type="range" min="0.1" max="3" step="0.05"
                class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
          </div>
        </div>

        <div v-if="selectedType === 'text' && selectedItemIndex !== null && collageTexts[selectedItemIndex]" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">调整文字</h3>
          <div class="space-y-2">
            <input v-model="collageTexts[selectedItemIndex].content" type="text"
              class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
            <div class="grid grid-cols-2 gap-2">
              <div>
                <label class="text-xs text-gray-500">字号</label>
                <input v-model.number="collageTexts[selectedItemIndex].fontSize" type="number" min="8" max="120"
                  class="w-full px-2 py-1 border border-gray-200 rounded text-sm focus:outline-none focus:ring-1 focus:ring-blue-500" />
              </div>
              <div>
                <label class="text-xs text-gray-500">颜色</label>
                <input type="color" v-model="collageTexts[selectedItemIndex].color"
                  class="w-full h-7 rounded border border-gray-200 cursor-pointer" />
              </div>
            </div>
            <div class="flex items-center space-x-2">
              <label class="flex items-center space-x-1 cursor-pointer text-xs text-gray-600">
                <input type="checkbox" v-model="collageTexts[selectedItemIndex].bold" class="w-3 h-3" />
                <span>粗体</span>
              </label>
              <label class="flex items-center space-x-1 cursor-pointer text-xs text-gray-600">
                <input type="checkbox" v-model="collageTexts[selectedItemIndex].italic" class="w-3 h-3" />
                <span>斜体</span>
              </label>
            </div>
          </div>
        </div>

        <div v-if="selectedType === 'sticker' && selectedItemIndex !== null && collageStickers[selectedItemIndex]" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">调整贴纸</h3>
          <div class="space-y-2">
            <div>
              <label class="text-xs text-gray-500 mb-1 block">大小: {{ collageStickers[selectedItemIndex].size }}px</label>
              <input v-model.number="collageStickers[selectedItemIndex].size" type="range" min="16" max="200"
                class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">旋转: {{ collageStickers[selectedItemIndex].rotation }}°</label>
              <input v-model.number="collageStickers[selectedItemIndex].rotation" type="range" min="0" max="360"
                class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-blue-500" />
            </div>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
          <h3 class="text-sm font-semibold text-gray-700 mb-3">我的拼图</h3>
          <div v-if="savedCollages.length === 0" class="text-center text-gray-400 text-xs py-4">
            暂无保存的拼图
          </div>
          <div class="space-y-2 max-h-60 overflow-y-auto">
            <div v-for="c in savedCollages" :key="c.id"
              class="flex items-center space-x-2 p-1.5 rounded-lg hover:bg-gray-50 transition cursor-pointer group"
              @click="loadCollage(c)">
              <div class="w-12 h-12 bg-gray-100 rounded-lg flex-shrink-0 flex items-center justify-center text-2xl overflow-hidden">
                <img v-if="c.url" :src="c.url" class="w-full h-full object-cover" />
                <span v-else>🖼️</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-xs font-medium text-gray-700 truncate">{{ c.name }}</p>
                <p class="text-[10px] text-gray-400">{{ c.templateType }} · {{ c.canvasWidth }}×{{ c.canvasHeight }}</p>
              </div>
              <button @click.stop="deleteCollage(c.id)" class="text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const emit = defineEmits(['back', 'add-pictures', 'toast'])
const props = defineProps({
  initialPictures: { type: Array, default: () => [] }
})

const templateTypes = [
  { value: 'grid2', label: '2宫格', icon: '⊞' },
  { value: 'grid4', label: '4宫格', icon: '⊞' },
  { value: 'grid6', label: '6宫格', icon: '⊟' },
  { value: 'grid9', label: '9宫格', icon: '⊡' },
  { value: 'vertical', label: '竖长图', icon: '▤' },
  { value: 'horizontal', label: '横长图', icon: '▥' },
  { value: 'free', label: '自由拼图', icon: '✦' },
  { value: 'poster_top', label: '海报上大图', icon: '▣' },
  { value: 'poster_magazine', label: '杂志风', icon: '▦' }
]

const sizePresets = [
  { value: '1:1', label: '1:1 方图', w: 1080, h: 1080 },
  { value: '9:16', label: '9:16 竖屏', w: 1080, h: 1920 },
  { value: '9:18', label: '9:18 长图', w: 1080, h: 2160 },
  { value: '4:3', label: '4:3 横图', w: 1440, h: 1080 },
  { value: '16:9', label: '16:9 宽屏', w: 1920, h: 1080 },
  { value: '3:4', label: '3:4 竖图', w: 1080, h: 1440 },
  { value: 'A4', label: 'A4 打印', w: 2480, h: 3508 },
  { value: '6inch', label: '6寸照片', w: 1800, h: 1200 },
  { value: 'custom', label: '自定义', w: 1080, h: 1080 }
]

const qualityOptions = [
  { value: 'standard', label: '标准' },
  { value: 'high', label: '高清' },
  { value: 'ultra', label: '超清' }
]

const stickerOptions = ['❤️', '⭐', '🔥', '✨', '🎉', '🌟', '💫', '🌈', '🌸', '🍀', '🎵', '📷', '🎨', '✈️', '🏆', '💎', '🎀', '☀️', '🌙', '💝']

const pictures = ref([...props.initialPictures])
const savedCollages = ref([])
const saving = ref(false)
const exporting = ref(false)
const editingCollageId = ref(null)

const collage = reactive({
  name: '我的拼图',
  templateType: 'grid4',
  canvasWidth: 1080,
  canvasHeight: 1080,
  backgroundColor: '#FFFFFF',
  backgroundImageUrl: '',
  gap: 10,
  borderRadius: 0,
  borderWidth: 0,
  borderColor: '#000000',
  sizePreset: '1:1',
  quality: 'high'
})

const collageItems = ref([])
const collageTexts = ref([])
const collageStickers = ref([])

const newText = reactive({ content: '', fontSize: 32, color: '#000000', bold: false, italic: false })

const selectedItemIndex = ref(null)
const selectedType = ref(null)

const canvasContainer = ref(null)
const canvasWrapper = ref(null)

const dragState = reactive({ active: false, type: null, index: -1, startX: 0, startY: 0, origX: 0, origY: 0 })

const previewScale = computed(() => {
  const maxW = 600
  const maxH = 500
  const scaleW = maxW / collage.canvasWidth
  const scaleH = maxH / collage.canvasHeight
  return Math.min(scaleW, scaleH, 1)
})

const canvasWrapperStyle = computed(() => ({
  width: collage.canvasWidth * previewScale.value + 'px',
  height: collage.canvasHeight * previewScale.value + 'px',
  borderRadius: '8px',
  overflow: 'hidden'
}))

function selectItem(index, type) {
  selectedItemIndex.value = index
  selectedType.value = type
}

function startDrag(event, index, type) {
  event.preventDefault()
  selectedItemIndex.value = index
  selectedType.value = type
  dragState.active = true
  dragState.type = type
  dragState.index = index
  dragState.startX = event.clientX
  dragState.startY = event.clientY
  if (type === 'item') {
    dragState.origX = collageItems.value[index].x
    dragState.origY = collageItems.value[index].y
  } else if (type === 'text') {
    dragState.origX = collageTexts.value[index].x
    dragState.origY = collageTexts.value[index].y
  } else if (type === 'sticker') {
    dragState.origX = collageStickers.value[index].x
    dragState.origY = collageStickers.value[index].y
  }
}

function onMouseMove(event) {
  if (!dragState.active) return
  const dx = (event.clientX - dragState.startX) / previewScale.value
  const dy = (event.clientY - dragState.startY) / previewScale.value
  const newX = Math.max(0, dragState.origX + dx)
  const newY = Math.max(0, dragState.origY + dy)
  if (dragState.type === 'item' && collageItems.value[dragState.index]) {
    collageItems.value[dragState.index].x = Math.round(newX)
    collageItems.value[dragState.index].y = Math.round(newY)
  } else if (dragState.type === 'text' && collageTexts.value[dragState.index]) {
    collageTexts.value[dragState.index].x = Math.round(newX)
    collageTexts.value[dragState.index].y = Math.round(newY)
  } else if (dragState.type === 'sticker' && collageStickers.value[dragState.index]) {
    collageStickers.value[dragState.index].x = Math.round(newX)
    collageStickers.value[dragState.index].y = Math.round(newY)
  }
}

function onMouseUp() {
  dragState.active = false
}

function getItemStyle(item) {
  return {
    left: item.x * previewScale.value + 'px',
    top: item.y * previewScale.value + 'px',
    width: item.width * previewScale.value + 'px',
    height: item.height * previewScale.value + 'px'
  }
}

function getTextStyle(text) {
  const fs = text.fontSize * previewScale.value
  return {
    left: text.x * previewScale.value + 'px',
    top: text.y * previewScale.value + 'px',
    fontSize: fs + 'px',
    color: text.color,
    fontWeight: text.bold ? 'bold' : 'normal',
    fontStyle: text.italic ? 'italic' : 'normal',
    whiteSpace: 'nowrap'
  }
}

function getStickerStyle(sticker) {
  const sz = sticker.size * previewScale.value
  return {
    left: sticker.x * previewScale.value + 'px',
    top: sticker.y * previewScale.value + 'px',
    fontSize: sz + 'px',
    transform: `rotate(${sticker.rotation || 0}deg)`,
    lineHeight: 1
  }
}

function removePicture(index) {
  pictures.value.splice(index, 1)
  applyTemplate(collage.templateType)
}

function removeItem(index) {
  collageItems.value.splice(index, 1)
  selectedItemIndex.value = null
  selectedType.value = null
}

function removeText(index) {
  collageTexts.value.splice(index, 1)
  selectedItemIndex.value = null
  selectedType.value = null
}

function removeSticker(index) {
  collageStickers.value.splice(index, 1)
  selectedItemIndex.value = null
  selectedType.value = null
}

function addText() {
  if (!newText.content.trim()) return
  collageTexts.value.push({
    content: newText.content,
    x: collage.canvasWidth / 2 - 100,
    y: collage.canvasHeight / 2,
    fontSize: newText.fontSize,
    color: newText.color,
    bold: newText.bold,
    italic: newText.italic
  })
  newText.content = ''
}

function addSticker(emoji) {
  collageStickers.value.push({
    emoji,
    x: collage.canvasWidth / 2 - 20,
    y: collage.canvasHeight / 2 - 20,
    size: 48,
    rotation: 0
  })
}

function applyTemplate(type) {
  collage.templateType = type
  const gap = collage.gap
  const w = collage.canvasWidth
  const h = collage.canvasHeight
  const urls = pictures.value.map(p => p.url)
  const items = []

  if (type === 'grid2') {
    const iw = w - gap * 3
    const ih = h - gap * 3
    items.push({ url: urls[0] || '', x: gap, y: gap, width: iw, height: ih / 2, scale: 1 })
    items.push({ url: urls[1] || '', x: gap, y: gap + ih / 2 + gap, width: iw, height: ih / 2, scale: 1 })
  } else if (type === 'grid4') {
    const iw = (w - gap * 3) / 2
    const ih = (h - gap * 3) / 2
    for (let r = 0; r < 2; r++) {
      for (let c = 0; c < 2; c++) {
        items.push({ url: urls[r * 2 + c] || '', x: gap + c * (iw + gap), y: gap + r * (ih + gap), width: iw, height: ih, scale: 1 })
      }
    }
  } else if (type === 'grid6') {
    const iw = (w - gap * 4) / 3
    const ih = (h - gap * 3) / 2
    for (let r = 0; r < 2; r++) {
      for (let c = 0; c < 3; c++) {
        items.push({ url: urls[r * 3 + c] || '', x: gap + c * (iw + gap), y: gap + r * (ih + gap), width: iw, height: ih, scale: 1 })
      }
    }
  } else if (type === 'grid9') {
    const iw = (w - gap * 4) / 3
    const ih = (h - gap * 4) / 3
    for (let r = 0; r < 3; r++) {
      for (let c = 0; c < 3; c++) {
        items.push({ url: urls[r * 3 + c] || '', x: gap + c * (iw + gap), y: gap + r * (ih + gap), width: iw, height: ih, scale: 1 })
      }
    }
  } else if (type === 'vertical') {
    const count = Math.max(urls.length, 1)
    const iw = w - gap * 2
    const ih = (h - gap * (count + 1)) / count
    for (let i = 0; i < count; i++) {
      items.push({ url: urls[i] || '', x: gap, y: gap + i * (ih + gap), width: iw, height: ih, scale: 1 })
    }
  } else if (type === 'horizontal') {
    const count = Math.max(urls.length, 1)
    const iw = (w - gap * (count + 1)) / count
    const ih = h - gap * 2
    for (let i = 0; i < count; i++) {
      items.push({ url: urls[i] || '', x: gap + i * (iw + gap), y: gap, width: iw, height: ih, scale: 1 })
    }
  } else if (type === 'free') {
    const count = Math.max(urls.length, 1)
    for (let i = 0; i < count; i++) {
      const iw = w * (0.3 + Math.random() * 0.2)
      const ih = h * (0.3 + Math.random() * 0.2)
      items.push({ url: urls[i] || '', x: gap + Math.random() * (w - iw - gap * 2), y: gap + Math.random() * (h - ih - gap * 2), width: iw, height: ih, scale: 1 })
    }
  } else if (type === 'poster_top') {
    const topH = h * 0.55
    const bottomH = h - topH - gap * 3
    const bottomCount = Math.max(urls.length - 1, 1)
    const bottomIw = (w - gap * (bottomCount + 1)) / bottomCount
    items.push({ url: urls[0] || '', x: gap, y: gap, width: w - gap * 2, height: topH, scale: 1 })
    for (let i = 0; i < bottomCount; i++) {
      items.push({ url: urls[i + 1] || '', x: gap + i * (bottomIw + gap), y: topH + gap * 2, width: bottomIw, height: bottomH, scale: 1 })
    }
  } else if (type === 'poster_magazine') {
    const leftW = w * 0.55
    const rightW = w - leftW - gap * 3
    const rightCount = Math.max(urls.length - 1, 2)
    const rightIh = (h - gap * (rightCount + 1)) / rightCount
    items.push({ url: urls[0] || '', x: gap, y: gap, width: leftW, height: h - gap * 2, scale: 1 })
    for (let i = 0; i < rightCount; i++) {
      items.push({ url: urls[i + 1] || '', x: leftW + gap * 2, y: gap + i * (rightIh + gap), width: rightW, height: rightIh, scale: 1 })
    }
  }

  collageItems.value = items
  selectedItemIndex.value = null
  selectedType.value = null
}

function applySizePreset(preset) {
  collage.sizePreset = preset.value
  if (preset.value !== 'custom') {
    collage.canvasWidth = preset.w
    collage.canvasHeight = preset.h
    applyTemplate(collage.templateType)
  }
}

watch(() => collage.gap, () => applyTemplate(collage.templateType))

async function saveCollage() {
  saving.value = true
  try {
    const data = {
      name: collage.name,
      templateType: collage.templateType,
      canvasWidth: collage.canvasWidth,
      canvasHeight: collage.canvasHeight,
      backgroundColor: collage.backgroundColor,
      backgroundImageUrl: collage.backgroundImageUrl,
      gap: collage.gap,
      borderRadius: collage.borderRadius,
      borderWidth: collage.borderWidth,
      borderColor: collage.borderColor,
      items: JSON.stringify(collageItems.value),
      texts: JSON.stringify(collageTexts.value),
      stickers: JSON.stringify(collageStickers.value),
      sizePreset: collage.sizePreset,
      quality: collage.quality,
      pictureIds: pictures.value.map(p => p.id)
    }
    if (editingCollageId.value) {
      const res = await api.put(`/collage/${editingCollageId.value}`, data)
      if (res.data.success) {
        emit('toast', '拼图已更新')
      } else {
        emit('toast', res.data.message || '保存失败')
      }
    } else {
      const res = await api.post('/collage/create', data)
      if (res.data.success) {
        editingCollageId.value = res.data.data.id
        emit('toast', '拼图已保存')
      } else {
        emit('toast', res.data.message || '保存失败')
      }
    }
    await fetchSavedCollages()
  } catch (e) {
    emit('toast', '保存失败: ' + (e.message || '网络错误'))
  } finally {
    saving.value = false
  }
}

async function generateAndExport() {
  exporting.value = true
  try {
    if (!editingCollageId.value) {
      await saveCollage()
    }
    const id = editingCollageId.value
    const res = await api.post(`/collage/${id}/generate`)
    if (res.data.success) {
      const url = res.data.data
      const link = document.createElement('a')
      link.href = url
      link.download = collage.name + '.png'
      link.target = '_blank'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      emit('toast', '拼图已导出')
    } else {
      emit('toast', res.data.message || '导出失败')
    }
  } catch (e) {
    emit('toast', '导出失败: ' + (e.message || '网络错误'))
  } finally {
    exporting.value = false
  }
}

async function fetchSavedCollages() {
  try {
    const res = await api.get('/collage/list')
    if (res.data.success) {
      savedCollages.value = res.data.data || []
    }
  } catch (e) {}
}

function loadCollage(c) {
  editingCollageId.value = c.id
  collage.name = c.name
  collage.templateType = c.templateType || 'grid4'
  collage.canvasWidth = c.canvasWidth || 1080
  collage.canvasHeight = c.canvasHeight || 1080
  collage.backgroundColor = c.backgroundColor || '#FFFFFF'
  collage.backgroundImageUrl = c.backgroundImageUrl || ''
  collage.gap = c.gap || 10
  collage.borderRadius = c.borderRadius || 0
  collage.borderWidth = c.borderWidth || 0
  collage.borderColor = c.borderColor || '#000000'
  collage.sizePreset = c.sizePreset || '1:1'
  collage.quality = c.quality || 'high'
  try {
    collageItems.value = c.items ? JSON.parse(c.items) : []
  } catch (e) { collageItems.value = [] }
  try {
    collageTexts.value = c.texts ? JSON.parse(c.texts) : []
  } catch (e) { collageTexts.value = [] }
  try {
    collageStickers.value = c.stickers ? JSON.parse(c.stickers) : []
  } catch (e) { collageStickers.value = [] }
  selectedItemIndex.value = null
  selectedType.value = null
}

async function deleteCollage(id) {
  try {
    const res = await api.delete(`/collage/${id}`)
    if (res.data.success) {
      emit('toast', '已删除')
      await fetchSavedCollages()
      if (editingCollageId.value === id) {
        editingCollageId.value = null
      }
    }
  } catch (e) {}
}

function addPicturesFromOutside(newPics) {
  const existing = new Set(pictures.value.map(p => p.id))
  for (const pic of newPics) {
    if (!existing.has(pic.id)) {
      pictures.value.push(pic)
    }
  }
  applyTemplate(collage.templateType)
}

onMounted(() => {
  fetchSavedCollages()
  applyTemplate(collage.templateType)
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
})

defineExpose({ addPicturesFromOutside, pictures })
</script>
