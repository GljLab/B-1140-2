<template>
  <div class="picture-note-editor h-full flex flex-col">
    <div class="flex items-center justify-between mb-4 flex-shrink-0">
      <div class="flex items-center space-x-2">
        <h3 class="text-lg font-bold text-gray-800">📝 图片笔记</h3>
        <span v-if="saveStatus" :class="['text-xs px-2 py-0.5 rounded-full', saveStatus === '已保存' ? 'bg-green-100 text-green-600' : 'bg-yellow-100 text-yellow-600']">{{ saveStatus }}</span>
      </div>
      <div class="flex items-center space-x-2">
        <button @click="showTemplatePicker = true"
          class="px-3 py-1.5 bg-purple-50 text-purple-600 rounded-lg hover:bg-purple-100 transition text-xs font-medium flex items-center space-x-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <span>模板</span>
        </button>
        <button @click="exportNote"
          class="px-3 py-1.5 bg-emerald-50 text-emerald-600 rounded-lg hover:bg-emerald-100 transition text-xs font-medium flex items-center space-x-1">
          <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          <span>导出</span>
        </button>
      </div>
    </div>

    <div class="mb-3">
      <input v-model="noteTitle" type="text" placeholder="给笔记起个标题..."
        @input="autoSave"
        class="w-full px-4 py-2 border border-gray-200 rounded-lg text-base font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
    </div>

    <div class="flex items-center space-x-1 mb-2 pb-2 border-b border-gray-200 flex-wrap gap-1">
      <button @click="formatText('h1')"
        :class="['px-2 py-1 rounded text-xs font-medium transition', activeFormat === 'h1' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="标题1">
        H1
      </button>
      <button @click="formatText('h2')"
        :class="['px-2 py-1 rounded text-xs font-medium transition', activeFormat === 'h2' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="标题2">
        H2
      </button>
      <button @click="formatText('h3')"
        :class="['px-2 py-1 rounded text-xs font-medium transition', activeFormat === 'h3' ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="标题3">
        H3
      </button>
      <div class="w-px h-5 bg-gray-200 mx-1"></div>
      <button @click="formatText('bold')"
        :class="['p-1.5 rounded transition', boldActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="粗体">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
          <path d="M15.6 10.79c.97-.67 1.65-1.77 1.65-2.79 0-2.26-1.75-4-4-4H7v14h7.04c2.09 0 3.71-1.7 3.71-3.79 0-1.52-.86-2.82-2.15-3.42zM10 6.5h3c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5h-3v-3zm3.5 9H10v-3h3.5c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5z"/>
        </svg>
      </button>
      <button @click="formatText('italic')"
        :class="['p-1.5 rounded transition', italicActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="斜体">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
          <path d="M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"/>
        </svg>
      </button>
      <div class="w-px h-5 bg-gray-200 mx-1"></div>
      <button @click="formatText('ul')"
        :class="['p-1.5 rounded transition', ulActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="无序列表">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
      <button @click="formatText('ol')"
        :class="['p-1.5 rounded transition', olActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="有序列表">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M7 20l4-16m2 16l4-16M6 9h14M4 15h14" />
        </svg>
      </button>
      <div class="w-px h-5 bg-gray-200 mx-1"></div>
      <button @click="formatText('quote')"
        :class="['p-1.5 rounded transition', quoteActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="引用">
        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
          <path d="M6 17h3l2-4V7H5v6h3zm8 0h3l2-4V7h-6v6h3z"/>
        </svg>
      </button>
      <button @click="formatText('code')"
        :class="['p-1.5 rounded transition', codeActive ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']"
        title="代码块">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
        </svg>
      </button>
      <div class="w-px h-5 bg-gray-200 mx-1"></div>
      <button @click="insertLink"
        class="p-1.5 rounded text-gray-600 hover:bg-gray-100 transition"
        title="插入链接">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
        </svg>
      </button>
      <button @click="showPicturePicker = true"
        class="p-1.5 rounded text-gray-600 hover:bg-gray-100 transition"
        title="关联图片">
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </button>
    </div>

    <div class="flex-1 min-h-0 flex flex-col">
      <textarea ref="editorRef" v-model="noteContent"
        @input="handleContentChange"
        @select="updateFormatState"
        @click="updateFormatState"
        @keyup="updateFormatState"
        @keydown="handleKeydown"
        placeholder="开始记录你的图文日记...

支持Markdown语法：
# 标题
**粗体** *斜体*
- 列表项
> 引用
`代码`"
        class="flex-1 w-full px-4 py-3 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition resize-none font-mono leading-relaxed"></textarea>
    </div>

    <div v-if="references && references.length > 0" class="mt-3 flex-shrink-0">
      <div class="flex items-center justify-between mb-2">
        <span class="text-xs font-medium text-gray-600">📎 关联图片 ({{ references.length }})</span>
      </div>
      <div class="flex space-x-2 overflow-x-auto pb-1">
        <div v-for="ref in references" :key="ref.id"
          @click="navigateToPicture(ref.pictureId)"
          class="flex-shrink-0 w-16 h-16 bg-gray-100 rounded-lg overflow-hidden cursor-pointer hover:ring-2 hover:ring-blue-400 transition relative group">
          <img :src="ref.pictureUrl" class="w-full h-full object-cover" />
          <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
            <span class="text-white text-[10px]">查看</span>
          </div>
          <button @click.stop="removeReference(ref.id)"
            class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition">
            ×
          </button>
        </div>
      </div>
    </div>

    <div v-if="showTemplatePicker" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="showTemplatePicker = false">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[80vh] overflow-hidden relative z-10">
        <div class="p-4 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">选择笔记模板</h3>
          <button @click="showTemplatePicker = false" class="p-2 hover:bg-gray-100 rounded-lg transition">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-4 overflow-y-auto max-h-[60vh]">
          <div class="space-y-3">
            <div v-for="tpl in templates" :key="tpl.id"
              @click="applyTemplate(tpl)"
              class="p-4 border border-gray-200 rounded-xl cursor-pointer hover:border-blue-400 hover:bg-blue-50/50 transition group">
              <div class="flex items-center justify-between mb-2">
                <div class="flex items-center space-x-2">
                  <span class="text-lg">{{ getTemplateIcon(tpl.category) }}</span>
                  <span class="font-medium text-gray-800">{{ tpl.name }}</span>
                  <span v-if="tpl.isSystem" class="text-[10px] px-1.5 py-0.5 bg-purple-100 text-purple-600 rounded-full">系统</span>
                </div>
                <button class="px-3 py-1 bg-blue-500 text-white rounded-lg text-xs font-medium opacity-0 group-hover:opacity-100 transition">
                  使用
                </button>
              </div>
              <p v-if="tpl.description" class="text-xs text-gray-500 mb-2">{{ tpl.description }}</p>
              <div class="text-xs text-gray-400 line-clamp-2 bg-gray-50 p-2 rounded">
                {{ tpl.content?.substring(0, 100) }}...
              </div>
            </div>
          </div>
        </div>
        <div class="p-4 border-t border-gray-100 bg-gray-50">
          <button @click="saveAsTemplate"
            class="w-full py-2 border-2 border-dashed border-gray-300 rounded-xl text-gray-500 hover:border-blue-400 hover:text-blue-500 transition text-sm font-medium">
            + 保存当前内容为模板
          </button>
        </div>
      </div>
    </div>

    <div v-if="showPicturePicker" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="showPicturePicker = false">
      <div class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[80vh] overflow-hidden relative z-10">
        <div class="p-4 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">选择关联图片</h3>
          <button @click="showPicturePicker = false" class="p-2 hover:bg-gray-100 rounded-lg transition">
            <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-4 border-b border-gray-100">
          <input v-model="pickerSearch" type="text" placeholder="搜索图片..."
            @input="searchPictures"
            class="w-full px-4 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div class="p-4 overflow-y-auto max-h-[50vh]">
          <div v-if="pickerLoading" class="flex justify-center py-10">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>
          <div v-else-if="pickerPictures.length === 0" class="text-center py-10">
            <div class="text-4xl mb-2">🖼️</div>
            <p class="text-gray-400 text-sm">暂无图片</p>
          </div>
          <div v-else class="grid grid-cols-4 sm:grid-cols-5 gap-3">
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
          <button @click="confirmAddReferences"
            :disabled="selectedPictureIds.length === 0"
            class="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed">
            添加关联
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const props = defineProps({
  pictureId: {
    type: Number,
    required: true
  },
  pictureUrl: {
    type: String,
    default: ''
  },
  pictureName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['saved', 'navigate-to-picture'])

const editorRef = ref(null)
const noteTitle = ref('')
const noteContent = ref('')
const saveStatus = ref('')
let autoSaveTimer = null

const currentNote = ref(null)
const references = ref([])

const templates = ref([])
const showTemplatePicker = ref(false)

const showPicturePicker = ref(false)
const pickerPictures = ref([])
const pickerLoading = ref(false)
const pickerSearch = ref('')
const selectedPictureIds = ref([])

const boldActive = ref(false)
const italicActive = ref(false)
const quoteActive = ref(false)
const codeActive = ref(false)
const ulActive = ref(false)
const olActive = ref(false)
const activeFormat = ref('')

const loadNote = async () => {
  try {
    const res = await api.get(`/notes/picture/${props.pictureId}`)
    if (res.data.success && res.data.data) {
      currentNote.value = res.data.data
      noteTitle.value = res.data.data.title || ''
      noteContent.value = res.data.data.content || ''
      references.value = res.data.data.references || []
    }
  } catch (e) {
    console.error('Failed to load note:', e)
  }
}

const saveNote = async () => {
  try {
    const res = await api.put(`/notes/picture/${props.pictureId}`, {
      title: noteTitle.value,
      content: noteContent.value,
      isDraft: true
    })
    if (res.data.success) {
      currentNote.value = res.data.data
      references.value = res.data.data.references || []
      saveStatus.value = '已保存'
      emit('saved', res.data.data)
      setTimeout(() => { saveStatus.value = '' }, 2000)
    }
  } catch (e) {
    saveStatus.value = '保存失败'
    console.error('Failed to save note:', e)
  }
}

const autoSave = () => {
  saveStatus.value = '保存中...'
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(async () => {
    await saveNote()
  }, 800)
}

const handleContentChange = () => {
  updateFormatState()
  autoSave()
}

const updateFormatState = () => {
  if (!editorRef.value) return
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const text = noteContent.value
  
  const lineStart = text.lastIndexOf('\n', start - 1) + 1
  const currentLine = text.substring(lineStart, text.indexOf('\n', start) === -1 ? text.length : text.indexOf('\n', start))
  
  boldActive.value = /\*\*.+\*\*/.test(currentLine)
  italicActive.value = /\*.+\*/.test(currentLine)
  quoteActive.value = currentLine.startsWith('> ')
  codeActive.value = currentLine.startsWith('```') || currentLine.startsWith('`')
  ulActive.value = /^[-*+] /.test(currentLine)
  olActive.value = /^\d+\. /.test(currentLine)
  
  if (currentLine.startsWith('# ')) activeFormat.value = 'h1'
  else if (currentLine.startsWith('## ')) activeFormat.value = 'h2'
  else if (currentLine.startsWith('### ')) activeFormat.value = 'h3'
  else activeFormat.value = ''
}

const formatText = (format) => {
  if (!editorRef.value) return
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = noteContent.value
  const selectedText = text.substring(start, end)

  let newText = ''
  let cursorOffset = 0

  const lineStart = text.lastIndexOf('\n', start - 1) + 1
  const lineEnd = text.indexOf('\n', start) === -1 ? text.length : text.indexOf('\n', start)
  const currentLine = text.substring(lineStart, lineEnd)

  switch (format) {
    case 'h1':
      if (currentLine.startsWith('# ')) {
        newText = text.substring(0, lineStart) + currentLine.substring(2) + text.substring(lineEnd)
        cursorOffset = -2
      } else if (currentLine.startsWith('## ')) {
        newText = text.substring(0, lineStart) + '# ' + currentLine.substring(3) + text.substring(lineEnd)
        cursorOffset = -1
      } else if (currentLine.startsWith('### ')) {
        newText = text.substring(0, lineStart) + '# ' + currentLine.substring(4) + text.substring(lineEnd)
        cursorOffset = -2
      } else {
        newText = text.substring(0, lineStart) + '# ' + currentLine + text.substring(lineEnd)
        cursorOffset = 2
      }
      break

    case 'h2':
      if (currentLine.startsWith('## ')) {
        newText = text.substring(0, lineStart) + currentLine.substring(3) + text.substring(lineEnd)
        cursorOffset = -3
      } else if (currentLine.startsWith('# ')) {
        newText = text.substring(0, lineStart) + '## ' + currentLine.substring(2) + text.substring(lineEnd)
        cursorOffset = 1
      } else if (currentLine.startsWith('### ')) {
        newText = text.substring(0, lineStart) + '## ' + currentLine.substring(4) + text.substring(lineEnd)
        cursorOffset = -1
      } else {
        newText = text.substring(0, lineStart) + '## ' + currentLine + text.substring(lineEnd)
        cursorOffset = 3
      }
      break

    case 'h3':
      if (currentLine.startsWith('### ')) {
        newText = text.substring(0, lineStart) + currentLine.substring(4) + text.substring(lineEnd)
        cursorOffset = -4
      } else if (currentLine.startsWith('# ')) {
        newText = text.substring(0, lineStart) + '### ' + currentLine.substring(2) + text.substring(lineEnd)
        cursorOffset = 2
      } else if (currentLine.startsWith('## ')) {
        newText = text.substring(0, lineStart) + '### ' + currentLine.substring(3) + text.substring(lineEnd)
        cursorOffset = 1
      } else {
        newText = text.substring(0, lineStart) + '### ' + currentLine + text.substring(lineEnd)
        cursorOffset = 4
      }
      break

    case 'bold':
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
      break

    case 'italic':
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
      break

    case 'ul':
      if (/^[-*+] /.test(currentLine)) {
        newText = text.substring(0, lineStart) + currentLine.replace(/^[-*+] /, '') + text.substring(lineEnd)
        cursorOffset = -2
      } else {
        newText = text.substring(0, lineStart) + '- ' + currentLine + text.substring(lineEnd)
        cursorOffset = 2
      }
      break

    case 'ol':
      if (/^\d+\. /.test(currentLine)) {
        newText = text.substring(0, lineStart) + currentLine.replace(/^\d+\. /, '') + text.substring(lineEnd)
        cursorOffset = -3
      } else {
        newText = text.substring(0, lineStart) + '1. ' + currentLine + text.substring(lineEnd)
        cursorOffset = 3
      }
      break

    case 'quote':
      if (currentLine.startsWith('> ')) {
        newText = text.substring(0, lineStart) + currentLine.substring(2) + text.substring(lineEnd)
        cursorOffset = -2
      } else {
        newText = text.substring(0, lineStart) + '> ' + currentLine + text.substring(lineEnd)
        cursorOffset = 2
      }
      break

    case 'code':
      if (selectedText) {
        if (text.charAt(start - 1) === '`' && text.charAt(end) === '`') {
          newText = text.substring(0, start - 1) + selectedText + text.substring(end + 1)
          cursorOffset = -1
        } else {
          newText = text.substring(0, start) + '`' + selectedText + '`' + text.substring(end)
          cursorOffset = 1
        }
      } else {
        newText = text.substring(0, start) + '`代码`' + text.substring(end)
        cursorOffset = 1
      }
      break
  }

  noteContent.value = newText
  autoSave()

  nextTick(() => {
    textarea.focus()
    if (selectedText && ['bold', 'italic', 'code'].includes(format)) {
      textarea.setSelectionRange(start + cursorOffset, end + cursorOffset)
    } else {
      textarea.setSelectionRange(start + cursorOffset, start + cursorOffset)
    }
    updateFormatState()
  })
}

const insertLink = () => {
  const url = prompt('请输入链接地址:', 'https://')
  if (!url) return
  const text = prompt('请输入链接文字:', '链接文字') || url
  
  if (!editorRef.value) return
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const oldText = noteContent.value
  
  const linkText = `[${text}](${url})`
  noteContent.value = oldText.substring(0, start) + linkText + oldText.substring(start)
  autoSave()
  
  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start + linkText.length, start + linkText.length)
  })
}

const handleKeydown = (e) => {
  if (e.key === 'Tab') {
    e.preventDefault()
    if (!editorRef.value) return
    const textarea = editorRef.value
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const oldText = noteContent.value
    
    noteContent.value = oldText.substring(0, start) + '  ' + oldText.substring(end)
    
    nextTick(() => {
      textarea.setSelectionRange(start + 2, end + 2)
      autoSave()
    })
  }
}

const loadTemplates = async () => {
  try {
    const res = await api.get('/notes/templates')
    if (res.data.success) {
      templates.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to load templates:', e)
  }
}

const getTemplateIcon = (category) => {
  const icons = {
    shooting: '📷',
    postprocessing: '🎨',
    travel: '✈️',
    food: '🍜',
    custom: '📝'
  }
  return icons[category] || '📝'
}

const applyTemplate = (tpl) => {
  if (noteContent.value && !confirm('应用模板将替换当前内容，确定继续吗？')) {
    return
  }
  noteTitle.value = tpl.name
  noteContent.value = tpl.content || ''
  showTemplatePicker.value = false
  autoSave()
}

const saveAsTemplate = async () => {
  const name = prompt('请输入模板名称:', '我的模板')
  if (!name) return
  
  try {
    const res = await api.post('/notes/templates', {
      name: name,
      content: noteContent.value,
      category: 'custom'
    })
    if (res.data.success) {
      templates.value.push(res.data.data)
      showToast('模板保存成功', 'success')
    }
  } catch (e) {
    console.error('Failed to save template:', e)
    showToast('保存模板失败', 'error')
  }
}

const searchPictures = async () => {
  pickerLoading.value = true
  try {
    const res = await api.get('/list')
    if (res.data.success) {
      let list = res.data.data.filter(p => p.id !== props.pictureId)
      if (pickerSearch.value.trim()) {
        const keyword = pickerSearch.value.trim().toLowerCase()
        list = list.filter(p => p.name && p.name.toLowerCase().includes(keyword))
      }
      pickerPictures.value = list
    }
  } catch (e) {
    console.error('Failed to search pictures:', e)
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

const confirmAddReferences = async () => {
  if (!currentNote.value) {
    await saveNote()
  }
  if (!currentNote.value) return

  for (const picId of selectedPictureIds.value) {
    try {
      await api.post(`/notes/${currentNote.value.id}/references/${picId}`)
    } catch (e) {
      console.error('Failed to add reference:', e)
    }
  }

  try {
    const res = await api.get(`/notes/${currentNote.value.id}/references`)
    if (res.data.success) {
      references.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to load references:', e)
  }

  selectedPictureIds.value = []
  showPicturePicker.value = false
  showToast(`已添加 ${selectedPictureIds.value.length} 张关联图片`)
}

const removeReference = async (refId) => {
  if (!confirm('确定移除这张关联图片吗？')) return
  try {
    await api.delete(`/notes/references/${refId}`)
    references.value = references.value.filter(r => r.id !== refId)
  } catch (e) {
    console.error('Failed to remove reference:', e)
  }
}

const navigateToPicture = (pictureId) => {
  emit('navigate-to-picture', pictureId)
}

const exportNote = async () => {
  if (!currentNote.value) {
    showToast('请先保存笔记', 'warning')
    return
  }
  try {
    const res = await api.get(`/notes/${currentNote.value.id}/export`, {
      params: { includeImages: true },
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `note_${props.pictureId}.md`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    showToast('导出成功')
  } catch (e) {
    console.error('Failed to export note:', e)
    showToast('导出失败', 'error')
  }
}

const showToast = (message, type = 'success') => {
  const toast = document.createElement('div')
  toast.className = `fixed top-4 right-4 z-[100] px-4 py-2 rounded-lg shadow-lg text-sm font-medium transition-all transform translate-x-full ${
    type === 'error' ? 'bg-red-500 text-white' :
    type === 'warning' ? 'bg-yellow-500 text-white' :
    'bg-green-500 text-white'
  }`
  toast.textContent = message
  document.body.appendChild(toast)
  setTimeout(() => { toast.classList.remove('translate-x-full') }, 10)
  setTimeout(() => {
    toast.classList.add('translate-x-full')
    setTimeout(() => toast.remove(), 300)
  }, 2000)
}

watch(() => props.pictureId, () => {
  if (props.pictureId) {
    loadNote()
    loadTemplates()
  }
})

onMounted(() => {
  if (props.pictureId) {
    loadNote()
    loadTemplates()
  }
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
