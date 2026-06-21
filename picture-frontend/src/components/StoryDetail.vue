<template>
  <div class="story-detail">
    <div class="bg-white border-b border-gray-200 sticky top-0 z-30">
      <div class="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        <button @click="$emit('close')" class="p-2 hover:bg-gray-100 rounded-lg transition">
          <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div class="flex items-center space-x-3">
          <button v-if="story?.isOwner" @click="$emit('edit', story)"
            class="px-4 py-1.5 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">
            编辑
          </button>
          <button @click="handleShare"
            class="px-4 py-1.5 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition text-sm font-medium flex items-center space-x-1">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
            </svg>
            <span>分享</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="!story" class="text-center py-20">
      <div class="text-5xl mb-4">😔</div>
      <h3 class="text-xl font-medium text-gray-500">故事不存在</h3>
      <p class="text-sm text-gray-400 mt-2">该故事可能已被删除</p>
    </div>

    <div v-else class="max-w-4xl mx-auto px-4 py-6">
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-gray-800 mb-3">{{ story.title }}</h1>
        <p v-if="story.description" class="text-gray-500 mb-4">{{ story.description }}</p>
        <div class="flex items-center justify-center space-x-4 text-sm text-gray-400">
          <div class="flex items-center space-x-2">
            <div class="w-6 h-6 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white text-xs font-bold">
              {{ (story.authorNickname || 'U').charAt(0).toUpperCase() }}
            </div>
            <span>{{ story.authorNickname || '未知' }}</span>
          </div>
          <span>{{ formatDate(story.publishTime || story.createTime) }}</span>
          <span>{{ story.itemCount || 0 }} 张图片</span>
        </div>
      </div>

      <div v-if="story.layout === 'classic'" class="story-layout-classic space-y-12">
        <div v-for="item in story.items" :key="item.id" class="story-item-classic">
          <div class="flex justify-center mb-4">
            <div class="max-w-2xl w-full">
              <img :src="item.pictureUrl" :alt="item.pictureName"
                class="w-full rounded-2xl shadow-lg" />
            </div>
          </div>
          <div v-if="item.content" class="max-w-2xl mx-auto">
            <div class="text-gray-700 text-center leading-relaxed prose prose-lg" v-html="renderMarkdown(item.content)"></div>
          </div>
        </div>
      </div>

      <div v-else-if="story.layout === 'alternate'" class="story-layout-alternate space-y-16">
        <div v-for="(item, idx) in story.items" :key="item.id"
          :class="['story-item-alternate flex items-center gap-8',
            idx % 2 === 0 ? 'flex-row' : 'flex-row-reverse']">
          <div class="flex-1">
            <img :src="item.pictureUrl" :alt="item.pictureName"
              class="w-full rounded-2xl shadow-lg" />
          </div>
          <div class="flex-1">
            <div v-if="item.content" class="text-gray-700 leading-relaxed text-lg prose" v-html="renderMarkdown(item.content)"></div>
          </div>
        </div>
      </div>

      <div v-else-if="story.layout === 'card'" class="story-layout-card grid grid-cols-1 md:grid-cols-2 gap-6">
        <div v-for="item in story.items" :key="item.id"
          class="story-item-card bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100 hover:shadow-xl transition-shadow">
          <div class="aspect-video">
            <img :src="item.pictureUrl" :alt="item.pictureName"
              class="w-full h-full object-cover" />
          </div>
          <div v-if="item.content" class="p-5">
            <div class="text-gray-700 text-sm leading-relaxed prose prose-sm" v-html="renderMarkdown(item.content)"></div>
          </div>
        </div>
      </div>

      <div v-else-if="story.layout === 'timeline'" class="story-layout-timeline relative pl-8">
        <div class="absolute left-3 top-0 bottom-0 w-0.5 bg-gradient-to-b from-blue-400 to-purple-500"></div>
        <div v-for="(item, idx) in story.items" :key="item.id" class="story-item-timeline relative mb-12">
          <div class="absolute -left-[26px] top-4 w-4 h-4 bg-blue-500 rounded-full border-4 border-white shadow"></div>
          <div class="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100">
            <div class="aspect-video">
              <img :src="item.pictureUrl" :alt="item.pictureName"
                class="w-full h-full object-cover" />
            </div>
            <div v-if="item.content" class="p-5">
              <div class="text-gray-700 leading-relaxed prose" v-html="renderMarkdown(item.content)"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-12 pt-8 border-t border-gray-200">
        <div class="flex items-center justify-center space-x-8 mb-8">
          <button @click="toggleLike"
            :class="['flex items-center space-x-2 px-6 py-3 rounded-full transition-all',
              story.liked
                ? 'bg-red-50 text-red-500'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            <svg class="w-6 h-6" :fill="story.liked ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span class="font-medium">{{ story.likeCount || 0 }}</span>
          </button>
          <button @click="scrollToComments"
            class="flex items-center space-x-2 px-6 py-3 bg-gray-100 text-gray-600 rounded-full hover:bg-gray-200 transition">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
            <span class="font-medium">{{ story.commentCount || 0 }}</span>
          </button>
          <button @click="handleShare"
            class="flex items-center space-x-2 px-6 py-3 bg-gray-100 text-gray-600 rounded-full hover:bg-gray-200 transition">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
            </svg>
            <span class="font-medium">分享</span>
          </button>
          <div class="flex items-center space-x-2 px-6 py-3 bg-gray-50 text-gray-400 rounded-full">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            <span class="font-medium">{{ story.viewCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div id="comments-section" class="mt-8 pt-8 border-t border-gray-200">
        <h3 class="text-xl font-bold text-gray-800 mb-6">评论 ({{ story.commentCount || 0 }})</h3>

        <div class="mb-6 bg-gray-50 rounded-xl p-4">
          <textarea v-model="commentInput" rows="3" placeholder="写下你的评论..."
            class="w-full px-4 py-3 bg-white border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition resize-none"></textarea>
          <div class="flex justify-end mt-3">
            <button @click="submitComment"
              :disabled="!commentInput.trim()"
              class="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed">
              发表评论
            </button>
          </div>
        </div>

        <div v-if="commentsLoading" class="flex justify-center py-10">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>

        <div v-else-if="comments.length === 0" class="text-center py-10">
          <div class="text-4xl mb-2">💬</div>
          <p class="text-gray-400 text-sm">暂无评论，快来抢沙发吧</p>
        </div>

        <div v-else class="space-y-4">
          <div v-for="comment in comments" :key="comment.id"
            class="flex space-x-3 p-4 bg-white rounded-xl border border-gray-100">
            <div class="flex-shrink-0 w-10 h-10 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
              {{ (comment.nickname || 'U').charAt(0).toUpperCase() }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between">
                <span class="font-medium text-gray-800 text-sm">{{ comment.nickname || '匿名用户' }}</span>
                <div class="flex items-center space-x-2">
                  <span class="text-xs text-gray-400">{{ formatDate(comment.createTime) }}</span>
                  <button v-if="story.isOwner || comment.isMine"
                    @click="deleteComment(comment.id)"
                    class="text-xs text-gray-400 hover:text-red-500 transition">
                    删除
                  </button>
                </div>
              </div>
              <p class="text-gray-600 text-sm mt-1 whitespace-pre-wrap">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

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
        result.push('<blockquote class="border-l-4 border-blue-300 pl-4 italic text-gray-600 my-3">')
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

const props = defineProps({
  storyId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close', 'edit'])

const story = ref(null)
const loading = ref(true)
const comments = ref([])
const commentsLoading = ref(false)
const commentInput = ref('')

const fetchStory = async () => {
  loading.value = true
  try {
    const res = await api.get(`/stories/${props.storyId}`)
    if (res.data.success) {
      story.value = res.data.data
      fetchComments()
    }
  } catch (e) {
    console.error('Failed to fetch story:', e)
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  commentsLoading.value = true
  try {
    const res = await api.get(`/story-interactions/stories/${props.storyId}/comments`)
    if (res.data.success) {
      comments.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to fetch comments:', e)
  } finally {
    commentsLoading.value = false
  }
}

const toggleLike = async () => {
  try {
    const res = await api.post(`/story-interactions/stories/${props.storyId}/like`)
    if (res.data.success) {
      story.value.liked = res.data.data.liked
      story.value.likeCount = res.data.data.likeCount
    }
  } catch (e) {
    console.error('Failed to toggle like:', e)
  }
}

const submitComment = async () => {
  if (!commentInput.value.trim()) return
  try {
    const res = await api.post(`/story-interactions/stories/${props.storyId}/comments`, {
      content: commentInput.value.trim()
    })
    if (res.data.success) {
      comments.value.unshift(res.data.data)
      story.value.commentCount = (story.value.commentCount || 0) + 1
      commentInput.value = ''
    }
  } catch (e) {
    alert('评论失败，请重试')
    console.error('Failed to submit comment:', e)
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    const res = await api.delete(`/story-interactions/comments/${commentId}`)
    if (res.data.success) {
      comments.value = comments.value.filter(c => c.id !== commentId)
      story.value.commentCount = Math.max(0, (story.value.commentCount || 0) - 1)
    }
  } catch (e) {
    alert('删除失败，请重试')
    console.error('Failed to delete comment:', e)
  }
}

const handleShare = async () => {
  const shareUrl = `${window.location.origin}${window.location.pathname}#/story/${props.storyId}`
  const text = `📖 ${story.value.title}\n${story.value.description || ''}`

  try {
    if (navigator.share) {
      await navigator.share({
        title: story.value.title,
        text: text,
        url: shareUrl
      })
    } else {
      await navigator.clipboard.writeText(`${text}\n${shareUrl}`)
      alert('故事链接已复制到剪贴板')
    }
  } catch (e) {
    try {
      await navigator.clipboard.writeText(`${text}\n${shareUrl}`)
      alert('故事链接已复制到剪贴板')
    } catch (e2) {
      alert('分享失败')
    }
  }
}

const scrollToComments = () => {
  const section = document.getElementById('comments-section')
  if (section) {
    section.scrollIntoView({ behavior: 'smooth' })
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

watch(() => props.storyId, () => {
  if (props.storyId) {
    fetchStory()
  }
})

onMounted(() => {
  fetchStory()
})

const refresh = () => {
  fetchStory()
}

defineExpose({ refresh })
</script>

<style scoped>
.story-layout-classic p {
  font-size: 1.125rem;
  line-height: 1.8;
}

.story-layout-alternate p {
  font-size: 1rem;
  line-height: 1.8;
}

.story-item-card:hover {
  transform: translateY(-2px);
}
</style>
