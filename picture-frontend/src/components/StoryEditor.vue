<template>
  <div class="story-editor fixed inset-0 z-50 bg-gray-50 flex flex-col">
    <header class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center space-x-4">
        <button @click="handleClose" class="p-2 hover:bg-gray-100 rounded-lg transition">
          <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div>
          <h2 class="text-lg font-bold text-gray-800">{{ editingStory ? '编辑故事' : '创建故事' }}</h2>
          <p class="text-xs text-gray-400">{{ saveStatus }}</p>
        </div>
      </div>
      <div class="flex items-center space-x-3">
        <button v-if="editingStory && editingStory.status === 1"
          @click="handleUnpublish"
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">
          取消发布
        </button>
        <button @click="showPublishModal = true"
          :disabled="storyItems.length === 0"
          class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg shadow-lg hover:shadow-xl transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed">
          {{ editingStory && editingStory.status === 1 ? '重新发布' : '发布' }}
        </button>
      </div>
    </header>

    <div class="flex-1 flex overflow-hidden">
      <div class="flex-1 overflow-y-auto p-6">
        <div class="max-w-3xl mx-auto">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">故事标题</label>
              <input v-model="storyForm.title" type="text" placeholder="给你的故事起个标题..."
                @input="autoSave"
                class="w-full px-4 py-3 border border-gray-200 rounded-xl text-lg font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">故事简介</label>
              <textarea v-model="storyForm.description" rows="3" placeholder="简单介绍一下这个故事..."
                @input="autoSave"
                class="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition resize-none"></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-3">选择布局模板</label>
              <div class="grid grid-cols-4 gap-3">
                <div v-for="layout in layoutOptions" :key="layout.id"
                  @click="selectLayout(layout.id)"
                  :class="['p-3 border-2 rounded-xl cursor-pointer transition-all',
                    selectedLayout === layout.id
                      ? 'border-blue-500 bg-blue-50'
                      : 'border-gray-200 hover:border-gray-300']">
                  <div class="aspect-[4/3] bg-gray-100 rounded-lg mb-2 flex items-center justify-center overflow-hidden">
                    <div v-if="layout.id === 'classic'" class="w-full h-full flex flex-col items-center justify-center p-2">
                      <div class="w-2/3 h-2/3 bg-gray-300 rounded mb-1"></div>
                      <div class="w-1/2 h-2 bg-gray-300 rounded"></div>
                    </div>
                    <div v-else-if="layout.id === 'alternate'" class="w-full h-full flex flex-col items-center justify-center p-1 space-y-1">
                      <div class="w-full flex items-center space-x-1">
                        <div class="w-1/2 h-4 bg-gray-300 rounded"></div>
                        <div class="w-1/2 h-3 bg-gray-200 rounded"></div>
                      </div>
                      <div class="w-full flex items-center space-x-1">
                        <div class="w-1/2 h-3 bg-gray-200 rounded"></div>
                        <div class="w-1/2 h-4 bg-gray-300 rounded"></div>
                      </div>
                    </div>
                    <div v-else-if="layout.id === 'card'" class="w-full h-full flex flex-col items-center justify-center p-1 space-y-1">
                      <div class="w-full h-6 bg-gray-300 rounded"></div>
                      <div class="w-full h-6 bg-gray-300 rounded"></div>
                    </div>
                    <div v-else-if="layout.id === 'timeline'" class="w-full h-full flex items-center justify-center p-1">
                      <div class="w-1 h-full bg-blue-300 rounded-full mx-2"></div>
                      <div class="flex-1 space-y-1">
                        <div class="h-3 bg-gray-300 rounded"></div>
                        <div class="h-3 bg-gray-300 rounded"></div>
                        <div class="h-3 bg-gray-300 rounded"></div>
                      </div>
                    </div>
                  </div>
                  <p class="text-xs text-center font-medium text-gray-700">{{ layout.name }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-bold text-gray-800">故事内容</h3>
              <button @click="showPicturePicker = true"
                class="px-4 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition text-sm font-medium flex items-center space-x-1">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                <span>添加图片</span>
              </button>
            </div>

            <div v-if="storyItems.length === 0" class="text-center py-12 border-2 border-dashed border-gray-200 rounded-xl">
              <div class="text-5xl mb-3">🖼️</div>
              <p class="text-gray-500 mb-4">还没有添加图片</p>
              <button @click="showPicturePicker = true"
                class="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium">
                从图库添加
              </button>
            </div>

            <div v-else class="space-y-4">
              <div v-for="(item, index) in storyItems" :key="item.id || item.tempId"
                :draggable="true"
                @dragstart="handleDragStart($event, index)"
                @dragover.prevent="handleDragOver($event, index)"
                @dragend="handleDragEnd"
                @drop="handleDrop($event, index)"
                :class="['story-item bg-gray-50 rounded-xl p-4 border-2 transition-all cursor-move',
                  dragIndex === index ? 'border-blue-500 bg-blue-50 scale-[1.02]' : 'border-gray-100 hover:border-gray-200',
                  dragOverIndex === index && dragIndex !== index ? 'border-dashed border-blue-400' : '']">
                <div class="flex items-start space-x-4">
                  <div class="flex-shrink-0 w-8 flex flex-col items-center justify-center pt-1">
                    <div class="text-gray-400 cursor-grab active:cursor-grabbing">
                      <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                        <circle cx="9" cy="6" r="1.5" />
                        <circle cx="15" cy="6" r="1.5" />
                        <circle cx="9" cy="12" r="1.5" />
                        <circle cx="15" cy="12" r="1.5" />
                        <circle cx="9" cy="18" r="1.5" />
                        <circle cx="15" cy="18" r="1.5" />
                      </svg>
                    </div>
                    <span class="text-xs text-gray-400 mt-1">{{ index + 1 }}</span>
                  </div>
                  <div class="flex-shrink-0 w-32 h-32 bg-gray-200 rounded-lg overflow-hidden">
                    <img :src="item.pictureUrl" class="w-full h-full object-cover" />
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-sm font-medium text-gray-700 truncate">{{ item.pictureName || '图片' }}</span>
                      <button @click="removeItem(item)"
                        class="p-1 text-gray-400 hover:text-red-500 transition">
                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                    <div class="rich-text-toolbar flex items-center space-x-1 mb-2 pb-2 border-b border-gray-200">
                      <button @click="formatText('bold', item)"
                        :class="['p-1.5 rounded transition', item.boldActive ? 'bg-blue-100 text-blue-600' : 'text-gray-500 hover:bg-gray-100']"
                        title="粗体">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M15.6 10.79c.97-.67 1.65-1.77 1.65-2.79 0-2.26-1.75-4-4-4H7v14h7.04c2.09 0 3.71-1.7 3.71-3.79 0-1.52-.86-2.82-2.15-3.42zM10 6.5h3c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5h-3v-3zm3.5 9H10v-3h3.5c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5z"/>
                        </svg>
                      </button>
                      <button @click="formatText('italic', item)"
                        :class="['p-1.5 rounded transition', item.italicActive ? 'bg-blue-100 text-blue-600' : 'text-gray-500 hover:bg-gray-100']"
                        title="斜体">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"/>
                        </svg>
                      </button>
                      <button @click="formatText('quote', item)"
                        :class="['p-1.5 rounded transition', item.quoteActive ? 'bg-blue-100 text-blue-600' : 'text-gray-500 hover:bg-gray-100']"
                        title="引用">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                          <path d="M6 17h3l2-4V7H5v6h3zm8 0h3l2-4V7h-6v6h3z"/>
                        </svg>
                      </button>
                    </div>
                    <textarea v-model="item.content"
                      @input="handleItemContentChange(item)"
                      @focus="handleTextareaFocus($event, item)"
                      placeholder="为这张图片写点什么..."
                      rows="3"
                      class="w-full px-3 py-2 bg-white border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition resize-none"></textarea>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="w-80 border-l border-gray-200 bg-white overflow-y-auto flex-shrink-0">
        <div class="p-4 border-b border-gray-100">
          <h3 class="font-bold text-gray-800 text-sm">实时预览</h3>
          <p class="text-xs text-gray-400 mt-1">{{ layoutOptions.find(l => l.id === selectedLayout)?.name }}</p>
        </div>
        <div class="p-4">
          <div class="bg-gray-50 rounded-xl p-3 preview-container">
            <div class="preview-classic" v-if="selectedLayout === 'classic'">
              <div v-for="item in storyItems.slice(0, 3)" :key="item.id || item.tempId" class="mb-4">
                <div class="aspect-video bg-gray-200 rounded-lg overflow-hidden mb-2">
                  <img v-if="item.pictureUrl" :src="item.pictureUrl" class="w-full h-full object-cover" />
                </div>
                <div v-if="item.content" class="text-xs text-gray-600 line-clamp-2 prose prose-sm" v-html="renderMarkdown(item.content)"></div>
              </div>
            </div>
            <div class="preview-alternate" v-else-if="selectedLayout === 'alternate'">
              <div v-for="(item, idx) in storyItems.slice(0, 4)" :key="item.id || item.tempId"
                :class="['flex items-center mb-3', idx % 2 === 0 ? 'flex-row' : 'flex-row-reverse']">
                <div class="w-1/2 aspect-square bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
                  <img v-if="item.pictureUrl" :src="item.pictureUrl" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 px-2">
                  <div v-if="item.content" class="text-[10px] text-gray-600 line-clamp-2" v-html="renderMarkdown(item.content)"></div>
                </div>
              </div>
            </div>
            <div class="preview-card" v-else-if="selectedLayout === 'card'">
              <div v-for="item in storyItems.slice(0, 3)" :key="item.id || item.tempId"
                class="bg-white rounded-lg shadow-sm overflow-hidden mb-3">
                <div class="aspect-video bg-gray-200">
                  <img v-if="item.pictureUrl" :src="item.pictureUrl" class="w-full h-full object-cover" />
                </div>
                <div v-if="item.content" class="p-2">
                  <p class="text-[10px] text-gray-600 line-clamp-2" v-html="renderMarkdown(item.content)"></p>
                </div>
              </div>
            </div>
            <div class="preview-timeline" v-else-if="selectedLayout === 'timeline'">
              <div class="relative pl-4">
                <div class="absolute left-1.5 top-0 bottom-0 w-0.5 bg-blue-200"></div>
                <div v-for="item in storyItems.slice(0, 3)" :key="item.id || item.tempId" class="relative mb-3">
                  <div class="absolute -left-[14px] top-2 w-3 h-3 bg-blue-500 rounded-full border-2 border-white"></div>
                  <div class="aspect-video bg-gray-200 rounded-lg overflow-hidden mb-1">
                    <img v-if="item.pictureUrl" :src="item.pictureUrl" class="w-full h-full object-cover" />
                  </div>
                  <div v-if="item.content" class="text-[10px] text-gray-600 line-clamp-2" v-html="renderMarkdown(item.content)"></div>
                </div>
              </div>
            </div>
            <div v-if="storyItems.length === 0" class="text-center py-8">
              <div class="text-3xl mb-2">📖</div>
              <p class="text-xs text-gray-400">添加图片后预览</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPicturePicker" class="fixed inset-0 z-60 flex items-center justify-center p-4" @click.self="showPicturePicker = false">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[85vh] overflow-hidden relative z-10">
        <div class="p-4 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">选择图片</h3>
          <button @click="showPicturePicker = false" class="p-2 hover:bg-gray-100 rounded-lg transition">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-4 border-b border-gray-100">
          <input v-model="pickerSearch" type="text" placeholder="搜索图片..."
            @input="fetchPickerPictures"
            class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div class="p-4 overflow-y-auto max-h-[55vh]">
          <div v-if="pickerLoading" class="flex justify-center py-10">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>
          <div v-else-if="pickerPictures.length === 0" class="text-center py-10">
            <div class="text-4xl mb-2">🖼️</div>
            <p class="text-gray-400 text-sm">暂无图片</p>
          </div>
          <div v-else class="grid grid-cols-4 sm:grid-cols-5 lg:grid-cols-6 gap-3">
            <div v-for="pic in pickerPictures" :key="pic.id"
              @click="togglePictureSelection(pic)"
              :class="['aspect-square rounded-lg overflow-hidden cursor-pointer relative transition-all',
                selectedPictureIds.includes(pic.id)
                  ? 'ring-4 ring-blue-500 ring-offset-2'
                  : 'hover:ring-2 hover:ring-blue-300']">
              <img :src="pic.url" class="w-full h-full object-cover" />
              <div v-if="selectedPictureIds.includes(pic.id)"
                class="absolute top-2 right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                <svg class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
            </div>
          </div>
        </div>
        <div class="p-4 border-t border-gray-100 flex items-center justify-between bg-gray-50">
          <span class="text-sm text-gray-500">已选择 {{ selectedPictureIds.length }} 张</span>
          <div class="flex space-x-3">
            <button @click="showPicturePicker = false"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">
              取消
            </button>
            <button @click="confirmAddPictures"
              :disabled="selectedPictureIds.length === 0"
              class="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed">
              添加到故事
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPublishModal" class="fixed inset-0 z-60 flex items-center justify-center p-4" @click.self="showPublishModal = false">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden relative z-10">
        <div class="p-6 border-b border-gray-100">
          <h3 class="text-lg font-bold text-gray-800">发布故事</h3>
          <p class="text-sm text-gray-500 mt-1">设置故事的可见范围</p>
        </div>
        <div class="p-6 space-y-4">
          <div class="space-y-3">
            <label v-for="opt in publishOptions" :key="opt.value"
              @click="publishOption = opt.value"
              :class="['flex items-start space-x-3 p-4 border-2 rounded-xl cursor-pointer transition-all',
                publishOption === opt.value
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-200 hover:border-gray-300']">
              <input type="radio" :value="opt.value" v-model="publishOption" class="mt-0.5" />
              <div class="flex-1">
                <p class="font-medium text-gray-800">{{ opt.label }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ opt.desc }}</p>
              </div>
            </label>
          </div>
        </div>
        <div class="p-6 border-t border-gray-100 flex justify-end space-x-3 bg-gray-50">
          <button @click="showPublishModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">
            取消
          </button>
          <button @click="handlePublish"
            :disabled="!storyForm.title.trim()"
            class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg shadow-lg hover:shadow-xl transition font-medium text-sm disabled:opacity-50 disabled:cursor-not-allowed">
            发布
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const props = defineProps({
  storyId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['close', 'saved', 'published'])

const storyForm = reactive({
  title: '',
  description: '',
  layout: 'classic'
})
const storyItems = ref([])
const editingStory = ref(null)
const selectedLayout = ref('classic')
const saveStatus = ref('')
let autoSaveTimer = null

const showPicturePicker = ref(false)
const pickerPictures = ref([])
const pickerLoading = ref(false)
const pickerSearch = ref('')
const selectedPictureIds = ref([])

const showPublishModal = ref(false)
const publishOption = ref('public')

const layoutOptions = [
  { id: 'classic', name: '经典模式' },
  { id: 'alternate', name: '左右交替' },
  { id: 'card', name: '卡片模式' },
  { id: 'timeline', name: '时间轴' }
]

const publishOptions = [
  { value: 'public', label: '公开', desc: '所有用户都可以浏览和互动' },
  { value: 'private', label: '私密', desc: '只有你自己可以查看' }
]

const dragIndex = ref(null)
const dragOverIndex = ref(null)
let draggedItem = null

let activeTextarea = null

const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>')
  const lines = html.split('\n')
  const result = []
  let inQuote = false
  for (let line of lines) {
    if (line.startsWith('&gt; ')) {
      if (!inQuote) {
        result.push('<blockquote class="border-l-4 border-blue-300 pl-4 italic text-gray-600 my-2">')
        inQuote = true
      }
      result.push(line.replace('&gt; ', ''))
    } else {
      if (inQuote) {
        result.push('</blockquote>')
        inQuote = false
      }
      result.push(line)
    }
  }
  if (inQuote) {
    result.push('</blockquote>')
  }
  return result.join('\n')
}

const loadStory = async () => {
  if (!props.storyId) return
  try {
    const res = await api.get(`/stories/${props.storyId}/edit`)
    if (res.data.success) {
      editingStory.value = res.data.data
      storyForm.title = res.data.data.title || ''
      storyForm.description = res.data.data.description || ''
      selectedLayout.value = res.data.data.layout || 'classic'
      storyForm.layout = selectedLayout.value
      storyItems.value = (res.data.data.items || []).map(item => ({
        ...item,
        boldActive: false,
        italicActive: false,
        quoteActive: false
      }))
    }
  } catch (e) {
    console.error('Failed to load story:', e)
  }
}

const saveStory = async () => {
  try {
    let storyId = props.storyId
    if (!storyId) {
      const createRes = await api.post('/stories', {
        title: storyForm.title || '未命名故事',
        description: storyForm.description,
        layout: selectedLayout.value
      })
      if (createRes.data.success) {
        storyId = createRes.data.data.id
        editingStory.value = createRes.data.data
        saveStatus.value = '已保存'
        emit('saved', createRes.data.data)
      }
    } else {
      const updateRes = await api.put(`/stories/${storyId}`, {
        title: storyForm.title,
        description: storyForm.description,
        layout: selectedLayout.value
      })
      if (updateRes.data.success) {
        editingStory.value = updateRes.data.data
        saveStatus.value = '已保存'
        emit('saved', updateRes.data.data)
      }
    }
    return storyId
  } catch (e) {
    console.error('Failed to save story:', e)
    saveStatus.value = '保存失败'
    return null
  }
}

const autoSave = () => {
  saveStatus.value = '保存中...'
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(async () => {
    if (!storyForm.title.trim() && !editingStory.value) return
    await saveStory()
  }, 1000)
}

const selectLayout = (layoutId) => {
  selectedLayout.value = layoutId
  storyForm.layout = layoutId
  autoSave()
}

const fetchPickerPictures = async () => {
  pickerLoading.value = true
  try {
    const res = await api.get('/list')
    if (res.data.success) {
      pickerPictures.value = res.data.data.filter(p => !storyItems.value.some(item => item.pictureId === p.id))
    }
  } catch (e) {
    console.error('Failed to fetch pictures:', e)
  } finally {
    pickerLoading.value = false
  }
}

const togglePictureSelection = (pic) => {
  const idx = selectedPictureIds.value.indexOf(pic.id)
  if (idx >= 0) {
    selectedPictureIds.value.splice(idx, 1)
  } else {
    selectedPictureIds.value.push(pic.id)
  }
}

const confirmAddPictures = async () => {
  if (selectedPictureIds.value.length === 0) return

  let storyId = editingStory.value?.id
  if (!storyId) {
    if (!storyForm.title.trim()) {
      storyForm.title = '我的故事'
    }
    storyId = await saveStory()
    if (!storyId) return
  }

  for (const picId of selectedPictureIds.value) {
    try {
      const res = await api.post(`/stories/${storyId}/items`, {
        pictureId: picId,
        content: '',
        sortOrder: storyItems.value.length
      })
      if (res.data.success) {
        const newItem = {
          ...res.data.data,
          boldActive: false,
          italicActive: false,
          quoteActive: false
        }
        storyItems.value.push(newItem)
      }
    } catch (e) {
      console.error('Failed to add picture:', e)
    }
  }

  selectedPictureIds.value = []
  showPicturePicker.value = false
  saveStatus.value = '已保存'
}

const removeItem = async (item) => {
  if (!confirm('确定要移除这张图片吗？')) return
  if (item.id) {
    try {
      await api.delete(`/stories/items/${item.id}`)
    } catch (e) {
      console.error('Failed to remove item:', e)
    }
  }
  storyItems.value = storyItems.value.filter(i => i.id !== item.id)
  saveStatus.value = '已保存'
}

const handleItemContentChange = (item) => {
  if (item.id) {
    updateItemContent(item)
  }
  autoSave()
}

let updateTimers = {}
const updateItemContent = (item) => {
  if (updateTimers[item.id]) clearTimeout(updateTimers[item.id])
  updateTimers[item.id] = setTimeout(async () => {
    try {
      await api.put(`/stories/items/${item.id}`, {
        content: item.content
      })
      saveStatus.value = '已保存'
    } catch (e) {
      console.error('Failed to update item:', e)
    }
  }, 800)
}

const handleTextareaFocus = (e, item) => {
  activeTextarea = e.target
}

const formatText = (format, item) => {
  if (!activeTextarea) {
    const textareas = document.querySelectorAll('textarea')
    if (textareas.length > 0) {
      activeTextarea = textareas[0]
    } else {
      return
    }
  }
  
  const textarea = activeTextarea
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = item.content || ''
  const selectedText = text.substring(start, end)
  
  let newText = ''
  let cursorOffset = 0
  
  if (format === 'bold') {
    if (selectedText) {
      if (text.substring(start - 2, start) === '**' && text.substring(end, end + 2) === '**') {
        newText = text.substring(0, start - 2) + selectedText + text.substring(end + 2)
        cursorOffset = -2
      } else {
        newText = text.substring(0, start) + '**' + selectedText + '**' + text.substring(end)
        cursorOffset = 2
      }
    } else {
      newText = text.substring(0, start) + '**粗体文字**' + text.substring(end)
      cursorOffset = 2
    }
  } else if (format === 'italic') {
    if (selectedText) {
      if (text.charAt(start - 1) === '*' && text.charAt(end) === '*') {
        newText = text.substring(0, start - 1) + selectedText + text.substring(end + 1)
        cursorOffset = -1
      } else {
        newText = text.substring(0, start) + '*' + selectedText + '*' + text.substring(end)
        cursorOffset = 1
      }
    } else {
      newText = text.substring(0, start) + '*斜体文字*' + text.substring(end)
      cursorOffset = 1
    }
  } else if (format === 'quote') {
    const lines = text.split('\n')
    let lineIndex = 0
    let charCount = 0
    for (let i = 0; i < lines.length; i++) {
      charCount += lines[i].length + 1
      if (charCount > start) {
        lineIndex = i
        break
      }
    }
    if (lines[lineIndex].startsWith('> ')) {
      lines[lineIndex] = lines[lineIndex].substring(2)
    } else {
      lines[lineIndex] = '> ' + lines[lineIndex]
    }
    newText = lines.join('\n')
  }
  
  item.content = newText
  handleItemContentChange(item)
  
  setTimeout(() => {
    textarea.focus()
    if (format !== 'quote' && selectedText) {
      textarea.setSelectionRange(start + cursorOffset, end + cursorOffset)
    }
  }, 0)
}

const handleDragStart = (e, index) => {
  dragIndex.value = index
  draggedItem = storyItems.value[index]
  e.dataTransfer.effectAllowed = 'move'
  e.dataTransfer.setData('text/plain', index)
}

const handleDragOver = (e, index) => {
  e.preventDefault()
  dragOverIndex.value = index
}

const handleDragEnd = () => {
  dragIndex.value = null
  dragOverIndex.value = null
  draggedItem = null
}

const handleDrop = async (e, targetIndex) => {
  e.preventDefault()
  if (dragIndex.value === null || dragIndex.value === targetIndex) return

  const items = [...storyItems.value]
  const [removed] = items.splice(dragIndex.value, 1)
  items.splice(targetIndex, 0, removed)
  storyItems.value = items

  const itemIds = items.map((item, idx) => {
    item.sortOrder = idx
    return item.id
  }).filter(id => id)

  if (editingStory.value && itemIds.length > 0) {
    try {
      await api.post(`/stories/${editingStory.value.id}/items/reorder`, itemIds)
      saveStatus.value = '已保存'
    } catch (e) {
      console.error('Failed to reorder items:', e)
    }
  }

  dragIndex.value = null
  dragOverIndex.value = null
  draggedItem = null
}

const handlePublish = async () => {
  if (!editingStory.value) {
    await saveStory()
  }
  if (!editingStory.value) return

  try {
    const isPublic = publishOption.value === 'public'
    const res = await api.post(`/stories/${editingStory.value.id}/publish?isPublic=${isPublic}`)
    if (res.data.success) {
      editingStory.value = res.data.data
      showPublishModal.value = false
      saveStatus.value = '发布成功'
      emit('published', res.data.data)
    } else {
      alert(res.data.message || '发布失败')
    }
  } catch (e) {
    alert('发布失败，请重试')
  }
}

const handleUnpublish = async () => {
  if (!confirm('确定要取消发布吗？取消后其他用户将无法查看。')) return
  try {
    const res = await api.post(`/stories/${editingStory.value.id}/unpublish`)
    if (res.data.success) {
      editingStory.value = res.data.data
      saveStatus.value = '已取消发布'
    }
  } catch (e) {
    alert('操作失败，请重试')
  }
}

const handleClose = () => {
  if (storyForm.title.trim() || storyItems.value.length > 0) {
    if (!confirm('确定要离开吗？未保存的内容可能会丢失。')) return
  }
  emit('close')
}

watch(() => props.storyId, () => {
  if (props.storyId) {
    loadStory()
  }
})

onMounted(() => {
  if (props.storyId) {
    loadStory()
  }
})
</script>

<style scoped>
.preview-container {
  font-size: 12px;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.story-item {
  transition: all 0.2s ease;
}
</style>
