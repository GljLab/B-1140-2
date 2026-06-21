<template>
  <div class="fixed inset-0 bg-slate-900/95 backdrop-blur-sm z-50 flex flex-col">
    <!-- 顶部工具栏 -->
    <div class="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center space-x-4">
        <button @click="$emit('close')" class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div>
          <h2 class="text-lg font-bold text-gray-800">图片对比</h2>
          <p class="text-xs text-gray-500">已选择 {{ pictures.length }} 张图片</p>
        </div>
      </div>
      
      <div class="flex items-center space-x-2">
        <!-- 对比模式选择 -->
        <div class="flex bg-gray-100 rounded-xl p-1">
          <button v-for="m in modes" :key="m.id" @click="currentMode = m.id"
            :class="['px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-200',
              currentMode === m.id ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:text-gray-900']">
            <span class="flex items-center space-x-1">
              <span v-html="m.icon"></span>
              <span>{{ m.name }}</span>
            </span>
          </button>
        </div>

        <div class="w-px h-8 bg-gray-200 mx-1"></div>

        <!-- 标注工具 -->
        <div v-if="currentMode === 'side-by-side' || currentMode === 'grid'" class="flex items-center space-x-1">
          <span class="text-xs text-gray-500 mr-1">标注:</span>
          <button v-for="tool in annotationTools" :key="tool.id" @click="selectTool(tool.id)"
            :title="tool.name"
            :class="['p-2 rounded-lg transition',
              currentTool === tool.id ? 'bg-blue-100 text-blue-600' : 'text-gray-600 hover:bg-gray-100']">
            <span v-html="tool.icon"></span>
          </button>
          <input v-model="currentColor" type="color" class="w-8 h-8 rounded cursor-pointer border border-gray-200 ml-1" title="颜色" />
          <input v-model.number="strokeWidth" type="range" min="1" max="10" class="w-20" title="线宽" />
        </div>

        <div class="w-px h-8 bg-gray-200 mx-1"></div>

        <!-- 特殊模式参数控制 -->
        <div v-if="currentMode === 'blink'" class="flex items-center space-x-2">
          <span class="text-xs text-gray-500">切换速度:</span>
          <input v-model.number="blinkSpeed" type="range" min="100" max="3000" step="100" class="w-24" />
          <span class="text-xs text-gray-600">{{ blinkSpeed }}ms</span>
          <button @click="toggleBlink" :class="['px-3 py-1.5 rounded-lg text-sm font-medium transition',
            isBlinking ? 'bg-red-500 text-white' : 'bg-green-500 text-white']">
            {{ isBlinking ? '停止' : '开始' }}
          </button>
        </div>
        <div v-if="currentMode === 'overlay'" class="flex items-center space-x-2">
          <span class="text-xs text-gray-500">透明度:</span>
          <input v-model.number="overlayOpacity" type="range" min="0" max="100" class="w-24" />
          <span class="text-xs text-gray-600">{{ overlayOpacity }}%</span>
        </div>
        <div v-if="currentMode === 'swipe'" class="flex items-center space-x-2">
          <span class="text-xs text-gray-500">分界线:</span>
          <input v-model.number="swipePosition" type="range" min="0" max="100" class="w-24" />
          <span class="text-xs text-gray-600">{{ swipePosition }}%</span>
        </div>

        <div class="w-px h-8 bg-gray-200 mx-1"></div>

        <!-- 缩放控制 -->
        <div class="flex items-center space-x-1">
          <button @click="zoomOut" class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition" title="缩小">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
            </svg>
          </button>
          <span class="text-sm text-gray-600 w-14 text-center">{{ Math.round(scale * 100) }}%</span>
          <button @click="zoomIn" class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition" title="放大">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
            </svg>
          </button>
          <button @click="resetView" class="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition" title="重置">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
          </button>
        </div>

        <div class="w-px h-8 bg-gray-200 mx-1"></div>

        <!-- 操作按钮 -->
        <button @click="saveComparison" class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
          </svg>
          <span>保存对比</span>
        </button>
        <button @click="exportImage" class="px-4 py-2 bg-gradient-to-r from-emerald-500 to-teal-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </svg>
          <span>导出图片</span>
        </button>
        <button @click="exportReport" class="px-4 py-2 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <span>导出报告</span>
        </button>
        <button v-if="!isPublic" @click="togglePublic" :class="['px-4 py-2 rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1',
          comparisonId ? 'bg-gradient-to-r from-amber-500 to-orange-600 text-white' : 'bg-gray-100 text-gray-600']">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
          </svg>
          <span>{{ isPublic ? '取消分享' : '分享' }}</span>
        </button>
      </div>
    </div>

    <!-- 主对比区域 -->
    <div class="flex-1 overflow-hidden relative bg-slate-800">
      <!-- 并排/网格模式 -->
      <div v-if="currentMode === 'side-by-side' || currentMode === 'grid'"
        :class="['h-full w-full flex', pictures.length <= 2 ? 'flex-row' : 'flex-wrap']"
        ref="compareContainerRef">
        <div v-for="(pic, idx) in pictures" :key="pic.id"
          :class="['relative overflow-hidden border-r border-b border-slate-700',
            pictures.length === 2 ? 'w-1/2 h-full' : 'w-1/2 h-1/2']"
          @mousedown="startPan($event, idx)"
          @wheel.stop.prevent="onWheel($event)"
          ref="panContainerRefs"
          :style="{ cursor: currentTool === 'select' ? (isPanning ? 'grabbing' : 'grab') : 'crosshair' }">
          <!-- 图片容器 -->
          <div class="absolute inset-0 flex items-center justify-center overflow-hidden"
            :style="{
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`,
              transformOrigin: 'center center',
              transition: isPanning ? 'none' : 'transform 0.1s ease-out'
            }">
            <img :src="pic.url" :alt="pic.name" draggable="false"
              class="select-none max-w-none"
              @load="onImageLoad($event, idx)"
              @mousedown.stop.prevent="onImageMouseDown($event, pic.id, idx)" />
          </div>

          <!-- 标注层 -->
          <svg class="absolute inset-0 w-full h-full pointer-events-none overflow-visible"
            :viewBox="`0 0 ${containerWidth} ${containerHeight}`" preserveAspectRatio="xMidYMid meet">
            <template v-for="ann in getAnnotationsForPicture(pic.id)" :key="ann.id">
              <circle v-if="ann.toolType === 'point'"
                :cx="ann.startX" :cy="ann.startY" :r="8"
                :fill="ann.color" :stroke="'white'" :stroke-width="2"
                class="pointer-events-none" />
              <rect v-if="ann.toolType === 'rect'"
                :x="Math.min(ann.startX, ann.endX)"
                :y="Math.min(ann.startY, ann.endY)"
                :width="Math.abs(ann.endX - ann.startX)"
                :height="Math.abs(ann.endY - ann.startY)"
                :fill="'none'" :stroke="ann.color"
                :stroke-width="ann.strokeWidth"
                class="pointer-events-none" />
              <ellipse v-if="ann.toolType === 'circle'"
                :cx="(ann.startX + ann.endX) / 2"
                :cy="(ann.startY + ann.endY) / 2"
                :rx="Math.abs(ann.endX - ann.startX) / 2"
                :ry="Math.abs(ann.endY - ann.startY) / 2"
                :fill="'none'" :stroke="ann.color"
                :stroke-width="ann.strokeWidth"
                class="pointer-events-none" />
              <line v-if="ann.toolType === 'arrow'"
                :x1="ann.startX" :y1="ann.startY"
                :x2="ann.endX" :y2="ann.endY"
                :stroke="ann.color" :stroke-width="ann.strokeWidth"
                :marker-end="`url(#arrowhead-${ann.color.replace('#', '')})`"
                class="pointer-events-none" />
              <defs>
                <marker :id="`arrowhead-${ann.color.replace('#', '')}`"
                  markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
                  <polygon points="0 0, 10 3, 0 6" :fill="ann.color" />
                </marker>
              </defs>
              <text v-if="ann.toolType === 'text'"
                :x="ann.startX" :y="ann.startY"
                :fill="ann.color"
                :font-size="ann.fontSize || 16"
                font-family="sans-serif"
                font-weight="bold"
                class="pointer-events-none select-none">
                {{ ann.textContent }}
              </text>
              <polyline v-if="ann.toolType === 'freehand' && ann.pointsData"
                :points="ann.pointsData"
                :fill="'none'" :stroke="ann.color"
                :stroke-width="ann.strokeWidth"
                :stroke-linejoin="'round'" :stroke-linecap="'round'"
                class="pointer-events-none" />
            </template>

            <template v-if="isDrawing && currentDrawingPictureId === pic.id">
              <circle v-if="currentTool === 'point'"
                :cx="drawStartX" :cy="drawStartY" :r="8"
                :fill="currentColor" :stroke="'white'" :stroke-width="2" />
              <rect v-if="currentTool === 'rect'"
                :x="Math.min(drawStartX, drawCurrentX)"
                :y="Math.min(drawStartY, drawCurrentY)"
                :width="Math.abs(drawCurrentX - drawStartX)"
                :height="Math.abs(drawCurrentY - drawStartY)"
                :fill="'none'" :stroke="currentColor" :stroke-width="strokeWidth" />
              <ellipse v-if="currentTool === 'circle'"
                :cx="(drawStartX + drawCurrentX) / 2"
                :cy="(drawStartY + drawCurrentY) / 2"
                :rx="Math.abs(drawCurrentX - drawStartX) / 2"
                :ry="Math.abs(drawCurrentY - drawStartY) / 2"
                :fill="'none'" :stroke="currentColor" :stroke-width="strokeWidth" />
              <line v-if="currentTool === 'arrow'"
                :x1="drawStartX" :y1="drawStartY"
                :x2="drawCurrentX" :y2="drawCurrentY"
                :stroke="currentColor" :stroke-width="strokeWidth"
                :marker-end="`url(#arrowhead-current)`" />
              <defs>
                <marker id="arrowhead-current" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
                  <polygon points="0 0, 10 3, 0 6" :fill="currentColor" />
                </marker>
              </defs>
              <polyline v-if="currentTool === 'freehand' && freehandPoints.length > 0"
                :points="freehandPoints.join(' ')"
                :fill="'none'" :stroke="currentColor"
                :stroke-width="strokeWidth"
                :stroke-linejoin="'round'" :stroke-linecap="'round'" />
            </template>
          </svg>

          <!-- 图片信息标签 -->
          <div class="absolute top-3 left-3 bg-black/60 backdrop-blur-sm text-white text-xs px-3 py-1.5 rounded-lg">
            <span class="font-semibold">{{ idx + 1 }}. {{ pic.name }}</span>
          </div>

          <!-- 删除标注按钮 -->
          <button v-if="currentTool === 'select'" 
            @click="clearAnnotationsForPicture(pic.id)"
            class="absolute bottom-3 right-3 p-2 bg-red-500/80 hover:bg-red-500 text-white rounded-lg transition text-xs">
            清除标注
          </button>
        </div>
      </div>

      <!-- 卷帘模式 -->
      <div v-if="currentMode === 'swipe' && pictures.length >= 2"
        class="h-full w-full relative flex items-center justify-center overflow-hidden"
        ref="compareContainerRef">
        <div class="relative" :style="{ width: '100%', height: '100%' }">
          <!-- 图片B（下层） -->
          <div class="absolute inset-0 flex items-center justify-center overflow-hidden"
            :style="{ transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})` }">
            <img :src="pictures[1].url" draggable="false" class="select-none max-w-none" />
          </div>
          <!-- 图片A（上层，带裁剪） -->
          <div class="absolute inset-0 flex items-center justify-center overflow-hidden"
            :style="{
              clipPath: `inset(0 ${100 - swipePosition}% 0 0)`,
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`
            }">
            <img :src="pictures[0].url" draggable="false" class="select-none max-w-none" />
          </div>
          <!-- 分界线 -->
          <div class="absolute top-0 bottom-0 bg-white/80"
            :style="{ left: `${swipePosition}%`, width: '3px', cursor: 'ew-resize', transform: 'translateX(-50%)' }"
            @mousedown="startSwipeDrag">
            <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-10 h-10 bg-white rounded-full shadow-lg flex items-center justify-center cursor-grab">
              <svg class="w-5 h-5 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8 9l4-4 4 4m0 6l-4 4-4-4" />
              </svg>
            </div>
          </div>
          <!-- 标签 -->
          <div class="absolute top-3 left-3 bg-black/60 text-white text-xs px-3 py-1.5 rounded-lg">A. {{ pictures[0].name }}</div>
          <div class="absolute top-3 right-3 bg-black/60 text-white text-xs px-3 py-1.5 rounded-lg">B. {{ pictures[1].name }}</div>
        </div>
      </div>

      <!-- 闪烁模式 -->
      <div v-if="currentMode === 'blink' && pictures.length >= 2"
        class="h-full w-full relative flex items-center justify-center overflow-hidden bg-slate-800"
        ref="compareContainerRef">
        <div class="relative flex items-center justify-center" :style="{ width: '100%', height: '100%' }">
          <div class="absolute inset-0 flex items-center justify-center"
            :style="{ 
              opacity: blinkShowFirst ? 1 : 0,
              transition: `opacity ${blinkSpeed / 2}ms ease-in-out`,
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`
            }">
            <img :src="pictures[0].url" draggable="false" class="select-none max-w-none" />
          </div>
          <div class="absolute inset-0 flex items-center justify-center"
            :style="{ 
              opacity: blinkShowFirst ? 0 : 1,
              transition: `opacity ${blinkSpeed / 2}ms ease-in-out`,
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`
            }">
            <img :src="pictures[1].url" draggable="false" class="select-none max-w-none" />
          </div>
          <div class="absolute top-3 left-3 bg-black/60 text-white text-xs px-3 py-1.5 rounded-lg">
            <span :class="blinkShowFirst ? 'text-green-400' : 'text-gray-400'">A: {{ pictures[0].name }}</span>
            <span class="mx-2 text-gray-500">|</span>
            <span :class="!blinkShowFirst ? 'text-green-400' : 'text-gray-400'">B: {{ pictures[1].name }}</span>
          </div>
        </div>
      </div>

      <!-- 叠加模式 -->
      <div v-if="currentMode === 'overlay' && pictures.length >= 2"
        class="h-full w-full relative flex items-center justify-center overflow-hidden"
        ref="compareContainerRef"
        @mousedown="startPan($event, 0)"
        @wheel.stop.prevent="onWheel($event)"
        :style="{ cursor: isPanning ? 'grabbing' : 'grab' }">
        <div class="relative" :style="{ width: '100%', height: '100%' }">
          <div class="absolute inset-0 flex items-center justify-center overflow-hidden"
            :style="{ 
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`,
              transformOrigin: 'center center'
            }">
            <img :src="pictures[0].url" draggable="false" class="select-none max-w-none" />
          </div>
          <div class="absolute inset-0 flex items-center justify-center overflow-hidden"
            :style="{ 
              opacity: overlayOpacity / 100,
              transform: `translate(${-offsetX}px, ${-offsetY}px) scale(${scale})`,
              transformOrigin: 'center center',
              mixBlendMode: 'normal'
            }">
            <img :src="pictures[1].url" draggable="false" class="select-none max-w-none" />
          </div>
          <div class="absolute top-3 left-3 bg-black/60 text-white text-xs px-3 py-1.5 rounded-lg">底层: {{ pictures[0].name }}</div>
          <div class="absolute top-3 right-3 bg-black/60 text-white text-xs px-3 py-1.5 rounded-lg">叠加: {{ pictures[1].name }} ({{ overlayOpacity }}%)</div>
        </div>
      </div>
    </div>

    <!-- 底部提示栏 -->
    <div class="bg-white border-t border-gray-200 px-4 py-2 flex items-center justify-between text-xs text-gray-500 flex-shrink-0">
      <div class="flex items-center space-x-4">
        <span>💡 提示: 滚轮缩放 | 拖动平移 | 选择标注工具后在图片上绘制</span>
        <span v-if="currentTool !== 'select'" class="text-blue-600">当前工具: {{ currentToolName }}</span>
      </div>
      <div class="flex items-center space-x-4">
        <span>缩放: {{ Math.round(scale * 100) }}%</span>
        <span>偏移: ({{ Math.round(offsetX) }}, {{ Math.round(offsetY) }})</span>
        <span>标注数: {{ annotations.length }}</span>
      </div>
    </div>

    <!-- 保存/分享对话框 -->
    <div v-if="showSaveDialog" class="fixed inset-0 bg-black/50 flex items-center justify-center z-60">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-4">{{ comparisonId ? '更新对比记录' : '保存对比记录' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">标题</label>
            <input v-model="saveTitle" type="text" placeholder="请输入对比标题"
              class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="flex items-center space-x-2 cursor-pointer">
              <input type="checkbox" v-model="saveIsPublic" class="w-4 h-4 text-blue-600 rounded" />
              <span class="text-sm text-gray-700">设为公开（其他用户可通过链接查看）</span>
            </label>
          </div>
          <div v-if="comparisonId && shareToken" class="p-3 bg-blue-50 rounded-lg">
            <p class="text-xs text-blue-700 font-medium mb-1">分享链接:</p>
            <div class="flex items-center space-x-2">
              <input :value="shareUrl" readonly class="flex-1 px-3 py-1.5 text-xs bg-white border border-blue-200 rounded" />
              <button @click="copyShareLink" class="px-3 py-1.5 bg-blue-500 text-white text-xs rounded hover:bg-blue-600 transition">复制</button>
            </div>
          </div>
        </div>
        <div class="flex justify-end space-x-3 mt-6">
          <button @click="showSaveDialog = false" class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition">取消</button>
          <button @click="confirmSave" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition">{{ comparisonId ? '更新' : '保存' }}</button>
        </div>
      </div>
    </div>

    <!-- 文字输入对话框 -->
    <div v-if="showTextDialog" class="fixed inset-0 bg-black/50 flex items-center justify-center z-60">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-4">添加文字说明</h3>
        <textarea v-model="pendingText" rows="3" placeholder="请输入文字内容..."
          class="w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"></textarea>
        <div class="mt-3">
          <label class="block text-xs text-gray-600 mb-1">字号: {{ fontSize }}px</label>
          <input v-model.number="fontSize" type="range" min="12" max="48" class="w-full" />
        </div>
        <div class="flex justify-end space-x-3 mt-4">
          <button @click="cancelText" class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition">取消</button>
          <button @click="confirmText" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ImageCompare',
  props: {
    pictures: {
      type: Array,
      required: true
    },
    existingComparison: {
      type: Object,
      default: null
    }
  },
  emits: ['close', 'saved'],
  data() {
    return {
      modes: [
        { id: 'side-by-side', name: '并排', icon: '⬌' },
        { id: 'grid', name: '网格', icon: '▦' },
        { id: 'swipe', name: '卷帘', icon: '⇋' },
        { id: 'blink', name: '闪烁', icon: '✨' },
        { id: 'overlay', name: '叠加', icon: '◐' }
      ],
      annotationTools: [
        { id: 'select', name: '选择/移动', icon: '↖' },
        { id: 'point', name: '标记点', icon: '●' },
        { id: 'rect', name: '矩形', icon: '▭' },
        { id: 'circle', name: '圆圈', icon: '○' },
        { id: 'arrow', name: '箭头', icon: '➤' },
        { id: 'freehand', name: '画笔', icon: '✎' },
        { id: 'text', name: '文字', icon: 'T' }
      ],
      currentMode: 'side-by-side',
      scale: 1,
      offsetX: 0,
      offsetY: 0,
      isPanning: false,
      panStartX: 0,
      panStartY: 0,
      panStartOffsetX: 0,
      panStartOffsetY: 0,
      containerWidth: 0,
      containerHeight: 0,
      
      currentTool: 'select',
      currentColor: '#ef4444',
      strokeWidth: 3,
      fontSize: 16,
      
      isDrawing: false,
      currentDrawingPictureId: null,
      drawStartX: 0,
      drawStartY: 0,
      drawCurrentX: 0,
      drawCurrentY: 0,
      freehandPoints: [],
      
      annotations: [],
      
      blinkSpeed: 800,
      isBlinking: false,
      blinkShowFirst: true,
      blinkTimer: null,
      
      overlayOpacity: 50,
      
      swipePosition: 50,
      isSwiping: false,
      
      showSaveDialog: false,
      saveTitle: '',
      saveIsPublic: false,
      comparisonId: null,
      isPublic: false,
      shareToken: '',
      
      showTextDialog: false,
      pendingText: '',
      pendingTextPos: { x: 0, y: 0, pictureId: null },

      compareContainerRef: null,
      panContainerRefs: []
    }
  },
  computed: {
    currentToolName() {
      const tool = this.annotationTools.find(t => t.id === this.currentTool)
      return tool ? tool.name : ''
    },
    shareUrl() {
      return `${window.location.origin}/#compare/${this.shareToken || this.comparisonId}`
    }
  },
  watch: {
    isBlinking(val) {
      if (val) {
        this.startBlinkTimer()
      } else {
        this.stopBlinkTimer()
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      if (this.$refs.compareContainerRef) {
        const rect = this.$refs.compareContainerRef.getBoundingClientRect()
        this.containerWidth = rect.width
        this.containerHeight = rect.height
      }
    })
    window.addEventListener('mousemove', this.onMouseMove)
    window.addEventListener('mouseup', this.onMouseUp)

    if (this.existingComparison) {
      this.comparisonId = this.existingComparison.id
      this.isPublic = this.existingComparison.isPublic
      if (this.existingComparison.mode) {
        this.currentMode = this.existingComparison.mode
      }
      if (this.existingComparison.viewConfig) {
        try {
          const config = JSON.parse(this.existingComparison.viewConfig)
          if (config.scale) this.scale = config.scale
          if (config.offsetX != null) this.offsetX = config.offsetX
          if (config.offsetY != null) this.offsetY = config.offsetY
        } catch (e) {}
      }
      if (this.existingComparison.annotations) {
        this.annotations = [...this.existingComparison.annotations]
      }
    }
  },
  beforeUnmount() {
    window.removeEventListener('mousemove', this.onMouseMove)
    window.removeEventListener('mouseup', this.onMouseUp)
    this.stopBlinkTimer()
  },
  methods: {
    onImageLoad(e, idx) {
    },
    
    getAnnotationsForPicture(picId) {
      return this.annotations.filter(a => a.pictureId === picId)
    },
    
    clearAnnotationsForPicture(picId) {
      const toDelete = this.annotations.filter(a => a.pictureId === picId)
      toDelete.forEach(a => {
        if (a.id) {
          axios.delete(`/api/comparisons/annotations/${a.id}`).catch(() => {})
        }
      })
      this.annotations = this.annotations.filter(a => a.pictureId !== picId)
    },
    
    selectTool(toolId) {
      this.currentTool = toolId
    },
    
    startPan(e, idx) {
      if (this.currentTool !== 'select' || e.button !== 0) return
      this.isPanning = true
      this.panStartX = e.clientX
      this.panStartY = e.clientY
      this.panStartOffsetX = this.offsetX
      this.panStartOffsetY = this.offsetY
    },
    
    onWheel(e) {
      const delta = e.deltaY > 0 ? 0.9 : 1.1
      const newScale = Math.max(0.1, Math.min(10, this.scale * delta))
      this.scale = newScale
    },
    
    zoomIn() {
      this.scale = Math.min(10, this.scale * 1.2)
    },
    
    zoomOut() {
      this.scale = Math.max(0.1, this.scale / 1.2)
    },
    
    resetView() {
      this.scale = 1
      this.offsetX = 0
      this.offsetY = 0
    },
    
    startSwipeDrag(e) {
      this.isSwiping = true
      e.preventDefault()
    },
    
    onMouseMove(e) {
      if (this.isPanning) {
        const dx = e.clientX - this.panStartX
        const dy = e.clientY - this.panStartY
        this.offsetX = this.panStartOffsetX - dx
        this.offsetY = this.panStartOffsetY - dy
      }
      
      if (this.isSwiping && this.$refs.compareContainerRef) {
        const rect = this.$refs.compareContainerRef.getBoundingClientRect()
        const x = e.clientX - rect.left
        this.swipePosition = Math.max(0, Math.min(100, (x / rect.width) * 100))
      }
      
      if (this.isDrawing) {
        const container = this.getContainerByPictureId(this.currentDrawingPictureId)
        if (container) {
          const rect = container.getBoundingClientRect()
          this.drawCurrentX = this.clientToImageX(e.clientX, rect)
          this.drawCurrentY = this.clientToImageY(e.clientY, rect)
          if (this.currentTool === 'freehand') {
            this.freehandPoints.push(`${this.drawCurrentX},${this.drawCurrentY}`)
          }
        }
      }
    },
    
    onMouseUp(e) {
      this.isPanning = false
      this.isSwiping = false
      
      if (this.isDrawing) {
        this.finishDrawing()
      }
    },
    
    onImageMouseDown(e, pictureId, idx) {
      if (this.currentTool === 'select') return
      
      const container = e.currentTarget.closest('.relative.overflow-hidden') || 
                        (this.$refs.panContainerRefs && this.$refs.panContainerRefs[idx])
      if (!container) return
      
      const rect = container.getBoundingClientRect()
      const x = this.clientToImageX(e.clientX, rect)
      const y = this.clientToImageY(e.clientY, rect)
      
      if (this.currentTool === 'text') {
        this.pendingTextPos = { x, y, pictureId }
        this.showTextDialog = true
        return
      }
      
      if (this.currentTool === 'point') {
        this.addAnnotation({
          pictureId,
          toolType: 'point',
          startX: x,
          startY: y,
          endX: x,
          endY: y,
          color: this.currentColor,
          strokeWidth: this.strokeWidth
        })
        return
      }
      
      this.isDrawing = true
      this.currentDrawingPictureId = pictureId
      this.drawStartX = x
      this.drawStartY = y
      this.drawCurrentX = x
      this.drawCurrentY = y
      this.freehandPoints = [`${x},${y}`]
    },
    
    clientToImageX(clientX, rect) {
      return (clientX - rect.left - rect.width / 2) / this.scale + this.containerWidth / 2 + this.offsetX
    },
    
    clientToImageY(clientY, rect) {
      return (clientY - rect.top - rect.height / 2) / this.scale + this.containerHeight / 2 + this.offsetY
    },
    
    getContainerByPictureId(picId) {
      const idx = this.pictures.findIndex(p => p.id === picId)
      if (idx >= 0 && this.$refs.panContainerRefs) {
        return this.$refs.panContainerRefs[idx]
      }
      return null
    },
    
    finishDrawing() {
      if (this.drawStartX === this.drawCurrentX && this.drawStartY === this.drawCurrentY && this.currentTool !== 'freehand') {
        this.isDrawing = false
        this.currentDrawingPictureId = null
        this.freehandPoints = []
        return
      }
      
      const ann = {
        pictureId: this.currentDrawingPictureId,
        toolType: this.currentTool,
        startX: this.drawStartX,
        startY: this.drawStartY,
        endX: this.drawCurrentX,
        endY: this.drawCurrentY,
        color: this.currentColor,
        strokeWidth: this.strokeWidth,
        pointsData: this.currentTool === 'freehand' ? this.freehandPoints.join(' ') : null
      }
      
      this.addAnnotation(ann)
      
      this.isDrawing = false
      this.currentDrawingPictureId = null
      this.freehandPoints = []
    },
    
    addAnnotation(ann) {
      if (this.comparisonId) {
        axios.post('/api/comparisons/annotations', {
          comparisonId: this.comparisonId,
          ...ann
        }).then(res => {
          if (res.data.success) {
            this.annotations.push(res.data.data)
          }
        }).catch(() => {
          this.annotations.push({ ...ann, id: Date.now(), temp: true })
        })
      } else {
        this.annotations.push({ ...ann, id: Date.now(), temp: true })
      }
    },
    
    cancelText() {
      this.showTextDialog = false
      this.pendingText = ''
    },
    
    confirmText() {
      if (!this.pendingText.trim()) {
        this.cancelText()
        return
      }
      this.addAnnotation({
        pictureId: this.pendingTextPos.pictureId,
        toolType: 'text',
        startX: this.pendingTextPos.x,
        startY: this.pendingTextPos.y,
        endX: this.pendingTextPos.x,
        endY: this.pendingTextPos.y,
        color: this.currentColor,
        strokeWidth: this.strokeWidth,
        textContent: this.pendingText,
        fontSize: this.fontSize
      })
      this.showTextDialog = false
      this.pendingText = ''
    },
    
    startBlinkTimer() {
      this.stopBlinkTimer()
      this.blinkTimer = setInterval(() => {
        this.blinkShowFirst = !this.blinkShowFirst
      }, this.blinkSpeed)
    },
    
    stopBlinkTimer() {
      if (this.blinkTimer) {
        clearInterval(this.blinkTimer)
        this.blinkTimer = null
      }
    },
    
    toggleBlink() {
      this.isBlinking = !this.isBlinking
    },
    
    saveComparison() {
      this.saveTitle = `图片对比 - ${this.pictures.map(p => p.name).join(' vs ')}`
      this.showSaveDialog = true
    },
    
    async confirmSave() {
      const viewConfig = JSON.stringify({
        mode: this.currentMode,
        scale: this.scale,
        offsetX: this.offsetX,
        offsetY: this.offsetY
      })
      
      const request = {
        title: this.saveTitle,
        mode: this.currentMode,
        pictureIds: this.pictures.map(p => p.id),
        viewConfig,
        isPublic: this.saveIsPublic
      }
      
      try {
        if (this.comparisonId) {
          const res = await axios.put(`/api/comparisons/${this.comparisonId}`, request)
          if (res.data.success) {
            this.isPublic = this.saveIsPublic
            this.showSaveDialog = false
            this.$emit('saved', res.data.data)
            alert('更新成功！')
          }
        } else {
          const res = await axios.post('/api/comparisons', request)
          if (res.data.success) {
            this.comparisonId = res.data.data.id
            this.isPublic = this.saveIsPublic
            
            for (let ann of this.annotations) {
              if (ann.temp) {
                try {
                  await axios.post('/api/comparisons/annotations', {
                    comparisonId: this.comparisonId,
                    ...ann
                  })
                } catch (e) {}
              }
            }
            
            this.showSaveDialog = false
            this.$emit('saved', res.data.data)
            alert('保存成功！')
          }
        }
      } catch (e) {
        alert('保存失败: ' + (e.response?.data?.message || e.message))
      }
    },
    
    async togglePublic() {
      if (!this.comparisonId) {
        alert('请先保存对比记录再分享')
        this.saveComparison()
        return
      }
      
      try {
        const res = await axios.put(`/api/comparisons/${this.comparisonId}`, {
          isPublic: !this.isPublic
        })
        if (res.data.success) {
          this.isPublic = !this.isPublic
          this.saveIsPublic = this.isPublic
          if (this.isPublic) {
            this.shareToken = this.comparisonId
            this.showSaveDialog = true
          }
        }
      } catch (e) {
        alert('操作失败')
      }
    },
    
    copyShareLink() {
      navigator.clipboard.writeText(this.shareUrl).then(() => {
        alert('链接已复制！')
      })
    },
    
    exportImage() {
      alert('导出功能提示：请使用浏览器截图功能保存当前对比视图，或使用系统截图工具。\n\n完整的Canvas导出功能需要额外的图像合成处理。')
    },
    
    exportReport() {
      const reportLines = []
      reportLines.push('=== 图片对比报告 ===')
      reportLines.push(`生成时间: ${new Date().toLocaleString()}`)
      reportLines.push(`对比模式: ${this.modes.find(m => m.id === this.currentMode)?.name || this.currentMode}`)
      reportLines.push('')
      reportLines.push('参与对比的图片:')
      this.pictures.forEach((p, i) => {
        reportLines.push(`  ${i + 1}. ${p.name} (ID: ${p.id})`)
      })
      reportLines.push('')
      reportLines.push(`标注总数: ${this.annotations.length}`)
      if (this.annotations.length > 0) {
        reportLines.push('标注详情:')
        const toolNames = { point: '标记点', rect: '矩形', circle: '圆圈', arrow: '箭头', freehand: '画笔', text: '文字' }
        this.annotations.forEach((a, i) => {
          const pic = this.pictures.find(p => p.id === a.pictureId)
          let desc = `  ${i + 1}. [${toolNames[a.toolType] || a.toolType}] 图片: ${pic?.name || '未知'}`
          if (a.textContent) desc += ` - "${a.textContent}"`
          reportLines.push(desc)
        })
      }
      
      const blob = new Blob([reportLines.join('\n')], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `对比报告_${Date.now()}.txt`
      a.click()
      URL.revokeObjectURL(url)
    }
  }
}
</script>
