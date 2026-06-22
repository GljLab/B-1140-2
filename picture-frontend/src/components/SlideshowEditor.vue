<template>
  <div class="fixed inset-0 z-50 bg-gray-50 flex flex-col">
    <header class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center space-x-4">
        <button @click="$emit('back')" class="p-2 hover:bg-gray-100 rounded-lg transition">
          <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div>
          <h2 class="text-lg font-bold text-gray-800">{{ editingSlideshow ? '编辑幻灯片' : '制作幻灯片' }}</h2>
          <p class="text-xs text-gray-400">{{ saveStatus }}</p>
        </div>
      </div>
      <div class="flex items-center space-x-3">
        <button @click="$emit('play', slideshowData)"
          :disabled="slideshowItems.length === 0"
          class="px-4 py-2 bg-gradient-to-r from-emerald-500 to-teal-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-1">
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
          <span>预览播放</span>
        </button>
        <button @click="showExportModal = true"
          :disabled="slideshowItems.length === 0"
          class="px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
          <span>导出视频</span>
        </button>
        <button @click="showShareModal = true"
          :disabled="!editingSlideshow"
          class="px-4 py-2 bg-gradient-to-r from-orange-500 to-pink-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z"/></svg>
          <span>分享</span>
        </button>
        <button @click="saveSlideshow" :disabled="saving"
          class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg shadow-lg hover:shadow-xl transition text-sm font-medium disabled:opacity-50 flex items-center space-x-1">
          <svg v-if="saving" class="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path></svg>
          <span>{{ saving ? '保存中...' : '保存' }}</span>
        </button>
      </div>
    </header>

    <div class="flex-1 flex overflow-hidden">
      <div class="w-80 flex-shrink-0 border-r border-gray-200 bg-white overflow-y-auto">
        <div class="p-4 space-y-4">
          <div class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-2xl p-4 border border-blue-100">
            <h3 class="text-sm font-semibold text-blue-700 mb-3 flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
              <span>基本信息</span>
            </h3>
            <div class="space-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">幻灯片标题</label>
                <input v-model="form.title" type="text" placeholder="输入标题..."
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">描述</label>
                <textarea v-model="form.description" rows="2" placeholder="简单描述..."
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"></textarea>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">标题页文字</label>
                <input v-model="form.titlePageText" type="text" placeholder="幻灯片开头显示的标题..."
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-emerald-50 to-teal-50 rounded-2xl p-4 border border-emerald-100">
            <h3 class="text-sm font-semibold text-emerald-700 mb-3 flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
              <span>播放设置</span>
            </h3>
            <div class="space-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">切换间隔（秒）</label>
                <div class="flex items-center space-x-2">
                  <select v-model="form.interval"
                    class="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500">
                    <option :value="2">2秒</option>
                    <option :value="5">5秒</option>
                    <option :value="10">10秒</option>
                    <option :value="0">自定义</option>
                  </select>
                </div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">切换效果</label>
                <div class="grid grid-cols-2 gap-2">
                  <button v-for="t in transitionOptions" :key="t.value"
                    @click="form.transition = t.value"
                    :class="['px-2 py-1.5 rounded-lg text-xs font-medium transition border',
                      form.transition === t.value ? 'bg-emerald-500 text-white border-emerald-500' : 'bg-white text-gray-600 border-gray-200 hover:border-emerald-300']">
                    {{ t.label }}
                  </button>
                </div>
              </div>
              <div class="flex items-center justify-between">
                <label class="text-xs text-gray-500">循环播放</label>
                <button @click="form.loop = !form.loop"
                  :class="['relative inline-flex h-5 w-9 items-center rounded-full transition-colors', form.loop ? 'bg-emerald-500' : 'bg-gray-300']">
                  <span :class="['inline-block h-4 w-4 transform rounded-full bg-white transition-transform', form.loop ? 'translate-x-4.5' : 'translate-x-0.5']"></span>
                </button>
              </div>
              <div class="flex items-center justify-between">
                <label class="text-xs text-gray-500">自动开始</label>
                <button @click="form.autoStart = !form.autoStart"
                  :class="['relative inline-flex h-5 w-9 items-center rounded-full transition-colors', form.autoStart ? 'bg-emerald-500' : 'bg-gray-300']">
                  <span :class="['inline-block h-4 w-4 transform rounded-full bg-white transition-transform', form.autoStart ? 'translate-x-4.5' : 'translate-x-0.5']"></span>
                </button>
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-pink-50 to-rose-50 rounded-2xl p-4 border border-pink-100">
            <h3 class="text-sm font-semibold text-pink-700 mb-3 flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3"/></svg>
              <span>背景音乐</span>
            </h3>
            <div class="space-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">音乐来源</label>
                <select v-model="form.bgMusicType"
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-pink-500">
                  <option value="none">无背景音乐</option>
                  <option value="preset">系统预设</option>
                  <option value="custom">自定义上传</option>
                </select>
              </div>
              <div v-if="form.bgMusicType === 'preset'">
                <label class="block text-xs text-gray-500 mb-1">选择预设音乐</label>
                <select v-model="form.bgMusicUrl"
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-pink-500">
                  <option v-for="m in presetMusic" :key="m.value" :value="m.value">{{ m.label }}</option>
                </select>
              </div>
              <div v-if="form.bgMusicType === 'custom'">
                <label class="block text-xs text-gray-500 mb-1">上传MP3</label>
                <input type="file" accept=".mp3,audio/*" @change="handleMusicUpload"
                  class="w-full text-xs text-gray-500 file:mr-2 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-medium file:bg-pink-50 file:text-pink-700 hover:file:bg-pink-100" />
                <p v-if="form.bgMusicUrl" class="text-xs text-green-600 mt-1">已上传音乐文件</p>
              </div>
              <div v-if="form.bgMusicType !== 'none'">
                <label class="block text-xs text-gray-500 mb-1">音量: {{ Math.round(form.bgMusicVolume * 100) }}%</label>
                <input v-model.number="form.bgMusicVolume" type="range" min="0" max="1" step="0.05"
                  class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-pink-500" />
              </div>
              <div v-if="form.bgMusicType !== 'none'" class="flex items-center justify-between">
                <label class="text-xs text-gray-500">音乐循环</label>
                <button @click="form.bgMusicLoop = !form.bgMusicLoop"
                  :class="['relative inline-flex h-5 w-9 items-center rounded-full transition-colors', form.bgMusicLoop ? 'bg-pink-500' : 'bg-gray-300']">
                  <span :class="['inline-block h-4 w-4 transform rounded-full bg-white transition-transform', form.bgMusicLoop ? 'translate-x-4.5' : 'translate-x-0.5']"></span>
                </button>
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-violet-50 to-purple-50 rounded-2xl p-4 border border-violet-100">
            <h3 class="text-sm font-semibold text-violet-700 mb-3 flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z"/></svg>
              <span>图片特效</span>
            </h3>
            <div class="space-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">全局特效</label>
                <div class="grid grid-cols-2 gap-2">
                  <button v-for="e in effectOptions" :key="e.value"
                    @click="form.effect = e.value"
                    :class="['px-2 py-1.5 rounded-lg text-xs font-medium transition border',
                      form.effect === e.value ? 'bg-violet-500 text-white border-violet-500' : 'bg-white text-gray-600 border-gray-200 hover:border-violet-300']">
                    {{ e.label }}
                  </button>
                </div>
              </div>
              <div v-if="form.effect !== 'none'">
                <label class="block text-xs text-gray-500 mb-1">特效强度: {{ Math.round(form.effectIntensity * 100) }}%</label>
                <input v-model.number="form.effectIntensity" type="range" min="0" max="1" step="0.05"
                  class="w-full h-2 bg-gray-200 rounded appearance-none cursor-pointer accent-violet-500" />
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-amber-50 to-yellow-50 rounded-2xl p-4 border border-amber-100">
            <h3 class="text-sm font-semibold text-amber-700 mb-3 flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
              <span>文字叠加</span>
            </h3>
            <div class="space-y-3">
              <div>
                <label class="block text-xs text-gray-500 mb-1">文字动画</label>
                <div class="grid grid-cols-2 gap-2">
                  <button v-for="a in textAnimationOptions" :key="a.value"
                    @click="form.textAnimation = a.value"
                    :class="['px-2 py-1.5 rounded-lg text-xs font-medium transition border',
                      form.textAnimation === a.value ? 'bg-amber-500 text-white border-amber-500' : 'bg-white text-gray-600 border-gray-200 hover:border-amber-300']">
                    {{ a.label }}
                  </button>
                </div>
              </div>
              <div>
                <label class="block text-xs text-gray-500 mb-1">文字位置</label>
                <select v-model="form.textPosition"
                  class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-amber-500">
                  <option value="top">顶部</option>
                  <option value="center">居中</option>
                  <option value="bottom">底部</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 flex flex-col overflow-hidden">
        <div class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
          <h3 class="text-sm font-semibold text-gray-700">图片列表 ({{ slideshowItems.length }}张)</h3>
          <div class="flex items-center space-x-2">
            <button @click="addPicturesFromGallery"
              class="px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg text-xs font-medium hover:bg-blue-100 transition flex items-center space-x-1">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
              <span>添加图片</span>
            </button>
            <button @click="addAlbumToSlideshow"
              class="px-3 py-1.5 bg-purple-50 text-purple-600 rounded-lg text-xs font-medium hover:bg-purple-100 transition flex items-center space-x-1">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
              <span>添加整个专辑</span>
            </button>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div v-if="slideshowItems.length === 0" class="flex flex-col items-center justify-center h-full text-gray-400">
            <div class="text-6xl mb-4">🎬</div>
            <h3 class="text-lg font-medium mb-2">还没有添加图片</h3>
            <p class="text-sm">点击上方按钮添加图片或整个专辑</p>
          </div>
          <div v-else class="space-y-3">
            <div v-for="(item, index) in slideshowItems" :key="item.id || index"
              :class="['bg-white rounded-2xl border-2 overflow-hidden transition-all',
                selectedItemIndex === index ? 'border-blue-500 shadow-lg' : 'border-gray-100 hover:border-gray-200']">
              <div class="flex items-stretch">
                <div class="w-48 h-36 flex-shrink-0 bg-gray-100 relative overflow-hidden cursor-pointer"
                  @click="selectedItemIndex = index">
                  <img :src="item.pictureUrl" class="w-full h-full object-cover" />
                  <div class="absolute top-2 left-2 bg-black/60 text-white text-xs px-2 py-0.5 rounded-md">
                    {{ index + 1 }}
                  </div>
                  <div v-if="item.effect && item.effect !== 'none'"
                    class="absolute bottom-2 right-2 bg-violet-500/80 text-white text-[10px] px-1.5 py-0.5 rounded-md">
                    {{ getEffectLabel(item.effect) }}
                  </div>
                </div>
                <div class="flex-1 p-4">
                  <div class="flex items-start justify-between mb-2">
                    <div>
                      <h4 class="text-sm font-medium text-gray-800">{{ item.pictureName || '图片' }}</h4>
                      <p v-if="item.caption" class="text-xs text-gray-500 mt-0.5">{{ item.caption }}</p>
                    </div>
                    <div class="flex items-center space-x-1">
                      <button v-if="index > 0" @click="moveItem(index, index - 1)"
                        class="p-1 hover:bg-gray-100 rounded transition" title="上移">
                        <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"/></svg>
                      </button>
                      <button v-if="index < slideshowItems.length - 1" @click="moveItem(index, index + 1)"
                        class="p-1 hover:bg-gray-100 rounded transition" title="下移">
                        <svg class="w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
                      </button>
                      <button @click="removeItem(index)"
                        class="p-1 hover:bg-red-50 rounded transition" title="删除">
                        <svg class="w-4 h-4 text-gray-400 hover:text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                      </button>
                    </div>
                  </div>
                  <div v-if="selectedItemIndex === index" class="space-y-2 mt-3 pt-3 border-t border-gray-100">
                    <div>
                      <label class="text-xs text-gray-500">说明文字</label>
                      <input v-model="item.caption" type="text" placeholder="为此图片添加说明..."
                        class="w-full px-2 py-1.5 border border-gray-200 rounded text-xs focus:outline-none focus:ring-1 focus:ring-blue-500" />
                    </div>
                    <div class="grid grid-cols-2 gap-2">
                      <div>
                        <label class="text-xs text-gray-500">单独特效</label>
                        <select v-model="item.effect"
                          class="w-full px-2 py-1.5 border border-gray-200 rounded text-xs focus:outline-none focus:ring-1 focus:ring-violet-500">
                          <option value="none">默认</option>
                          <option v-for="e in effectOptions" :key="e.value" :value="e.value">{{ e.label }}</option>
                        </select>
                      </div>
                      <div>
                        <label class="text-xs text-gray-500">切换间隔</label>
                        <select v-model="item.customInterval"
                          class="w-full px-2 py-1.5 border border-gray-200 rounded text-xs focus:outline-none focus:ring-1 focus:ring-emerald-500">
                          <option :value="null">使用全局</option>
                          <option :value="2">2秒</option>
                          <option :value="3">3秒</option>
                          <option :value="5">5秒</option>
                          <option :value="8">8秒</option>
                          <option :value="10">10秒</option>
                          <option :value="15">15秒</option>
                        </select>
                      </div>
                    </div>
                    <div class="grid grid-cols-2 gap-2">
                      <div>
                        <label class="text-xs text-gray-500">文字动画</label>
                        <select v-model="item.textAnimation"
                          class="w-full px-2 py-1.5 border border-gray-200 rounded text-xs focus:outline-none focus:ring-1 focus:ring-amber-500">
                          <option value="fade">默认</option>
                          <option v-for="a in textAnimationOptions" :key="a.value" :value="a.value">{{ a.label }}</option>
                        </select>
                      </div>
                      <div>
                        <label class="text-xs text-gray-500">文字位置</label>
                        <select v-model="item.textPosition"
                          class="w-full px-2 py-1.5 border border-gray-200 rounded text-xs focus:outline-none focus:ring-1 focus:ring-amber-500">
                          <option value="top">顶部</option>
                          <option value="center">居中</option>
                          <option value="bottom">底部</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showExportModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-96 overflow-hidden">
        <div class="bg-gradient-to-r from-cyan-500 to-blue-600 p-4 text-white">
          <h3 class="font-bold">导出视频</h3>
          <p class="text-sm text-blue-100">将幻灯片导出为MP4视频文件</p>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">视频分辨率</label>
            <select v-model="exportForm.resolution"
              class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option value="720p">720p (1280x720)</option>
              <option value="1080p">1080p (1920x1080)</option>
              <option value="4k">4K (3840x2160)</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">视频质量</label>
            <select v-model="exportForm.quality"
              class="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option value="standard">标准</option>
              <option value="high">高清</option>
            </select>
          </div>
          <div v-if="exporting" class="space-y-2">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">导出进度</span>
              <span class="text-blue-600 font-medium">{{ exportProgress }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2.5">
              <div class="bg-gradient-to-r from-cyan-500 to-blue-600 h-2.5 rounded-full transition-all duration-300" :style="{ width: exportProgress + '%' }"></div>
            </div>
          </div>
          <div class="flex space-x-3">
            <button @click="showExportModal = false" :disabled="exporting"
              class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium disabled:opacity-50">取消</button>
            <button @click="startExport" :disabled="exporting"
              class="flex-1 px-4 py-2 bg-gradient-to-r from-cyan-500 to-blue-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50">
              {{ exporting ? '导出中...' : '开始导出' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showShareModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-96 overflow-hidden">
        <div class="bg-gradient-to-r from-orange-500 to-pink-600 p-4 text-white">
          <h3 class="font-bold">分享幻灯片</h3>
          <p class="text-sm text-orange-100">生成播放链接供他人观看</p>
        </div>
        <div class="p-6 space-y-4">
          <div class="flex items-center justify-between">
            <label class="text-sm text-gray-700">公开分享</label>
            <button @click="shareForm.isPublic = !shareForm.isPublic"
              :class="['relative inline-flex h-6 w-11 items-center rounded-full transition-colors', shareForm.isPublic ? 'bg-orange-500' : 'bg-gray-300']">
              <span :class="['inline-block h-5 w-5 transform rounded-full bg-white transition-transform shadow-sm', shareForm.isPublic ? 'translate-x-5.5' : 'translate-x-0.5']"></span>
            </button>
          </div>
          <div class="flex items-center justify-between">
            <label class="text-sm text-gray-700">允许下载</label>
            <button @click="shareForm.allowDownload = !shareForm.allowDownload"
              :class="['relative inline-flex h-6 w-11 items-center rounded-full transition-colors', shareForm.allowDownload ? 'bg-orange-500' : 'bg-gray-300']">
              <span :class="['inline-block h-5 w-5 transform rounded-full bg-white transition-transform shadow-sm', shareForm.allowDownload ? 'translate-x-5.5' : 'translate-x-0.5']"></span>
            </button>
          </div>
          <div v-if="editingSlideshow && editingSlideshow.shareToken" class="bg-gray-50 rounded-lg p-3">
            <label class="text-xs text-gray-500 block mb-1">分享链接</label>
            <div class="flex items-center space-x-2">
              <input :value="shareUrl" readonly
                class="flex-1 px-2 py-1.5 border border-gray-200 rounded text-xs bg-white" />
              <button @click="copyShareUrl"
                class="px-3 py-1.5 bg-blue-500 text-white text-xs rounded-lg hover:bg-blue-600 transition">复制</button>
            </div>
          </div>
          <div class="flex space-x-3">
            <button @click="showShareModal = false"
              class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">关闭</button>
            <button @click="publishSlideshow"
              class="flex-1 px-4 py-2 bg-gradient-to-r from-orange-500 to-pink-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium">
              发布分享
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showPicturePicker" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-[600px] max-h-[80vh] overflow-hidden flex flex-col">
        <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-4 text-white flex items-center justify-between">
          <div>
            <h3 class="font-bold">选择图片</h3>
            <p class="text-sm text-blue-100">点击图片添加到幻灯片</p>
          </div>
          <button @click="showPicturePicker = false" class="p-1 hover:bg-white/20 rounded-lg transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-4 gap-3">
            <div v-for="pic in availablePictures" :key="pic.id"
              @click="togglePictureSelection(pic.id)"
              :class="['relative rounded-xl overflow-hidden cursor-pointer border-2 transition-all',
                selectedPictureIds.includes(pic.id) ? 'border-blue-500 shadow-lg' : 'border-transparent hover:border-gray-300']">
              <div class="aspect-square bg-gray-100">
                <img :src="pic.url" class="w-full h-full object-cover" loading="lazy" />
              </div>
              <div v-if="selectedPictureIds.includes(pic.id)"
                class="absolute top-1 right-1 w-5 h-5 bg-blue-500 rounded-full flex items-center justify-center">
                <svg class="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/></svg>
              </div>
            </div>
          </div>
        </div>
        <div class="border-t border-gray-200 p-4 flex items-center justify-between">
          <span class="text-sm text-gray-500">已选 {{ selectedPictureIds.length }} 张</span>
          <div class="flex space-x-2">
            <button @click="showPicturePicker = false"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">取消</button>
            <button @click="confirmAddPictures" :disabled="selectedPictureIds.length === 0"
              class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50">
              添加 {{ selectedPictureIds.length }} 张
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAlbumPicker" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-96 overflow-hidden">
        <div class="bg-gradient-to-r from-purple-500 to-pink-600 p-4 text-white">
          <h3 class="font-bold">选择专辑</h3>
          <p class="text-sm text-purple-100">将整个专辑的图片添加到幻灯片</p>
        </div>
        <div class="p-4 max-h-64 overflow-y-auto">
          <div v-for="album in availableAlbums" :key="album.id"
            @click="selectedAlbumId = album.id"
            :class="['flex items-center space-x-3 p-3 rounded-xl cursor-pointer transition',
              selectedAlbumId === album.id ? 'bg-purple-50 border-2 border-purple-500' : 'hover:bg-gray-50 border-2 border-transparent']">
            <div class="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
              <img v-if="album.coverUrl" :src="album.coverUrl" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
              </div>
            </div>
            <div>
              <p class="text-sm font-medium text-gray-800">{{ album.name }}</p>
              <p class="text-xs text-gray-500">{{ album.pictureCount || 0 }} 张图片</p>
            </div>
          </div>
        </div>
        <div class="border-t border-gray-200 p-4 flex justify-end space-x-2">
          <button @click="showAlbumPicker = false"
            class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition text-sm font-medium">取消</button>
          <button @click="confirmAddAlbum" :disabled="!selectedAlbumId"
            class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:shadow-lg transition text-sm font-medium disabled:opacity-50">
            添加专辑图片
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SlideshowEditor',
  props: {
    editingSlideshow: { type: Object, default: null },
    allPictures: { type: Array, default: () => [] },
    allAlbums: { type: Array, default: () => [] }
  },
  emits: ['back', 'play', 'saved'],
  data() {
    return {
      form: {
        title: '',
        description: '',
        titlePageText: '',
        transition: 'fade',
        interval: 5,
        loop: true,
        autoStart: true,
        bgMusicType: 'none',
        bgMusicUrl: '',
        bgMusicVolume: 0.5,
        bgMusicLoop: true,
        effect: 'none',
        effectIntensity: 0.5,
        textAnimation: 'fade',
        textStyle: '{}',
        textPosition: 'bottom'
      },
      slideshowItems: [],
      selectedItemIndex: -1,
      saving: false,
      saveStatus: '未保存',
      slideshowData: null,
      showExportModal: false,
      showShareModal: false,
      showPicturePicker: false,
      showAlbumPicker: false,
      selectedPictureIds: [],
      selectedAlbumId: null,
      exporting: false,
      exportProgress: 0,
      exportForm: { resolution: '1080p', quality: 'high' },
      shareForm: { isPublic: true, allowDownload: false },
      transitionOptions: [
        { value: 'fade', label: '淡入淡出' },
        { value: 'slide', label: '滑动' },
        { value: 'zoom', label: '缩放' },
        { value: 'flip', label: '翻转' }
      ],
      effectOptions: [
        { value: 'kenburns', label: '肯伯恩效果' },
        { value: 'parallax', label: '视差效果' },
        { value: 'filter', label: '滤镜渐变' }
      ],
      textAnimationOptions: [
        { value: 'fade', label: '淡入' },
        { value: 'typewriter', label: '打字效果' },
        { value: 'slide', label: '滑入' }
      ],
      presetMusic: [
        { value: '/music/piano_gentle.mp3', label: '轻柔钢琴' },
        { value: '/music/guitar_acoustic.mp3', label: '木吉他' },
        { value: '/music/ambient_calm.mp3', label: '安静氛围' },
        { value: '/music/nature_sounds.mp3', label: '自然之声' }
      ]
    }
  },
  computed: {
    availablePictures() {
      return this.allPictures.filter(p => !p.inRecycleBin)
    },
    availableAlbums() {
      return this.allAlbums.filter(a => !a.isDefault)
    },
    shareUrl() {
      if (this.editingSlideshow && this.editingSlideshow.shareToken) {
        return window.location.origin + '/#/slideshow/' + this.editingSlideshow.shareToken
      }
      return ''
    }
  },
  mounted() {
    if (this.editingSlideshow) {
      this.loadSlideshowData()
    }
  },
  methods: {
    async loadSlideshowData() {
      try {
        const res = await axios.get('/api/slideshows/' + this.editingSlideshow.id + '/edit')
        if (res.data.success) {
          const data = res.data.data
          this.form.title = data.title || ''
          this.form.description = data.description || ''
          this.form.titlePageText = data.titlePageText || ''
          this.form.transition = data.transition || 'fade'
          this.form.interval = data.interval || 5
          this.form.loop = data.loop !== false
          this.form.autoStart = data.autoStart !== false
          this.form.bgMusicType = data.bgMusicType || 'none'
          this.form.bgMusicUrl = data.bgMusicUrl || ''
          this.form.bgMusicVolume = data.bgMusicVolume || 0.5
          this.form.bgMusicLoop = data.bgMusicLoop !== false
          this.form.effect = data.effect || 'none'
          this.form.effectIntensity = data.effectIntensity || 0.5
          this.form.textAnimation = data.textAnimation || 'fade'
          this.form.textStyle = data.textStyle || '{}'
          this.form.textPosition = data.textPosition || 'bottom'
          this.slideshowItems = (data.items || []).map(item => ({...item}))
          this.shareForm.isPublic = data.isPublic || false
          this.shareForm.allowDownload = data.allowDownload || false
          this.slideshowData = data
          this.saveStatus = '已加载'
        }
      } catch (e) {
        console.error('Failed to load slideshow', e)
      }
    },
    async saveSlideshow() {
      if (!this.form.title.trim()) {
        alert('请输入幻灯片标题')
        return
      }
      this.saving = true
      try {
        let res
        if (this.editingSlideshow) {
          res = await axios.put('/api/slideshows/' + this.editingSlideshow.id, this.form)
        } else {
          res = await axios.post('/api/slideshows', {
            ...this.form,
            pictureIds: this.slideshowItems.map(i => i.pictureId)
          })
        }
        if (res.data.success) {
          this.slideshowData = res.data.data
          this.saveStatus = '已保存 ' + new Date().toLocaleTimeString()
          this.$emit('saved', res.data.data)
        } else {
          alert(res.data.message)
        }
      } catch (e) {
        alert('保存失败: ' + (e.response?.data?.message || e.message))
      } finally {
        this.saving = false
      }
    },
    addPicturesFromGallery() {
      this.selectedPictureIds = []
      this.showPicturePicker = true
    },
    togglePictureSelection(picId) {
      const idx = this.selectedPictureIds.indexOf(picId)
      if (idx >= 0) {
        this.selectedPictureIds.splice(idx, 1)
      } else {
        this.selectedPictureIds.push(picId)
      }
    },
    async confirmAddPictures() {
      const newItems = this.selectedPictureIds.map((picId, i) => {
        const pic = this.availablePictures.find(p => p.id === picId)
        return {
          pictureId: picId,
          pictureUrl: pic?.url || '',
          pictureName: pic?.name || '图片',
          caption: '',
          effect: 'none',
          effectIntensity: 0.5,
          textAnimation: 'fade',
          textStyle: '{}',
          textPosition: 'bottom',
          sortOrder: this.slideshowItems.length + i,
          customInterval: null
        }
      })
      this.slideshowItems.push(...newItems)

      if (this.slideshowData && this.slideshowData.id) {
        for (const picId of this.selectedPictureIds) {
          try {
            await axios.post('/api/slideshows/' + this.slideshowData.id + '/items', {
              pictureId: picId,
              sortOrder: this.slideshowItems.length
            })
          } catch (e) {
            console.error('Failed to add item', e)
          }
        }
      }
      this.showPicturePicker = false
      this.saveStatus = '有未保存的更改'
    },
    addAlbumToSlideshow() {
      this.selectedAlbumId = null
      this.showAlbumPicker = true
    },
    async confirmAddAlbum() {
      if (!this.selectedAlbumId) return
      try {
        const res = await axios.get('/api/albums/' + this.selectedAlbumId)
        if (res.data.success && res.data.data) {
          const album = res.data.data
          if (album.pictures && album.pictures.length > 0) {
            const newItems = album.pictures.map((pic, i) => ({
              pictureId: pic.id,
              pictureUrl: pic.url,
              pictureName: pic.name,
              caption: '',
              effect: 'none',
              effectIntensity: 0.5,
              textAnimation: 'fade',
              textStyle: '{}',
              textPosition: 'bottom',
              sortOrder: this.slideshowItems.length + i,
              customInterval: null
            }))
            this.slideshowItems.push(...newItems)
            this.saveStatus = '有未保存的更改'
          }
        }
      } catch (e) {
        console.error('Failed to add album', e)
      }
      this.showAlbumPicker = false
    },
    moveItem(from, to) {
      const item = this.slideshowItems.splice(from, 1)[0]
      this.slideshowItems.splice(to, 0, item)
      this.saveStatus = '有未保存的更改'
    },
    async removeItem(index) {
      const item = this.slideshowItems[index]
      this.slideshowItems.splice(index, 1)
      if (item.id && this.slideshowData && this.slideshowData.id) {
        try {
          await axios.delete('/api/slideshows/items/' + item.id)
        } catch (e) {
          console.error('Failed to remove item', e)
        }
      }
      this.saveStatus = '有未保存的更改'
    },
    async handleMusicUpload(e) {
      const file = e.target.files[0]
      if (!file) return
      const formData = new FormData()
      formData.append('file', file)
      try {
        const res = await axios.post('/api/pictures/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.data.success) {
          this.form.bgMusicUrl = res.data.data.url
          this.saveStatus = '有未保存的更改'
        }
      } catch (e) {
        alert('音乐上传失败')
      }
    },
    async startExport() {
      if (!this.slideshowData || !this.slideshowData.id) {
        await this.saveSlideshow()
      }
      if (!this.slideshowData || !this.slideshowData.id) return

      this.exporting = true
      this.exportProgress = 0
      try {
        const res = await axios.post('/api/slideshows/' + this.slideshowData.id + '/export', this.exportForm)
        if (res.data.success) {
          const exportId = res.data.data
          const pollInterval = setInterval(async () => {
            try {
              const progRes = await axios.get('/api/slideshows/export/' + exportId + '/progress')
              if (progRes.data.success) {
                this.exportProgress = progRes.data.data
                if (this.exportProgress >= 100) {
                  clearInterval(pollInterval)
                  this.exporting = false
                  this.showExportModal = false
                  alert('导出完成！视频已准备好下载')
                }
              }
            } catch (e) {
              clearInterval(pollInterval)
              this.exporting = false
            }
          }, 1000)
        }
      } catch (e) {
        alert('导出失败: ' + (e.response?.data?.message || e.message))
        this.exporting = false
      }
    },
    async publishSlideshow() {
      if (!this.slideshowData || !this.slideshowData.id) {
        await this.saveSlideshow()
      }
      if (!this.slideshowData || !this.slideshowData.id) return

      try {
        const updateRes = await axios.put('/api/slideshows/' + this.slideshowData.id, {
          isPublic: this.shareForm.isPublic,
          allowDownload: this.shareForm.allowDownload
        })
        if (!updateRes.data.success) {
          alert(updateRes.data.message)
          return
        }
        const res = await axios.post('/api/slideshows/' + this.slideshowData.id + '/publish?isPublic=' + this.shareForm.isPublic)
        if (res.data.success) {
          this.slideshowData = res.data.data
          alert('发布成功！分享链接: ' + this.shareUrl)
          this.showShareModal = false
        } else {
          alert(res.data.message)
        }
      } catch (e) {
        alert('发布失败: ' + (e.response?.data?.message || e.message))
      }
    },
    copyShareUrl() {
      navigator.clipboard.writeText(this.shareUrl).then(() => {
        alert('链接已复制到剪贴板')
      }).catch(() => {
        const input = document.createElement('input')
        input.value = this.shareUrl
        document.body.appendChild(input)
        input.select()
        document.execCommand('copy')
        document.body.removeChild(input)
        alert('链接已复制到剪贴板')
      })
    },
    getEffectLabel(value) {
      const found = this.effectOptions.find(e => e.value === value)
      return found ? found.label : value
    }
  }
}
</script>
