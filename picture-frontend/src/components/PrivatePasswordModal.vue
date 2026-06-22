<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <div class="absolute inset-0 bg-black/60" @click="handleClose"></div>
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden relative z-10">
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-6 text-white text-center">
        <div class="w-16 h-16 bg-white/20 rounded-2xl mx-auto flex items-center justify-center text-3xl mb-3">
          🔒
        </div>
        <h3 class="text-xl font-bold">{{ modalTitle }}</h3>
        <p class="text-sm text-blue-100 mt-1">{{ modalSubtitle }}</p>
      </div>

      <div class="p-6">
        <div v-if="mode === 'set' || mode === 'change' || mode === 'reset'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">{{ passwordLabel }}</label>
          <input
            v-model="passwordInput"
            type="password"
            placeholder="请输入4-6位密码"
            maxlength="6"
            @keyup.enter="handleSubmit"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-center text-2xl tracking-widest font-mono focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div v-if="mode === 'set' || mode === 'change' || mode === 'reset'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">确认密码</label>
          <input
            v-model="confirmPasswordInput"
            type="password"
            placeholder="请再次输入密码"
            maxlength="6"
            @keyup.enter="handleSubmit"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-center text-2xl tracking-widest font-mono focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div v-if="mode === 'verify'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">请输入密码</label>
          <input
            v-model="passwordInput"
            type="password"
            placeholder="请输入密码"
            maxlength="6"
            @keyup.enter="handleSubmit"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-center text-2xl tracking-widest font-mono focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p v-if="remainingAttempts !== null" class="text-xs text-orange-500 mt-2 text-center">
            还剩 {{ remainingAttempts }} 次机会
          </p>
        </div>

        <div v-if="mode === 'change'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">旧密码</label>
          <input
            v-model="oldPasswordInput"
            type="password"
            placeholder="请输入旧密码"
            maxlength="6"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-center text-2xl tracking-widest font-mono focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div v-if="mode === 'reset'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">账号密码</label>
          <input
            v-model="accountPasswordInput"
            type="password"
            placeholder="请输入您的账号密码"
            @keyup.enter="handleSubmit"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p class="text-xs text-gray-400 mt-1">为了安全，重置私密密码需要验证账号密码</p>
        </div>

        <div v-if="mode === 'security-question'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">安全问题</label>
          <select v-model="securityQuestion" class="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">请选择安全问题</option>
            <option value="您的出生地是？">您的出生地是？</option>
            <option value="您的小学名称是？">您的小学名称是？</option>
            <option value="您最喜欢的颜色是？">您最喜欢的颜色是？</option>
            <option value="您的宠物名字是？">您的宠物名字是？</option>
            <option value="您母亲的名字是？">您母亲的名字是？</option>
          </select>
        </div>

        <div v-if="mode === 'security-question' || mode === 'security-verify'" class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">{{ securityAnswerLabel }}</label>
          <input
            v-model="securityAnswerInput"
            type="text"
            placeholder="请输入答案"
            @keyup.enter="handleSubmit"
            class="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <p v-if="errorMessage" class="text-sm text-red-500 mb-4 text-center">{{ errorMessage }}</p>

        <button
          @click="handleSubmit"
          :disabled="loading"
          class="w-full py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl font-medium hover:shadow-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <span v-if="loading" class="flex items-center justify-center space-x-2">
            <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            <span>处理中...</span>
          </span>
          <span v-else>{{ submitButtonText }}</span>
        </button>

        <div v-if="mode === 'verify'" class="mt-4 text-center">
          <button @click="handleForgotPassword" class="text-sm text-blue-600 hover:text-blue-700">
            忘记密码？
          </button>
        </div>

        <button v-if="showCancel" @click="handleClose" class="w-full mt-3 py-3 text-gray-500 hover:text-gray-700 text-sm">
          取消
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const props = defineProps({
  visible: Boolean,
  mode: {
    type: String,
    default: 'verify'
  },
  showCancel: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close', 'success', 'forgot-password', 'locked'])

const passwordInput = ref('')
const confirmPasswordInput = ref('')
const oldPasswordInput = ref('')
const accountPasswordInput = ref('')
const securityQuestion = ref('')
const securityAnswerInput = ref('')
const errorMessage = ref('')
const loading = ref(false)
const remainingAttempts = ref(null)

const modalTitle = computed(() => {
  switch (props.mode) {
    case 'set': return '设置私密密码'
    case 'verify': return '验证私密密码'
    case 'change': return '修改私密密码'
    case 'reset': return '重置私密密码'
    case 'security-question': return '设置安全问题'
    case 'security-verify': return '验证安全问题'
    default: return '私密空间'
  }
})

const modalSubtitle = computed(() => {
  switch (props.mode) {
    case 'set': return '请设置4-6位密码保护您的私密空间'
    case 'verify': return '请输入密码进入私密空间'
    case 'change': return '请输入旧密码和新密码'
    case 'reset': return '验证账号密码后重置私密密码'
    case 'security-question': return '设置安全问题，用于找回密码'
    case 'security-verify': return '请回答安全问题'
    default: return ''
  }
})

const passwordLabel = computed(() => {
  if (props.mode === 'set') return '新密码'
  if (props.mode === 'change') return '新密码'
  if (props.mode === 'reset') return '新密码'
  return '密码'
})

const submitButtonText = computed(() => {
  switch (props.mode) {
    case 'set': return '设置密码'
    case 'verify': return '验证进入'
    case 'change': return '确认修改'
    case 'reset': return '重置密码'
    case 'security-question': return '保存设置'
    case 'security-verify': return '验证答案'
    default: return '确定'
  }
})

const securityAnswerLabel = computed(() => {
  return props.mode === 'security-question' ? '安全答案' : '您的答案'
})

watch(() => props.visible, (val) => {
  if (val) {
    passwordInput.value = ''
    confirmPasswordInput.value = ''
    oldPasswordInput.value = ''
    accountPasswordInput.value = ''
    securityQuestion.value = ''
    securityAnswerInput.value = ''
    errorMessage.value = ''
    remainingAttempts.value = null
  }
})

const handleClose = () => {
  if (!loading.value) {
    emit('close')
  }
}

const handleForgotPassword = () => {
  emit('forgot-password')
}

const validatePassword = (pwd) => {
  if (!pwd || pwd.trim().length < 4 || pwd.trim().length > 6) {
    return '密码长度需在4-6位之间'
  }
  if (!/^\d+$/.test(pwd.trim())) {
    return '密码只能包含数字'
  }
  return null
}

const handleSubmit = async () => {
  errorMessage.value = ''

  try {
    if (props.mode === 'set') {
      const err = validatePassword(passwordInput.value)
      if (err) { errorMessage.value = err; return }
      if (passwordInput.value !== confirmPasswordInput.value) {
        errorMessage.value = '两次输入的密码不一致'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/password/set', {
        password: passwordInput.value.trim(),
        passwordType: 'pin'
      })
      if (res.data.success) {
        emit('success', res.data.data)
      } else {
        errorMessage.value = res.data.message || '设置失败'
      }
    } else if (props.mode === 'verify') {
      if (!passwordInput.value.trim()) {
        errorMessage.value = '请输入密码'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/password/verify', {
        password: passwordInput.value.trim()
      })
      if (res.data.success) {
        emit('success', res.data.data)
      } else {
        errorMessage.value = res.data.message || '验证失败'
        if (res.data.message && res.data.message.includes('还剩')) {
          const match = res.data.message.match(/还剩 (\d+) 次机会/)
          if (match) remainingAttempts.value = parseInt(match[1])
        }
        if (res.data.message && (res.data.message.includes('锁定') || res.data.message.includes('请') && res.data.message.includes('分钟后再试'))) {
          setTimeout(() => {
            emit('locked')
          }, 1500)
        }
      }
    } else if (props.mode === 'change') {
      if (!oldPasswordInput.value.trim()) {
        errorMessage.value = '请输入旧密码'
        return
      }
      const err = validatePassword(passwordInput.value)
      if (err) { errorMessage.value = err; return }
      if (passwordInput.value !== confirmPasswordInput.value) {
        errorMessage.value = '两次输入的新密码不一致'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/password/change', {
        oldPassword: oldPasswordInput.value.trim(),
        newPassword: passwordInput.value.trim()
      })
      if (res.data.success) {
        emit('success', res.data.data)
      } else {
        errorMessage.value = res.data.message || '修改失败'
      }
    } else if (props.mode === 'reset') {
      if (!accountPasswordInput.value) {
        errorMessage.value = '请输入账号密码'
        return
      }
      const err = validatePassword(passwordInput.value)
      if (err) { errorMessage.value = err; return }
      if (passwordInput.value !== confirmPasswordInput.value) {
        errorMessage.value = '两次输入的密码不一致'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/password/reset', null, {
        params: {
          accountPassword: accountPasswordInput.value,
          newPassword: passwordInput.value.trim()
        }
      })
      if (res.data.success) {
        emit('success', res.data.data)
      } else {
        errorMessage.value = res.data.message || '重置失败'
      }
    } else if (props.mode === 'security-question') {
      if (!securityQuestion.value) {
        errorMessage.value = '请选择安全问题'
        return
      }
      if (!securityAnswerInput.value.trim()) {
        errorMessage.value = '请输入安全答案'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/security-question/set', {
        question: securityQuestion.value,
        answer: securityAnswerInput.value.trim()
      })
      if (res.data.success) {
        emit('success', res.data.data)
      } else {
        errorMessage.value = res.data.message || '设置失败'
      }
    } else if (props.mode === 'security-verify') {
      if (!securityAnswerInput.value.trim()) {
        errorMessage.value = '请输入答案'
        return
      }
      loading.value = true
      const res = await api.post('/private-space/security-question/verify', {
        answer: securityAnswerInput.value.trim()
      })
      if (res.data.success && res.data.data) {
        emit('success', true)
      } else {
        errorMessage.value = res.data.message || '答案错误'
      }
    }
  } catch (e) {
    errorMessage.value = '操作失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>
