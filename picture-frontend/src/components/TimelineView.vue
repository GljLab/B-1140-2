<template>
  <div class="timeline-view">
    <!-- 回忆推送卡片 -->
    <div v-if="memories.length > 0" class="mb-6">
      <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center space-x-2">
        <span class="text-2xl">✨</span>
        <span>回忆时刻</span>
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="memory in memories" :key="memory.year"
          class="bg-gradient-to-br from-pink-500 via-rose-500 to-orange-400 rounded-2xl p-5 text-white shadow-lg cursor-pointer hover:shadow-xl transform hover:-translate-y-1 transition-all duration-300"
          @click="expandMemory(memory)">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-pink-100 text-sm">{{ memory.yearsAgo }}年前的今天</p>
              <p class="text-2xl font-bold mt-1">{{ memory.dateStr }}</p>
              <p class="text-pink-100 text-sm mt-2">你上传了 {{ memory.count }} 张照片</p>
            </div>
            <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
              <span class="text-2xl">📸</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回忆详情弹窗 -->
    <div v-if="expandedMemory" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="expandedMemory = null">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[85vh] overflow-hidden relative z-10">
        <div class="bg-gradient-to-r from-pink-500 to-orange-400 p-6 text-white">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-xl font-bold">{{ expandedMemory.yearsAgo }}年前的今天</h3>
              <p class="text-pink-100 text-sm mt-1">{{ expandedMemory.dateStr }} · {{ expandedMemory.count }} 张照片</p>
            </div>
            <button @click="expandedMemory = null" class="p-2 hover:bg-white/20 rounded-lg transition">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
        <div class="p-6 overflow-y-auto max-h-[60vh]">
          <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            <div v-for="pic in expandedMemory.pictures" :key="pic.id"
              class="aspect-square rounded-xl overflow-hidden bg-gray-100 cursor-pointer group"
              @click="viewPicture(pic)">
              <img :src="pic.url" class="w-full h-full object-cover group-hover:scale-105 transition duration-300" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计图表 -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
      <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
        <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <span>上传活跃度</span>
        <span class="text-xs text-gray-400 font-normal ml-auto">点击月份快速跳转</span>
      </h3>
      <div class="flex items-end justify-between h-40 gap-1">
        <div v-for="(month, idx) in monthlyStats" :key="idx"
          class="flex-1 flex flex-col items-center cursor-pointer group"
          @click="jumpToMonth(month.year, month.month)">
          <div class="w-full flex-1 flex items-end">
            <div class="w-full rounded-t-lg transition-all duration-300 group-hover:opacity-80"
              :class="month.isMax ? 'bg-gradient-to-t from-purple-500 to-pink-500' : 'bg-gradient-to-t from-blue-400 to-blue-300'"
              :style="{ height: month.height + '%', minHeight: month.count > 0 ? '4px' : '0' }">
            </div>
          </div>
          <p class="text-[10px] text-gray-400 mt-2 group-hover:text-blue-500 transition">{{ month.month }}月</p>
          <p class="text-[10px] font-medium text-gray-600">{{ month.count }}</p>
        </div>
      </div>
    </div>

    <!-- 纪念日列表 -->
    <div v-if="anniversaries.length > 0" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
      <h3 class="font-bold text-gray-800 mb-3 flex items-center space-x-2">
        <span class="text-xl">🎉</span>
        <span>我的纪念日</span>
      </h3>
      <div class="flex flex-wrap gap-2">
        <button v-for="ann in anniversaries" :key="ann.date"
          @click="jumpToDate(ann.date)"
          class="px-3 py-1.5 bg-gradient-to-r from-amber-100 to-orange-100 text-amber-700 rounded-full text-sm hover:shadow-md transition flex items-center space-x-1">
          <span>{{ ann.emoji || '🎉' }}</span>
          <span>{{ ann.title }}</span>
          <span class="text-amber-500 text-xs">· {{ formatShortDate(ann.date) }}</span>
        </button>
      </div>
    </div>

    <!-- 年度回忆集锦入口 -->
    <div class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 rounded-2xl p-5 mb-6 text-white cursor-pointer hover:shadow-xl transition-all duration-300"
      @click="openYearlyHighlights">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-indigo-100 text-sm">年度回忆</p>
          <h3 class="text-xl font-bold mt-1">{{ currentYear }}年度回忆集锦</h3>
          <p class="text-indigo-100 text-sm mt-2">共 {{ yearlyPictureCount }} 张照片，自动精选代表性瞬间</p>
        </div>
        <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center">
          <span class="text-3xl">🎬</span>
        </div>
      </div>
    </div>

    <!-- 年度回忆集锦播放弹窗 -->
    <div v-if="showYearlyHighlights" class="fixed inset-0 z-50 flex items-center justify-center bg-black/90" @click.self="closeYearlyHighlights">
      <button @click="closeYearlyHighlights" class="absolute top-4 right-4 z-10 p-2 text-white/70 hover:text-white transition">
        <svg class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
      <div class="relative w-full max-w-4xl px-4">
        <div class="text-center mb-6">
          <h2 class="text-3xl font-bold text-white">{{ highlightsYear }}年度回忆</h2>
          <p class="text-white/60 mt-2">{{ yearlyHighlightPictures.length }} 个精选瞬间</p>
        </div>
        <div class="relative aspect-video bg-gray-900 rounded-2xl overflow-hidden">
          <transition name="fade" mode="out-in">
            <img v-if="yearlyHighlightPictures[currentHighlightIndex]"
              :key="currentHighlightIndex"
              :src="yearlyHighlightPictures[currentHighlightIndex].url"
              class="w-full h-full object-contain" />
          </transition>
          <button @click="prevHighlight" class="absolute left-4 top-1/2 -translate-y-1/2 p-3 bg-black/30 hover:bg-black/50 rounded-full text-white transition">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <button @click="nextHighlight" class="absolute right-4 top-1/2 -translate-y-1/2 p-3 bg-black/30 hover:bg-black/50 rounded-full text-white transition">
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex space-x-2">
            <span v-for="(_, idx) in yearlyHighlightPictures" :key="idx"
              @click="currentHighlightIndex = idx"
              :class="['w-2 h-2 rounded-full cursor-pointer transition',
                idx === currentHighlightIndex ? 'bg-white' : 'bg-white/40']">
            </span>
          </div>
        </div>
        <div class="text-center mt-4">
          <p class="text-white font-medium">{{ yearlyHighlightPictures[currentHighlightIndex]?.name || '' }}</p>
          <p class="text-white/50 text-sm">{{ formatTime(yearlyHighlightPictures[currentHighlightIndex]?.createTime) }}</p>
        </div>
        <div class="flex justify-center space-x-4 mt-6">
          <button @click="toggleAutoPlay"
            :class="['px-4 py-2 rounded-lg transition flex items-center space-x-2',
              isAutoPlaying ? 'bg-purple-500 text-white' : 'bg-white/10 text-white hover:bg-white/20']">
            <svg v-if="isAutoPlaying" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 9v6m4-6v6m7-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ isAutoPlaying ? '暂停' : '自动播放' }}</span>
          </button>
          <button @click="shareYearlyHighlights"
            class="px-4 py-2 rounded-lg bg-white/10 text-white hover:bg-white/20 transition flex items-center space-x-2">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
            </svg>
            <span>分享</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 时间筛选器 -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
      <div class="flex flex-wrap items-center gap-3">
        <span class="text-sm text-gray-500 flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <span>时间筛选:</span>
        </span>

        <div class="flex space-x-1">
          <button v-for="opt in quickOptions" :key="opt.value" @click="setQuickFilter(opt.value)"
            :class="['px-3 py-1.5 text-sm rounded-lg transition',
              quickFilter === opt.value
                ? 'bg-blue-500 text-white shadow-sm'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
            {{ opt.label }}
          </button>
        </div>

        <div class="h-6 w-px bg-gray-200"></div>

        <select v-model="filterYear" @change="applyFilter"
          class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
          <option :value="null">全部年份</option>
          <option v-for="y in availableYears" :key="y" :value="y">{{ y }}年</option>
        </select>

        <select v-model="filterMonth" @change="applyFilter"
          class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
          <option :value="null">全部月份</option>
          <option v-for="m in 12" :key="m" :value="m">{{ m }}月</option>
        </select>

        <div class="h-6 w-px bg-gray-200"></div>

        <input type="date" v-model="filterStartDate" @change="applyFilter"
          class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        <span class="text-gray-400 text-sm">至</span>
        <input type="date" v-model="filterEndDate" @change="applyFilter"
          class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />

        <button v-if="hasActiveFilter" @click="clearFilter"
          class="px-3 py-1.5 text-sm text-gray-500 hover:text-red-500 transition">
          清除筛选
        </button>
      </div>
    </div>

    <!-- 时光轴主体 -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="groupedPictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
      <div class="text-6xl mb-4">📅</div>
      <h3 class="text-xl font-medium text-gray-500">暂无图片记录</h3>
      <p class="text-sm text-gray-400 mt-2">上传图片后，时光轴将按日期展示你的回忆</p>
    </div>

    <div v-else class="space-y-8">
      <div v-for="group in groupedPictures" :key="group.dateKey" class="timeline-group relative" :id="'group-' + group.dateKey">
        <!-- 日期标题 -->
        <div class="flex items-center space-x-3 mb-4 sticky top-16 z-10 bg-gradient-to-b from-slate-50 via-slate-50/95 to-transparent pt-2 pb-3">
          <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-500 rounded-xl flex items-center justify-center text-white shadow-md flex-shrink-0 z-10">
            <span class="text-xs font-bold">{{ group.month }}/{{ group.day }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center flex-wrap gap-2">
              <h3 class="font-bold text-gray-800">{{ group.dateTitle }}</h3>
              <span v-if="getAnniversary(group.dateKey)" class="inline-flex items-center px-2 py-0.5 bg-gradient-to-r from-amber-400 to-orange-400 text-white text-xs rounded-full font-medium">
                {{ getAnniversary(group.dateKey).emoji || '🎉' }} {{ getAnniversary(group.dateKey).title }}
              </span>
            </div>
            <p class="text-xs text-gray-400">{{ group.count }} 张照片</p>
          </div>
          <button @click="toggleAddAnniversary(group)"
            class="text-xs text-gray-400 hover:text-amber-500 transition flex items-center space-x-1 flex-shrink-0">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>{{ getAnniversary(group.dateKey) ? '编辑' : '纪念日' }}</span>
          </button>
        </div>

        <!-- 图片网格 -->
        <div class="ml-6 pl-6 border-l-2 border-blue-100">
          <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3">
            <div v-for="pic in group.pictures" :key="pic.id"
              class="group relative bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer"
              @click="viewPicture(pic)">
              <div class="aspect-square bg-gray-100 relative overflow-hidden">
                <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
                <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition duration-300"></div>
              </div>
              <div class="p-2">
                <h4 class="text-xs font-medium text-gray-700 truncate">{{ pic.name }}</h4>
                <p class="text-[10px] text-gray-400">{{ formatTimeOfDay(pic.createTime) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑纪念日弹窗 -->
    <div v-if="showAddAnniversary" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="showAddAnniversary = false">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative z-10">
        <div class="p-6 border-b border-gray-100">
          <h3 class="text-lg font-bold text-gray-800">{{ isEditingAnniversary ? '编辑纪念日' : '添加纪念日' }}</h3>
          <p class="text-sm text-gray-500 mt-1">{{ currentAnniversaryDate }}</p>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">纪念日名称</label>
            <input v-model="anniversaryTitle" type="text" placeholder="例如：生日聚会、毕业旅行"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-amber-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">选择图标</label>
            <div class="flex flex-wrap gap-2">
              <button v-for="emoji in anniversaryEmojis" :key="emoji"
                @click="selectedAnniversaryEmoji = emoji"
                :class="['w-10 h-10 rounded-lg text-xl transition',
                  selectedAnniversaryEmoji === emoji ? 'bg-amber-100 ring-2 ring-amber-400' : 'bg-gray-100 hover:bg-gray-200']">
                {{ emoji }}
              </button>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
          <button v-if="isEditingAnniversary" @click="confirmDeleteAnniversary"
            class="px-4 py-2 text-red-500 hover:bg-red-50 rounded-lg transition text-sm font-medium">
            删除纪念日
          </button>
          <div v-else></div>
          <div class="flex space-x-3">
            <button @click="showAddAnniversary = false"
              class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
            <button @click="saveAnniversary" :disabled="!anniversaryTitle.trim()"
              :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
                anniversaryTitle.trim()
                  ? 'bg-gradient-to-r from-amber-500 to-orange-500 text-white hover:shadow-lg'
                  : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
              保存
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'

const props = defineProps({
  pictures: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view-picture', 'fetch-pictures'])

// 当前年份
const currentYear = new Date().getFullYear()

// 快速筛选选项
const quickOptions = [
  { value: 'week', label: '本周' },
  { value: 'month', label: '本月' },
  { value: 'year', label: '本年' },
  { value: 'all', label: '全部' }
]
const quickFilter = ref('all')

// 筛选条件
const filterYear = ref(null)
const filterMonth = ref(null)
const filterStartDate = ref('')
const filterEndDate = ref('')

// 获取可用年份
const availableYears = computed(() => {
  const years = new Set()
  props.pictures.forEach(pic => {
    const d = new Date(pic.createTime)
    years.add(d.getFullYear())
  })
  return Array.from(years).sort((a, b) => b - a)
})

const hasActiveFilter = computed(() => {
  return quickFilter.value !== 'all' || filterYear.value || filterMonth.value || filterStartDate.value || filterEndDate.value
})

// 筛选后的图片
const filteredPictures = computed(() => {
  let pics = [...props.pictures]

  if (quickFilter.value === 'week') {
    const now = new Date()
    const weekStart = new Date(now)
    weekStart.setDate(now.getDate() - now.getDay())
    weekStart.setHours(0, 0, 0, 0)
    pics = pics.filter(p => new Date(p.createTime) >= weekStart)
  } else if (quickFilter.value === 'month') {
    const now = new Date()
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
    pics = pics.filter(p => new Date(p.createTime) >= monthStart)
  } else if (quickFilter.value === 'year') {
    const yearStart = new Date(currentYear, 0, 1)
    pics = pics.filter(p => new Date(p.createTime) >= yearStart)
  }

  if (filterYear.value) {
    pics = pics.filter(p => new Date(p.createTime).getFullYear() === filterYear.value)
  }
  if (filterMonth.value) {
    pics = pics.filter(p => new Date(p.createTime).getMonth() + 1 === filterMonth.value)
  }

  if (filterStartDate.value) {
    const start = new Date(filterStartDate.value)
    pics = pics.filter(p => new Date(p.createTime) >= start)
  }
  if (filterEndDate.value) {
    const end = new Date(filterEndDate.value)
    end.setHours(23, 59, 59, 999)
    pics = pics.filter(p => new Date(p.createTime) <= end)
  }

  return pics
})

// 按日期分组的图片
const groupedPictures = computed(() => {
  const groups = {}
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

  filteredPictures.value.forEach(pic => {
    const d = new Date(pic.createTime)
    const dateKey = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

    if (!groups[dateKey]) {
      groups[dateKey] = {
        dateKey,
        year: d.getFullYear(),
        month: d.getMonth() + 1,
        day: d.getDate(),
        weekDay: weekDays[d.getDay()],
        dateTitle: `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${weekDays[d.getDay()]}`,
        pictures: [],
        count: 0
      }
    }
    groups[dateKey].pictures.push(pic)
    groups[dateKey].count++
  })

  return Object.values(groups).sort((a, b) => new Date(b.dateKey) - new Date(a.dateKey))
})

// 月度统计
const monthlyStats = computed(() => {
  const stats = []
  const now = new Date()
  const year = filterYear.value || now.getFullYear()

  for (let m = 1; m <= 12; m++) {
    const monthPics = props.pictures.filter(p => {
      const d = new Date(p.createTime)
      return d.getFullYear() === year && d.getMonth() + 1 === m
    })
    stats.push({
      year,
      month: m,
      count: monthPics.length
    })
  }

  const maxCount = Math.max(...stats.map(s => s.count), 1)
  stats.forEach(s => {
    s.height = s.count > 0 ? Math.max((s.count / maxCount) * 100, 5) : 0
    s.isMax = s.count === maxCount && s.count > 0
  })

  return stats
})

// 回忆推送
const memories = computed(() => {
  const today = new Date()
  const todayMonth = today.getMonth()
  const todayDay = today.getDate()
  const result = []

  const picsByYear = {}
  props.pictures.forEach(pic => {
    const d = new Date(pic.createTime)
    const year = d.getFullYear()
    const month = d.getMonth()
    const day = d.getDate()

    if (month === todayMonth && day === todayDay && year < today.getFullYear()) {
      if (!picsByYear[year]) picsByYear[year] = []
      picsByYear[year].push(pic)
    }
  })

  Object.keys(picsByYear).forEach(year => {
    const yearsAgo = today.getFullYear() - parseInt(year)
    if (yearsAgo >= 1 && yearsAgo <= 5) {
      result.push({
        year: parseInt(year),
        yearsAgo,
        dateStr: `${year}年${todayMonth + 1}月${todayDay}日`,
        count: picsByYear[year].length,
        pictures: picsByYear[year]
      })
    }
  })

  return result.sort((a, b) => a.yearsAgo - b.yearsAgo)
})

const expandedMemory = ref(null)

const expandMemory = (memory) => {
  expandedMemory.value = memory
}

// 纪念日
const anniversaries = ref([])
const showAddAnniversary = ref(false)
const currentAnniversaryGroup = ref(null)
const anniversaryTitle = ref('')
const selectedAnniversaryEmoji = ref('🎉')
const anniversaryEmojis = ['🎉', '🎂', '🎓', '✈️', '💝', '🏆', '🌟', '🎄', '🎃', '📸']

const currentAnniversaryDate = computed(() => {
  if (currentAnniversaryGroup.value) {
    return currentAnniversaryGroup.value.dateTitle
  }
  return ''
})

const loadAnniversaries = () => {
  try {
    const saved = localStorage.getItem('picture_anniversaries')
    if (saved) {
      anniversaries.value = JSON.parse(saved)
    }
  } catch (e) {}
}

const saveAnniversariesToStorage = () => {
  try {
    localStorage.setItem('picture_anniversaries', JSON.stringify(anniversaries.value))
  } catch (e) {}
}

const getAnniversary = (dateKey) => {
  return anniversaries.value.find(a => a.date === dateKey)
}

const toggleAddAnniversary = (group) => {
  const existing = getAnniversary(group.dateKey)
  currentAnniversaryGroup.value = group
  if (existing) {
    anniversaryTitle.value = existing.title
    selectedAnniversaryEmoji.value = existing.emoji || '🎉'
    isEditingAnniversary.value = true
  } else {
    anniversaryTitle.value = ''
    selectedAnniversaryEmoji.value = '🎉'
  }
  showAddAnniversary.value = true
}

const isEditingAnniversary = ref(false)

const saveAnniversary = () => {
  if (!currentAnniversaryGroup.value || !anniversaryTitle.value.trim()) return

  const dateKey = currentAnniversaryGroup.value.dateKey
  const existingIdx = anniversaries.value.findIndex(a => a.date === dateKey)

  const newAnn = {
    date: dateKey,
    title: anniversaryTitle.value.trim(),
    emoji: selectedAnniversaryEmoji.value
  }

  if (existingIdx >= 0) {
    anniversaries.value[existingIdx] = newAnn
  } else {
    anniversaries.value.push(newAnn)
  }

  saveAnniversariesToStorage()
  showAddAnniversary.value = false
}

const deleteAnniversary = (dateKey) => {
  anniversaries.value = anniversaries.value.filter(a => a.date !== dateKey)
  saveAnniversariesToStorage()
}

const confirmDeleteAnniversary = () => {
  if (currentAnniversaryGroup.value) {
    const dateKey = currentAnniversaryGroup.value.dateKey
    deleteAnniversary(dateKey)
    showAddAnniversary.value = false
    isEditingAnniversary.value = false
  }
}

// 跳转到指定日期
const jumpToDate = (dateKey) => {
  quickFilter.value = 'all'
  filterYear.value = null
  filterMonth.value = null
  filterStartDate.value = ''
  filterEndDate.value = ''

  setTimeout(() => {
    const el = document.getElementById('group-' + dateKey)
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }, 100)
}

// 跳转到指定月份
const jumpToMonth = (year, month) => {
  filterYear.value = year
  filterMonth.value = month
  quickFilter.value = 'all'
  filterStartDate.value = ''
  filterEndDate.value = ''
}

// 设置快速筛选
const setQuickFilter = (value) => {
  quickFilter.value = value
  if (value === 'all') {
    filterYear.value = null
    filterMonth.value = null
    filterStartDate.value = ''
    filterEndDate.value = ''
  }
}

// 清除筛选
const clearFilter = () => {
  quickFilter.value = 'all'
  filterYear.value = null
  filterMonth.value = null
  filterStartDate.value = ''
  filterEndDate.value = ''
}

const applyFilter = () => {
  // 响应式会自动处理
}

// 查看图片
const viewPicture = (pic) => {
  emit('view-picture', pic)
}

// 格式化日期
const formatShortDate = (dateStr) => {
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const formatTimeOfDay = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 年度回忆集锦
const showYearlyHighlights = ref(false)
const highlightsYear = ref(currentYear)
const currentHighlightIndex = ref(0)
const isAutoPlaying = ref(false)
let autoPlayTimer = null

const yearlyPictureCount = computed(() => {
  return props.pictures.filter(p => new Date(p.createTime).getFullYear() === currentYear).length
})

const yearlyHighlightPictures = computed(() => {
  const yearPics = props.pictures.filter(p => new Date(p.createTime).getFullYear() === highlightsYear.value)
  
  const sorted = [...yearPics].sort((a, b) => {
    const scoreA = (a.likeCount || 0) * 3 + (a.commentCount || 0) * 2 + (a.viewCount || 0)
    const scoreB = (b.likeCount || 0) * 3 + (b.commentCount || 0) * 2 + (b.viewCount || 0)
    return scoreB - scoreA
  })

  return sorted.slice(0, Math.min(20, sorted.length))
})

const openYearlyHighlights = () => {
  highlightsYear.value = currentYear
  currentHighlightIndex.value = 0
  showYearlyHighlights.value = true
}

const closeYearlyHighlights = () => {
  showYearlyHighlights.value = false
  stopAutoPlay()
}

const nextHighlight = () => {
  if (yearlyHighlightPictures.value.length === 0) return
  currentHighlightIndex.value = (currentHighlightIndex.value + 1) % yearlyHighlightPictures.value.length
}

const prevHighlight = () => {
  if (yearlyHighlightPictures.value.length === 0) return
  currentHighlightIndex.value = (currentHighlightIndex.value - 1 + yearlyHighlightPictures.value.length) % yearlyHighlightPictures.value.length
}

const toggleAutoPlay = () => {
  if (isAutoPlaying.value) {
    stopAutoPlay()
  } else {
    startAutoPlay()
  }
}

const startAutoPlay = () => {
  isAutoPlaying.value = true
  autoPlayTimer = setInterval(() => {
    nextHighlight()
  }, 3000)
}

const stopAutoPlay = () => {
  isAutoPlaying.value = false
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer)
    autoPlayTimer = null
  }
}

const shareYearlyHighlights = async () => {
  const text = `📸 我的${highlightsYear.value}年度回忆集锦\n共 ${yearlyHighlightPictures.value.length} 个精选瞬间，快来看看吧！`
  try {
    if (navigator.share) {
      await navigator.share({
        title: `${highlightsYear.value}年度回忆集锦`,
        text: text
      })
    } else {
      await navigator.clipboard.writeText(text)
      alert('年度回忆信息已复制到剪贴板')
    }
  } catch (e) {
    try {
      await navigator.clipboard.writeText(text)
      alert('年度回忆信息已复制到剪贴板')
    } catch (e2) {}
  }
}

onMounted(() => {
  loadAnniversaries()
})

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
