<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-4">
        <div>
          <h2 class="text-2xl font-bold text-gray-800 flex items-center space-x-2">
            <span>🔒</span>
            <span>保密空间</span>
          </h2>
          <p class="text-sm text-gray-500 mt-1">共 {{ pictures.length }} 张私密图片</p>
        </div>
      </div>
      <div class="flex items-center space-x-3">
        <label v-if="pictures.length > 0" class="flex items-center space-x-2 cursor-pointer text-sm text-gray-600 hover:text-gray-900">
          <input type="checkbox" v-model="multiSelectMode" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
          <span>多选模式</span>
        </label>
        <button @click="$emit('open-settings')"
          class="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span>设置</span>
        </button>
        <button @click="handleLock"
          class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
          <span>锁定</span>
        </button>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>
    <div v-else-if="pictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
      <div class="text-6xl mb-4">🔒</div>
      <h3 class="text-xl font-medium text-gray-500">保密空间暂无图片</h3>
      <p class="text-sm text-gray-400 mt-2">在图片详情页点击"移入保密空间"来加密您的图片</p>
    </div>
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
      <div v-for="pic in pictures" :key="pic.id"
        class="group relative bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
        <div v-if="multiSelectMode" class="absolute top-2 left-2 z-10">
          <input type="checkbox" :value="pic.id" v-model="selectedPictureIds"
            class="w-5 h-5 text-blue-600 rounded bg-white/90 border-2 border-gray-300 focus:ring-blue-500 cursor-pointer" />
        </div>
        <div class="aspect-square bg-gray-100 relative overflow-hidden cursor-pointer" @click="viewPicture(pic)">
          <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
          <div class="absolute top-2 left-10 z-[5]">
            <span class="inline-flex items-center space-x-0.5 px-1.5 py-0.5 bg-purple-600/80 backdrop-blur-sm rounded text-[10px] text-white font-medium">
              🔒 私密
            </span>
          </div>
          <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition duration-300"></div>
          <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition duration-300 flex space-x-1">
            <a :href="pic.url" download @click.stop target="_blank"
              class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-blue-600 shadow-md hover:bg-white transition" title="下载">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
            </a>
            <button @click.stop="confirmMoveOut(pic)"
              class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-orange-600 shadow-md hover:bg-white transition" title="移出保密空间">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 11V7a4 4 0 118 0m-4 8v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2z" />
              </svg>
            </button>
          </div>
          <div class="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition duration-300">
            <div class="flex flex-wrap gap-1">
              <span v-for="tag in (pic.tags || []).slice(0, 3)" :key="tag.id"
                class="text-[10px] px-1.5 py-0.5 bg-white/30 backdrop-blur-sm rounded text-white">#{{ tag.name }}</span>
            </div>
          </div>
        </div>
        <div class="p-3">
          <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
          <div class="flex justify-between items-center mt-1.5">
            <span class="text-xs text-gray-400">{{ formatTime(pic.createTime) }}</span>
            <span class="text-xs text-gray-400">{{ formatSize(pic.size) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="multiSelectMode && selectedPictureIds.length > 0"
      class="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-30 bg-white rounded-2xl shadow-2xl border border-gray-100 px-4 py-3 flex items-center space-x-3">
      <span class="text-sm font-medium text-gray-700 border-r border-gray-200 pr-3">已选 {{ selectedPictureIds.length }} 张</span>
      <button @click="confirmBatchMoveOut"
        class="px-4 py-2 bg-gradient-to-r from-orange-500 to-amber-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
        <span class="flex items-center space-x-1">
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8 11V7a4 4 0 118 0m-4 8v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2z" />
          </svg>
          <span>批量移出</span>
        </span>
      </button>
      <button @click="selectedPictureIds = []" class="px-3 py-2 text-gray-500 hover:text-gray-700 text-sm">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const emit = defineEmits(['view-picture', 'open-settings', 'locked', 'toast', 'confirm'])

const pictures = ref([])
const loading = ref(false)
const multiSelectMode = ref(false)
const selectedPictureIds = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

const fetchPictures = async () => {
  loading.value = true
  try {
    const res = await api.get('/private-space/pictures')
    if (res.data.success) {
      pictures.value = res.data.data || []
    }
  } catch (e) {
    emit('toast', '加载私密图片失败', 'error')
  } finally {
    loading.value = false
  }
}

const viewPicture = (pic) => {
  emit('view-picture', pic, true)
}

const confirmMoveOut = (pic) => {
  emit('confirm', {
    title: `将"${pic.name}"移出保密空间？`,
    message: '移出后图片将在普通图片列表中显示。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post(`/private-space/pictures/${pic.id}/move-out`)
        if (res.data.success) {
          emit('toast', '已移出保密空间')
          await fetchPictures()
        } else {
          emit('toast', res.data.message || '移出失败', 'error')
        }
      } catch (e) {
        emit('toast', '操作失败', 'error')
      }
    }
  })
}

const confirmBatchMoveOut = () => {
  if (selectedPictureIds.value.length === 0) return
  emit('confirm', {
    title: `将 ${selectedPictureIds.value.length} 张图片移出保密空间？`,
    message: '移出后图片将在普通图片列表中显示。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post('/private-space/pictures/batch-move-out', {
          pictureIds: selectedPictureIds.value
        })
        if (res.data.success) {
          emit('toast', `成功移出 ${res.data.data} 张图片`)
          selectedPictureIds.value = []
          multiSelectMode.value = false
          await fetchPictures()
        } else {
          emit('toast', res.data.message || '操作失败', 'error')
        }
      } catch (e) {
        emit('toast', '操作失败', 'error')
      }
    }
  })
}

const handleLock = async () => {
  try {
    const res = await api.post('/private-space/lock')
    if (res.data.success) {
      emit('toast', '保密空间已锁定')
      emit('locked')
    }
  } catch (e) {
    emit('toast', '锁定失败', 'error')
  }
}

const refresh = () => {
  fetchPictures()
}

defineExpose({ refresh, fetchPictures })

onMounted(() => {
  fetchPictures()
})
</script>
