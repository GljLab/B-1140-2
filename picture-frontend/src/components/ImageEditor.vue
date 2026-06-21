<template>
  <div v-if="visible" class="fixed inset-0 z-[100] flex items-center justify-center">
    <div class="absolute inset-0 bg-black/80" @click="handleClose"></div>
    <div class="relative w-[98vw] h-[96vh] bg-gray-900 rounded-2xl shadow-2xl overflow-hidden flex flex-col">
      <!-- Header -->
      <div class="flex items-center justify-between px-6 py-3 bg-gray-800/90 border-b border-gray-700">
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-gradient-to-tr from-blue-500 via-purple-500 to-pink-500 rounded-lg flex items-center justify-center text-white font-bold text-sm">E</div>
          <h2 class="text-white font-semibold">图片编辑器</h2>
          <span class="text-xs text-gray-400 bg-gray-700/50 px-2 py-0.5 rounded">{{ imageName }}</span>
        </div>
        <div class="flex items-center space-x-2">
          <button @click="resetAll" class="px-4 py-2 text-sm text-gray-300 hover:text-white hover:bg-gray-700 rounded-lg transition flex items-center space-x-1">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            <span>重置</span>
          </button>
          <button @click="handleClose" class="p-2 text-gray-400 hover:text-white hover:bg-gray-700 rounded-lg transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Body -->
      <div class="flex-1 flex overflow-hidden">
        <!-- Left Toolbar -->
        <div class="w-14 bg-gray-800/70 border-r border-gray-700 flex flex-col items-center py-3 space-y-1">
          <button v-for="tool in tools" :key="tool.id" @click="activeTool = tool.id"
            :title="tool.name"
            :class="['w-10 h-10 rounded-lg flex items-center justify-center transition',
              activeTool === tool.id ? 'bg-blue-500 text-white shadow-lg shadow-blue-500/30' : 'text-gray-400 hover:text-white hover:bg-gray-700']">
            <svg class="w-5 h-5" :viewBox="tool.viewbox || '0 0 24 24'" fill="none" stroke="currentColor" stroke-width="2">
              <path :stroke-linecap="tool.linecap || 'round'" :stroke-linejoin="tool.linejoin || 'round'" :d="tool.icon" />
            </svg>
          </button>
        </div>

        <!-- Canvas Area -->
        <div class="flex-1 bg-gray-950 flex items-center justify-center overflow-auto p-8 relative" ref="canvasContainer">
          <div class="relative" ref="canvasWrapper" :style="canvasWrapperStyle">
            <!-- Crop Overlay -->
            <div v-if="activeTool === 'crop' && !cropApplied" class="absolute inset-0 z-20 cursor-crosshair"
              @mousedown="startCrop" @mousemove="updateCrop" @mouseup="endCrop" @mouseleave="endCrop">
              <div v-if="cropSelection.startX !== null" class="absolute border-2 border-blue-400"
                :style="cropSelectionStyle">
                <div class="absolute -inset-[3px] border border-white/30"></div>
                <!-- Corner handles -->
                <div v-for="handle in cropHandles" :key="handle.id"
                  @mousedown.stop="startCropResize($event, handle.id)"
                  :class="['absolute w-3 h-3 bg-white border-2 border-blue-500 rounded-sm', handle.class]"></div>
              </div>
              <!-- Dim overlay outside selection -->
              <div v-if="cropSelection.startX !== null" class="absolute inset-0 pointer-events-none">
                <div class="absolute top-0 left-0 right-0 bg-black/50" :style="{ height: cropSelection.y + 'px' }"></div>
                <div class="absolute bottom-0 left-0 right-0 bg-black/50" :style="{ height: (canvasHeight - cropSelection.y - cropSelection.height) + 'px' }"></div>
                <div class="absolute left-0 bg-black/50" :style="{ top: cropSelection.y + 'px', height: cropSelection.height + 'px', width: cropSelection.x + 'px' }"></div>
                <div class="absolute right-0 bg-black/50" :style="{ top: cropSelection.y + 'px', height: cropSelection.height + 'px', width: (canvasWidth - cropSelection.x - cropSelection.width) + 'px' }"></div>
                <!-- Crop ratio lines (rule of thirds) -->
                <div class="absolute pointer-events-none" :style="cropSelectionStyle">
                  <div class="absolute top-1/3 left-0 right-0 border-t border-white/30"></div>
                  <div class="absolute top-2/3 left-0 right-0 border-t border-white/30"></div>
                  <div class="absolute left-1/3 top-0 bottom-0 border-l border-white/30"></div>
                  <div class="absolute left-2/3 top-0 bottom-0 border-l border-white/30"></div>
                </div>
              </div>
            </div>

            <canvas ref="mainCanvas" class="shadow-2xl block"></canvas>

            <!-- Text Overlays -->
            <div v-for="(txt, idx) in textItems" :key="txt.id"
              class="absolute z-10 select-none cursor-move"
              :class="{ 'ring-2 ring-blue-400': selectedTextId === txt.id }"
              :style="textItemStyle(txt)"
              @mousedown.stop="startDragText($event, idx)"
              @dblclick.stop="startEditText(idx)">
              <span v-if="!editingTextId || editingTextId !== txt.id"
                :style="{ color: txt.color, fontSize: txt.size + 'px', fontWeight: txt.bold ? 'bold' : 'normal', fontStyle: txt.italic ? 'italic' : 'normal', textShadow: '1px 1px 2px rgba(0,0,0,0.5)' }">{{ txt.content }}</span>
              <input v-else ref="textInputRef"
                v-model="editingTextContent"
                @keyup.enter="finishEditText(idx)"
                @blur="finishEditText(idx)"
                @mousedown.stop
                class="bg-transparent outline-none border-none"
                :style="{ color: txt.color, fontSize: txt.size + 'px', fontWeight: txt.bold ? 'bold' : 'normal', fontStyle: txt.italic ? 'italic' : 'normal', minWidth: '50px' }" />
              <div v-if="selectedTextId === txt.id" class="absolute -top-2 -right-2 z-30">
                <button @mousedown.stop="removeText(idx)" class="w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center hover:bg-red-600 transition">×</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Panel -->
        <div class="w-72 bg-gray-800/90 border-l border-gray-700 overflow-y-auto">
          <!-- Crop Options -->
          <div v-if="activeTool === 'crop'" class="p-4 space-y-4">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 16v2a2 2 0 002 2h2M4 8V6a2 2 0 012-2h2m8 16h2a2 2 0 002-2v-2m0-8V6a2 2 0 00-2-2h-2" />
              </svg>
              <span>裁剪</span>
            </h3>
            <div>
              <label class="text-xs text-gray-400 mb-2 block">裁剪比例</label>
              <div class="grid grid-cols-4 gap-1.5">
                <button v-for="r in cropRatios" :key="r.label" @click="setCropRatio(r.ratio)"
                  :class="['px-2 py-1.5 text-xs rounded-lg transition',
                    currentCropRatio === r.label ? 'bg-blue-500 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600']">
                  {{ r.label }}
                </button>
              </div>
            </div>
            <div class="flex space-x-2">
              <button @click="applyCrop" :disabled="cropSelection.width === 0"
                :class="['flex-1 py-2 text-sm rounded-lg transition font-medium',
                  cropSelection.width > 0 ? 'bg-blue-500 text-white hover:bg-blue-600' : 'bg-gray-700 text-gray-500 cursor-not-allowed']">应用裁剪</button>
              <button @click="cancelCrop" class="flex-1 py-2 text-sm rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 transition font-medium">取消</button>
            </div>
            <p class="text-[10px] text-gray-500">在图片上拖动鼠标选择裁剪区域</p>
          </div>

          <!-- Rotate/Flip Options -->
          <div v-if="activeTool === 'rotate'" class="p-4 space-y-4">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>旋转 / 翻转</span>
            </h3>
            <div class="grid grid-cols-2 gap-2">
              <button @click="rotate(-90)" class="p-3 bg-gray-700 hover:bg-gray-600 rounded-xl text-gray-200 transition flex flex-col items-center space-y-1">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                </svg>
                <span class="text-xs">左旋转 90°</span>
              </button>
              <button @click="rotate(90)" class="p-3 bg-gray-700 hover:bg-gray-600 rounded-xl text-gray-200 transition flex flex-col items-center space-y-1">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M21 10h-10a8 8 0 00-8 8v2M21 10l-6 6m6-6l-6-6" />
                </svg>
                <span class="text-xs">右旋转 90°</span>
              </button>
              <button @click="flipH" class="p-3 bg-gray-700 hover:bg-gray-600 rounded-xl text-gray-200 transition flex flex-col items-center space-y-1">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8 7h8m-8 5h8m-8 5h8M12 3v18" />
                </svg>
                <span class="text-xs">水平翻转</span>
              </button>
              <button @click="flipV" class="p-3 bg-gray-700 hover:bg-gray-600 rounded-xl text-gray-200 transition flex flex-col items-center space-y-1">
                <svg class="w-6 h-6 rotate-90" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8 7h8m-8 5h8m-8 5h8M12 3v18" />
                </svg>
                <span class="text-xs">垂直翻转</span>
              </button>
            </div>
            <div>
              <label class="text-xs text-gray-400 mb-2 block">自由旋转角度: {{ adjustments.rotation }}°</label>
              <input type="range" v-model.number="adjustments.rotation" min="-180" max="180" step="1"
                @input="render" class="w-full accent-blue-500" />
            </div>
          </div>

          <!-- Resize Options -->
          <div v-if="activeTool === 'resize'" class="p-4 space-y-4">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
              </svg>
              <span>调整尺寸</span>
            </h3>
            <div>
              <label class="text-xs text-gray-400 mb-2 block">缩放比例: {{ adjustments.scale }}%</label>
              <input type="range" v-model.number="adjustments.scale" min="10" max="300" step="1"
                @input="render" class="w-full accent-blue-500" />
            </div>
            <div class="space-y-2">
              <div>
                <label class="text-xs text-gray-400 mb-1 block">宽度 (px)</label>
                <input type="number" v-model.number="targetWidth" @input="onWidthChange"
                  class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label class="text-xs text-gray-400 mb-1 block">高度 (px)</label>
                <input type="number" v-model.number="targetHeight" @input="onHeightChange"
                  class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <label class="flex items-center space-x-2 cursor-pointer text-xs text-gray-400">
                <input type="checkbox" v-model="keepAspectRatio" class="w-4 h-4 accent-blue-500 rounded" />
                <span>锁定宽高比</span>
              </label>
            </div>
            <button @click="applyResize"
              class="w-full py-2 text-sm rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition font-medium">应用尺寸</button>
          </div>

          <!-- Filter Options -->
          <div v-if="activeTool === 'filter'" class="p-4 space-y-4">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              <span>滤镜效果</span>
            </h3>
            <div class="grid grid-cols-3 gap-2">
              <div v-for="f in filters" :key="f.id" @click="selectFilter(f)"
                :class="['cursor-pointer rounded-lg overflow-hidden border-2 transition',
                  activeFilter.id === f.id ? 'border-blue-500 shadow-lg shadow-blue-500/30' : 'border-transparent hover:border-gray-600']">
                <div class="aspect-square bg-gray-700 overflow-hidden">
                  <div class="w-full h-full bg-cover bg-center" :style="{ backgroundImage: `url(${imageUrl})`, filter: f.filter }"></div>
                </div>
                <div class="p-1.5 bg-gray-700/50 text-center">
                  <span class="text-[10px] text-gray-300">{{ f.name }}</span>
                </div>
              </div>
            </div>
            <div v-if="activeFilter.id !== 'none'">
              <label class="text-xs text-gray-400 mb-2 block">滤镜强度: {{ filterIntensity }}%</label>
              <input type="range" v-model.number="filterIntensity" min="0" max="100" step="1"
                @input="render" class="w-full accent-blue-500" />
            </div>
          </div>

          <!-- Adjust Options -->
          <div v-if="activeTool === 'adjust'" class="p-4 space-y-5">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4" />
              </svg>
              <span>调整参数</span>
            </h3>
            <div v-for="adj in adjustmentFields" :key="adj.key">
              <div class="flex justify-between mb-1.5">
                <label class="text-xs text-gray-400">{{ adj.label }}</label>
                <span class="text-xs text-gray-500">{{ adjustments[adj.key] }}</span>
              </div>
              <input type="range" v-model.number="adjustments[adj.key]" :min="adj.min" :max="adj.max" :step="adj.step"
                @input="render" class="w-full accent-blue-500" />
            </div>
          </div>

          <!-- Text Options -->
          <div v-if="activeTool === 'text'" class="p-4 space-y-4">
            <h3 class="text-white font-semibold text-sm flex items-center space-x-2">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              <span>文字标注</span>
            </h3>
            <button @click="addText"
              class="w-full py-2.5 text-sm rounded-lg bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg transition font-medium flex items-center justify-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              <span>添加文字</span>
            </button>
            <div v-if="selectedTextItem" class="space-y-3 pt-3 border-t border-gray-700">
              <div>
                <label class="text-xs text-gray-400 mb-1.5 block">文字内容</label>
                <input v-model="selectedTextItem.content" @input="render"
                  class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label class="text-xs text-gray-400 mb-1.5 block">字体大小: {{ selectedTextItem.size }}px</label>
                <input type="range" v-model.number="selectedTextItem.size" min="12" max="120" step="1"
                  @input="render" class="w-full accent-blue-500" />
              </div>
              <div>
                <label class="text-xs text-gray-400 mb-1.5 block">颜色</label>
                <div class="flex space-x-2">
                  <div v-for="c in textColors" :key="c" @click="selectedTextItem.color = c; render()"
                    :class="['w-7 h-7 rounded-full cursor-pointer border-2 transition',
                      selectedTextItem.color === c ? 'border-white scale-110' : 'border-gray-600']"
                    :style="{ backgroundColor: c }"></div>
                </div>
                <div class="mt-2">
                  <input type="color" v-model="selectedTextItem.color" @input="render"
                    class="w-full h-8 rounded cursor-pointer" />
                </div>
              </div>
              <div class="flex space-x-2">
                <button @click="selectedTextItem.bold = !selectedTextItem.bold; render()"
                  :class="['flex-1 py-2 text-sm rounded-lg transition font-bold',
                    selectedTextItem.bold ? 'bg-blue-500 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600']">B</button>
                <button @click="selectedTextItem.italic = !selectedTextItem.italic; render()"
                  :class="['flex-1 py-2 text-sm rounded-lg transition italic',
                    selectedTextItem.italic ? 'bg-blue-500 text-white' : 'bg-gray-700 text-gray-300 hover:bg-gray-600']">I</button>
              </div>
            </div>
            <div v-else-if="textItems.length > 0" class="text-xs text-gray-500 text-center pt-2">
              点击画布上的文字进行编辑
            </div>
          </div>
        </div>
      </div>

      <!-- Footer / Save Bar -->
      <div class="px-6 py-3 bg-gray-800/90 border-t border-gray-700 flex items-center justify-between">
        <div class="flex items-center space-x-4 text-xs text-gray-400">
          <span>尺寸: <span class="text-gray-300">{{ currentWidth }} × {{ currentHeight }}px</span></span>
          <span class="h-4 w-px bg-gray-700"></span>
          <span>已修改: <span :class="hasChanges ? 'text-blue-400' : 'text-gray-500'">{{ hasChanges ? '是' : '否' }}</span></span>
        </div>
        <div class="flex items-center space-x-2">
          <button @click="handleClose"
            class="px-5 py-2 text-sm bg-gray-700 text-gray-300 hover:bg-gray-600 rounded-lg transition font-medium">取消</button>
          <button @click="showSaveOptions = true"
            class="px-5 py-2 text-sm bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:shadow-lg transition font-medium flex items-center space-x-1.5">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
            </svg>
            <span>保存</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Save Options Modal -->
    <div v-if="showSaveOptions" class="fixed inset-0 z-[200] flex items-center justify-center">
      <div class="absolute inset-0 bg-black/50" @click="showSaveOptions = false"></div>
      <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden transform">
        <div class="p-5 border-b border-gray-100">
          <h3 class="text-lg font-bold text-gray-800">保存方式</h3>
        </div>
        <div class="p-5 space-y-3">
          <button @click="doSave('overwrite')"
            class="w-full p-4 rounded-xl border-2 border-gray-200 hover:border-blue-500 hover:bg-blue-50 transition text-left group">
            <div class="flex items-start space-x-3">
              <div class="w-10 h-10 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center group-hover:bg-blue-500 group-hover:text-white transition">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </div>
              <div class="flex-1">
                <h4 class="font-semibold text-gray-800">覆盖原图</h4>
                <p class="text-xs text-gray-500 mt-0.5">替换当前图片，原图片将被替换</p>
              </div>
            </div>
          </button>
          <button @click="doSave('new')"
            class="w-full p-4 rounded-xl border-2 border-gray-200 hover:border-purple-500 hover:bg-purple-50 transition text-left group">
            <div class="flex items-start space-x-3">
              <div class="w-10 h-10 rounded-lg bg-purple-100 text-purple-600 flex items-center justify-center group-hover:bg-purple-500 group-hover:text-white transition">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                </svg>
              </div>
              <div class="flex-1">
                <h4 class="font-semibold text-gray-800">另存为新图片</h4>
                <p class="text-xs text-gray-500 mt-0.5">保存为新图片，原图片保持不变</p>
                <p class="text-xs text-purple-600 mt-1">文件名：{{ newFileName }}</p>
              </div>
            </div>
          </button>
        </div>
        <div class="px-5 py-3 bg-gray-50 flex justify-end">
          <button @click="showSaveOptions = false"
            class="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition">取消</button>
        </div>
      </div>
    </div>

    <!-- Loading Overlay -->
    <div v-if="saving" class="fixed inset-0 z-[300] flex items-center justify-center bg-black/50">
      <div class="bg-white rounded-2xl p-6 flex flex-col items-center space-y-3 shadow-2xl">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500"></div>
        <p class="text-sm font-medium text-gray-700">{{ saveStatus || '正在保存...' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue';
import axios from 'axios';
const props = defineProps({
 visible: { type: Boolean, default: false },
 imageUrl: { type: String, default: '' },
 imageId: { type: [Number, String], default: null },
 imageName: { type: String, default: 'image' },
 imageAlbumIds: { type: Array, default: () => [] },
 imageTagNames: { type: Array, default: () => [] },
 api: { type: Object, default: null }
});
const emit = defineEmits(['update:visible', 'saved', 'close']);
// State
const activeTool = ref('adjust');
const showSaveOptions = ref(false);
const saving = ref(false);
const saveStatus = ref('');
const mainCanvas = ref(null);
const canvasContainer = ref(null);
const canvasWrapper = ref(null);
const textInputRef = ref(null);
const originalImage = ref(null);
const canvasWidth = ref(0);
const canvasHeight = ref(0);
const currentWidth = ref(0);
const currentHeight = ref(0);
const targetWidth = ref(0);
const targetHeight = ref(0);
const keepAspectRatio = ref(true);
const aspectRatio = ref(1);
const hasChanges = ref(false);
const activeFilter = ref({ id: 'none', name: '原图', filter: 'none' });
const filterIntensity = ref(100);
const currentCropRatio = ref('自由');
const cropApplied = ref(false);
const adjustments = reactive({
 brightness: 100,
 contrast: 100,
 saturation: 100,
 sharpness: 0,
 rotation: 0,
 scale: 100
});
const initialAdjustments = {
 brightness: 100,
 contrast: 100,
 saturation: 100,
 sharpness: 0,
 rotation: 0,
 scale: 100
};
const textItems = ref([]);
const selectedTextId = ref(null);
const editingTextId = ref(null);
const editingTextContent = ref('');
let nextTextId = 1;
// Crop state
const cropSelection = reactive({
 startX: null,
 startY: null,
 x: 0,
 y: 0,
 width: 0,
 height: 0
});
let isCropping = false;
let cropResizeHandle = null;
// Text drag state
let isDraggingText = false;
let dragTextStartX = 0;
let dragTextStartY = 0;
let dragTextOrigX = 0;
let dragTextOrigY = 0;
let dragTextIndex = -1;
// Tools
const tools = [
 { id: 'crop', name: '裁剪', icon: 'M4 16v2a2 2 0 002 2h2M4 8V6a2 2 0 012-2h2m8 16h2a2 2 0 002-2v-2m0-8V6a2 2 0 00-2-2h-2M4 4l16 16' },
 { id: 'rotate', name: '旋转/翻转', icon: 'M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15' },
 { id: 'resize', name: '调整尺寸', icon: 'M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4' },
 { id: 'filter', name: '滤镜', icon: 'M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z' },
 { id: 'adjust', name: '调整', icon: 'M12 6V4m0 2a2 2 0 100 4m0-4a2 2 0 110 4m-6 8a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4m6 6v10m6-2a2 2 0 100-4m0 4a2 2 0 110-4m0 4v2m0-6V4' },
 { id: 'text', name: '文字', icon: 'M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z' }
];
const cropRatios = [
 { label: '自由', ratio: null },
 { label: '1:1', ratio: 1 },
 { label: '4:3', ratio: 4 / 3 },
 { label: '16:9', ratio: 16 / 9 },
 { label: '3:4', ratio: 3 / 4 },
 { label: '9:16', ratio: 9 / 16 },
 { label: '3:2', ratio: 3 / 2 },
 { label: '2:3', ratio: 2 / 3 }
];
const cropHandles = [
 { id: 'nw', class: 'top-0 left-0 -translate-x-1/2 -translate-y-1/2 cursor-nw-resize' },
 { id: 'ne', class: 'top-0 right-0 translate-x-1/2 -translate-y-1/2 cursor-ne-resize' },
 { id: 'sw', class: 'bottom-0 left-0 -translate-x-1/2 translate-y-1/2 cursor-sw-resize' },
 { id: 'se', class: 'bottom-0 right-0 translate-x-1/2 translate-y-1/2 cursor-se-resize' },
 { id: 'n', class: 'top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 cursor-n-resize' },
 { id: 's', class: 'bottom-0 left-1/2 -translate-x-1/2 translate-y-1/2 cursor-s-resize' },
 { id: 'w', class: 'top-1/2 left-0 -translate-x-1/2 -translate-y-1/2 cursor-w-resize' },
 { id: 'e', class: 'top-1/2 right-0 translate-x-1/2 -translate-y-1/2 cursor-e-resize' }
];
const filters = [
 { id: 'none', name: '原图', filter: 'none' },
 { id: 'grayscale', name: '黑白', filter: 'grayscale(100%)' },
 { id: 'sepia', name: '复古', filter: 'sepia(80%) contrast(110%)' },
 { id: 'vibrant', name: '鲜艳', filter: 'saturate(180%) contrast(110%) brightness(105%)' },
 { id: 'cool', name: '冷色调', filter: 'hue-rotate(-15deg) saturate(110%) brightness(105%)' },
 { id: 'warm', name: '暖色调', filter: 'sepia(30%) saturate(130%) hue-rotate(10deg)' },
 { id: 'fade', name: '褪色', filter: 'saturate(60%) contrast(90%) brightness(110%)' },
 { id: 'dramatic', name: '戏剧', filter: 'contrast(150%) saturate(120%) brightness(95%)' },
 { id: 'noir', name: '黑色电影', filter: 'grayscale(100%) contrast(140%) brightness(95%)' }
];
const adjustmentFields = [
 { key: 'brightness', label: '亮度', min: 0, max: 200, step: 1 },
 { key: 'contrast', label: '对比度', min: 0, max: 200, step: 1 },
 { key: 'saturation', label: '饱和度', min: 0, max: 200, step: 1 },
 { key: 'sharpness', label: '锐化', min: -100, max: 100, step: 1 }
];
const textColors = ['#ffffff', '#000000', '#ef4444', '#f97316', '#eab308', '#22c55e', '#3b82f6', '#8b5cf6', '#ec4899'];
// Computed
const newFileName = computed(() => {
 const name = props.imageName.replace(/\.[^.]+$/, '');
 const ext = (props.imageName.match(/\.[^.]+$/) || ['.jpg'])[0];
 return `${name}_edited${ext}`;
});
const selectedTextItem = computed(() => {
 const found = textItems.value.find(t => t.id === selectedTextId.value);
 return found || null;
});
const canvasWrapperStyle = computed(() => {
 const w = canvasWidth.value;
 const h = canvasHeight.value;
 const scale = adjustments.scale / 100;
 const rot = adjustments.rotation;
 return {
 width: w + 'px',
 height: h + 'px',
 transform: `scale(${scale}) rotate(${rot}deg)`,
 transformOrigin: 'center center',
 transition: 'transform 0.15s ease-out'
 };
});
const cropSelectionStyle = computed(() => ({
 left: cropSelection.x + 'px',
 top: cropSelection.y + 'px',
 width: cropSelection.width + 'px',
 height: cropSelection.height + 'px'
}));
const textItemStyle = (txt) => ({
 left: txt.x + 'px',
 top: txt.y + 'px'
});
// Methods
const loadImage = () => {
 if (!props.imageUrl)
 return;
 const img = new Image();
 img.crossOrigin = 'anonymous';
 img.onload = () => {
 originalImage.value = img;
 canvasWidth.value = img.width;
 canvasHeight.value = img.height;
 currentWidth.value = img.width;
 currentHeight.value = img.height;
 targetWidth.value = img.width;
 targetHeight.value = img.height;
 aspectRatio.value = img.width / img.height;
 nextTick(() => {
 const canvas = mainCanvas.value;
 if (canvas) {
 canvas.width = img.width;
 canvas.height = img.height;
 render();
 }
 });
 };
 img.onerror = () => {
 const ts = Date.now();
 const separator = props.imageUrl.includes('?') ? '&' : '?';
 const img2 = new Image();
 img2.crossOrigin = 'anonymous';
 img2.onload = () => {
 originalImage.value = img2;
 canvasWidth.value = img2.width;
 canvasHeight.value = img2.height;
 currentWidth.value = img2.width;
 currentHeight.value = img2.height;
 targetWidth.value = img2.width;
 targetHeight.value = img2.height;
 aspectRatio.value = img2.width / img2.height;
 nextTick(() => {
 const canvas = mainCanvas.value;
 if (canvas) {
 canvas.width = img2.width;
 canvas.height = img2.height;
 render();
 }
 });
 };
 img2.src = props.imageUrl + separator + '_t=' + ts;
 };
 img.src = props.imageUrl;
};
const getCSSFilter = () => {
 let parts = [];
 parts.push(`brightness(${adjustments.brightness}%)`);
 parts.push(`contrast(${adjustments.contrast}%)`);
 parts.push(`saturate(${adjustments.saturation}%)`);
 if (activeFilter.value.id !== 'none' && filterIntensity.value > 0) {
 const intensity = filterIntensity.value / 100;
 const baseFilter = activeFilter.value.filter;
 const reducedFilter = reduceFilterIntensity(baseFilter, intensity);
 parts.push(reducedFilter);
 }
 return parts.join(' ');
};
const reduceFilterIntensity = (filterStr, intensity) => {
 return filterStr.replace(/\(([^)]+)\)/g, (match, val) => {
 const num = parseFloat(val);
 if (val.includes('%')) {
 const newVal = 100 + (num - 100) * intensity;
 return `(${newVal}%)`;
 }
 else if (val.includes('deg')) {
 const newVal = num * intensity;
 return `(${newVal}deg)`;
 }
 else {
 const newVal = num * intensity;
 return `(${newVal})`;
 }
 });
};
const render = () => {
 if (!originalImage.value || !mainCanvas.value)
 return;
 const canvas = mainCanvas.value;
 const ctx = canvas.getContext('2d');
 canvas.width = canvasWidth.value;
 canvas.height = canvasHeight.value;
 ctx.clearRect(0, 0, canvas.width, canvas.height);
 ctx.save();
 ctx.filter = getCSSFilter();
 ctx.drawImage(originalImage.value, 0, 0, canvas.width, canvas.height);
 ctx.restore();
 // Sharpness via convolution
 if (adjustments.sharpness !== 0) {
 const sharpAmount = adjustments.sharpness / 100;
 const kernel = [
 0, -sharpAmount, 0,
 -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
 0, -sharpAmount, 0
 ];
 applyConvolution(ctx, canvas, kernel);
 }
 hasChanges.value = checkHasChanges();
};
const applyConvolution = (ctx, canvas, kernel) => {
 const w = canvas.width;
 const h = canvas.height;
 const imageData = ctx.getImageData(0, 0, w, h);
 const src = imageData.data;
 const dst = new Uint8ClampedArray(src);
 const kSize = 3;
 const halfK = Math.floor(kSize / 2);
 for (let y = 0; y < h; y++) {
 for (let x = 0; x < w; x++) {
 let r = 0, g = 0, b = 0;
 for (let ky = 0; ky < kSize; ky++) {
 for (let kx = 0; kx < kSize; kx++) {
 const px = Math.min(w - 1, Math.max(0, x + kx - halfK));
 const py = Math.min(h - 1, Math.max(0, y + ky - halfK));
 const idx = (py * w + px) * 4;
 const k = kernel[ky * kSize + kx];
 r += src[idx] * k;
 g += src[idx + 1] * k;
 b += src[idx + 2] * k;
 }
 }
 const idx = (y * w + x) * 4;
 dst[idx] = Math.min(255, Math.max(0, r));
 dst[idx + 1] = Math.min(255, Math.max(0, g));
 dst[idx + 2] = Math.min(255, Math.max(0, b));
 }
 }
 const newImageData = new ImageData(dst, w, h);
 ctx.putImageData(newImageData, 0, 0);
};
const checkHasChanges = () => {
 for (const k of Object.keys(initialAdjustments)) {
 if (adjustments[k] !== initialAdjustments[k])
 return true;
 }
 if (activeFilter.value.id !== 'none' && filterIntensity.value > 0)
 return true;
 if (textItems.value.length > 0)
 return true;
 if (currentWidth.value !== originalImage.value?.width)
 return true;
 if (currentHeight.value !== originalImage.value?.height)
 return true;
 return false;
};
// Crop
const startCrop = (e) => {
 const rect = e.currentTarget.getBoundingClientRect();
 cropSelection.startX = e.clientX - rect.left;
 cropSelection.startY = e.clientY - rect.top;
 cropSelection.x = cropSelection.startX;
 cropSelection.y = cropSelection.startY;
 cropSelection.width = 0;
 cropSelection.height = 0;
 isCropping = true;
 cropResizeHandle = null;
};
const startCropResize = (e, handleId) => {
 cropResizeHandle = handleId;
 isCropping = true;
 e.preventDefault();
};
const updateCrop = (e) => {
 if (!isCropping)
 return;
 const rect = e.currentTarget.getBoundingClientRect();
 const x = Math.max(0, Math.min(canvasWidth.value, e.clientX - rect.left));
 const y = Math.max(0, Math.min(canvasHeight.value, e.clientY - rect.top));
 if (cropResizeHandle) {
 resizeCropSelection(x, y);
 }
 else {
 let dx = x - cropSelection.startX;
 let dy = y - cropSelection.startY;
 const ratio = cropRatios.find(r => r.label === currentCropRatio.value)?.ratio;
 if (ratio) {
 if (Math.abs(dx) > Math.abs(dy * ratio)) {
 dy = dx / ratio;
 }
 else {
 dx = dy * ratio;
 }
 }
 cropSelection.x = dx >= 0 ? cropSelection.startX : cropSelection.startX + dx;
 cropSelection.y = dy >= 0 ? cropSelection.startY : cropSelection.startY + dy;
 cropSelection.width = Math.abs(dx);
 cropSelection.height = Math.abs(dy);
 // Keep within bounds
 cropSelection.x = Math.max(0, cropSelection.x);
 cropSelection.y = Math.max(0, cropSelection.y);
 if (cropSelection.x + cropSelection.width > canvasWidth.value) {
 cropSelection.width = canvasWidth.value - cropSelection.x;
 }
 if (cropSelection.y + cropSelection.height > canvasHeight.value) {
 cropSelection.height = canvasHeight.value - cropSelection.y;
 }
 }
};
const resizeCropSelection = (x, y) => {
 let left = cropSelection.x;
 let top = cropSelection.y;
 let right = cropSelection.x + cropSelection.width;
 let bottom = cropSelection.y + cropSelection.height;
 switch (cropResizeHandle) {
 case 'nw': left = x; top = y; break;
 case 'ne': right = x; top = y; break;
 case 'sw': left = x; bottom = y; break;
 case 'se': right = x; bottom = y; break;
 case 'n': top = y; break;
 case 's': bottom = y; break;
 case 'w': left = x; break;
 case 'e': right = x; break;
 }
 const ratio = cropRatios.find(r => r.label === currentCropRatio.value)?.ratio;
 if (ratio) {
 const w = right - left;
 const h = bottom - top;
 if (cropResizeHandle.includes('n') || cropResizeHandle.includes('s')) {
 const newH = h;
 const newW = newH * ratio;
 if (cropResizeHandle.includes('e'))
 right = left + newW;
 else
 left = right - newW;
 }
 else {
 const newW = w;
 const newH = newW / ratio;
 if (cropResizeHandle.includes('s'))
 bottom = top + newH;
 else
 top = bottom - newH;
 }
 }
 left = Math.max(0, left);
 top = Math.max(0, top);
 right = Math.min(canvasWidth.value, right);
 bottom = Math.min(canvasHeight.value, bottom);
 cropSelection.x = left;
 cropSelection.y = top;
 cropSelection.width = right - left;
 cropSelection.height = bottom - top;
};
const endCrop = () => {
 isCropping = false;
 cropResizeHandle = null;
};
const setCropRatio = (ratio) => {
 currentCropRatio.value = cropRatios.find(r => r.ratio === ratio || (r.label === '自由' && ratio === null))?.label || '自由';
 if (cropSelection.width > 0 && ratio) {
 // Adjust selection to match ratio
 if (cropSelection.width > cropSelection.height * ratio) {
 cropSelection.height = cropSelection.width / ratio;
 }
 else {
 cropSelection.width = cropSelection.height * ratio;
 }
 }
};
const applyCrop = () => {
 if (cropSelection.width < 10 || cropSelection.height < 10)
 return;
 // Render current state to temp canvas
 const tempCanvas = document.createElement('canvas');
 tempCanvas.width = canvasWidth.value;
 tempCanvas.height = canvasHeight.value;
 const tempCtx = tempCanvas.getContext('2d');
 tempCtx.filter = getCSSFilter();
 tempCtx.drawImage(originalImage.value, 0, 0, tempCanvas.width, tempCanvas.height);
 // Apply sharpness too
 if (adjustments.sharpness !== 0) {
 const sharpAmount = adjustments.sharpness / 100;
 const kernel = [
 0, -sharpAmount, 0,
 -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
 0, -sharpAmount, 0
 ];
 applyConvolution(tempCtx, tempCanvas, kernel);
 }
 // Crop
 const croppedCanvas = document.createElement('canvas');
 croppedCanvas.width = Math.floor(cropSelection.width);
 croppedCanvas.height = Math.floor(cropSelection.height);
 const cropCtx = croppedCanvas.getContext('2d');
 cropCtx.drawImage(tempCanvas, Math.floor(cropSelection.x), Math.floor(cropSelection.y), croppedCanvas.width, croppedCanvas.height, 0, 0, croppedCanvas.width, croppedCanvas.height);
 // Reset adjustments, set cropped image as base
 const croppedImg = new Image();
 croppedImg.onload = () => {
 originalImage.value = croppedImg;
 canvasWidth.value = croppedImg.width;
 canvasHeight.value = croppedImg.height;
 currentWidth.value = croppedImg.width;
 currentHeight.value = croppedImg.height;
 targetWidth.value = croppedImg.width;
 targetHeight.value = croppedImg.height;
 aspectRatio.value = croppedImg.width / croppedImg.height;
 // Reset adjustments
 Object.assign(adjustments, initialAdjustments);
 activeFilter.value = filters[0];
 filterIntensity.value = 100;
 // Reset crop selection
 cropSelection.startX = null;
 cropSelection.startY = null;
 cropSelection.x = 0;
 cropSelection.y = 0;
 cropSelection.width = 0;
 cropSelection.height = 0;
 cropApplied.value = true;
 nextTick(() => {
 render();
 });
 };
 croppedImg.src = croppedCanvas.toDataURL();
 hasChanges.value = true;
};
const cancelCrop = () => {
 cropSelection.startX = null;
 cropSelection.startY = null;
 cropSelection.x = 0;
 cropSelection.y = 0;
 cropSelection.width = 0;
 cropSelection.height = 0;
};
// Rotate / Flip
const rotate = (degrees) => {
 if (!originalImage.value)
 return;
 const tempCanvas = document.createElement('canvas');
 const tempCtx = tempCanvas.getContext('2d');
 const isOdd90 = Math.abs(degrees) % 180 !== 0;
 if (isOdd90) {
 tempCanvas.width = canvasHeight.value;
 tempCanvas.height = canvasWidth.value;
 }
 else {
 tempCanvas.width = canvasWidth.value;
 tempCanvas.height = canvasHeight.value;
 }
 // First render current state with filters
 const stateCanvas = document.createElement('canvas');
 stateCanvas.width = canvasWidth.value;
 stateCanvas.height = canvasHeight.value;
 const stateCtx = stateCanvas.getContext('2d');
 stateCtx.filter = getCSSFilter();
 stateCtx.drawImage(originalImage.value, 0, 0);
 if (adjustments.sharpness !== 0) {
 const sharpAmount = adjustments.sharpness / 100;
 const kernel = [
 0, -sharpAmount, 0,
 -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
 0, -sharpAmount, 0
 ];
 applyConvolution(stateCtx, stateCanvas, kernel);
 }
 // Now rotate
 tempCtx.save();
 tempCtx.translate(tempCanvas.width / 2, tempCanvas.height / 2);
 tempCtx.rotate(degrees * Math.PI / 180);
 tempCtx.drawImage(stateCanvas, -stateCanvas.width / 2, -stateCanvas.height / 2);
 tempCtx.restore();
 const newImg = new Image();
 newImg.onload = () => {
 originalImage.value = newImg;
 canvasWidth.value = newImg.width;
 canvasHeight.value = newImg.height;
 currentWidth.value = newImg.width;
 currentHeight.value = newImg.height;
 targetWidth.value = newImg.width;
 targetHeight.value = newImg.height;
 aspectRatio.value = newImg.width / newImg.height;
 Object.assign(adjustments, initialAdjustments);
 activeFilter.value = filters[0];
 filterIntensity.value = 100;
 nextTick(render);
 };
 newImg.src = tempCanvas.toDataURL();
 hasChanges.value = true;
};
const flipH = () => {
 if (!originalImage.value)
 return;
 flipCanvas(1, -1);
};
const flipV = () => {
 if (!originalImage.value)
 return;
 flipCanvas(-1, 1);
};
const flipCanvas = (sX, sY) => {
 const tempCanvas = document.createElement('canvas');
 tempCanvas.width = canvasWidth.value;
 tempCanvas.height = canvasHeight.value;
 const tempCtx = tempCanvas.getContext('2d');
 const stateCanvas = document.createElement('canvas');
 stateCanvas.width = canvasWidth.value;
 stateCanvas.height = canvasHeight.value;
 const stateCtx = stateCanvas.getContext('2d');
 stateCtx.filter = getCSSFilter();
 stateCtx.drawImage(originalImage.value, 0, 0);
 if (adjustments.sharpness !== 0) {
 const sharpAmount = adjustments.sharpness / 100;
 const kernel = [
 0, -sharpAmount, 0,
 -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
 0, -sharpAmount, 0
 ];
 applyConvolution(stateCtx, stateCanvas, kernel);
 }
 tempCtx.save();
 tempCtx.translate(tempCanvas.width / 2, tempCanvas.height / 2);
 tempCtx.scale(sX, sY);
 tempCtx.drawImage(stateCanvas, -stateCanvas.width / 2, -stateCanvas.height / 2);
 tempCtx.restore();
 const newImg = new Image();
 newImg.onload = () => {
 originalImage.value = newImg;
 Object.assign(adjustments, initialAdjustments);
 activeFilter.value = filters[0];
 filterIntensity.value = 100;
 nextTick(render);
 };
 newImg.src = tempCanvas.toDataURL();
 hasChanges.value = true;
};
// Resize
const onWidthChange = () => {
 if (keepAspectRatio.value && targetWidth.value > 0) {
 targetHeight.value = Math.round(targetWidth.value / aspectRatio.value);
 }
};
const onHeightChange = () => {
 if (keepAspectRatio.value && targetHeight.value > 0) {
 targetWidth.value = Math.round(targetHeight.value * aspectRatio.value);
 }
};
const applyResize = () => {
 if (targetWidth.value <= 0 || targetHeight.value <= 0)
 return;
 const tempCanvas = document.createElement('canvas');
 tempCanvas.width = targetWidth.value;
 tempCanvas.height = targetHeight.value;
 const tempCtx = tempCanvas.getContext('2d');
 tempCtx.imageSmoothingEnabled = true;
 tempCtx.imageSmoothingQuality = 'high';
 const stateCanvas = document.createElement('canvas');
 stateCanvas.width = canvasWidth.value;
 stateCanvas.height = canvasHeight.value;
 const stateCtx = stateCanvas.getContext('2d');
 stateCtx.filter = getCSSFilter();
 stateCtx.drawImage(originalImage.value, 0, 0);
 if (adjustments.sharpness !== 0) {
 const sharpAmount = adjustments.sharpness / 100;
 const kernel = [
 0, -sharpAmount, 0,
 -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
 0, -sharpAmount, 0
 ];
 applyConvolution(stateCtx, stateCanvas, kernel);
 }
 tempCtx.drawImage(stateCanvas, 0, 0, stateCanvas.width, stateCanvas.height, 0, 0, tempCanvas.width, tempCanvas.height);
 const newImg = new Image();
 newImg.onload = () => {
 originalImage.value = newImg;
 canvasWidth.value = newImg.width;
 canvasHeight.value = newImg.height;
 currentWidth.value = newImg.width;
 currentHeight.value = newImg.height;
 targetWidth.value = newImg.width;
 targetHeight.value = newImg.height;
 aspectRatio.value = newImg.width / newImg.height;
 adjustments.scale = 100;
 Object.assign(adjustments, initialAdjustments);
 activeFilter.value = filters[0];
 filterIntensity.value = 100;
 nextTick(render);
 };
 newImg.src = tempCanvas.toDataURL();
 hasChanges.value = true;
};
// Filters
const selectFilter = (f) => {
 activeFilter.value = f;
 filterIntensity.value = 100;
 render();
};
// Text
const addText = () => {
 const newText = {
 id: nextTextId++,
 content: '双击编辑文字',
 x: Math.max(20, canvasWidth.value / 2 - 60),
 y: Math.max(20, canvasHeight.value / 2),
 size: 32,
 color: '#ffffff',
 bold: false,
 italic: false
 };
 textItems.value.push(newText);
 selectedTextId.value = newText.id;
 hasChanges.value = true;
};
const startDragText = (e, idx) => {
 isDraggingText = true;
 dragTextIndex = idx;
 dragTextStartX = e.clientX;
 dragTextStartY = e.clientY;
 dragTextOrigX = textItems.value[idx].x;
 dragTextOrigY = textItems.value[idx].y;
 selectedTextId.value = textItems.value[idx].id;
 document.addEventListener('mousemove', onDragText);
 document.addEventListener('mouseup', endDragText);
};
const onDragText = (e) => {
 if (!isDraggingText || dragTextIndex < 0)
 return;
 const dx = e.clientX - dragTextStartX;
 const dy = e.clientY - dragTextStartY;
 textItems.value[dragTextIndex].x = dragTextOrigX + dx;
 textItems.value[dragTextIndex].y = dragTextOrigY + dy;
};
const endDragText = () => {
 isDraggingText = false;
 dragTextIndex = -1;
 document.removeEventListener('mousemove', onDragText);
 document.removeEventListener('mouseup', endDragText);
 hasChanges.value = true;
};
const startEditText = (idx) => {
  editingTextId.value = textItems.value[idx].id;
  editingTextContent.value = textItems.value[idx].content;
  nextTick(() => {
    if (textInputRef.value) textInputRef.value.focus();
  });
};
const finishEditText = (idx) => {
  if (editingTextContent.value.trim()) {
    textItems.value[idx].content = editingTextContent.value;
  }
  editingTextId.value = null;
  editingTextContent.value = '';
  render();
  hasChanges.value = true;
};
const removeText = (idx) => {
  const removedId = textItems.value[idx].id;
  textItems.value.splice(idx, 1);
  if (selectedTextId.value === removedId) {
    selectedTextId.value = textItems.value.length > 0 ? textItems.value[0].id : null;
  }
  hasChanges.value = true;
};
// Reset
const resetAll = () => {
  loadImage();
  Object.assign(adjustments, initialAdjustments);
  activeFilter.value = filters[0];
  filterIntensity.value = 100;
  textItems.value = [];
  selectedTextId.value = null;
  cropSelection.startX = null;
  cropSelection.startY = null;
  cropSelection.x = 0;
  cropSelection.y = 0;
  cropSelection.width = 0;
  cropSelection.height = 0;
  cropApplied.value = false;
  currentCropRatio.value = '自由';
  hasChanges.value = false;
};
// Close
const handleClose = () => {
  if (hasChanges.value) {
    if (!confirm('您有未保存的修改，确定要关闭吗？')) return;
  }
  emit('update:visible', false);
  emit('close');
};
// Generate final canvas with all effects and text
const generateFinalCanvas = () => {
  const finalCanvas = document.createElement('canvas');
  finalCanvas.width = canvasWidth.value;
  finalCanvas.height = canvasHeight.value;
  const ctx = finalCanvas.getContext('2d');
  // Draw image with filters
  ctx.filter = getCSSFilter();
  ctx.drawImage(originalImage.value, 0, 0, finalCanvas.width, finalCanvas.height);
  ctx.filter = 'none';
  // Apply sharpness
  if (adjustments.sharpness !== 0) {
    const sharpAmount = adjustments.sharpness / 100;
    const kernel = [
      0, -sharpAmount, 0,
      -sharpAmount, 1 + 4 * sharpAmount, -sharpAmount,
      0, -sharpAmount, 0
    ];
    applyConvolution(ctx, finalCanvas, kernel);
  }
  // Draw text
  textItems.value.forEach(txt => {
    ctx.save();
    ctx.font = `${txt.italic ? 'italic ' : ''}${txt.bold ? 'bold ' : ''}${txt.size}px sans-serif`;
    ctx.fillStyle = txt.color;
    ctx.textBaseline = 'top';
    ctx.shadowColor = 'rgba(0,0,0,0.5)';
    ctx.shadowBlur = 2;
    ctx.shadowOffsetX = 1;
    ctx.shadowOffsetY = 1;
    ctx.fillText(txt.content, txt.x, txt.y);
    ctx.restore();
  });
  return finalCanvas;
};
const blobToFile = (blob, fileName) => {
  const file = new File([blob], fileName, { type: blob.type });
  return file;
};
// Save
const doSave = async (mode) => {
  let mimeType = 'image/png';
  let quality = undefined;
  const nameVal = props.imageName || 'image.png';
  if (nameVal.match(/\.(jpg|jpeg)$/i)) {
    mimeType = 'image/jpeg';
    quality = 0.92;
  }
  try {
    showSaveOptions.value = false;
    saving.value = true;
    saveStatus.value = '正在生成编辑图片...';
    const finalCanvas = generateFinalCanvas();
    const blob = await new Promise(resolve => finalCanvas.toBlob(resolve, mimeType, quality));
    if (!blob) throw new Error('生成图片失败');
    const fileName = mode === 'overwrite' ? props.imageName : newFileName.value;
    const file = blobToFile(blob, fileName);
    const formData = new FormData();
    formData.append('file', file);
    props.imageAlbumIds.forEach(id => formData.append('albumIds', id));
    props.imageTagNames.forEach(t => formData.append('tags', t));
    saveStatus.value = '正在上传到服务器...';
    const apiObj = props.api || axios;
    const res = await apiObj.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    if (res.data?.success) {
      const newPictureData = res.data.data;
      if (mode === 'overwrite' && props.imageId) {
        saveStatus.value = '正在替换原图...';
        try {
          await apiObj.delete(`/delete/${props.imageId}`);
        } catch (delErr) {
          console.error('Delete old picture error:', delErr);
        }
      }
      saveStatus.value = '保存成功！';
      setTimeout(() => {
        saving.value = false;
        emit('saved', { mode: mode, data: newPictureData, newPicture: mode === 'new' });
        emit('update:visible', false);
        emit('close');
      }, 500);
    } else {
      throw new Error(res.data?.message || '保存失败');
    }
  } catch (e) {
    console.error('Save error:', e);
    saving.value = false;
    saveStatus.value = '';
    if (confirm('上传失败，是否下载到本地？')) {
      try {
        const finalCanvas = generateFinalCanvas();
        const link = document.createElement('a');
        link.download = mode === 'new' ? newFileName.value : props.imageName;
        link.href = finalCanvas.toDataURL(mimeType === 'image/jpeg' ? 'image/jpeg' : 'image/png', quality || 0.92);
        link.click();
      } catch (downloadErr) {
        console.error('Download error:', downloadErr);
      }
    }
  }
};
// Watch
watch(() => props.visible, (v) => {
  if (v) {
    resetAll();
    nextTick(loadImage);
  }
});
onMounted(() => {
  if (props.visible) {
    nextTick(loadImage);
  }
});
</script>

<style scoped>
canvas {
  image-rendering: -webkit-optimize-contrast;
}

input[type="range"] {
  height: 6px;
  border-radius: 3px;
  background: #374151;
  cursor: pointer;
  outline: none;
  -webkit-appearance: none;
}

input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #3b82f6;
  cursor: pointer;
  border: 2px solid #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  transition: transform 0.15s;
}

input[type="range"]::-webkit-slider-thumb:hover {
  transform: scale(1.1);
}

input[type="range"]::-moz-range-thumb {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #3b82f6;
  cursor: pointer;
  border: 2px solid #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

input[type="color"] {
  -webkit-appearance: none;
  border: 1px solid #374151;
  background: none;
}

input[type="color"]::-webkit-color-swatch-wrapper {
  padding: 0;
}

input[type="color"]::-webkit-color-swatch {
  border: none;
  border-radius: 6px;
}
</style>