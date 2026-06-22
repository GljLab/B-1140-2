<template>
  <div class="space-y-6">
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center space-x-2">
        <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>伪装模式</span>
      </h3>
      <p class="text-sm text-gray-500 mb-4">启用伪装后，保密空间入口将显示为其他名称，保护您的隐私</p>
      
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <button v-for="opt in disguiseOptions" :key="opt.value" @click="updateDisguise(opt.value)"
          :class="['p-4 rounded-xl border-2 text-left transition',
            config?.disguiseType === opt.value
              ? 'border-blue-500 bg-blue-50'
              : 'border-gray-200 hover:border-blue-300']">
          <div class="text-2xl mb-2">{{ opt.icon }}</div>
          <div :class="['text-sm font-medium', config?.disguiseType === opt.value ? 'text-blue-700' : 'text-gray-700']">
            {{ opt.label }}
          </div>
          <div class="text-xs text-gray-400 mt-1">{{ opt.desc }}</div>
        </button>
      </div>
      
      <div v-if="config?.disguiseType" class="mt-4 p-3 bg-amber-50 rounded-lg text-sm text-amber-700">
        💡 提示：伪装模式下，点击"{{ disguiseLabel }}"并快速连续点击3次标题可触发真实功能
      </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center space-x-2">
        <svg class="w-5 h-5 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span>自动锁定</span>
      </h3>
      <p class="text-sm text-gray-500 mb-4">在指定时间无操作后，自动锁定保密空间</p>
      
      <div class="flex flex-wrap gap-3">
        <button v-for="t in autoLockOptions" :key="t.value" @click="updateAutoLock(t.value)"
          :class="['px-5 py-3 rounded-xl border-2 text-center transition',
            config?.autoLockTime === t.value
              ? 'border-purple-500 bg-purple-50 text-purple-700'
              : 'border-gray-200 hover:border-purple-300 text-gray-700']">
          <div class="font-medium">{{ t.label }}</div>
        </button>
        <button @click="updateAutoLock(null)"
          :class="['px-5 py-3 rounded-xl border-2 text-center transition',
            !config?.autoLockTime
              ? 'border-gray-500 bg-gray-100 text-gray-700'
              : 'border-gray-200 hover:border-gray-400 text-gray-500']">
          <div class="font-medium">永不</div>
        </button>
      </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center space-x-2">
        <svg class="w-5 h-5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        <span>密码管理</span>
      </h3>
      
      <div class="space-y-3">
        <button @click="$emit('change-password')"
          class="w-full px-4 py-3 flex items-center justify-between bg-gray-50 hover:bg-gray-100 rounded-xl transition">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center text-blue-600">
              🔑
            </div>
            <div class="text-left">
              <div class="text-sm font-medium text-gray-800">修改密码</div>
              <div class="text-xs text-gray-400">修改保密空间的访问密码</div>
            </div>
          </div>
          <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
          </svg>
        </button>
        
        <button @click="$emit('reset-password')"
          class="w-full px-4 py-3 flex items-center justify-between bg-gray-50 hover:bg-gray-100 rounded-xl transition">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center text-orange-600">
              🔄
            </div>
            <div class="text-left">
              <div class="text-sm font-medium text-gray-800">忘记密码</div>
              <div class="text-xs text-gray-400">通过账号密码重置</div>
            </div>
          </div>
          <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center space-x-2">
        <svg class="w-5 h-5 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <span>访客模式</span>
      </h3>
      <p class="text-sm text-gray-500 mb-4">开启后，进入保密空间需要额外验证安全问题</p>
      
      <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
        <div>
          <div class="text-sm font-medium text-gray-800">访客模式</div>
          <div class="text-xs text-gray-400">{{ config?.guestModeEnabled ? '已开启，进入需验证安全问题' : '未开启' }}</div>
        </div>
        <div class="relative cursor-pointer" @click="toggleGuestMode">
          <div :class="['w-12 h-6 rounded-full transition-colors', config?.guestModeEnabled ? 'bg-emerald-500' : 'bg-gray-300']"></div>
          <div :class="['absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform', config?.guestModeEnabled ? 'translate-x-6' : '']"></div>
        </div>
      </div>
      
      <div v-if="config?.guestModeEnabled" class="mt-4 space-y-3">
        <div class="p-3 bg-emerald-50 rounded-lg text-sm text-emerald-700">
          {{ config?.hasSecurityQuestion ? '✅ 已设置安全问题' : '⚠️ 请先设置安全问题' }}
        </div>
        <button @click="$emit('set-security-question')"
          class="w-full px-4 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white rounded-lg text-sm font-medium transition">
          {{ config?.hasSecurityQuestion ? '修改安全问题' : '设置安全问题' }}
        </button>
      </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-6 flex items-center space-x-2">
        <svg class="w-5 h-5 text-rose-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
        </svg>
        <span>安全退出</span>
      </h3>
      <p class="text-sm text-gray-500 mb-4">立即锁定保密空间，下次进入需要重新验证密码</p>
      <button @click="$emit('lock-now')"
        class="w-full py-3 bg-gradient-to-r from-rose-500 to-red-600 text-white rounded-xl font-medium hover:shadow-lg transition flex items-center justify-center space-x-2">
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        <span>立即锁定</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const props = defineProps({
  config: Object
})

const emit = defineEmits(['change-password', 'reset-password', 'set-security-question', 'lock-now', 'updated', 'toast'])

const disguiseOptions = [
  { value: null, label: '关闭伪装', icon: '🔓', desc: '正常显示保密空间' },
  { value: 'recycle', label: '回收站', icon: '🗑️', desc: '伪装成回收站入口' },
  { value: 'help', label: '帮助中心', icon: '❓', desc: '伪装成帮助中心入口' },
  { value: 'settings', label: '设置', icon: '⚙️', desc: '伪装成设置入口' }
]

const autoLockOptions = [
  { value: 1, label: '1分钟' },
  { value: 5, label: '5分钟' },
  { value: 10, label: '10分钟' }
]

const disguiseLabel = computed(() => {
  const opt = disguiseOptions.find(o => o.value === props.config?.disguiseType)
  return opt ? opt.label : '保密空间'
})

const updateDisguise = async (type) => {
  try {
    const res = await api.put('/private-space/settings', {
      disguiseType: type
    })
    if (res.data.success) {
      emit('updated', res.data.data)
      emit('toast', '伪装设置已更新')
    } else {
      emit('toast', res.data.message || '设置失败', 'error')
    }
  } catch (e) {
    emit('toast', '设置失败', 'error')
  }
}

const updateAutoLock = async (time) => {
  try {
    const res = await api.put('/private-space/settings', {
      autoLockTime: time
    })
    if (res.data.success) {
      emit('updated', res.data.data)
      emit('toast', '自动锁定设置已更新')
    } else {
      emit('toast', res.data.message || '设置失败', 'error')
    }
  } catch (e) {
    emit('toast', '设置失败', 'error')
  }
}

const toggleGuestMode = async () => {
  const newValue = !props.config?.guestModeEnabled
  try {
    const res = await api.put('/private-space/settings', {
      guestModeEnabled: newValue
    })
    if (res.data.success) {
      emit('updated', res.data.data)
      emit('toast', newValue ? '访客模式已开启' : '访客模式已关闭')
    } else {
      emit('toast', res.data.message || '设置失败', 'error')
    }
  } catch (e) {
    emit('toast', '设置失败', 'error')
  }
}
</script>
