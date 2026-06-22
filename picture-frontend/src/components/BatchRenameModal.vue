<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm" @click.self="handleClose">
    <div class="bg-white rounded-2xl shadow-2xl w-[900px] max-h-[85vh] flex flex-col overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between bg-gradient-to-r from-blue-500 to-purple-600 text-white">
        <div class="flex items-center space-x-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          <h3 class="text-lg font-bold">批量重命名</h3>
          <span class="text-sm text-blue-100">(已选 {{ pictureIds.length }} 张)</span>
        </div>
        <button @click="handleClose" class="text-white/80 hover:text-white transition">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="flex-1 flex overflow-hidden">
        <div class="w-1/2 border-r border-gray-100 p-4 overflow-y-auto">
          <div class="space-y-5">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">命名模板</label>
              <div class="grid grid-cols-2 gap-2">
                <button v-for="tpl in templates" :key="tpl.value" @click="selectTemplate(tpl.value)"
                  :class="['px-3 py-2 text-sm rounded-lg border transition',
                    template === tpl.value
                      ? 'border-blue-500 bg-blue-50 text-blue-700'
                      : 'border-gray-200 hover:border-gray-300 text-gray-600']">
                  {{ tpl.label }}
                </button>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                命名规则
                <span class="text-xs text-gray-400 font-normal ml-1">可用变量: {date} {album} {tag} {index} {原名} {custom}</span>
              </label>
              <input v-model="template" type="text" @input="debouncedPreview"
                class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="例如: 旅行照片_{index}" />
            </div>

            <div v-if="template.includes('{custom}')">
              <label class="block text-sm font-medium text-gray-700 mb-2">自定义文本</label>
              <input v-model="customText" type="text" @input="debouncedPreview"
                class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="输入自定义文本" />
            </div>

            <div class="flex space-x-4">
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-2">序号起始值</label>
                <input v-model.number="indexStart" type="number" min="0" @input="debouncedPreview"
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-2">序号位数</label>
                <select v-model.number="indexDigits" @change="debouncedPreview"
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                  <option :value="1">1位 (1, 2, 3...)</option>
                  <option :value="2">2位 (01, 02...)</option>
                  <option :value="3">3位 (001, 002...)</option>
                  <option :value="4">4位 (0001...)</option>
                </select>
              </div>
            </div>

            <div class="border-t border-gray-100 pt-4">
              <div class="flex items-center justify-between mb-3">
                <label class="text-sm font-medium text-gray-700">查找替换</label>
                <label class="flex items-center space-x-1 text-xs text-gray-500 cursor-pointer">
                  <input type="checkbox" v-model="useRegex" @change="debouncedPreview" class="w-3.5 h-3.5 text-blue-600 rounded" />
                  <span>正则表达式</span>
                </label>
              </div>
              <div class="flex space-x-2">
                <input v-model="findText" type="text" placeholder="查找内容" @input="debouncedPreview"
                  class="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input v-model="replaceText" type="text" placeholder="替换为" @input="debouncedPreview"
                  class="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>

            <div class="border-t border-gray-100 pt-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">大小写转换</label>
              <div class="flex flex-wrap gap-2">
                <button v-for="opt in caseOptions" :key="opt.value" @click="caseTransform = opt.value; debouncedPreview()"
                  :class="['px-3 py-1.5 text-xs rounded-full border transition',
                    caseTransform === opt.value
                      ? 'border-blue-500 bg-blue-50 text-blue-700'
                      : 'border-gray-200 hover:border-gray-300 text-gray-600']">
                  {{ opt.label }}
                </button>
              </div>
            </div>

            <div class="border-t border-gray-100 pt-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">快速变量</label>
              <div class="flex flex-wrap gap-2">
                <button v-for="v in quickVars" :key="v" @click="insertVariable(v)"
                  class="px-2 py-1 text-xs bg-gray-100 hover:bg-gray-200 rounded text-gray-600 transition">
                  {{ v }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="w-1/2 flex flex-col">
          <div class="px-4 py-3 border-b border-gray-100 bg-gray-50">
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-700">预览效果</span>
              <div class="flex items-center space-x-2 text-xs text-gray-500">
                <span v-if="conflictCount > 0" class="text-red-500">
                  <svg class="w-3.5 h-3.5 inline mr-0.5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
                  </svg>
                  {{ conflictCount }} 个重名冲突
                </span>
                <span v-if="loading" class="text-blue-500">更新中...</span>
              </div>
            </div>
          </div>
          <div class="flex-1 overflow-y-auto p-2">
            <div v-if="loading && previewItems.length === 0" class="flex items-center justify-center h-40">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-500"></div>
            </div>
            <div v-else-if="previewItems.length === 0" class="text-center py-10 text-gray-400 text-sm">
              暂无预览数据
            </div>
            <div v-else class="space-y-1">
              <div v-for="item in previewItems" :key="item.pictureId"
                :class="['p-2 rounded-lg text-sm', item.hasConflict ? 'bg-red-50' : 'bg-gray-50']">
                <div class="flex items-center justify-between">
                  <span class="text-gray-500 truncate flex-1" :title="item.oldName">{{ item.oldName }}</span>
                  <svg class="w-4 h-4 text-gray-400 mx-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                  </svg>
                  <span :class="['truncate flex-1 font-medium', item.hasConflict ? 'text-red-600' : 'text-gray-800']" :title="item.newName">
                    {{ item.newName }}
                  </span>
                </div>
                <div v-if="item.hasConflict && item.conflictReason" class="text-xs text-red-500 mt-1">
                  ⚠️ {{ item.conflictReason }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="px-6 py-4 border-t border-gray-100 bg-gray-50 flex items-center justify-between">
        <div>
          <div v-if="lastUndoToken" class="flex items-center space-x-2">
            <button @click="handleUndo" :disabled="undoLoading"
              class="px-3 py-1.5 text-sm text-amber-600 hover:text-amber-700 hover:bg-amber-50 rounded-lg transition flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
              </svg>
              <span>撤销上次重命名</span>
            </button>
            <span class="text-xs text-gray-400">(24小时内有效)</span>
          </div>
        </div>
        <div class="flex items-center space-x-3">
          <button @click="handleClose" class="px-4 py-2 text-gray-600 hover:text-gray-800 text-sm font-medium transition">
            取消
          </button>
          <button @click="handleSubmit" :disabled="loading || conflictCount > 0 || previewItems.length === 0"
            :class="['px-6 py-2 rounded-lg text-sm font-medium transition flex items-center space-x-1',
              loading || conflictCount > 0 || previewItems.length === 0
                ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                : 'bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg']">
            <svg v-if="loading" class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ loading ? '处理中...' : '开始重命名' }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  visible: Boolean,
  pictureIds: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['close', 'success', 'undo'])

const template = ref('{custom}_{index}')
const customText = ref('图片')
const indexStart = ref(1)
const indexDigits = ref(3)
const findText = ref('')
const replaceText = ref('')
const useRegex = ref(false)
const caseTransform = ref('')
const previewItems = ref([])
const loading = ref(false)
const undoLoading = ref(false)
const lastUndoToken = ref('')

const templates = [
  { label: '固定前缀 + 序号', value: '{custom}_{index}' },
  { label: '日期 + 序号', value: '{date}_{index}' },
  { label: '专辑名 + 序号', value: '{album}_{index}' },
  { label: '标签 + 序号', value: '{tag}_{index}' },
  { label: '原名 + 序号', value: '{原名}_{index}' },
  { label: '仅序号', value: '{index}' },
]

const caseOptions = [
  { label: '不转换', value: '' },
  { label: '全部大写', value: 'upper' },
  { label: '全部小写', value: 'lower' },
  { label: '首字母大写', value: 'capitalize' },
  { label: '驼峰命名', value: 'camel' },
]

const quickVars = ['{date}', '{album}', '{tag}', '{index}', '{原名}', '{custom}']

const conflictCount = computed(() => {
  return previewItems.value.filter(item => item.hasConflict).length
})

let previewTimer = null

const debouncedPreview = () => {
  if (previewTimer) clearTimeout(previewTimer)
  previewTimer = setTimeout(() => {
    fetchPreview()
  }, 300)
}

const fetchPreview = async () => {
  if (props.pictureIds.length === 0) {
    previewItems.value = []
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/pictures/rename/preview', {
      pictureIds: props.pictureIds,
      template: template.value,
      customText: customText.value,
      indexStart: indexStart.value,
      indexDigits: indexDigits.value,
      findText: findText.value,
      replaceText: replaceText.value,
      useRegex: useRegex.value,
      caseTransform: caseTransform.value,
    })
    if (res.data.code === 0) {
      previewItems.value = res.data.data
    }
  } catch (e) {
    console.error('预览失败', e)
  } finally {
    loading.value = false
  }
}

const selectTemplate = (tpl) => {
  template.value = tpl
  debouncedPreview()
}

const insertVariable = (v) => {
  template.value += v
  debouncedPreview()
}

const handleSubmit = async () => {
  if (conflictCount.value > 0 || loading.value) return

  loading.value = true
  try {
    const res = await axios.post('/api/pictures/batch-rename', {
      pictureIds: props.pictureIds,
      template: template.value,
      customText: customText.value,
      indexStart: indexStart.value,
      indexDigits: indexDigits.value,
      findText: findText.value,
      replaceText: replaceText.value,
      useRegex: useRegex.value,
      caseTransform: caseTransform.value,
    })
    if (res.data.code === 0) {
      lastUndoToken.value = res.data.data.undoToken
      emit('success', res.data.data)
    } else {
      alert(res.data.message || '重命名失败')
    }
  } catch (e) {
    alert('重命名失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleUndo = async () => {
  if (!lastUndoToken.value || undoLoading.value) return

  undoLoading.value = true
  try {
    const res = await axios.post('/api/pictures/rename/undo', null, {
      params: { undoToken: lastUndoToken.value }
    })
    if (res.data.code === 0) {
      lastUndoToken.value = ''
      emit('undo', res.data.data)
      fetchPreview()
    } else {
      alert(res.data.message || '撤销失败')
    }
  } catch (e) {
    alert('撤销失败: ' + (e.response?.data?.message || e.message))
  } finally {
    undoLoading.value = false
  }
}

const handleClose = () => {
  emit('close')
}

watch(() => props.visible, (val) => {
  if (val && props.pictureIds.length > 0) {
    fetchPreview()
  }
})

watch(() => props.pictureIds, () => {
  if (props.visible) {
    fetchPreview()
  }
}, { deep: true })
</script>
