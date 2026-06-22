<template>
  <div class="note-list-page">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">📝 我的笔记</h2>
        <p class="text-sm text-gray-500 mt-1">记录每一张图片背后的故事</p>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <div class="lg:col-span-3">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
          <div class="flex items-center space-x-3">
            <div class="flex-1 relative">
              <svg class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input v-model="searchKeyword" type="text" placeholder="搜索笔记内容..."
                @keyup.enter="searchNotes"
                class="w-full pl-9 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <button @click="searchNotes"
              class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium">
              搜索
            </button>
            <button v-if="selectedNoteIds.length > 0" @click="exportSelected"
              class="px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition text-sm font-medium flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              <span>导出选中 ({{ selectedNoteIds.length }})</span>
            </button>
          </div>
        </div>

        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="notes.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">📝</div>
          <h3 class="text-xl font-medium text-gray-500">暂无笔记</h3>
          <p class="text-sm text-gray-400 mt-2">打开图片详情，开始记录你的图文日记吧</p>
        </div>
        <div v-else class="space-y-4">
          <div v-for="note in notes" :key="note.id"
            :class="['bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-all cursor-pointer group',
              selectedNoteIds.includes(note.id) ? 'ring-2 ring-blue-500' : '']"
            @click="viewNote(note)">
            <div class="flex">
              <div class="w-40 h-40 flex-shrink-0 bg-gray-100 relative">
                <img v-if="note.pictureUrl" :src="note.pictureUrl" class="w-full h-full object-cover" />
                <div class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition flex items-center justify-center">
                  <span class="text-white text-xs opacity-0 group-hover:opacity-100 transition">查看详情</span>
                </div>
              </div>
              <div class="flex-1 p-4 min-w-0">
                <div class="flex items-start justify-between mb-2">
                  <div class="flex items-center space-x-2 min-w-0">
                    <input type="checkbox" :value="note.id" v-model="selectedNoteIds"
                      @click.stop
                      class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                    <h3 class="font-semibold text-gray-800 truncate">{{ note.title || '未命名笔记' }}</h3>
                  </div>
                  <span :class="['text-[10px] px-2 py-0.5 rounded-full flex-shrink-0',
                    note.isDraft ? 'bg-yellow-100 text-yellow-700' : 'bg-green-100 text-green-700']">
                    {{ note.isDraft ? '草稿' : '已发布' }}
                  </span>
                </div>
                <p class="text-sm text-gray-600 line-clamp-3 mb-3">{{ stripMarkdown(note.content) || '暂无内容，点击开始记录...' }}</p>
                <div class="flex items-center justify-between text-xs text-gray-400">
                  <div class="flex items-center space-x-3">
                    <span class="flex items-center space-x-1">
                      <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <span>{{ formatTime(note.updateTime) }}</span>
                    </span>
                    <span class="flex items-center space-x-1">
                      <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      <span>{{ note.wordCount || 0 }} 字</span>
                    </span>
                  </div>
                  <button @click.stop="deleteNote(note)"
                    class="text-gray-400 hover:text-red-500 transition opacity-0 group-hover:opacity-100">
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-1">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
          <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
            <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            <span>笔记统计</span>
          </h3>
          <div v-if="stats" class="space-y-4">
            <div class="grid grid-cols-2 gap-3">
              <div class="bg-blue-50 rounded-xl p-3 text-center">
                <div class="text-2xl font-bold text-blue-600">{{ stats.totalNotes || 0 }}</div>
                <div class="text-xs text-blue-500 mt-1">笔记总数</div>
              </div>
              <div class="bg-purple-50 rounded-xl p-3 text-center">
                <div class="text-2xl font-bold text-purple-600">{{ stats.totalWords || 0 }}</div>
                <div class="text-xs text-purple-500 mt-1">总字数</div>
              </div>
              <div class="bg-yellow-50 rounded-xl p-3 text-center">
                <div class="text-2xl font-bold text-yellow-600">{{ stats.draftCount || 0 }}</div>
                <div class="text-xs text-yellow-500 mt-1">草稿数</div>
              </div>
              <div class="bg-green-50 rounded-xl p-3 text-center">
                <div class="text-2xl font-bold text-green-600">{{ stats.publishedCount || 0 }}</div>
                <div class="text-xs text-green-500 mt-1">已发布</div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
          <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
            <svg class="w-5 h-5 text-orange-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>最近编辑</span>
          </h3>
          <div v-if="stats?.recentNotes?.length > 0" class="space-y-3">
            <div v-for="note in stats.recentNotes.slice(0, 5)" :key="note.id"
              @click="viewNote(note)"
              class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 cursor-pointer transition">
              <div class="w-10 h-10 rounded-lg bg-gray-100 flex-shrink-0 overflow-hidden">
                <img v-if="note.pictureUrl" :src="note.pictureUrl" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-800 truncate">{{ note.title || '未命名笔记' }}</p>
                <p class="text-xs text-gray-400">{{ formatTime(note.updateTime) }}</p>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-4 text-xs text-gray-400">
            暂无笔记
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
            <svg class="w-5 h-5 text-pink-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            <span>字数排行</span>
          </h3>
          <div v-if="stats?.topNotesByWords?.length > 0" class="space-y-3">
            <div v-for="(note, index) in stats.topNotesByWords.slice(0, 5)" :key="note.id"
              @click="viewNote(note)"
              class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 cursor-pointer transition">
              <div :class="['w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0',
                index === 0 ? 'bg-yellow-400 text-white' :
                index === 1 ? 'bg-gray-300 text-white' :
                index === 2 ? 'bg-orange-400 text-white' :
                'bg-gray-100 text-gray-500']">
                {{ index + 1 }}
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-800 truncate">{{ note.title || '未命名笔记' }}</p>
              </div>
              <span class="text-xs text-gray-400 flex-shrink-0">{{ note.wordCount || 0 }} 字</span>
            </div>
          </div>
          <div v-else class="text-center py-4 text-xs text-gray-400">
            暂无笔记
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const emit = defineEmits(['view-note', 'refresh'])

const notes = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedNoteIds = ref([])
const stats = ref(null)

const fetchNotes = async () => {
  loading.value = true
  try {
    const res = await api.get('/notes/my')
    if (res.data.success) {
      notes.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to fetch notes:', e)
  } finally {
    loading.value = false
  }
}

const searchNotes = async () => {
  loading.value = true
  try {
    const res = await api.get('/notes/search', {
      params: {
        keyword: searchKeyword.value,
        page: 0,
        size: 50
      }
    })
    if (res.data.success) {
      notes.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to search notes:', e)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const res = await api.get('/notes/stats')
    if (res.data.success) {
      stats.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to fetch stats:', e)
  }
}

const viewNote = (note) => {
  emit('view-note', note)
}

const deleteNote = async (note) => {
  if (!confirm(`确定要删除笔记"${note.title || '未命名笔记'}"吗？`)) return
  try {
    const res = await api.delete(`/notes/${note.id}`)
    if (res.data.success) {
      notes.value = notes.value.filter(n => n.id !== note.id)
      fetchStats()
      emit('refresh')
    }
  } catch (e) {
    console.error('Failed to delete note:', e)
  }
}

const exportSelected = async () => {
  if (selectedNoteIds.value.length === 0) return
  try {
    const res = await api.post('/notes/export', {
      noteIds: selectedNoteIds.value,
      includeImages: true
    }, {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'notes_export.md')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    console.error('Failed to export notes:', e)
  }
}

const stripMarkdown = (text) => {
  if (!text) return ''
  return text
    .replace(/#{1,6}\s/g, '')
    .replace(/\*\*(.+?)\*\*/g, '$1')
    .replace(/\*(.+?)\*/g, '$1')
    .replace(/`{1,3}[^`]*`{1,3}/g, '')
    .replace(/\[(.+?)\]\(.+?\)/g, '$1')
    .replace(/^> /gm, '')
    .replace(/^[-*+] /gm, '')
    .replace(/^\d+\. /gm, '')
    .replace(/\n/g, ' ')
    .trim()
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const loadData = () => {
  fetchNotes()
  fetchStats()
}

onMounted(() => {
  loadData()
})

defineExpose({
  refresh: loadData
})
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
