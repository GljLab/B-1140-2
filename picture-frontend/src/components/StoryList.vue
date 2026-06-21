<template>
  <div class="story-list">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">故事</h2>
        <p class="text-sm text-gray-500 mt-1">用图片和文字记录你的精彩故事</p>
      </div>
      <button v-if="activeCategory === 'mine'"
        @click="$emit('create')"
        class="px-5 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl shadow-lg hover:shadow-xl transition text-sm font-medium flex items-center space-x-2">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>创建故事</span>
      </button>
    </div>

    <div class="flex bg-gray-100 rounded-xl p-1 mb-4 w-fit">
      <button @click="activeCategory = 'mine'; activeListTab = 'all'"
        :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
          activeCategory === 'mine'
            ? 'bg-white text-blue-600 shadow-sm'
            : 'text-gray-600 hover:text-gray-900']">
        我的故事
      </button>
      <button @click="activeCategory = 'public'; activeListTab = 'public'"
        :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
          activeCategory === 'public'
            ? 'bg-white text-blue-600 shadow-sm'
            : 'text-gray-600 hover:text-gray-900']">
        公开故事
      </button>
    </div>

    <div v-if="activeCategory === 'mine'" class="flex bg-gray-100 rounded-xl p-1 mb-6 w-fit">
      <button v-for="tab in listTabs" :key="tab.id" @click="activeListTab = tab.id"
        :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
          activeListTab === tab.id
            ? 'bg-white text-blue-600 shadow-sm'
            : 'text-gray-600 hover:text-gray-900']">
        {{ tab.name }}
        <span v-if="tab.count !== undefined" class="ml-1 text-xs">({{ tab.count }})</span>
      </button>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="stories.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
      <div class="text-6xl mb-4">📖</div>
      <h3 class="text-xl font-medium text-gray-500">暂无故事</h3>
      <p class="text-sm text-gray-400 mt-2">{{ emptyTip }}</p>
      <button v-if="activeCategory === 'mine'"
        @click="$emit('create')"
        class="mt-4 px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium">
        创建第一个故事
      </button>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
      <div v-for="story in stories" :key="story.id"
        class="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer"
        @click="openStory(story)">
        <div class="aspect-[4/3] relative overflow-hidden bg-gradient-to-br from-blue-100 to-purple-100">
          <img v-if="story.coverUrl" :src="story.coverUrl"
            class="w-full h-full object-cover transform group-hover:scale-105 transition duration-500" />
          <div v-else class="w-full h-full flex items-center justify-center">
            <span class="text-5xl">📖</span>
          </div>
          <div class="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent"></div>
          <div class="absolute top-3 right-3 flex flex-col items-end space-y-2 opacity-0 group-hover:opacity-100 transition">
            <button v-if="story.isOwner" @click.stop="$emit('edit', story)"
              class="p-2 bg-white/90 rounded-lg text-gray-700 hover:text-blue-600 shadow-md hover:bg-white transition"
              title="编辑">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>
            <button v-if="story.isOwner" @click.stop="confirmDelete(story)"
              class="p-2 bg-white/90 rounded-lg text-gray-700 hover:text-red-600 shadow-md hover:bg-white transition"
              title="删除">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
          <div class="absolute bottom-3 left-3 right-3">
            <div class="flex items-center space-x-2 mb-2">
              <span v-if="story.status === 0" class="inline-block px-2 py-0.5 bg-gray-500/80 text-white text-xs rounded-md">
                草稿
              </span>
              <span v-else-if="story.isPublic" class="inline-block px-2 py-0.5 bg-green-500/80 text-white text-xs rounded-md">
                公开
              </span>
              <span v-else class="inline-block px-2 py-0.5 bg-amber-500/80 text-white text-xs rounded-md">
                私密
              </span>
              <span class="inline-block px-2 py-0.5 bg-white/20 backdrop-blur-sm text-white text-xs rounded-md">
                {{ story.itemCount || 0 }} 张
              </span>
            </div>
          </div>
        </div>
        <div class="p-4">
          <h3 class="font-semibold text-gray-800 truncate">{{ story.title }}</h3>
          <p v-if="story.description" class="text-xs text-gray-500 mt-1 line-clamp-2">{{ story.description }}</p>
          <div class="flex items-center justify-between mt-3 pt-3 border-t border-gray-100">
            <div class="flex items-center space-x-3 text-xs text-gray-400">
              <span class="flex items-center space-x-1">
                <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
                <span>{{ story.likeCount || 0 }}</span>
              </span>
              <span class="flex items-center space-x-1">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round"
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <span>{{ story.commentCount || 0 }}</span>
              </span>
              <span class="flex items-center space-x-1">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <span>{{ story.viewCount || 0 }}</span>
              </span>
            </div>
            <span class="text-xs text-gray-400">{{ formatDate(story.updateTime || story.createTime) }}</span>
          </div>
          <div v-if="activeListTab === 'public'" class="flex items-center mt-2">
            <div class="w-5 h-5 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white text-[10px] font-bold flex-shrink-0">
              {{ (story.authorNickname || 'U').charAt(0).toUpperCase() }}
            </div>
            <span class="ml-2 text-xs text-gray-500 truncate">{{ story.authorNickname || '未知作者' }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="activeListTab === 'public' && !loading && stories.length > 0"
      class="flex justify-center mt-8">
      <button @click="loadMore" :disabled="loadingMore"
        class="px-6 py-2 bg-white border border-gray-200 text-gray-600 rounded-lg hover:bg-gray-50 transition text-sm disabled:opacity-50">
        {{ loadingMore ? '加载中...' : '加载更多' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const props = defineProps({
  category: {
    type: String,
    default: 'mine'
  }
})

const emit = defineEmits(['create', 'edit', 'view', 'delete'])

const activeCategory = ref(props.category)
const activeListTab = ref(props.category === 'public' ? 'public' : 'all')
const stories = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const page = ref(0)
const pageSize = 20
const hasMore = ref(true)
const stats = ref({ draftCount: 0, publishedPublicCount: 0, publishedPrivateCount: 0 })

const listTabs = computed(() => {
  return [
    { id: 'all', name: '全部', count: (stats.value.draftCount || 0) + (stats.value.publishedPublicCount || 0) + (stats.value.publishedPrivateCount || 0) },
    { id: 'drafts', name: '草稿', count: stats.value.draftCount || 0 },
    { id: 'published', name: '已发布', count: (stats.value.publishedPublicCount || 0) + (stats.value.publishedPrivateCount || 0) }
  ]
})

const emptyTip = computed(() => {
  if (activeCategory.value === 'public') return '还没有公开故事，快去发布第一个吧！'
  if (activeListTab.value === 'drafts') return '暂无草稿故事'
  if (activeListTab.value === 'published') return '暂无已发布故事'
  return '创建你的第一个故事吧'
})

const fetchStats = async () => {
  if (activeCategory.value !== 'mine') return
  try {
    const res = await api.get('/stories/my/stats')
    if (res.data.success) {
      stats.value = res.data.data
    }
  } catch (e) {}
}

const fetchStories = async () => {
  loading.value = true
  page.value = 0
  try {
    let res
    if (activeCategory.value === 'public') {
      res = await api.get('/stories/public', { params: { page: 0, size: pageSize } })
    } else if (activeListTab.value === 'drafts') {
      res = await api.get('/stories/my/status/0')
    } else if (activeListTab.value === 'published') {
      res = await api.get('/stories/my/status/1')
    } else {
      res = await api.get('/stories/my')
    }
    if (res.data.success) {
      stories.value = res.data.data
      hasMore.value = res.data.data.length >= pageSize
    }
  } catch (e) {
    console.error('Failed to fetch stories:', e)
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (loadingMore.value || !hasMore.value || activeCategory.value !== 'public') return
  loadingMore.value = true
  try {
    page.value++
    const res = await api.get('/stories/public', { params: { page: page.value, size: pageSize } })
    if (res.data.success) {
      stories.value = [...stories.value, ...res.data.data]
      hasMore.value = res.data.data.length >= pageSize
    }
  } catch (e) {
    console.error('Failed to load more stories:', e)
  } finally {
    loadingMore.value = false
  }
}

const openStory = (story) => {
  emit('view', story)
}

const confirmDelete = (story) => {
  if (confirm(`确定要删除故事"${story.title}"吗？`)) {
    deleteStory(story.id)
  }
}

const deleteStory = async (id) => {
  try {
    const res = await api.delete(`/stories/${id}`)
    if (res.data.success) {
      stories.value = stories.value.filter(s => s.id !== id)
      emit('delete')
    } else {
      alert(res.data.message || '删除失败')
    }
  } catch (e) {
    alert('删除失败，请重试')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

watch(activeCategory, () => {
  fetchStats()
  fetchStories()
})

watch(activeListTab, () => {
  fetchStories()
})

onMounted(() => {
  fetchStats()
  fetchStories()
})

const refresh = () => {
  fetchStats()
  fetchStories()
}

defineExpose({ refresh })
</script>
