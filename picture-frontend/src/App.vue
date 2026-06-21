<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 font-sans text-gray-700">
    <header v-if="!isSharePage" class="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-20 border-b border-gray-100">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex justify-between items-center">
        <div class="flex items-center space-x-3">
          <div class="w-9 h-9 bg-gradient-to-tr from-blue-500 via-purple-500 to-pink-500 rounded-xl flex items-center justify-center text-white font-bold shadow-md">P</div>
          <h1 class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-purple-600">智能图库</h1>
        </div>
        <nav class="flex items-center space-x-1 bg-gray-100/60 rounded-xl p-1">
          <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
            :class="['px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
              activeTab === tab.id ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-600 hover:text-gray-900 hover:bg-white/50']">
            <span class="flex items-center space-x-1.5">
              <svg v-if="tab.id === 'discover'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <svg v-if="tab.id === 'favorites'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
              </svg>
              <svg v-if="tab.id === 'pictures'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <svg v-if="tab.id === 'albums'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              <svg v-if="tab.id === 'tags'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <svg v-if="tab.id === 'recycle'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <svg v-if="tab.id === 'watermark'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
              </svg>
              <svg v-if="tab.id === 'stats'" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span>{{ tab.name }}</span>
            </span>
          </button>
        </nav>
        <div class="flex items-center space-x-3">
          <div class="relative" v-if="isLoggedIn">
            <input v-model="globalSearch" type="text" placeholder="搜索图片/专辑/主题词..."
              @keyup.enter="doGlobalSearch"
              class="w-56 pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            <svg class="w-4 h-4 absolute left-3 top-2.5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <div v-if="isLoggedIn" class="relative" ref="notificationPanelRef">
            <button id="notification-bell-btn" @click.stop="toggleNotificationPanel"
              class="relative p-2 text-gray-600 hover:text-blue-600 hover:bg-gray-100 rounded-lg transition">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
              <span v-if="unreadCount > 0"
                class="absolute -top-0.5 -right-0.5 w-5 h-5 bg-gradient-to-r from-red-500 to-rose-600 text-white text-[10px] font-bold rounded-full flex items-center justify-center shadow-md">
                {{ unreadCount > 99 ? '99+' : unreadCount }}
              </span>
            </button>
            <div v-if="showNotificationPanel"
              class="absolute right-0 top-full mt-2 w-80 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50">
              <div class="bg-gradient-to-r from-blue-500 to-purple-600 px-4 py-3 flex items-center justify-between">
                <h3 class="text-white font-semibold">消息通知</h3>
                <button @click="markAllNotificationsRead"
                  class="text-xs text-blue-100 hover:text-white transition">全部已读</button>
              </div>
              <div class="max-h-96 overflow-y-auto">
                <div v-if="notifications.length === 0" class="py-12 text-center text-gray-400 text-sm">
                  <div class="text-4xl mb-2">🔔</div>
                  暂无通知
                </div>
                <div v-for="n in notifications" :key="n.id"
                  @click="handleNotificationClick(n)"
                  :class="['px-4 py-3 border-b border-gray-50 hover:bg-gray-50 transition cursor-pointer', !n.isRead ? 'bg-blue-50/30' : '']">
                  <div class="flex items-start space-x-3">
                    <div class="w-8 h-8 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                      {{ n.type === 'COLLABORATION_INVITE' ? '👥' : (n.fromNickname || 'U').charAt(0).toUpperCase() }}
                    </div>
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center justify-between">
                        <p class="text-sm text-gray-800 font-medium">
                          <span v-if="!n.isRead" class="inline-block w-2 h-2 bg-blue-500 rounded-full mr-2"></span>
                          {{ n.title || '系统通知' }}
                        </p>
                      </div>
                      <p class="text-xs text-gray-500 mt-0.5 line-clamp-2">{{ n.content }}</p>
                      <p class="text-xs text-gray-400 mt-1">{{ formatTime(n.createTime) }}</p>
                      <div v-if="n.type === 'COLLABORATION_INVITE' && n.inviteId && n.inviteStatus === 'PENDING'" class="flex space-x-2 mt-2">
                        <button @click.stop="acceptInvite(n.inviteId, n.id)"
                          class="px-3 py-1 bg-gradient-to-r from-emerald-500 to-teal-600 text-white text-xs rounded-lg hover:shadow-md transition">
                          接受
                        </button>
                        <button @click.stop="rejectInvite(n.inviteId, n.id)"
                          class="px-3 py-1 bg-gray-100 text-gray-600 text-xs rounded-lg hover:bg-gray-200 transition">
                          拒绝
                        </button>
                      </div>
                      <div v-if="n.type === 'COLLABORATION_INVITE' && n.inviteStatus === 'ACCEPTED'" class="mt-1">
                        <span class="text-xs text-green-600 font-medium">已接受</span>
                      </div>
                      <div v-if="n.type === 'COLLABORATION_INVITE' && n.inviteStatus === 'REJECTED'" class="mt-1">
                        <span class="text-xs text-gray-500 font-medium">已拒绝</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <button v-if="isLoggedIn" @click="handleUploadClick"
            class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition duration-200 flex items-center space-x-2 text-sm font-medium">
            <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>上传</span>
          </button>
          <div v-if="isLoggedIn" class="flex items-center space-x-2 pl-2 border-l border-gray-200">
            <div class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-sm font-bold shadow-sm">
                {{ currentUser.nickname ? currentUser.nickname.charAt(0).toUpperCase() : 'U' }}
              </div>
              <span class="text-sm font-medium text-gray-700 max-w-20 truncate">{{ currentUser.nickname || currentUser.username }}</span>
            </div>
            <button @click="doLogout"
              class="px-3 py-1.5 text-sm text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition">
              退出
            </button>
          </div>
          <button v-else @click="openLoginModal"
            class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition duration-200 text-sm font-medium">
            登录 / 注册
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 pb-24">
      <!-- Share Page -->
      <div v-if="isSharePage" class="min-h-[60vh]">
        <div v-if="sharePageLoading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="sharePageError" class="flex flex-col items-center justify-center py-20">
          <div class="text-6xl mb-4">🔒</div>
          <h3 class="text-xl font-medium text-gray-500 mb-2">{{ sharePageError }}</h3>
          <button @click="goHome" class="px-5 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium">
            返回首页
          </button>
        </div>
        <div v-else-if="sharePageData && sharePageData.hasPassword && !sharePageData.picture && !sharePageData.album">
          <div class="max-w-md mx-auto bg-white rounded-2xl shadow-lg border border-gray-100 p-8 mt-20">
            <div class="text-center mb-6">
              <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl mx-auto flex items-center justify-center text-white text-2xl font-bold mb-4">
                🔒
              </div>
              <h3 class="text-xl font-bold text-gray-800 mb-2">{{ sharePageData.title }}</h3>
              <p class="text-sm text-gray-500">分享者：{{ sharePageData.creator?.nickname || sharePageData.creator?.username }}</p>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">请输入访问密码</label>
                <input v-model="sharePasswordInput" type="password" placeholder="请输入密码"
                  @keyup.enter="submitSharePassword"
                  class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <p v-if="sharePasswordError" class="text-xs text-red-500 mt-1">{{ sharePasswordError }}</p>
              </div>
              <button @click="submitSharePassword"
                class="w-full py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg font-medium hover:shadow-lg transition">
                查看内容
              </button>
            </div>
          </div>
        </div>
        <div v-else-if="sharePageData && sharePageData.picture">
          <div class="max-w-4xl mx-auto">
            <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
              <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-4 text-white">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center text-white font-bold">
                      {{ sharePageData.creator?.nickname?.charAt(0).toUpperCase() || 'U' }}
                    </div>
                    <div>
                      <p class="text-sm text-blue-100">分享者</p>
                      <p class="font-medium">{{ sharePageData.creator?.nickname || sharePageData.creator?.username }}</p>
                    </div>
                  </div>
                  <span class="text-xs bg-white/20 px-3 py-1 rounded-full">图片分享</span>
                </div>
              </div>
              <div class="bg-gray-900 flex items-center justify-center p-8">
                <img :src="sharePageData.picture.url" class="max-w-full max-h-[70vh] object-contain rounded-lg" />
              </div>
              <div class="p-6">
                <h2 class="text-xl font-bold text-gray-800 mb-2">{{ sharePageData.picture.name }}</h2>
                <div class="flex items-center space-x-4 text-sm text-gray-500">
                  <span>📅 {{ formatTime(sharePageData.picture.createTime) }}</span>
                  <span>📦 {{ formatSize(sharePageData.picture.size) }}</span>
                </div>
                <div v-if="sharePageData.picture.tags && sharePageData.picture.tags.length > 0" class="mt-4">
                  <div class="flex flex-wrap gap-1.5">
                    <span v-for="t in sharePageData.picture.tags" :key="t.id"
                      class="inline-flex items-center px-2.5 py-1 bg-purple-50 text-purple-700 rounded-full text-xs">
                      #{{ t.name }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="sharePageData && sharePageData.album">
          <div class="max-w-6xl mx-auto">
            <div class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-6">
              <div class="bg-gradient-to-r from-purple-500 to-pink-600 p-6 text-white">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-4">
                    <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center text-white text-2xl font-bold">
                      {{ sharePageData.creator?.nickname?.charAt(0).toUpperCase() || 'U' }}
                    </div>
                    <div>
                      <p class="text-sm text-purple-100">分享者</p>
                      <p class="text-lg font-bold">{{ sharePageData.creator?.nickname || sharePageData.creator?.username }}</p>
                    </div>
                  </div>
                  <span class="text-xs bg-white/20 px-4 py-1.5 rounded-full">专辑分享</span>
                </div>
              </div>
              <div class="p-6">
                <h2 class="text-2xl font-bold text-gray-800 mb-2">{{ sharePageData.album.name }}</h2>
                <p v-if="sharePageData.album.description" class="text-sm text-gray-500 mb-3">{{ sharePageData.album.description }}</p>
                <div class="flex items-center space-x-4 text-sm text-gray-500">
                  <span>🖼️ {{ sharePageData.album.pictureCount || 0 }} 张图片</span>
                  <span>📅 创建于 {{ formatTime(sharePageData.album.createTime) }}</span>
                </div>
              </div>
            </div>
            <div v-if="sharePageData.album.pictures && sharePageData.album.pictures.length > 0">
              <h3 class="text-lg font-bold text-gray-800 mb-4">相册图片</h3>
              <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                <div v-for="pic in sharePageData.album.pictures" :key="pic.id"
                  class="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
                  <div class="aspect-square bg-gray-100 relative overflow-hidden">
                    <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
                  </div>
                  <div class="p-3">
                    <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
                    <span class="text-xs text-gray-400">{{ formatTime(pic.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
              <div class="text-6xl mb-4">📭</div>
              <h3 class="text-xl font-medium text-gray-500">该专辑暂无图片</h3>
            </div>
          </div>
        </div>
      </div>

      <!-- Guest landing -->
      <div v-else-if="!isLoggedIn" class="flex flex-col items-center justify-center py-20">
        <div class="w-24 h-24 bg-gradient-to-br from-blue-500 via-purple-500 to-pink-500 rounded-3xl flex items-center justify-center text-white text-4xl font-bold shadow-2xl mb-8">
          P
        </div>
        <h2 class="text-3xl font-bold text-gray-800 mb-3">欢迎来到智能图库</h2>
        <p class="text-gray-500 mb-8 text-center max-w-md">
          上传、管理您的图片，创建专属专辑，用主题词整理回忆。
          <br />登录后开始使用全部功能。
        </p>
        <div class="flex space-x-4">
          <button @click="openLoginModal"
            class="px-8 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition duration-200 font-medium">
            登录 / 注册
          </button>
        </div>
        <div class="grid grid-cols-3 gap-6 mt-16 max-w-2xl">
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-blue-100 rounded-xl flex items-center justify-center text-blue-600 mb-3">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <h4 class="font-semibold text-gray-800 mb-1">图片管理</h4>
            <p class="text-xs text-gray-500">轻松上传、整理您的图片</p>
          </div>
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-purple-100 rounded-xl flex items-center justify-center text-purple-600 mb-3">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <h4 class="font-semibold text-gray-800 mb-1">专辑分类</h4>
            <p class="text-xs text-gray-500">创建专辑，归类整理</p>
          </div>
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-pink-100 rounded-xl flex items-center justify-center text-pink-600 mb-3">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
            </div>
            <h4 class="font-semibold text-gray-800 mb-1">主题词标签</h4>
            <p class="text-xs text-gray-500">智能标签，快速搜索</p>
          </div>
        </div>
      </div>

      <!-- PICTURES -->
      <div v-else-if="activeTab === 'pictures'">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
          <div class="flex flex-wrap items-center gap-3">
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-500">筛选:</span>
              <select v-model="filterAlbumId" @change="fetchPictures"
                class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option :value="null">全部专辑</option>
                <option v-for="a in allAlbums" :key="a.id" :value="a.id">{{ a.name }} ({{ a.pictureCount }})</option>
              </select>
              <select v-model="filterTagId" @change="fetchPictures"
                class="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option :value="null">全部主题词</option>
                <option v-for="t in hotTags" :key="t.id" :value="t.id">#{{ t.name }} ({{ t.referenceCount }})</option>
              </select>
            </div>
            <div class="flex-1"></div>
            <div class="flex items-center space-x-3">
              <label class="flex items-center space-x-2 cursor-pointer text-sm text-gray-600 hover:text-gray-900">
                <input type="checkbox" v-model="multiSelectMode" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                <span>多选模式</span>
              </label>
              <span v-if="multiSelectMode && selectedPictureIds.length > 0" class="text-sm text-blue-600 font-medium">
                已选 {{ selectedPictureIds.length }} 张
              </span>
            </div>
          </div>
        </div>

        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="pictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">🖼️</div>
          <h3 class="text-xl font-medium text-gray-500">暂无图片，快来上传吧！</h3>
          <button @click="handleUploadClick"
            class="mt-4 px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm font-medium">
            立即上传
          </button>
        </div>
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div v-for="pic in pictures" :key="pic.id"
            class="group relative bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
            <div v-if="multiSelectMode" class="absolute top-2 left-2 z-10">
              <input type="checkbox" :value="pic.id" v-model="selectedPictureIds"
                class="w-5 h-5 text-blue-600 rounded bg-white/90 border-2 border-gray-300 focus:ring-blue-500 cursor-pointer" />
            </div>
            <div class="aspect-square bg-gray-100 relative overflow-hidden cursor-pointer" @click="viewPicture(pic)">
              <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
              <div v-if="pic.hasWatermark" class="absolute top-2 left-2 z-[5]">
                <span class="inline-flex items-center space-x-0.5 px-1.5 py-0.5 bg-black/50 backdrop-blur-sm rounded text-[10px] text-white font-medium">
                  <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
                  </svg>
                  <span>水印</span>
                </span>
              </div>
              <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition duration-300"></div>
              <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition duration-300 flex space-x-1">
                <a :href="pic.url" download @click.stop target="_blank"
                  class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-blue-600 shadow-md hover:bg-white transition" title="下载">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                  </svg>
                </a>
                <button @click.stop="confirmDeletePicture(pic)"
                  class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-red-600 shadow-md hover:bg-white transition" title="删除">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
              <div class="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition duration-300">
                <div class="flex flex-wrap gap-1">
                  <span v-for="tag in (pic.tags || []).slice(0, 3)" :key="tag.id"
                    class="text-[10px] px-1.5 py-0.5 bg-white/30 backdrop-blur-sm rounded text-white">#{{ tag.name }}</span>
                </div>
              </div>
            </div>
            <div class="p-3">
              <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
              <div class="flex justify-between items-center mt-1.5">
                <span class="text-xs text-gray-400">{{ formatTime(pic.createTime) }}</span>
                <span class="text-xs text-gray-400">{{ formatSize(pic.size) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="multiSelectMode && selectedPictureIds.length > 0"
          class="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-30 bg-white rounded-2xl shadow-2xl border border-gray-100 px-4 py-3 flex items-center space-x-3">
          <span class="text-sm font-medium text-gray-700 border-r border-gray-200 pr-3">已选 {{ selectedPictureIds.length }} 张</span>
          <button @click="showBatchTagModal = true"
            class="px-4 py-2 bg-gradient-to-r from-emerald-500 to-teal-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
              <span>批量加主题词</span>
            </span>
          </button>
          <button @click="showBatchWatermarkModal = true"
            class="px-4 py-2 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
              </svg>
              <span>批量加水印</span>
            </span>
          </button>
          <button @click="showBatchAlbumModal = true"
            class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              <span>批量归专辑</span>
            </span>
          </button>
          <button @click="confirmBatchDelete"
            class="px-4 py-2 bg-gradient-to-r from-red-500 to-rose-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>批量删除</span>
            </span>
          </button>
          <button @click="selectedPictureIds = []" class="px-3 py-2 text-gray-500 hover:text-gray-700 text-sm">取消</button>
        </div>
      </div>

      <!-- DISCOVER -->
      <div v-else-if="activeTab === 'discover'">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">发现广场</h2>
            <p class="text-sm text-gray-500 mt-1">探索所有用户的公开图片</p>
          </div>
        </div>
        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="discoverPictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">🌍</div>
          <h3 class="text-xl font-medium text-gray-500">暂无公开图片</h3>
          <p class="text-sm text-gray-400 mt-2">快去发布你的第一张公开图片吧！</p>
        </div>
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div v-for="pic in discoverPictures" :key="pic.id"
            class="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer"
            @click="viewDiscoverPicture(pic)">
            <div class="aspect-square bg-gray-100 relative overflow-hidden">
              <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
              <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition duration-300"></div>
              <div class="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition duration-300">
                <div class="flex items-center space-x-3 text-white text-xs">
                  <span class="flex items-center space-x-1">
                    <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                    <span>{{ pic.likeCount }}</span>
                  </span>
                  <span class="flex items-center space-x-1">
                    <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>
                    <span>{{ pic.commentCount }}</span>
                  </span>
                </div>
              </div>
            </div>
            <div class="p-3">
              <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
              <div class="flex justify-between items-center mt-1.5">
                <span class="text-xs text-blue-600 flex items-center space-x-1">
                  <span class="w-4 h-4 bg-blue-100 rounded-full flex items-center justify-center text-[10px] font-bold text-blue-600">{{ (pic.authorNickname || 'U').charAt(0).toUpperCase() }}</span>
                  <span class="truncate max-w-16">{{ pic.authorNickname || '未知' }}</span>
                </span>
                <span class="text-xs text-gray-400">{{ formatTime(pic.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- FAVORITES -->
      <div v-else-if="isLoggedIn && activeTab === 'favorites'">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">我的收藏</h2>
            <p class="text-sm text-gray-500 mt-1">共 {{ favoritePictures.length }} 张收藏</p>
          </div>
        </div>
        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="favoritePictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">⭐</div>
          <h3 class="text-xl font-medium text-gray-500">暂无收藏</h3>
          <p class="text-sm text-gray-400 mt-2">在发现广场中收藏喜欢的图片吧！</p>
        </div>
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div v-for="pic in favoritePictures" :key="pic.id"
            class="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer"
            @click="viewDiscoverPicture(pic)">
            <div class="aspect-square bg-gray-100 relative overflow-hidden">
              <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
              <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition duration-300"></div>
            </div>
            <div class="p-3">
              <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
              <div class="flex justify-between items-center mt-1.5">
                <span class="text-xs text-blue-600 flex items-center space-x-1">
                  <span class="w-4 h-4 bg-blue-100 rounded-full flex items-center justify-center text-[10px] font-bold text-blue-600">{{ (pic.authorNickname || 'U').charAt(0).toUpperCase() }}</span>
                  <span class="truncate max-w-16">{{ pic.authorNickname || '未知' }}</span>
                </span>
                <span class="text-xs text-gray-400">收藏于 {{ formatTime(pic.favoriteTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ALBUMS -->
      <div v-else-if="isLoggedIn && activeTab === 'albums'">
        <div v-if="!viewingAlbum">
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-4">
              <div>
                <h2 class="text-2xl font-bold text-gray-800">专辑管理</h2>
                <p class="text-sm text-gray-500 mt-1">共 {{ displayedAlbums.length }} 个专辑</p>
              </div>
              <div class="flex bg-gray-100 rounded-xl p-1">
                <button @click="albumCategory = 'created'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
                    albumCategory === 'created'
                      ? 'bg-white text-blue-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900']">
                  我创建的
                </button>
                <button @click="albumCategory = 'collaboration'"
                  :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
                    albumCategory === 'collaboration'
                      ? 'bg-white text-blue-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900']">
                  我协作的
                </button>
              </div>
            </div>
            <button v-if="albumCategory === 'created'" @click="openCreateAlbum"
              class="px-5 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl shadow-lg hover:shadow-xl transition text-sm font-medium">
              <span class="flex items-center space-x-1.5">
                <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                <span>新建专辑</span>
              </span>
            </button>
          </div>
          <div v-if="displayedAlbums.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm">
            <div class="text-6xl mb-4">📁</div>
            <h3 class="text-xl font-medium text-gray-500">{{ albumCategory === 'created' ? '还没有专辑' : '暂无协作的专辑' }}</h3>
            <p v-if="albumCategory === 'collaboration'" class="text-sm text-gray-400 mt-2">接受邀请后，协作的专辑将显示在这里</p>
          </div>
          <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            <div v-for="album in displayedAlbums" :key="album.id"
              class="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer"
              @click="openAlbumDetail(album)">
              <div class="aspect-video relative overflow-hidden bg-gradient-to-br from-blue-100 to-purple-100">
                <img v-if="album.coverUrl" :src="album.coverUrl" class="w-full h-full object-cover transform group-hover:scale-105 transition duration-500" />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <svg class="w-16 h-16 text-white/60" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                  </svg>
                </div>
                <div class="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent"></div>
                <div class="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition duration-300 flex space-x-1">
                  <button v-if="album.userRole === 'OWNER' || album.userRole === 'COLLABORATOR'" @click.stop="openEditAlbum(album)"
                    class="p-2 bg-white/90 rounded-lg text-gray-700 hover:text-blue-600 shadow-md hover:bg-white transition" title="编辑">
                    <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button v-if="album.userRole === 'OWNER' && !album.isDefault" @click.stop="confirmDeleteAlbum(album)"
                    class="p-2 bg-white/90 rounded-lg text-gray-700 hover:text-red-600 shadow-md hover:bg-white transition" title="删除">
                    <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
                <div class="absolute bottom-3 left-4 right-4">
                  <div class="flex items-center space-x-2 mb-2">
                    <span v-if="album.isDefault" class="inline-block px-2 py-0.5 bg-blue-500/90 text-white text-xs rounded-md">系统默认</span>
                    <span v-if="album.isCollaboration" class="inline-block px-2 py-0.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white text-xs rounded-md shadow-sm">协作</span>
                  </div>
                  <p v-if="album.isCollaboration" class="text-xs text-white/80">
                    创建者：{{ album.creatorNickname || album.creatorUsername }}
                  </p>
                </div>
              </div>
              <div class="p-4">
                <h3 class="font-semibold text-gray-800 truncate">{{ album.name }}</h3>
                <p v-if="album.description" class="text-xs text-gray-500 mt-1 line-clamp-2">{{ album.description }}</p>
                <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-100">
                  <span class="text-xs text-gray-500 flex items-center space-x-1">
                    <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span>{{ album.pictureCount }} 张</span>
                  </span>
                  <span v-if="album.collaboratorCount > 0" class="text-xs text-blue-600 flex items-center space-x-1">
                    <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <span>{{ album.collaboratorCount }} 人协作</span>
                  </span>
                  <span v-else class="text-xs text-gray-500">{{ formatSize(album.totalSize) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <div class="mb-6">
            <button @click="viewingAlbum = false"
              class="inline-flex items-center space-x-1 text-sm text-gray-600 hover:text-blue-600 transition mb-3">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
              <span>返回专辑列表</span>
            </button>
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 flex items-center space-x-5">
              <div class="w-24 h-24 rounded-xl overflow-hidden bg-gradient-to-br from-blue-100 to-purple-100 flex-shrink-0">
                <img v-if="currentAlbumDetail.coverUrl" :src="currentAlbumDetail.coverUrl" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1">
                <div class="flex items-center space-x-2 mb-1">
                  <h2 class="text-2xl font-bold text-gray-800">{{ currentAlbumDetail.name }}</h2>
                  <span v-if="currentAlbumDetail.isCollaboration" class="inline-block px-2 py-0.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white text-xs rounded-md shadow-sm">协作</span>
                </div>
                <p v-if="currentAlbumDetail.description" class="text-sm text-gray-500 mt-1">{{ currentAlbumDetail.description }}</p>
                <div class="flex items-center space-x-4 mt-2 text-sm text-gray-500">
                  <span>{{ currentAlbumDetail.pictureCount }} 张图片</span>
                  <span>{{ formatSize(currentAlbumDetail.totalSize) }}</span>
                  <span v-if="currentAlbumDetail.lastUploadTime">最近更新: {{ formatTime(currentAlbumDetail.lastUploadTime) }}</span>
                  <span v-if="currentAlbumDetail.isCollaboration" class="text-blue-600">
                    创建者：{{ currentAlbumDetail.creatorNickname || currentAlbumDetail.creatorUsername }}
                  </span>
                  <span v-if="currentAlbumDetail.collaboratorCount > 0" class="flex items-center space-x-1 text-blue-600">
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <span>{{ currentAlbumDetail.collaboratorCount }} 人协作</span>
                  </span>
                </div>
              </div>
              <div class="flex items-center space-x-2">
                <button @click="openShareModal('album', currentAlbumDetail)"
                  class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                  </svg>
                  <span>分享</span>
                </button>
                <button v-if="currentAlbumDetail.userRole === 'OWNER' || currentAlbumDetail.userRole === 'COLLABORATOR'"
                  @click="openCollaborationManagement(currentAlbumDetail)"
                  class="px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  <span>协作管理</span>
                </button>
                <button v-if="currentAlbumDetail.userRole === 'OWNER' || currentAlbumDetail.userRole === 'COLLABORATOR'"
                  @click="openEditAlbum(currentAlbumDetail)"
                  class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition">编辑专辑</button>
              </div>
            </div>
          </div>
          <div v-if="loading" class="flex justify-center py-20"><div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div></div>
          <div v-else-if="albumPictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm">
            <div class="text-6xl mb-4">📭</div>
            <h3 class="text-xl font-medium text-gray-500">该专辑暂无图片</h3>
          </div>
          <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="pic in albumPictures" :key="pic.id"
              class="group relative bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
              <div class="aspect-square bg-gray-100 relative overflow-hidden cursor-pointer" @click="viewPicture(pic)">
                <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
                <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition duration-300">
                  <button v-if="currentAlbumDetail.userRole === 'OWNER' || pic.userId === currentUser?.id"
                    @click.stop="removeFromAlbum(pic)"
                    class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-red-600 shadow-md transition" title="从专辑移除">
                    <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4" />
                    </svg>
                  </button>
                </div>
              </div>
              <div class="p-3">
                <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
                <p v-if="currentAlbumDetail.isCollaboration && pic.userId !== currentUser?.id" class="text-xs text-gray-400 mt-0.5">
                  上传者：{{ pic.authorNickname || '未知' }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- WATERMARK CONFIG -->
      <div v-else-if="isLoggedIn && activeTab === 'watermark'">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">水印配置</h2>
            <p class="text-sm text-gray-500 mt-1">配置水印参数，保护您的图片版权</p>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-6">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-6">
                <h3 class="text-lg font-semibold text-gray-800 flex items-center space-x-2">
                  <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <span>基本设置</span>
                </h3>
                <label class="flex items-center space-x-2 cursor-pointer">
                  <span class="text-sm text-gray-600">{{ watermarkConfig.enabled ? '已开启' : '已关闭' }}</span>
                  <div class="relative" @click="watermarkConfig.enabled = !watermarkConfig.enabled">
                    <div :class="['w-11 h-6 rounded-full transition-colors', watermarkConfig.enabled ? 'bg-blue-500' : 'bg-gray-300']"></div>
                    <div :class="['absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform', watermarkConfig.enabled ? 'translate-x-5' : '']"></div>
                  </div>
                </label>
              </div>

              <div class="space-y-5" :class="watermarkConfig.enabled ? '' : 'opacity-50 pointer-events-none'">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">水印类型</label>
                  <div class="grid grid-cols-2 gap-3">
                    <button @click="watermarkConfig.type = 'text'"
                      :class="['p-3 rounded-xl border-2 text-center transition',
                        watermarkConfig.type === 'text' ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-gray-200 text-gray-600 hover:border-blue-300']">
                      <svg class="w-6 h-6 mx-auto mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                      <span class="text-sm font-medium">文字水印</span>
                    </button>
                    <button @click="watermarkConfig.type = 'image'"
                      :class="['p-3 rounded-xl border-2 text-center transition',
                        watermarkConfig.type === 'image' ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-gray-200 text-gray-600 hover:border-blue-300']">
                      <svg class="w-6 h-6 mx-auto mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                      <span class="text-sm font-medium">图片水印</span>
                    </button>
                  </div>
                </div>

                <div v-if="watermarkConfig.type === 'text'" class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">水印内容</label>
                    <input v-model="watermarkConfig.textContent" type="text" placeholder="输入水印文字"
                      class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    <p class="text-xs text-gray-400 mt-1">支持 {username} 占位符，自动替换为您的昵称</p>
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1.5">字体大小</label>
                      <input v-model.number="watermarkConfig.fontSize" type="number" min="8" max="120"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1.5">字体颜色</label>
                      <div class="flex items-center space-x-2">
                        <input type="color" v-model="watermarkConfig.fontColor"
                          class="w-10 h-10 rounded-lg border border-gray-200 cursor-pointer" />
                        <input v-model="watermarkConfig.fontColor" type="text"
                          class="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="watermarkConfig.type === 'image'" class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">水印图片</label>
                    <div @click="$refs.watermarkImageInput.click()"
                      class="border-2 border-dashed border-gray-200 rounded-xl p-4 text-center cursor-pointer hover:border-blue-300 transition">
                      <img v-if="watermarkConfig.imageUrl" :src="watermarkConfig.imageUrl" class="max-h-24 mx-auto" />
                      <div v-else>
                        <svg class="w-8 h-8 mx-auto text-gray-400 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <p class="text-sm text-gray-500">点击上传水印图片</p>
                      </div>
                      <input ref="watermarkImageInput" type="file" class="hidden" accept="image/*" @change="uploadWatermarkImage" />
                    </div>
                  </div>
                  <div v-if="watermarkConfig.imageUrl" class="grid grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1.5">图片宽度 (px)</label>
                      <input v-model.number="watermarkConfig.imageWidth" type="number" min="10"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1.5">图片高度 (px)</label>
                      <input v-model.number="watermarkConfig.imageHeight" type="number" min="10"
                        class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">透明度 <span class="text-blue-600 font-semibold">{{ Math.round(watermarkConfig.opacity * 100) }}%</span></label>
                  <input v-model.number="watermarkConfig.opacity" type="range" min="0.05" max="1" step="0.05"
                    class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-500" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">水印位置</label>
                  <div class="grid grid-cols-3 gap-2">
                    <button v-for="pos in watermarkPositions" :key="pos.value" @click="watermarkConfig.position = pos.value"
                      :class="['p-2 rounded-lg text-xs font-medium transition border',
                        watermarkConfig.position === pos.value
                          ? 'bg-blue-500 text-white border-blue-500'
                          : 'bg-white text-gray-600 border-gray-200 hover:border-blue-300']">
                      {{ pos.label }}
                    </button>
                  </div>
                </div>

                <div v-if="watermarkConfig.position !== 'center' && watermarkConfig.position !== 'tile'" class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">水平边距 (px)</label>
                    <input v-model.number="watermarkConfig.marginX" type="number" min="0"
                      class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">垂直边距 (px)</label>
                    <input v-model.number="watermarkConfig.marginY" type="number" min="0"
                      class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                  </div>
                </div>

                <div v-if="watermarkConfig.position === 'tile'">
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">平铺间距 (px)</label>
                  <input v-model.number="watermarkConfig.tileGap" type="number" min="50"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>

                <label class="flex items-center space-x-2 cursor-pointer">
                  <input type="checkbox" v-model="watermarkConfig.smartMode" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                  <span class="text-sm text-gray-700">智能模式</span>
                  <span class="text-xs text-gray-400">自动调整水印颜色以适应图片内容</span>
                </label>
              </div>

              <div class="mt-6 flex space-x-3">
                <button @click="saveWatermarkConfig"
                  class="px-6 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
                  保存设置
                </button>
                <button @click="previewWatermark"
                  :disabled="watermarkPreviewLoading"
                  class="px-6 py-2.5 bg-white border border-gray-200 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition">
                  <span v-if="watermarkPreviewLoading" class="flex items-center space-x-1">
                    <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                    <span>生成中...</span>
                  </span>
                  <span v-else>预览效果</span>
                </button>
              </div>
            </div>

            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-800 flex items-center space-x-2">
                  <svg class="w-5 h-5 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z" />
                  </svg>
                  <span>模板管理</span>
                </h3>
                <button @click="openCreateWatermarkTemplate"
                  class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition flex items-center space-x-1">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                  <span>新建模板</span>
                </button>
              </div>

              <div v-if="watermarkTemplates.length === 0" class="text-center py-10 text-gray-400 text-sm">
                暂无模板
              </div>
              <div v-else class="space-y-3">
                <div class="mb-3">
                  <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">系统模板</p>
                  <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                    <div v-for="t in watermarkTemplates.filter(t => t.isSystem)" :key="t.id"
                      class="p-3 rounded-xl border border-gray-200 hover:border-blue-300 transition cursor-pointer group"
                      @click="applyWatermarkTemplate(t)">
                      <div class="flex items-center justify-between">
                        <div class="flex-1 min-w-0">
                          <p class="text-sm font-medium text-gray-800 truncate">{{ t.name }}</p>
                          <p class="text-xs text-gray-400 mt-0.5">
                            {{ t.type === 'text' ? '文字' : '图片' }} · {{ watermarkPositionLabel(t.position) }}
                          </p>
                        </div>
                        <div class="flex items-center space-x-1">
                          <span class="text-[10px] px-2 py-0.5 bg-blue-50 text-blue-600 rounded-full">系统</span>
                          <button @click.stop="applyWatermarkTemplate(t)"
                            class="p-1 text-gray-400 hover:text-blue-600 opacity-0 group-hover:opacity-100 transition" title="应用">
                            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                            </svg>
                          </button>
                        </div>
                      </div>
                      <div v-if="t.type === 'text'" class="mt-2 px-2 py-1 bg-gray-50 rounded text-xs text-gray-500 truncate">
                        {{ t.textContent }}
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="watermarkMyTemplates.length > 0">
                  <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">我的模板</p>
                  <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                    <div v-for="t in watermarkMyTemplates" :key="t.id"
                      class="p-3 rounded-xl border border-gray-200 hover:border-blue-300 transition cursor-pointer group"
                      @click="applyWatermarkTemplate(t)">
                      <div class="flex items-center justify-between">
                        <div class="flex-1 min-w-0">
                          <p class="text-sm font-medium text-gray-800 truncate">{{ t.name }}</p>
                          <p class="text-xs text-gray-400 mt-0.5">
                            {{ t.type === 'text' ? '文字' : '图片' }} · {{ watermarkPositionLabel(t.position) }}
                          </p>
                        </div>
                        <div class="flex items-center space-x-1">
                          <button @click.stop="openEditWatermarkTemplate(t)"
                            class="p-1 text-gray-400 hover:text-blue-600 opacity-0 group-hover:opacity-100 transition" title="编辑">
                            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                          </button>
                          <button @click.stop="confirmDeleteWatermarkTemplate(t)"
                            class="p-1 text-gray-400 hover:text-red-600 opacity-0 group-hover:opacity-100 transition" title="删除">
                            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      </div>
                      <div v-if="t.type === 'text'" class="mt-2 px-2 py-1 bg-gray-50 rounded text-xs text-gray-500 truncate">
                        {{ t.textContent }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="space-y-6">
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <h3 class="text-lg font-semibold text-gray-800 mb-4">预览</h3>
              <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 rounded-xl flex items-center justify-center overflow-hidden relative">
                <img v-if="watermarkPreviewUrl" :src="watermarkPreviewUrl" class="w-full h-full object-contain" />
                <div v-else class="text-center text-gray-400">
                  <svg class="w-12 h-12 mx-auto mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <p class="text-sm">点击"预览效果"查看</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- TAGS -->
      <div v-else-if="isLoggedIn && activeTab === 'tags'">
        <div v-if="!viewingTag">
          <div class="flex items-center justify-between mb-6">
            <div>
              <h2 class="text-2xl font-bold text-gray-800">主题词</h2>
              <p class="text-sm text-gray-500 mt-1">共 {{ allTags.length }} 个主题词</p>
            </div>
            <div class="relative w-64">
              <input v-model="tagSearch" @input="searchTags" type="text" placeholder="搜索主题词..."
                class="w-full pl-10 pr-4 py-2 bg-white border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <svg class="w-4 h-4 absolute left-3 top-2.5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 mb-6">
            <h3 class="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-6">主题词云</h3>
            <div v-if="displayedTags.length === 0" class="text-center py-10 text-gray-400">暂无主题词，上传图片时添加主题词即可显示</div>
            <div v-else class="flex flex-wrap items-center justify-center gap-3 min-h-[200px]">
              <button v-for="tag in displayedTags" :key="tag.id" @click="openTagDetail(tag)"
                :style="tagCloudStyle(tag)"
                class="rounded-full px-4 py-2 transition-all duration-200 hover:shadow-lg hover:scale-105 bg-opacity-60">
                #{{ tag.name }}
              </button>
            </div>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div class="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
              <h3 class="font-semibold text-gray-800">主题词列表</h3>
              <span class="text-xs text-gray-500">按热度排序</span>
            </div>
            <div v-if="displayedTags.length === 0" class="p-10 text-center text-gray-400">暂无主题词</div>
            <div v-else class="divide-y divide-gray-50">
              <div v-for="tag in displayedTags" :key="tag.id"
                class="px-5 py-3 hover:bg-gray-50 transition flex items-center justify-between group">
                <button @click="openTagDetail(tag)" class="flex items-center space-x-4 flex-1 text-left">
                  <span :class="tagRankColor(tag.referenceCount)"
                    class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold">
                    {{ tag.referenceCount }}
                  </span>
                  <div>
                    <span class="font-medium text-gray-800">#{{ tag.name }}</span>
                    <p class="text-xs text-gray-400 mt-0.5">创建于 {{ formatTime(tag.createTime) }}</p>
                  </div>
                </button>
                <button v-if="tag.referenceCount === 0" @click="confirmDeleteTag(tag)"
                  class="p-2 text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition" title="删除未使用的主题词">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <div class="mb-6">
            <button @click="viewingTag = false"
              class="inline-flex items-center space-x-1 text-sm text-gray-600 hover:text-blue-600 transition mb-3">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
              <span>返回主题词列表</span>
            </button>
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
              <div class="flex items-center space-x-4">
                <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center text-white text-2xl font-bold shadow-lg">#</div>
                <div>
                  <h2 class="text-2xl font-bold text-gray-800">{{ currentTagDetail.name }}</h2>
                  <p class="text-sm text-gray-500 mt-1">
                    被引用 <span class="font-semibold text-purple-600">{{ currentTagDetail.referenceCount }}</span> 次
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div v-if="loading" class="flex justify-center py-20"><div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div></div>
          <div v-else-if="tagPictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm">
            <div class="text-6xl mb-4">🏷️</div>
            <h3 class="text-xl font-medium text-gray-500">暂无使用该主题词的图片</h3>
          </div>
          <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="pic in tagPictures" :key="pic.id"
              class="group relative bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
              <div class="aspect-square bg-gray-100 relative overflow-hidden cursor-pointer" @click="viewPicture(pic)">
                <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
              </div>
              <div class="p-3">
                <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- RECYCLE BIN -->
      <div v-else-if="isLoggedIn && activeTab === 'recycle'">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">回收站</h2>
            <p class="text-sm text-gray-500 mt-1">共 {{ recyclePictures.length }} 张图片，保留 30 天后自动清除</p>
          </div>
          <div class="flex items-center space-x-3">
            <button v-if="recyclePictures.length > 0" @click="confirmClearRecycleBin"
              class="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg text-sm font-medium transition flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>清空回收站</span>
            </button>
          </div>
        </div>

        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 mb-6">
          <div class="flex flex-wrap items-center gap-3">
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-500">回收站中的图片将在 30 天后自动永久删除</span>
            </div>
            <div class="flex-1"></div>
            <div class="flex items-center space-x-3">
              <label class="flex items-center space-x-2 cursor-pointer text-sm text-gray-600 hover:text-gray-900">
                <input type="checkbox" v-model="recycleMultiSelectMode" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
                <span>多选模式</span>
              </label>
              <span v-if="recycleMultiSelectMode && selectedRecyclePictureIds.length > 0" class="text-sm text-blue-600 font-medium">
                已选 {{ selectedRecyclePictureIds.length }} 张
              </span>
            </div>
          </div>
        </div>

        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="recyclePictures.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">🗑️</div>
          <h3 class="text-xl font-medium text-gray-500">回收站为空</h3>
          <p class="text-sm text-gray-400 mt-2">删除的图片会暂时保留在这里</p>
        </div>
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div v-for="pic in recyclePictures" :key="pic.id"
            class="group relative bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
            <div v-if="recycleMultiSelectMode" class="absolute top-2 left-2 z-10">
              <input type="checkbox" :value="pic.id" v-model="selectedRecyclePictureIds"
                class="w-5 h-5 text-blue-600 rounded bg-white/90 border-2 border-gray-300 focus:ring-blue-500 cursor-pointer" />
            </div>
            <div class="aspect-square bg-gray-100 relative overflow-hidden">
              <img :src="pic.url" class="object-cover w-full h-full transform group-hover:scale-105 transition duration-500" loading="lazy" />
              <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition duration-300"></div>
              <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition duration-300 flex space-x-1">
                <button @click.stop="confirmRestorePicture(pic)"
                  class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-green-600 shadow-md hover:bg-white transition" title="恢复">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                </button>
                <button @click.stop="confirmPermanentDelete(pic)"
                  class="p-1.5 bg-white/90 rounded-full text-gray-700 hover:text-red-600 shadow-md hover:bg-white transition" title="永久删除">
                  <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
              <div class="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/60 to-transparent">
                <div class="flex items-center justify-between">
                  <span class="text-xs text-white font-medium">剩余 {{ pic.remainingDays }} 天</span>
                </div>
              </div>
            </div>
            <div class="p-3">
              <h3 class="text-sm font-medium text-gray-800 truncate">{{ pic.name }}</h3>
              <div class="flex justify-between items-center mt-1.5">
                <span class="text-xs text-gray-400">删除时间: {{ formatTime(pic.deleteTime) }}</span>
              </div>
              <div class="mt-2">
                <div class="flex flex-wrap gap-1">
                  <span v-for="tag in (pic.tags || []).slice(0, 2)" :key="tag.id"
                    class="text-[10px] px-1.5 py-0.5 bg-purple-50 text-purple-700 rounded">#{{ tag.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="recycleMultiSelectMode && selectedRecyclePictureIds.length > 0"
          class="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-30 bg-white rounded-2xl shadow-2xl border border-gray-100 px-4 py-3 flex items-center space-x-3">
          <span class="text-sm font-medium text-gray-700 border-r border-gray-200 pr-3">已选 {{ selectedRecyclePictureIds.length }} 张</span>
          <button @click="confirmBatchRestore"
            class="px-4 py-2 bg-gradient-to-r from-emerald-500 to-teal-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>批量恢复</span>
            </span>
          </button>
          <button @click="confirmBatchPermanentDelete"
            class="px-4 py-2 bg-gradient-to-r from-red-500 to-rose-600 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
            <span class="flex items-center space-x-1">
              <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>批量永久删除</span>
            </span>
          </button>
          <button @click="selectedRecyclePictureIds = []; recycleMultiSelectMode = false" class="px-3 py-2 text-gray-500 hover:text-gray-700 text-sm">取消</button>
        </div>
      </div>

      <!-- SHARES -->
      <div v-else-if="isLoggedIn && activeTab === 'shares'">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-800">我的分享</h2>
            <p class="text-sm text-gray-500 mt-1">共 {{ myShares.length }} 条分享记录</p>
          </div>
        </div>
        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
        <div v-else-if="myShares.length === 0" class="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
          <div class="text-6xl mb-4">🔗</div>
          <h3 class="text-xl font-medium text-gray-500 mb-2">暂无分享记录</h3>
          <p class="text-sm text-gray-400">打开图片或专辑详情，点击"分享"按钮创建分享链接</p>
        </div>
        <div v-else class="space-y-4">
          <div v-for="share in myShares" :key="share.id"
            class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 hover:shadow-lg transition">
            <div class="flex items-start space-x-4">
              <div class="w-16 h-16 rounded-xl overflow-hidden bg-gradient-to-br from-blue-100 to-purple-100 flex-shrink-0">
                <img v-if="share.coverUrl" :src="share.coverUrl" class="w-full h-full object-cover" />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <svg class="w-8 h-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between">
                  <div>
                    <div class="flex items-center space-x-2 mb-1">
                      <h3 class="font-semibold text-gray-800 truncate">{{ share.title }}</h3>
                      <span :class="['text-[10px] px-2 py-0.5 rounded-full',
                        share.shareType === 'picture' ? 'bg-blue-100 text-blue-700' : 'bg-purple-100 text-purple-700']">
                        {{ share.shareType === 'picture' ? '图片' : '专辑' }}
                      </span>
                      <span v-if="share.expired" class="text-[10px] px-2 py-0.5 rounded-full bg-red-100 text-red-700">已过期</span>
                      <span v-if="share.hasPassword" class="text-[10px] px-2 py-0.5 rounded-full bg-yellow-100 text-yellow-700">🔒 密码保护</span>
                    </div>
                    <div class="flex items-center space-x-4 text-xs text-gray-500">
                      <span>创建时间：{{ formatTime(share.createTime) }}</span>
                      <span>有效期：{{ share.expireDays === 0 ? '永久' : share.expireDays + '天' }}</span>
                      <span>访问次数：{{ share.viewCount }} 次</span>
                    </div>
                    <div class="mt-2 flex items-center space-x-2">
                      <div class="flex-1 bg-gray-100 rounded-lg px-3 py-1.5 text-xs text-gray-600 font-mono truncate">
                        {{ getShareFullUrl(share.shareUrl) }}
                      </div>
                      <button @click="copyShareLink(share)"
                        class="px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg text-xs hover:bg-blue-100 transition">
                        复制链接
                      </button>
                    </div>
                  </div>
                  <button @click="confirmDeleteShare(share)"
                    class="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition" title="删除分享">
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- STATS -->
      <div v-else-if="isLoggedIn && activeTab === 'stats'">
        <h2 class="text-2xl font-bold text-gray-800 mb-6">数据统计</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-5 mb-8">
          <div class="bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-blue-100 text-sm">专辑总数</p>
                <p class="text-3xl font-bold mt-1">{{ globalStats.albumCount || 0 }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-emerald-100 text-sm">图片总数</p>
                <p class="text-3xl font-bold mt-1">{{ globalStats.pictureCount || 0 }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-br from-orange-500 to-amber-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-orange-100 text-sm">存储空间</p>
                <p class="text-3xl font-bold mt-1">{{ formatSize(globalStats.totalStorageSize) }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-br from-pink-500 to-rose-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-pink-100 text-sm">主题词种类</p>
                <p class="text-3xl font-bold mt-1">{{ allTags.length }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                </svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-br from-red-500 to-rose-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-red-100 text-sm">收到点赞</p>
                <p class="text-3xl font-bold mt-1">{{ globalStats.receivedLikeCount || 0 }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
              </div>
            </div>
          </div>
          <div class="bg-gradient-to-br from-cyan-500 to-blue-600 rounded-2xl p-6 text-white shadow-lg">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-cyan-100 text-sm">收到评论</p>
                <p class="text-3xl font-bold mt-1">{{ globalStats.receivedCommentCount || 0 }}</p>
              </div>
              <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>
              </div>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
              <svg class="w-5 h-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              <span>专辑图片分布</span>
            </h3>
            <div v-if="allAlbums.length === 0" class="text-center py-10 text-gray-400">暂无专辑数据</div>
            <div v-else class="space-y-3">
              <div v-for="album in allAlbumsSorted" :key="album.id">
                <div class="flex justify-between items-center mb-1">
                  <span class="text-sm font-medium text-gray-700">{{ album.name }}</span>
                  <span class="text-xs text-gray-500">{{ album.pictureCount }} 张 · {{ formatSize(album.totalSize) }}</span>
                </div>
                <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-blue-400 to-purple-500 rounded-full transition-all duration-500"
                    :style="{ width: albumPercentage(album) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 class="font-bold text-gray-800 mb-4 flex items-center space-x-2">
              <svg class="w-5 h-5 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
              </svg>
              <span>热门主题词 TOP 10</span>
            </h3>
            <div v-if="topTenTags.length === 0" class="text-center py-10 text-gray-400">暂无主题词数据</div>
            <div v-else class="space-y-2">
              <div v-for="(tag, idx) in topTenTags" :key="tag.id" class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 transition">
                <span :class="['w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold',
                    idx === 0 ? 'bg-yellow-400 text-white' :
                    idx === 1 ? 'bg-gray-300 text-white' :
                    idx === 2 ? 'bg-amber-600 text-white' : 'bg-gray-100 text-gray-600']">
                  {{ idx + 1 }}
                </span>
                <span class="font-medium text-gray-700 flex-1 cursor-pointer hover:text-blue-600" @click="openTagDetail(tag)">
                  #{{ tag.name }}
                </span>
                <div class="flex items-center space-x-2">
                  <div class="w-24 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                    <div class="h-full bg-gradient-to-r from-purple-400 to-pink-500"
                      :style="{ width: (tag.referenceCount / topRefCount * 100) + '%' }"></div>
                  </div>
                  <span class="text-xs text-gray-500 w-8 text-right">{{ tag.referenceCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Auth Modal -->
    <div v-if="showAuthModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showAuthModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all overflow-hidden">
        <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-6 text-white">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-xl font-bold">{{ authMode === 'login' ? '欢迎回来' : '创建账号' }}</h3>
              <p class="text-blue-100 text-sm mt-1">{{ authMode === 'login' ? '登录后管理您的图片' : '注册后开始您的图片之旅' }}</p>
            </div>
            <button @click="showAuthModal = false" class="text-white/70 hover:text-white transition">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
        <div class="p-6 space-y-5">
          <div v-if="authError" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg text-sm">
            {{ authError }}
          </div>
          <div v-if="authMode === 'register'">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">昵称</label>
            <input v-model="authForm.nickname" type="text" placeholder="请输入昵称（可选）"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">用户名</label>
            <input v-model="authForm.username" type="text" placeholder="请输入用户名"
              @keyup.enter="authMode === 'login' ? doLogin() : doRegister()"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            <p class="text-xs text-gray-400 mt-1">3-20个字符</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">密码</label>
            <input v-model="authForm.password" type="password" placeholder="请输入密码"
              @keyup.enter="authMode === 'login' ? doLogin() : doRegister()"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            <p class="text-xs text-gray-400 mt-1">6-20个字符</p>
          </div>
          <button @click="authMode === 'login' ? doLogin() : doRegister()" :disabled="authLoading"
            :class="['w-full py-2.5 rounded-lg font-medium text-white transition shadow-md',
              authLoading ? 'bg-gray-400 cursor-not-allowed' : 'bg-gradient-to-r from-blue-500 to-purple-600 hover:shadow-lg hover:scale-[1.02]']">
            <span v-if="authLoading" class="flex items-center justify-center space-x-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ authMode === 'login' ? '登录中...' : '注册中...' }}</span>
            </span>
            <span v-else>{{ authMode === 'login' ? '登 录' : '注 册' }}</span>
          </button>
          <div class="text-center text-sm text-gray-500">
            {{ authMode === 'login' ? '还没有账号？' : '已有账号？' }}
            <button @click="switchAuthMode" class="text-blue-600 hover:text-blue-700 font-medium ml-1">
              {{ authMode === 'login' ? '立即注册' : '去登录' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Upload Modal -->
    <div v-if="showUploadModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showUploadModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg relative transform transition-all max-h-[90vh] overflow-y-auto">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between sticky top-0 bg-white z-10">
          <h3 class="text-lg font-bold text-gray-800">上传图片</h3>
          <button @click="showUploadModal = false" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-5">
          <div @click="$refs.uploadFileInput.click()" @dragover.prevent @drop.prevent="handleFileDrop"
            :class="['border-2 border-dashed rounded-xl p-10 text-center cursor-pointer transition-all',
              uploadPreview ? 'border-blue-400 bg-blue-50' : 'border-gray-200 hover:border-blue-300 hover:bg-gray-50']">
            <div v-if="uploadPreview">
              <img :src="uploadPreview" class="max-h-40 mx-auto rounded-lg shadow-md" />
              <p class="text-sm text-gray-500 mt-2">{{ uploadFileName }}</p>
            </div>
            <div v-else>
              <svg class="w-12 h-12 mx-auto text-gray-400 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              <p class="text-gray-600 font-medium">点击或拖拽图片到此处</p>
              <p class="text-xs text-gray-400 mt-1">支持 JPG, PNG, GIF 等格式，最大 10MB</p>
            </div>
            <input ref="uploadFileInput" type="file" class="hidden" accept="image/*" @change="handleUploadFileSelect" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">选择专辑
              <span class="text-gray-400 text-xs">(可多选，不选则归入默认专辑)</span>
            </label>
            <div class="space-y-2">
              <div class="flex flex-wrap gap-2 max-h-32 overflow-y-auto p-2 bg-gray-50 rounded-lg border border-gray-200">
                <label v-for="a in allAlbums" :key="a.id"
                  :class="['px-3 py-1.5 rounded-lg text-sm cursor-pointer transition border',
                    uploadAlbumIds.includes(a.id)
                      ? 'bg-blue-500 text-white border-blue-500'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300']">
                  <input type="checkbox" class="hidden" :value="a.id" v-model="uploadAlbumIds" />
                  <span v-if="a.isDefault">📁 </span>{{ a.name }}
                </label>
              </div>
              <button @click="showUploadModal = false; openCreateAlbum(true)"
                class="text-sm text-blue-600 hover:text-blue-700 flex items-center space-x-1">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                <span>新建专辑</span>
              </button>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">添加主题词
              <span class="text-gray-400 text-xs">(用逗号分隔，支持多个)</span>
            </label>
            <input v-model="uploadTagInput" type="text" placeholder="例如：风景, 旅行, 春天"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
            <div v-if="hotTags.length > 0" class="mt-2">
              <p class="text-xs text-gray-500 mb-1.5">热门主题词快速添加：</p>
              <div class="flex flex-wrap gap-1.5">
                <button v-for="t in hotTags.slice(0, 10)" :key="t.id" @click="toggleUploadTag(t.name)"
                  :class="['text-xs px-2.5 py-1 rounded-full transition',
                    uploadTagInput.includes(t.name) ? 'bg-purple-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
                  #{{ t.name }}
                </button>
              </div>
            </div>
          </div>
          <div class="border-t border-gray-100 pt-4">
            <label class="flex items-center space-x-2 cursor-pointer mb-3">
              <input type="checkbox" v-model="uploadAddWatermark" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
              <span class="text-sm font-medium text-gray-700">上传时添加水印</span>
            </label>
            <div v-if="uploadAddWatermark">
              <label class="block text-sm font-medium text-gray-700 mb-1.5">选择水印模板</label>
              <select v-model="uploadWatermarkTemplateId"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option :value="null">使用默认配置</option>
                <optgroup label="系统模板">
                  <option v-for="t in watermarkTemplates.filter(t => t.isSystem)" :key="t.id" :value="t.id">{{ t.name }}</option>
                </optgroup>
                <optgroup v-if="watermarkMyTemplates.length > 0" label="我的模板">
                  <option v-for="t in watermarkMyTemplates" :key="t.id" :value="t.id">{{ t.name }}</option>
                </optgroup>
              </select>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3 sticky bottom-0 bg-white">
          <button @click="showUploadModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="doUpload" :disabled="!uploadFile || uploading"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              uploadFile && !uploading
                ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg'
                : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
            <span v-if="uploading" class="flex items-center space-x-1.5">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>上传中...</span>
            </span>
            <span v-else>确认上传</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Album Modal -->
    <div v-if="showAlbumModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="closeAlbumModal"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">{{ editingAlbum ? '编辑专辑' : '新建专辑' }}</h3>
          <button @click="closeAlbumModal" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">专辑名称 <span class="text-red-500">*</span></label>
            <input v-model="albumForm.name" type="text" maxlength="100" placeholder="请输入专辑名称"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">专辑描述</label>
            <textarea v-model="albumForm.description" maxlength="500" rows="3" placeholder="请输入专辑描述（可选）"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">显示顺序（数字越小越靠前）</label>
            <input v-model.number="albumForm.displayOrder" type="number"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
          </div>
          <label class="flex items-center space-x-2 cursor-pointer">
            <input type="checkbox" v-model="albumForm.isPublic" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
            <span class="text-sm text-gray-700">公开专辑</span>
          </label>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3">
          <button @click="closeAlbumModal"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="submitAlbumForm" :disabled="!albumForm.name.trim()"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              albumForm.name.trim() ? 'bg-blue-500 text-white hover:bg-blue-600' : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
            确认
          </button>
        </div>
      </div>
    </div>

    <!-- Picture Detail Modal -->
    <div v-if="showPictureDetail" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/60" @click="closePictureDetail"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden relative flex flex-col lg:flex-row">
        <button @click="closePictureDetail"
          class="absolute top-4 right-4 z-10 p-2 bg-white/90 rounded-full text-gray-600 hover:bg-white transition shadow-md">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
        <div class="lg:w-1/2 bg-gray-900 flex items-center justify-center p-4 min-h-[300px] lg:min-h-0">
          <img :src="currentPicture.url" class="max-w-full max-h-[50vh] lg:max-h-[80vh] object-contain rounded-lg" />
        </div>
        <div class="lg:w-1/2 p-6 overflow-y-auto">
          <div class="flex items-start justify-between mb-1">
            <h3 class="text-xl font-bold text-gray-800">{{ currentPicture.name }}</h3>
            <button @click="openShareModal('picture', currentPicture)"
              class="px-3 py-1.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg text-xs font-medium hover:shadow-lg transition flex items-center space-x-1">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
              </svg>
              <span>分享</span>
            </button>
          </div>
          <p class="text-xs text-gray-500 mb-4">
            <span v-if="currentPicture.authorNickname" class="inline-flex items-center space-x-1">
              <span class="w-4 h-4 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-[10px] font-bold text-white">{{ currentPicture.authorNickname.charAt(0).toUpperCase() }}</span>
              <span>{{ currentPicture.authorNickname }}</span>
            </span>
            <span v-if="currentPicture.authorNickname" class="mx-1">·</span>
            {{ formatTime(currentPicture.createTime) }} · {{ formatSize(currentPicture.size) }}
          </p>
          <div v-if="currentPicture.userId === currentUser?.id" class="mb-3 flex items-center space-x-2 flex-wrap gap-y-2">
            <button @click="togglePicturePublic"
              :class="['inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition',
                currentPicture.isPublic ? 'bg-green-100 text-green-700 hover:bg-green-200' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
              <svg v-if="currentPicture.isPublic" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg v-else class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
              <span>{{ currentPicture.isPublic ? '公开' : '私密' }}</span>
            </button>
            <span :class="['inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-medium',
              currentPicture.hasWatermark ? 'bg-violet-100 text-violet-700' : 'bg-gray-100 text-gray-500']">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
              </svg>
              <span>{{ currentPicture.hasWatermark ? '已加水印' : '未加水印' }}</span>
            </span>
            <button v-if="!currentPicture.hasWatermark" @click="addWatermarkToCurrentPicture"
              class="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg text-xs font-medium hover:shadow-md transition">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              <span>加水印</span>
            </button>
            <button v-if="currentPicture.hasWatermark" @click="removeWatermarkFromCurrentPicture"
              class="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-red-50 text-red-600 rounded-lg text-xs font-medium hover:bg-red-100 transition">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>移除水印</span>
            </button>
            <button @click="openImageEditor"
              class="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-gradient-to-r from-orange-500 to-amber-600 text-white rounded-lg text-xs font-medium hover:shadow-md transition">
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              <span>编辑</span>
            </button>
            <p v-if="currentPicture.hasWatermark && !currentPicture.originalUrl" class="text-[10px] text-amber-500 mt-0.5">无原图备份，无法移除水印</p>
          </div>
          <div class="flex items-center space-x-4 mb-5 pb-4 border-b border-gray-100">
            <button @click="toggleLike" class="flex items-center space-x-1.5 group transition">
              <svg v-if="currentPicture.isLiked" class="w-5 h-5 text-red-500" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
              <svg v-else class="w-5 h-5 text-gray-400 group-hover:text-red-400 transition" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
              <span :class="['text-sm font-medium', currentPicture.isLiked ? 'text-red-500' : 'text-gray-500']">{{ currentPicture.likeCount || 0 }}</span>
            </button>
            <button @click="focusCommentInput" class="flex items-center space-x-1.5 group transition">
              <svg class="w-5 h-5 text-gray-400 group-hover:text-blue-400 transition" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>
              <span class="text-sm font-medium text-gray-500">{{ currentPicture.commentCount || 0 }}</span>
            </button>
            <button @click="toggleFavorite" class="flex items-center space-x-1.5 group transition">
              <svg v-if="currentPicture.isFavorited" class="w-5 h-5 text-yellow-500" fill="currentColor" viewBox="0 0 24 24"><path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" /></svg>
              <svg v-else class="w-5 h-5 text-gray-400 group-hover:text-yellow-400 transition" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" /></svg>
              <span :class="['text-sm font-medium', currentPicture.isFavorited ? 'text-yellow-500' : 'text-gray-500']">{{ currentPicture.favoriteCount || 0 }}</span>
            </button>
          </div>
          <div class="mb-5">
            <h4 class="text-sm font-semibold text-gray-700 mb-3 flex items-center space-x-2">
              <svg class="w-4 h-4 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>
              <span>评论 ({{ pictureComments.length }})</span>
            </h4>
            <div class="space-y-3 max-h-60 overflow-y-auto mb-3">
              <div v-if="pictureComments.length === 0" class="text-center py-4 text-sm text-gray-400">暂无评论，来说点什么吧~</div>
              <div v-for="comment in pictureComments" :key="comment.id" class="flex items-start space-x-2 p-2 rounded-lg hover:bg-gray-50 transition">
                <div class="w-7 h-7 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                  {{ (comment.nickname || 'U').charAt(0).toUpperCase() }}
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center justify-between">
                    <span class="text-xs font-semibold text-gray-700">{{ comment.nickname || '未知用户' }}</span>
                    <button v-if="currentPicture.userId === currentUser?.id || comment.userId === currentUser?.id" @click="deleteComment(comment.id)"
                      class="text-xs text-gray-400 hover:text-red-500 transition">删除</button>
                  </div>
                  <p class="text-sm text-gray-600 mt-0.5 break-words">{{ comment.content }}</p>
                  <span class="text-xs text-gray-400">{{ formatTime(comment.createTime) }}</span>
                </div>
              </div>
            </div>
            <div class="flex space-x-2">
              <input ref="commentInput" v-model="newCommentContent" type="text" placeholder="写下你的评论..."
                @keyup.enter="submitComment"
                class="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <button @click="submitComment" :disabled="!newCommentContent.trim()"
                :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
                  newCommentContent.trim() ? 'bg-blue-500 text-white hover:bg-blue-600' : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
                发送
              </button>
            </div>
          </div>
          <div class="mb-5">
            <div class="flex items-center justify-between mb-2">
              <label class="text-sm font-semibold text-gray-700">所属专辑</label>
              <button v-if="!editingPictureFields && currentPicture.userId === currentUser?.id" @click="startEditPictureFields"
                class="text-xs text-blue-600 hover:text-blue-700 flex items-center space-x-0.5">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                <span>编辑</span>
              </button>
            </div>
            <div v-if="!editingPictureFields">
              <div v-if="(currentPicture.albums || []).length === 0" class="text-xs text-gray-400">未归属任何专辑</div>
              <div v-else class="flex flex-wrap gap-1.5">
                <span v-for="a in currentPicture.albums" :key="a.id"
                  class="inline-flex items-center space-x-1 px-2.5 py-1 bg-blue-50 text-blue-700 rounded-full text-xs cursor-pointer hover:bg-blue-100 transition"
                  @click="navigateToAlbum(a.id)">
                  <span v-if="a.isDefault">📁 </span>{{ a.name }}
                </span>
              </div>
            </div>
            <div v-else class="space-y-2">
              <div class="flex flex-wrap gap-1.5 max-h-32 overflow-y-auto p-2 bg-gray-50 rounded-lg border border-gray-200">
                <label v-for="a in allAlbums" :key="a.id"
                  :class="['px-2.5 py-1 rounded-lg text-xs cursor-pointer transition border',
                    editPictureAlbumIds.includes(a.id)
                      ? 'bg-blue-500 text-white border-blue-500'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300']">
                  <input type="checkbox" class="hidden" :value="a.id" v-model="editPictureAlbumIds" />
                  <span v-if="a.isDefault">📁 </span>{{ a.name }}
                </label>
              </div>
            </div>
          </div>
          <div class="mb-5">
            <div class="flex items-center justify-between mb-2">
              <label class="text-sm font-semibold text-gray-700">主题词</label>
              <button v-if="!editingPictureFields && currentPicture.userId === currentUser?.id" @click="startEditPictureFields"
                class="text-xs text-blue-600 hover:text-blue-700 flex items-center space-x-0.5">
                <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                <span>新增/编辑</span>
              </button>
            </div>
            <div v-if="!editingPictureFields">
              <div v-if="(currentPicture.tags || []).length === 0" class="text-xs text-gray-400">暂无主题词</div>
              <div v-else class="flex flex-wrap gap-1.5">
                <span v-for="t in currentPicture.tags" :key="t.id"
                  class="inline-flex items-center space-x-1 px-2.5 py-1 bg-purple-50 text-purple-700 rounded-full text-xs cursor-pointer hover:bg-purple-100 transition"
                  @click="navigateToTag(t)">
                  #{{ t.name }}
                </span>
              </div>
            </div>
            <div v-else class="space-y-2">
              <input v-model="editPictureTagInput" type="text" placeholder="输入主题词，多个用逗号分隔"
                class="w-full px-3 py-2 border border-gray-200 rounded-lg text-xs focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <div v-if="hotTags.length > 0" class="flex flex-wrap gap-1">
                <button v-for="t in hotTags.slice(0, 10)" :key="t.id" @click="toggleEditPictureTag(t.name)"
                  :class="['text-[10px] px-2 py-0.5 rounded-full transition',
                    editPictureTagInput.includes(t.name)
                      ? 'bg-purple-500 text-white'
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
                  #{{ t.name }}
                </button>
              </div>
            </div>
          </div>
          <div v-if="editingPictureFields" class="flex space-x-2 pt-2">
            <button @click="savePictureFields"
              class="flex-1 px-4 py-2 bg-blue-500 text-white rounded-lg text-sm font-medium hover:bg-blue-600 transition">保存修改</button>
            <button @click="cancelEditPictureFields"
              class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-200 transition">取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Batch Tag Modal -->
    <div v-if="showBatchTagModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showBatchTagModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">批量添加主题词</h3>
          <button @click="showBatchTagModal = false" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <p class="text-sm text-gray-500">已选择 {{ selectedPictureIds.length }} 张图片，将为它们添加以下主题词：</p>
          <input v-model="batchTagInput" type="text" placeholder="输入主题词，多个用逗号分隔（如：风景, 旅行）"
            class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500" />
          <div v-if="hotTags.length > 0" class="flex flex-wrap gap-1.5">
            <button v-for="t in hotTags.slice(0, 10)" :key="t.id" @click="toggleBatchTag(t.name)"
              :class="['text-xs px-2.5 py-1 rounded-full transition',
                batchTagInput.includes(t.name) ? 'bg-emerald-500 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']">
              #{{ t.name }}
            </button>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3">
          <button @click="showBatchTagModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="doBatchTag" :disabled="!batchTagInput.trim()"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              batchTagInput.trim() ? 'bg-emerald-500 text-white hover:bg-emerald-600' : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
            确认添加
          </button>
        </div>
      </div>
    </div>

    <!-- Batch Album Modal -->
    <div v-if="showBatchAlbumModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showBatchAlbumModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">批量归入专辑</h3>
          <button @click="showBatchAlbumModal = false" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <p class="text-sm text-gray-500">已选择 {{ selectedPictureIds.length }} 张图片，将添加到：</p>
          <select v-model="batchAlbumId" class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option :value="null">请选择专辑...</option>
            <option v-for="a in allAlbums" :key="a.id" :value="a.id">{{ a.name }} ({{ a.pictureCount }} 张)</option>
          </select>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3">
          <button @click="showBatchAlbumModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="doBatchAlbum" :disabled="!batchAlbumId"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              batchAlbumId ? 'bg-blue-500 text-white hover:bg-blue-600' : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
            确认添加
          </button>
        </div>
      </div>
    </div>

    <!-- Share Modal -->
    <div v-if="showShareModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="closeShareModal"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">分享设置</h3>
          <button @click="closeShareModal" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-5">
          <div class="flex items-center space-x-3 p-3 bg-gray-50 rounded-xl">
            <div class="w-12 h-12 rounded-lg overflow-hidden bg-gradient-to-br from-blue-100 to-purple-100 flex-shrink-0">
              <img v-if="shareTarget.coverUrl" :src="shareTarget.coverUrl" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-800 truncate">{{ shareTarget.title }}</p>
              <p class="text-xs text-gray-500">{{ shareTarget.type === 'picture' ? '图片分享' : '专辑分享' }}</p>
            </div>
          </div>

          <div v-if="!createdShare">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">有效期</label>
              <div class="grid grid-cols-4 gap-2">
                <button v-for="opt in expireOptions" :key="opt.value" @click="shareForm.expireDays = opt.value"
                  :class="['py-2 px-3 rounded-lg text-sm font-medium transition border',
                    shareForm.expireDays === opt.value
                      ? 'bg-blue-500 text-white border-blue-500'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300']">
                  {{ opt.label }}
                </button>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">访问密码 <span class="text-gray-400 text-xs">(可选)</span></label>
              <input v-model="shareForm.password" type="text" placeholder="不设置则无需密码即可访问"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <p class="text-xs text-gray-400 mt-1">设置密码后，访问者需要输入密码才能查看</p>
            </div>
          </div>

          <div v-else class="space-y-4">
            <div class="p-4 bg-green-50 border border-green-200 rounded-xl">
              <div class="flex items-center space-x-2 mb-2">
                <svg class="w-5 h-5 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span class="text-sm font-medium text-green-800">分享链接已生成</span>
              </div>
              <div class="flex items-center space-x-2">
                <input :value="getShareFullUrl(createdShare?.shareUrl)" readonly
                  class="flex-1 px-3 py-2 bg-white border border-green-200 rounded-lg text-sm text-gray-700 font-mono" />
                <button @click="copyCreatedShareLink"
                  class="px-4 py-2 bg-green-500 text-white rounded-lg text-sm font-medium hover:bg-green-600 transition">
                  复制
                </button>
              </div>
            </div>
            <div v-if="createdShare.hasPassword" class="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
              <p class="text-xs text-yellow-800">
                🔒 已设置访问密码：<span class="font-mono font-bold">{{ shareForm.password }}</span>
              </p>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3">
          <button v-if="!createdShare" @click="closeShareModal"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button v-if="!createdShare" @click="generateShare" :disabled="shareGenerating"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition shadow-md',
              shareGenerating ? 'bg-gray-400 cursor-not-allowed text-white' : 'bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg']">
            <span v-if="shareGenerating" class="flex items-center space-x-1.5">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>生成中...</span>
            </span>
            <span v-else>生成分享链接</span>
          </button>
          <button v-if="createdShare" @click="closeShareModal"
            class="px-5 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg font-medium text-sm hover:shadow-lg transition">完成</button>
        </div>
      </div>
    </div>

    <!-- Collaboration Management Modal -->
    <div v-if="showCollaborationModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showCollaborationModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[85vh] overflow-hidden relative flex flex-col">
        <div class="bg-gradient-to-r from-indigo-500 to-purple-600 p-6 text-white">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-xl font-bold">协作管理</h3>
              <p class="text-indigo-100 text-sm mt-1">{{ currentCollaborationAlbum?.name }}</p>
            </div>
            <button @click="showCollaborationModal = false" class="text-white/70 hover:text-white transition">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <div class="p-6 flex-1 overflow-y-auto">
          <div v-if="currentAlbumPermissions.canManageCollaborators" class="mb-6">
            <h4 class="text-sm font-semibold text-gray-700 mb-3">邀请协作者</h4>
            <div class="flex space-x-2">
              <input v-model="inviteUsername" type="text" placeholder="输入用户名邀请协作者"
                @keyup.enter="inviteCollaborator"
                class="flex-1 px-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
              <button @click="inviteCollaborator" :disabled="!inviteUsername.trim()"
                :class="['px-5 py-2 rounded-lg text-sm font-medium transition',
                  inviteUsername.trim()
                    ? 'bg-gradient-to-r from-indigo-500 to-purple-600 text-white hover:shadow-lg'
                    : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
                邀请
              </button>
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between mb-3">
              <h4 class="text-sm font-semibold text-gray-700">协作者列表</h4>
              <span class="text-xs text-gray-500">共 {{ collaborators.length }} 人</span>
            </div>

            <div v-if="collaborators.length === 0" class="text-center py-10 text-gray-400 text-sm">
              <div class="text-4xl mb-2">👥</div>
              暂无协作者
            </div>

            <div v-else class="space-y-3">
              <div v-for="collaborator in collaborators" :key="collaborator.userId"
                class="bg-white border border-gray-100 rounded-xl p-4 hover:shadow-md transition">
                <div class="flex items-center space-x-4">
                  <div class="w-12 h-12 bg-gradient-to-br from-blue-400 to-purple-500 rounded-full flex items-center justify-center text-white font-bold flex-shrink-0">
                    {{ (collaborator.nickname || collaborator.username || 'U').charAt(0).toUpperCase() }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center space-x-2">
                      <span class="font-medium text-gray-800 truncate">{{ collaborator.nickname || collaborator.username }}</span>
                      <span :class="['text-[10px] px-2 py-0.5 rounded-full', getRoleBadgeClass(collaborator.role)]">
                        {{ getRoleText(collaborator.role) }}
                      </span>
                    </div>
                    <p v-if="collaborator.username !== collaborator.nickname" class="text-xs text-gray-400">@{{ collaborator.username }}</p>
                    <div class="flex items-center space-x-4 mt-1 text-xs text-gray-500">
                      <span>加入时间：{{ formatTime(collaborator.joinedTime) }}</span>
                      <span v-if="collaborator.contributedPictures >= 0">
                        贡献图片：{{ collaborator.contributedPictures }} 张
                      </span>
                    </div>
                  </div>
                  <div class="flex space-x-2">
                    <button v-if="currentAlbumPermissions.canManageCollaborators && collaborator.role !== 'OWNER'"
                      @click="removeCollaborator(collaborator.userId)"
                      class="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition" title="移除协作者">
                      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                    <button v-if="collaborator.role === 'COLLABORATOR' && collaborator.userId === currentUser?.id"
                      @click="leaveCollaboration"
                      class="px-3 py-1.5 bg-gray-100 text-gray-600 text-xs rounded-lg hover:bg-gray-200 transition">
                      退出协作
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="px-6 py-4 border-t border-gray-100 bg-gray-50 flex justify-end space-x-3">
          <button @click="showCollaborationModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- Batch Watermark Modal -->
    <div v-if="showBatchWatermarkModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showBatchWatermarkModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md relative transform transition-all">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-lg font-bold text-gray-800">批量添加水印</h3>
          <button @click="showBatchWatermarkModal = false" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <p class="text-sm text-gray-500">已选择 {{ selectedPictureIds.length }} 张图片，将为它们添加水印：</p>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">选择水印模板</label>
            <select v-model="batchWatermarkTemplateId"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-violet-500">
              <option :value="null">使用默认配置</option>
              <optgroup label="系统模板">
                <option v-for="t in watermarkTemplates.filter(t => t.isSystem)" :key="t.id" :value="t.id">{{ t.name }}</option>
              </optgroup>
              <optgroup v-if="watermarkMyTemplates.length > 0" label="我的模板">
                <option v-for="t in watermarkMyTemplates" :key="t.id" :value="t.id">{{ t.name }}</option>
              </optgroup>
            </select>
          </div>
          <div v-if="batchWatermarkProgress.show" class="space-y-2">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">处理进度</span>
              <span class="font-medium text-violet-600">{{ batchWatermarkProgress.current }} / {{ batchWatermarkProgress.total }}</span>
            </div>
            <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
              <div class="h-full bg-gradient-to-r from-violet-500 to-purple-600 rounded-full transition-all duration-300"
                :style="{ width: (batchWatermarkProgress.total > 0 ? (batchWatermarkProgress.current / batchWatermarkProgress.total * 100) : 0) + '%' }"></div>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3">
          <button @click="showBatchWatermarkModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="doBatchWatermark" :disabled="batchWatermarkProgress.show"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              batchWatermarkProgress.show ? 'bg-gray-200 text-gray-400 cursor-not-allowed' : 'bg-gradient-to-r from-violet-500 to-purple-600 text-white hover:shadow-lg']">
            确认添加
          </button>
        </div>
      </div>
    </div>

    <!-- Watermark Template Modal -->
    <div v-if="showWatermarkTemplateModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showWatermarkTemplateModal = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg relative transform transition-all max-h-[85vh] overflow-y-auto">
        <div class="p-6 border-b border-gray-100 flex items-center justify-between sticky top-0 bg-white z-10">
          <h3 class="text-lg font-bold text-gray-800">{{ editingWatermarkTemplate ? '编辑模板' : '新建模板' }}</h3>
          <button @click="showWatermarkTemplateModal = false" class="text-gray-400 hover:text-gray-600 transition">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1.5">模板名称 <span class="text-red-500">*</span></label>
            <input v-model="watermarkTemplateForm.name" type="text" placeholder="输入模板名称"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">水印类型</label>
            <div class="grid grid-cols-2 gap-3">
              <button @click="watermarkTemplateForm.type = 'text'"
                :class="['p-2 rounded-lg border-2 text-center transition text-sm',
                  watermarkTemplateForm.type === 'text' ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-gray-200 text-gray-600']">
                文字水印
              </button>
              <button @click="watermarkTemplateForm.type = 'image'"
                :class="['p-2 rounded-lg border-2 text-center transition text-sm',
                  watermarkTemplateForm.type === 'image' ? 'border-blue-500 bg-blue-50 text-blue-700' : 'border-gray-200 text-gray-600']">
                图片水印
              </button>
            </div>
          </div>
          <div v-if="watermarkTemplateForm.type === 'text'">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">水印内容</label>
            <input v-model="watermarkTemplateForm.textContent" type="text" placeholder="输入水印文字"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div v-if="watermarkTemplateForm.type === 'image'">
            <label class="block text-sm font-medium text-gray-700 mb-1.5">水印图片URL</label>
            <input v-model="watermarkTemplateForm.imageUrl" type="text" placeholder="输入图片URL"
              class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div v-if="watermarkTemplateForm.type === 'text'">
              <label class="block text-sm font-medium text-gray-700 mb-1.5">字体大小</label>
              <input v-model.number="watermarkTemplateForm.fontSize" type="number" min="8" max="120"
                class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div v-if="watermarkTemplateForm.type === 'text'">
              <label class="block text-sm font-medium text-gray-700 mb-1.5">字体颜色</label>
              <div class="flex items-center space-x-2">
                <input type="color" v-model="watermarkTemplateForm.fontColor"
                  class="w-8 h-8 rounded border border-gray-200 cursor-pointer" />
                <input v-model="watermarkTemplateForm.fontColor" type="text"
                  class="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">透明度 <span class="text-blue-600 font-semibold">{{ Math.round(watermarkTemplateForm.opacity * 100) }}%</span></label>
            <input v-model.number="watermarkTemplateForm.opacity" type="range" min="0.05" max="1" step="0.05"
              class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">水印位置</label>
            <div class="grid grid-cols-3 gap-2">
              <button v-for="pos in watermarkPositions" :key="pos.value" @click="watermarkTemplateForm.position = pos.value"
                :class="['p-1.5 rounded-lg text-xs font-medium transition border',
                  watermarkTemplateForm.position === pos.value
                    ? 'bg-blue-500 text-white border-blue-500'
                    : 'bg-white text-gray-600 border-gray-200 hover:border-blue-300']">
                {{ pos.label }}
              </button>
            </div>
          </div>
          <label class="flex items-center space-x-2 cursor-pointer">
            <input type="checkbox" v-model="watermarkTemplateForm.smartMode" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500" />
            <span class="text-sm text-gray-700">智能模式</span>
          </label>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3 sticky bottom-0 bg-white">
          <button @click="showWatermarkTemplateModal = false"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium text-sm">取消</button>
          <button @click="saveWatermarkTemplate" :disabled="!watermarkTemplateForm.name.trim()"
            :class="['px-5 py-2 rounded-lg font-medium text-sm transition',
              watermarkTemplateForm.name.trim() ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:shadow-lg' : 'bg-gray-200 text-gray-400 cursor-not-allowed']">
            确认
          </button>
        </div>
      </div>
    </div>

    <!-- Generic Confirm Modal -->
    <div v-if="confirmModal.show" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="confirmModal.show = false"></div>
      <div class="bg-white rounded-2xl shadow-2xl max-w-sm w-full relative transform transition-all">
        <div class="p-6">
          <div class="flex items-start space-x-4">
            <div :class="['mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full',
                confirmModal.type === 'danger' ? 'bg-red-100' :
                confirmModal.type === 'warning' ? 'bg-yellow-100' : 'bg-blue-100']">
              <svg :class="['h-6 w-6',
                  confirmModal.type === 'danger' ? 'text-red-600' :
                  confirmModal.type === 'warning' ? 'text-yellow-600' : 'text-blue-600']"
                xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div class="mt-0 text-center flex-1">
              <h3 class="text-lg font-semibold text-gray-900">{{ confirmModal.title }}</h3>
              <p class="text-sm text-gray-500 mt-2">{{ confirmModal.message }}</p>
              <div v-if="confirmModal.options" class="mt-4 space-y-2 text-left">
                <label v-for="opt in confirmModal.options" :key="opt.value"
                  class="flex items-center space-x-2 p-2 rounded-lg cursor-pointer hover:bg-gray-50 transition"
                  :class="confirmModal.selectedOption === opt.value ? 'bg-blue-50 ring-1 ring-blue-300' : ''"
                  @click="confirmModal.selectedOption = opt.value">
                  <input type="radio" v-model="confirmModal.selectedOption" :value="opt.value" class="text-blue-600" />
                  <div>
                    <p class="text-sm font-medium text-gray-800">{{ opt.label }}</p>
                    <p class="text-xs text-gray-500">{{ opt.description }}</p>
                  </div>
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t border-gray-100 flex space-x-3">
          <button @click="confirmModal.show = false"
            class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium">取消</button>
          <button @click="confirmModal.onConfirm"
            :class="['flex-1 px-4 py-2 rounded-lg font-medium text-white transition shadow-md',
                confirmModal.type === 'danger' ? 'bg-red-500 hover:bg-red-600' :
                confirmModal.type === 'warning' ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-blue-500 hover:bg-blue-600']">
            确认
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <transition enter-active-class="transform ease-out duration-300 transition" enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0" leave-active-class="transition ease-in duration-100" leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="toast.show" class="fixed top-20 right-5 z-50 max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden">
        <div class="p-4">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <svg v-if="toast.type === 'success'" class="h-6 w-6 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg v-else class="h-6 w-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="ml-3 w-0 flex-1 pt-0.5">
              <p class="text-sm font-medium text-gray-900">{{ toast.message }}</p>
            </div>
            <div class="ml-4 flex-shrink-0 flex">
              <button @click="toast.show = false" class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none">
                <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Image Editor -->
    <ImageEditor
      v-model:visible="showImageEditor"
      :imageUrl="currentPicture.url"
      :imageId="currentPicture.id"
      :imageName="currentPicture.name"
      :api="api"
      @saved="onEditorSaved"
      @close="onEditorClosed"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, onUnmounted } from 'vue'
import axios from 'axios'
import ImageEditor from './components/ImageEditor.vue'

const api = axios.create({ baseURL: '/api', withCredentials: true })

const tabs = [
  { id: 'discover', name: '发现' },
  { id: 'favorites', name: '我的收藏' },
  { id: 'pictures', name: '图片墙' },
  { id: 'albums', name: '专辑' },
  { id: 'tags', name: '主题词' },
  { id: 'watermark', name: '水印' },
  { id: 'recycle', name: '回收站' },
  { id: 'stats', name: '统计' },
  { id: 'shares', name: '我的分享' }
]

const baseUrl = computed(() => {
  if (typeof window !== 'undefined') {
    return window.location.origin + window.location.pathname
  }
  return ''
})

const getShareFullUrl = (shareUrl) => {
  return baseUrl.value + (shareUrl || '')
}

const isSharePage = ref(false)
const currentShareToken = ref('')

const activeTab = ref('pictures')
const loading = ref(false)
const globalSearch = ref('')

// User / Auth
const currentUser = ref(null)
const isLoggedIn = computed(() => !!currentUser.value)
const showAuthModal = ref(false)
const authMode = ref('login')
const authForm = reactive({ username: '', password: '', nickname: '' })
const authLoading = ref(false)
const authError = ref('')

const openLoginModal = () => {
  authMode.value = 'login'
  authForm.username = ''; authForm.password = ''; authForm.nickname = ''
  authError.value = ''
  showAuthModal.value = true
}
const switchAuthMode = () => {
  authMode.value = authMode.value === 'login' ? 'register' : 'login'
  authError.value = ''
  authForm.password = ''
}
const doLogin = async () => {
  if (!authForm.username.trim() || !authForm.password.trim()) {
    authError.value = '请输入用户名和密码'
    return
  }
  authLoading.value = true
  authError.value = ''
  try {
    const res = await api.post('/auth/login', {
      username: authForm.username.trim(),
      password: authForm.password
    })
    if (res.data.success) {
      currentUser.value = res.data.data
      showAuthModal.value = false
      showToast('登录成功！')
      await fetchAll()
    } else {
      authError.value = res.data.message || '登录失败'
    }
  } catch (e) {
    authError.value = '网络错误，请重试'
  } finally {
    authLoading.value = false
  }
}
const doRegister = async () => {
  if (!authForm.username.trim()) { authError.value = '请输入用户名'; return }
  if (!authForm.password.trim()) { authError.value = '请输入密码'; return }
  if (authForm.username.trim().length < 3 || authForm.username.trim().length > 20) {
    authError.value = '用户名长度需在3-20个字符之间'; return
  }
  if (authForm.password.length < 6 || authForm.password.length > 20) {
    authError.value = '密码长度需在6-20个字符之间'; return
  }
  authLoading.value = true
  authError.value = ''
  try {
    const res = await api.post('/auth/register', {
      username: authForm.username.trim(),
      password: authForm.password,
      nickname: authForm.nickname.trim() || authForm.username.trim()
    })
    if (res.data.success) {
      showToast('注册成功！请登录')
      authMode.value = 'login'
      authForm.password = ''
    } else {
      authError.value = res.data.message || '注册失败'
    }
  } catch (e) {
    authError.value = '网络错误，请重试'
  } finally {
    authLoading.value = false
  }
}
const doLogout = async () => {
  try {
    await api.post('/auth/logout')
  } catch (e) {}
  currentUser.value = null
  pictures.value = []; allAlbums.value = []; allTags.value = []; globalStats.value = {}; discoverPictures.value = []; favoritePictures.value = []
  showToast('已退出登录')
}
const checkAuth = async () => {
  try {
    const res = await api.get('/auth/me')
    if (res.data.success) {
      currentUser.value = res.data.data
      return true
    }
  } catch (e) {}
  currentUser.value = null
  return false
}

// Axios interceptor for 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      currentUser.value = null
      openLoginModal()
    }
    return Promise.reject(error)
  }
)

// Global data
const pictures = ref([])
const recyclePictures = ref([])
const allAlbums = ref([])
const allTags = ref([])
const globalStats = ref({})
const myShares = ref([])
const discoverPictures = ref([])
const favoritePictures = ref([])
const pictureComments = ref([])
const newCommentContent = ref('')
const commentInput = ref(null)

// Collaboration & Notifications
const notifications = ref([])
const unreadCount = ref(0)
const showNotificationPanel = ref(false)
const notificationPanelRef = ref(null)
const albumCategory = ref('created')
const createdAlbums = ref([])
const collaborationAlbums = ref([])
const showCollaborationModal = ref(false)
const collaborators = ref([])
const inviteUsername = ref('')
const currentAlbumPermissions = ref({})
const currentCollaborationAlbum = ref(null)
let unreadCountTimer = null

// Share modal
const showShareModal = ref(false)
const shareTarget = ref({ type: '', id: null, title: '', coverUrl: '' })
const shareForm = reactive({
  expireDays: 0,
  password: ''
})
const createdShare = ref(null)
const shareGenerating = ref(false)

// Share page
const sharePageData = ref(null)
const sharePageLoading = ref(false)
const sharePasswordInput = ref('')
const sharePasswordError = ref('')
const sharePageError = ref('')

// Filter / select
const filterAlbumId = ref(null)
const filterTagId = ref(null)
const multiSelectMode = ref(false)
const selectedPictureIds = ref([])
const recycleMultiSelectMode = ref(false)
const selectedRecyclePictureIds = ref([])

// Watermark
const watermarkConfig = reactive({
  id: null, userId: null, type: 'text', textContent: '版权所有', fontFamily: 'SansSerif',
  fontSize: 20, fontColor: '#FFFFFF', opacity: 0.6, imageUrl: '', imageWidth: null, imageHeight: null,
  position: 'bottom_right', marginX: 20, marginY: 20, tileGap: 150, smartMode: true, enabled: false,
  createTime: null, updateTime: null
})
const watermarkTemplates = ref([])
const watermarkMyTemplates = ref([])
const showWatermarkTemplateModal = ref(false)
const editingWatermarkTemplate = ref(null)
const watermarkTemplateForm = reactive({
  name: '', type: 'text', textContent: '', fontFamily: 'SansSerif', fontSize: 20, fontColor: '#FFFFFF',
  opacity: 0.6, imageUrl: '', imageWidth: null, imageHeight: null, position: 'bottom_right',
  marginX: 20, marginY: 20, tileGap: 150, smartMode: true
})
const watermarkPreviewUrl = ref('')
const watermarkPreviewLoading = ref(false)
const showBatchWatermarkModal = ref(false)
const batchWatermarkTemplateId = ref(null)
const batchWatermarkProgress = reactive({ show: false, current: 0, total: 0 })
const uploadAddWatermark = ref(false)
const uploadWatermarkTemplateId = ref(null)

// Toast
const toast = reactive({ show: false, message: '', type: 'success' })
const showToast = (message, type = 'success') => {
  toast.show = true; toast.message = message; toast.type = type
  setTimeout(() => toast.show = false, 3000)
}

// Confirm modal
const confirmModal = reactive({
  show: false, title: '', message: '', type: 'danger',
  options: null, selectedOption: null, onConfirm: () => {}
})
const openConfirm = (config) => {
  Object.assign(confirmModal, { show: true, options: null, selectedOption: null, ...config })
}

// Utility formatters
const formatSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let i = 0, b = Number(bytes)
  while (b >= 1024 && i < units.length - 1) { b /= 1024; i++ }
  return b.toFixed(i > 0 ? 2 : 0) + ' ' + units[i]
}
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const handleUploadClick = () => {
  if (!isLoggedIn.value) {
    openLoginModal()
    return
  }
  fetchWatermarkTemplates()
  showUploadModal.value = true
}

// Upload
const showUploadModal = ref(false)
const uploadFile = ref(null)
const uploadPreview = ref('')
const uploadFileName = ref('')
const uploadAlbumIds = ref([])
const uploadTagInput = ref('')
const uploading = ref(false)
let reopenUploadAfterAlbum = false

const handleUploadFileSelect = (e) => {
  const f = e.target.files[0]
  if (!f) return
  uploadFile.value = f
  uploadFileName.value = f.name
  const reader = new FileReader()
  reader.onload = (ev) => uploadPreview.value = ev.target.result
  reader.readAsDataURL(f)
}
const handleFileDrop = (e) => {
  const f = e.dataTransfer.files[0]
  if (!f || !f.type.startsWith('image/')) return
  uploadFile.value = f
  uploadFileName.value = f.name
  const reader = new FileReader()
  reader.onload = (ev) => uploadPreview.value = ev.target.result
  reader.readAsDataURL(f)
}
const resetUploadForm = () => {
  uploadFile.value = null; uploadPreview.value = ''; uploadFileName.value = ''
  uploadAlbumIds.value = []; uploadTagInput.value = ''; uploading.value = false
  uploadAddWatermark.value = false; uploadWatermarkTemplateId.value = null
}
const toggleUploadTag = (name) => {
  const cur = uploadTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
  const idx = cur.indexOf(name)
  if (idx >= 0) cur.splice(idx, 1); else cur.push(name)
  uploadTagInput.value = cur.join(', ')
}
const doUpload = async () => {
  if (!uploadFile.value) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', uploadFile.value)
    uploadAlbumIds.value.forEach(id => fd.append('albumIds', id))
    const tagList = uploadTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
    tagList.forEach(t => fd.append('tags', t))
    if (uploadAddWatermark.value) {
      fd.append('addWatermark', 'true')
      if (uploadWatermarkTemplateId.value) {
        fd.append('templateId', uploadWatermarkTemplateId.value)
      }
    }
    const res = await api.post('/upload', fd)
    if (res.data.success) {
      showToast('上传成功！')
      showUploadModal.value = false
      resetUploadForm()
      await fetchAll()
    } else {
      showToast(res.data.message || '上传失败', 'error')
    }
  } catch (e) {
    console.error(e); showToast('上传失败', 'error')
  } finally {
    uploading.value = false
  }
}

// Album
const viewingAlbum = ref(false)
const currentAlbumDetail = ref({})
const albumPictures = ref([])
const showAlbumModal = ref(false)
const editingAlbum = ref(null)
const albumForm = reactive({ name: '', description: '', isPublic: true, displayOrder: 0 })
const openCreateAlbum = (reopenUpload = false) => {
  reopenUploadAfterAlbum = reopenUpload
  editingAlbum.value = null
  albumForm.name = ''; albumForm.description = ''; albumForm.isPublic = true; albumForm.displayOrder = 0
  showAlbumModal.value = true
}
const openEditAlbum = (album) => {
  editingAlbum.value = album
  albumForm.name = album.name || ''
  albumForm.description = album.description || ''
  albumForm.isPublic = album.isPublic ?? true
  albumForm.displayOrder = album.displayOrder ?? 0
  showAlbumModal.value = true
}
const closeAlbumModal = () => {
  showAlbumModal.value = false
  if (reopenUploadAfterAlbum) { showUploadModal.value = true; reopenUploadAfterAlbum = false }
}
const submitAlbumForm = async () => {
  try {
    let res
    if (editingAlbum.value) {
      res = await api.put(`/albums/${editingAlbum.value.id}`, { ...albumForm })
    } else {
      res = await api.post('/albums', { ...albumForm })
    }
    if (res.data.success) {
      showToast(editingAlbum.value ? '专辑已更新' : '专辑已创建')
      showAlbumModal.value = false
      if (reopenUploadAfterAlbum) { showUploadModal.value = true; reopenUploadAfterAlbum = false }
      await fetchAlbums()
    } else {
      showToast(res.data.message || '操作失败', 'error')
    }
  } catch (e) { showToast('网络错误', 'error') }
}
const openAlbumDetail = async (album) => {
  currentAlbumDetail.value = album
  viewingAlbum.value = true
  loading.value = true
  try {
    const res = await api.get(`/albums/${album.id}/pictures`)
    if (res.data.success) albumPictures.value = res.data.data || []
  } catch (e) {}
  finally { loading.value = false }
}
const getAlbumCover = (album) => {
  if (album.coverUrl) return album.coverUrl
  if (album.coverPictureId) {
    const pic = pictures.value.find(p => p.id === album.coverPictureId)
    return pic ? pic.url : ''
  }
  return ''
}
const confirmDeleteAlbum = (album) => {
  openConfirm({
    title: `删除专辑"${album.name}"?`,
    message: '请选择删除方式：',
    type: 'danger',
    options: [
      { value: 'move', label: '仅删除专辑', description: '图片将移至"默认专辑"' },
      { value: 'delete', label: '删除专辑及图片', description: '所有图片将被永久删除（不可恢复）' }
    ],
    selectedOption: 'move',
    onConfirm: async () => {
      try {
        const mode = confirmModal.selectedOption
        const res = await api.delete(`/albums/${album.id}?deletePictures=${mode === 'delete'}`)
        if (res.data.success) {
          showToast('专辑已删除')
          confirmModal.show = false
          await fetchAll()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}
const removeFromAlbum = (pic) => {
  openConfirm({
    title: `从"${currentAlbumDetail.value.name}"移除图片？`,
    message: '图片本身不会被删除，将从该专辑中移除。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/albums/${currentAlbumDetail.value.id}/pictures/${pic.id}`)
        if (res.data.success) {
          showToast('已从专辑移除')
          confirmModal.show = false
          await openAlbumDetail(currentAlbumDetail.value)
          await fetchAlbums()
        } else showToast(res.data.message || '操作失败', 'error')
      } catch (e) { showToast('操作失败', 'error') }
    }
  })
}
const navigateToAlbum = (id) => {
  const album = allAlbums.value.find(a => a.id === id)
  if (album) {
    showPictureDetail.value = false
    activeTab.value = 'albums'
    openAlbumDetail(album)
  }
}
const allAlbumsSorted = computed(() =>
  [...allAlbums.value].sort((a, b) => (b.pictureCount || 0) - (a.pictureCount || 0))
)
const albumPercentage = (album) => {
  const max = Math.max(...allAlbums.value.map(a => a.pictureCount || 0), 1)
  return Math.round((album.pictureCount || 0) / max * 100)
}

// Tags
const viewingTag = ref(false)
const currentTagDetail = ref({})
const tagPictures = ref([])
const tagSearch = ref('')
const displayedTags = computed(() => {
  const kw = tagSearch.value.trim().toLowerCase()
  if (!kw) return allTags.value
  return allTags.value.filter(t => t.name.toLowerCase().includes(kw))
})
const hotTags = computed(() =>
  [...allTags.value].sort((a, b) => (b.referenceCount || 0) - (a.referenceCount || 0)).slice(0, 20)
)
const topTenTags = computed(() =>
  [...allTags.value].sort((a, b) => (b.referenceCount || 0) - (a.referenceCount || 0)).slice(0, 10)
)
const topRefCount = computed(() => {
  const t = topTenTags.value[0]
  return t ? (t.referenceCount || 1) : 1
})
const tagCloudStyle = (tag) => {
  const base = 12
  const max = 32
  const ratio = Math.min((tag.referenceCount || 0) / Math.max(topRefCount.value, 1), 1)
  const size = base + ratio * (max - base)
  const colors = ['#2563EB', '#9333EA', '#DB2777', '#059669', '#D97706', '#E11D48', '#4F46E5', '#0D9488']
  const bgColors = ['#DBEAFE', '#F3E8FF', '#FCE7F3', '#D1FAE5', '#FEF3C7', '#FFE4E6', '#E0E7FF', '#CCFBF1']
  const idx = (tag.id || 0) % colors.length
  return {
    fontSize: size + 'px',
    fontWeight: 500 + Math.round(ratio * 3) * 100,
    color: colors[idx],
    backgroundColor: bgColors[idx]
  }
}
const tagRankColor = (count) => {
  if (count >= 10) return 'bg-gradient-to-br from-pink-500 to-rose-600 text-white'
  if (count >= 5) return 'bg-gradient-to-br from-purple-500 to-indigo-600 text-white'
  if (count >= 3) return 'bg-gradient-to-br from-blue-500 to-cyan-600 text-white'
  if (count >= 1) return 'bg-gradient-to-br from-emerald-500 to-teal-600 text-white'
  return 'bg-gray-200 text-gray-500'
}
const searchTags = () => {}
const openTagDetail = async (tag) => {
  currentTagDetail.value = tag
  viewingTag.value = true
  loading.value = true
  try {
    const res = await api.get(`/tags/${tag.id}/pictures`)
    if (res.data.success) tagPictures.value = res.data.data || []
  } catch (e) {} finally { loading.value = false }
}
const navigateToTag = (tag) => {
  showPictureDetail.value = false
  activeTab.value = 'tags'
  openTagDetail(tag)
}
const confirmDeleteTag = (tag) => {
  openConfirm({
    title: `删除主题词"#${tag.name}"?`,
    message: '该主题词未被使用，可以安全删除。',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/tags/${tag.id}`)
        if (res.data.success) {
          showToast('主题词已删除')
          confirmModal.show = false
          await fetchTags()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

// Share
const expireOptions = [
  { value: 0, label: '永久' },
  { value: 7, label: '7天' },
  { value: 3, label: '3天' },
  { value: 1, label: '1天' }
]

const openShareModal = (type, target) => {
  shareTarget.value = {
    type: type,
    id: target.id,
    title: target.name,
    coverUrl: target.url || target.coverUrl
  }
  shareForm.expireDays = 0
  shareForm.password = ''
  createdShare.value = null
  showShareModal.value = true
}

const closeShareModal = () => {
  showShareModal.value = false
  shareTarget.value = { type: '', id: null, title: '', coverUrl: '' }
  shareForm.expireDays = 0
  shareForm.password = ''
  createdShare.value = null
}

const generateShare = async () => {
  shareGenerating.value = true
  try {
    const res = await api.post('/shares', {
      shareType: shareTarget.value.type,
      pictureId: shareTarget.value.type === 'picture' ? shareTarget.value.id : null,
      albumId: shareTarget.value.type === 'album' ? shareTarget.value.id : null,
      password: shareForm.password.trim() || null,
      expireDays: shareForm.expireDays
    })
    if (res.data.success) {
      createdShare.value = res.data.data
      showToast('分享链接已生成')
      await fetchShares()
    } else {
      showToast(res.data.message || '生成失败', 'error')
    }
  } catch (e) {
    showToast('生成失败', 'error')
  } finally {
    shareGenerating.value = false
  }
}

const copyCreatedShareLink = async () => {
  if (!createdShare.value) return
  const url = getShareFullUrl(createdShare.value.shareUrl)
  try {
    await navigator.clipboard.writeText(url)
    showToast('链接已复制到剪贴板')
  } catch (e) {
    showToast('复制失败，请手动复制', 'error')
  }
}

const copyShareLink = async (share) => {
  const url = getShareFullUrl(share.shareUrl)
  try {
    await navigator.clipboard.writeText(url)
    showToast('链接已复制到剪贴板')
  } catch (e) {
    showToast('复制失败，请手动复制', 'error')
  }
}

const confirmDeleteShare = (share) => {
  openConfirm({
    title: `删除分享"${share.title}"?`,
    message: '删除后分享链接将立即失效，不可恢复。',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/shares/${share.id}`)
        if (res.data.success) {
          showToast('分享已删除')
          confirmModal.show = false
          await fetchShares()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

const fetchShares = async () => {
  try {
    const res = await api.get('/shares')
    if (res.data.success) {
      myShares.value = res.data.data || []
    }
  } catch (e) {}
}

const goHome = () => {
  if (typeof window !== 'undefined') {
    window.location.hash = ''
  }
  isSharePage.value = false
  sharePageData.value = null
  sharePageError.value = ''
  sharePasswordInput.value = ''
  sharePasswordError.value = ''
}

const submitSharePassword = async () => {
  if (!sharePasswordInput.value.trim()) {
    sharePasswordError.value = '请输入密码'
    return
  }
  sharePasswordError.value = ''
  sharePageLoading.value = true
  try {
    const res = await api.post(`/shares/${currentShareToken.value}/access`, {
      password: sharePasswordInput.value
    })
    if (res.data.success) {
      sharePageData.value = res.data.data
    } else {
      sharePasswordError.value = res.data.message || '密码错误'
    }
  } catch (e) {
    sharePasswordError.value = '验证失败，请重试'
  } finally {
    sharePageLoading.value = false
  }
}

const loadSharePage = async (token) => {
  sharePageLoading.value = true
  sharePageError.value = ''
  sharePageData.value = null
  sharePasswordInput.value = ''
  sharePasswordError.value = ''
  try {
    const res = await api.get(`/shares/${token}`)
    if (res.data.success) {
      const info = res.data.data
      if (info.expired) {
        sharePageError.value = '分享已过期'
      } else if (info.hasPassword) {
        sharePageData.value = info
      } else {
        const accessRes = await api.post(`/shares/${token}/access`)
        if (accessRes.data.success) {
          sharePageData.value = accessRes.data.data
        } else {
          sharePageError.value = accessRes.data.message || '无法访问该分享'
        }
      }
    } else {
      sharePageError.value = res.data.message || '分享链接不存在'
    }
  } catch (e) {
    sharePageError.value = '加载失败，请稍后重试'
  } finally {
    sharePageLoading.value = false
  }
}

const parseHashRoute = () => {
  if (typeof window === 'undefined') return false
  const hash = window.location.hash
  if (hash.startsWith('#/share/')) {
    const token = hash.replace('#/share/', '')
    if (token) {
      isSharePage.value = true
      currentShareToken.value = token
      loadSharePage(token)
      return true
    }
  } else {
    isSharePage.value = false
    sharePageData.value = null
    sharePageError.value = ''
  }
  return false
}

if (typeof window !== 'undefined') {
  window.addEventListener('hashchange', parseHashRoute)
}

// Picture Detail
const showPictureDetail = ref(false)
const currentPicture = ref({})
const editingPictureFields = ref(false)
const editPictureAlbumIds = ref([])
const editPictureTagInput = ref('')

// Image Editor
const showImageEditor = ref(false)
const openImageEditor = () => {
  showPictureDetail.value = false
  showImageEditor.value = true
}
const onEditorSaved = async (result) => {
  showImageEditor.value = false
  if (result?.newPicture) {
    showToast('已另存为成功', 'success')
  } else {
    showToast('图片编辑成功', 'success')
    // 更新当前图片信息
    if (result?.data) {
      currentPicture.value = result.data
    }
  }
  await fetchAll()
  // 重新打开详情
  setTimeout(() => {
    if (currentPicture.value.id && result?.data?.id) {
      viewPicture(result.data)
    }
  }, 300)
}
const onEditorClosed = () => {
  showImageEditor.value = false
  // 重新打开详情页
  if (currentPicture.value.id) {
    showPictureDetail.value = true
  }
}

const fetchDiscoverPictures = async () => {
  loading.value = true
  try {
    const res = await api.get('/discover')
    if (res.data.success) discoverPictures.value = res.data.data || []
  } catch (e) {} finally { loading.value = false }
}

const fetchMyFavorites = async () => {
  loading.value = true
  try {
    const res = await api.get('/discover/favorites')
    if (res.data.success) favoritePictures.value = res.data.data || []
  } catch (e) {} finally { loading.value = false }
}

const fetchPictureComments = async (pictureId) => {
  try {
    const res = await api.get(`/interactions/pictures/${pictureId}/comments`)
    if (res.data.success) pictureComments.value = res.data.data || []
  } catch (e) {}
}

const viewDiscoverPicture = async (pic) => {
  loading.value = true
  try {
    const res = await api.get(`/pictures/${pic.id}`)
    if (res.data.success) {
      currentPicture.value = res.data.data
      showPictureDetail.value = true
      await fetchPictureComments(pic.id)
    }
  } catch (e) { showToast('无法查看该图片', 'error') } finally { loading.value = false }
}

const toggleLike = async () => {
  try {
    const res = await api.post(`/interactions/pictures/${currentPicture.value.id}/like`)
    if (res.data.success) {
      const data = res.data.data
      currentPicture.value.isLiked = data.liked
      currentPicture.value.likeCount = data.likeCount
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const toggleFavorite = async () => {
  try {
    const res = await api.post(`/interactions/pictures/${currentPicture.value.id}/favorite`)
    if (res.data.success) {
      const data = res.data.data
      currentPicture.value.isFavorited = data.favorited
      currentPicture.value.favoriteCount = data.favoriteCount
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const togglePicturePublic = async () => {
  try {
    const newPublic = !currentPicture.value.isPublic
    const res = await api.put(`/pictures/${currentPicture.value.id}`, { isPublic: newPublic })
    if (res.data.success) {
      currentPicture.value = res.data.data
      showToast(newPublic ? '已设为公开' : '已设为私密')
      await fetchAll()
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const focusCommentInput = () => {
  if (commentInput.value) commentInput.value.focus()
}

const submitComment = async () => {
  if (!newCommentContent.value.trim()) return
  try {
    const res = await api.post(`/interactions/pictures/${currentPicture.value.id}/comments`, {
      content: newCommentContent.value.trim()
    })
    if (res.data.success) {
      newCommentContent.value = ''
      await fetchPictureComments(currentPicture.value.id)
      currentPicture.value.commentCount = pictureComments.value.length
      showToast('评论成功')
    } else {
      showToast(res.data.message || '评论失败', 'error')
    }
  } catch (e) { showToast('评论失败', 'error') }
}

const deleteComment = async (commentId) => {
  try {
    const res = await api.delete(`/interactions/comments/${commentId}`)
    if (res.data.success) {
      await fetchPictureComments(currentPicture.value.id)
      currentPicture.value.commentCount = pictureComments.value.length
      showToast('评论已删除')
    } else {
      showToast(res.data.message || '删除失败', 'error')
    }
  } catch (e) { showToast('删除失败', 'error') }
}

const viewPicture = async (pic) => {
  loading.value = true
  try {
    const res = await api.get(`/pictures/${pic.id}`)
    if (res.data.success) {
      currentPicture.value = res.data.data
      showPictureDetail.value = true
      await fetchPictureComments(pic.id)
    }
  } catch (e) {} finally { loading.value = false }
}
const closePictureDetail = () => {
  showPictureDetail.value = false
  editingPictureFields.value = false
  pictureComments.value = []
  newCommentContent.value = ''
}
const startEditPictureFields = () => {
  editingPictureFields.value = true
  editPictureAlbumIds.value = (currentPicture.value.albums || []).map(a => a.id)
  editPictureTagInput.value = (currentPicture.value.tags || []).map(t => t.name).join(', ')
}
const cancelEditPictureFields = () => {
  editingPictureFields.value = false
  editPictureAlbumIds.value = []
  editPictureTagInput.value = ''
}
const toggleEditPictureTag = (name) => {
  const cur = editPictureTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
  const idx = cur.indexOf(name)
  if (idx >= 0) cur.splice(idx, 1); else cur.push(name)
  editPictureTagInput.value = cur.join(', ')
}
const savePictureFields = async () => {
  try {
    const tagNames = editPictureTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
    const res = await api.put(`/pictures/${currentPicture.value.id}`, {
      albumIds: editPictureAlbumIds.value,
      tagNames: tagNames
    })
    if (res.data.success) {
      showToast('图片信息已更新')
      editingPictureFields.value = false
      currentPicture.value = res.data.data
      await fetchAll()
    } else showToast(res.data.message || '保存失败', 'error')
  } catch (e) { showToast('保存失败', 'error') }
}
const confirmDeletePicture = (pic) => {
  openConfirm({
    title: `删除图片"${pic.name}"?`,
    message: '图片将移入回收站，30天内可恢复。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/delete/${pic.id}`)
        if (res.data.success) {
          showToast('图片已移入回收站')
          confirmModal.show = false
          await fetchAll()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

// Batch operations
const showBatchTagModal = ref(false)
const batchTagInput = ref('')
const toggleBatchTag = (name) => {
  const cur = batchTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
  const idx = cur.indexOf(name)
  if (idx >= 0) cur.splice(idx, 1); else cur.push(name)
  batchTagInput.value = cur.join(', ')
}
const doBatchTag = async () => {
  if (!batchTagInput.value.trim() || selectedPictureIds.value.length === 0) return
  try {
    const tagNames = batchTagInput.value.split(/[,，]/).map(s => s.trim()).filter(Boolean)
    const res = await api.post('/pictures/batch-tags', {
      pictureIds: selectedPictureIds.value,
      tagNames: tagNames
    })
    if (res.data.success) {
      showToast(`已为 ${selectedPictureIds.value.length} 张图片添加主题词`)
      showBatchTagModal.value = false
      batchTagInput.value = ''
      selectedPictureIds.value = []
      await fetchAll()
    } else showToast(res.data.message || '操作失败', 'error')
  } catch (e) { showToast('操作失败', 'error') }
}

const showBatchAlbumModal = ref(false)
const batchAlbumId = ref(null)
const doBatchAlbum = async () => {
  if (!batchAlbumId.value || selectedPictureIds.value.length === 0) return
  try {
    const res = await api.post('/pictures/batch-album', {
      pictureIds: selectedPictureIds.value,
      albumId: batchAlbumId.value
    })
    if (res.data.success) {
      showToast(`已为 ${selectedPictureIds.value.length} 张图片添加到专辑`)
      showBatchAlbumModal.value = false
      batchAlbumId.value = null
      selectedPictureIds.value = []
      await fetchAll()
    } else showToast(res.data.message || '操作失败', 'error')
  } catch (e) { showToast('操作失败', 'error') }
}

const confirmBatchDelete = () => {
  openConfirm({
    title: `批量删除 ${selectedPictureIds.value.length} 张图片？`,
    message: '图片将移入回收站，30天内可恢复。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post('/pictures/batch-delete', { pictureIds: selectedPictureIds.value })
        if (res.data.success) {
          showToast(`已将 ${selectedPictureIds.value.length} 张图片移入回收站`)
          confirmModal.show = false
          selectedPictureIds.value = []
          multiSelectMode.value = false
          await fetchAll()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

// Recycle bin operations
const confirmRestorePicture = (pic) => {
  openConfirm({
    title: `恢复图片"${pic.name}"?`,
    message: '图片将恢复到原来的专辑，主题词关系也会恢复。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post(`/recycle/restore/${pic.id}`)
        if (res.data.success) {
          showToast('图片已恢复')
          confirmModal.show = false
          await fetchAll()
        } else showToast(res.data.message || '恢复失败', 'error')
      } catch (e) { showToast('恢复失败', 'error') }
    }
  })
}

const confirmPermanentDelete = (pic) => {
  openConfirm({
    title: `永久删除图片"${pic.name}"?`,
    message: '此操作将永久删除图片，无法恢复。请谨慎操作！',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/recycle/${pic.id}`)
        if (res.data.success) {
          showToast('图片已永久删除')
          confirmModal.show = false
          await fetchRecycleBin()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

const confirmBatchRestore = () => {
  openConfirm({
    title: `批量恢复 ${selectedRecyclePictureIds.value.length} 张图片？`,
    message: '选中的图片将恢复到原来的专辑。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post('/recycle/batch-restore', { pictureIds: selectedRecyclePictureIds.value })
        if (res.data.success) {
          showToast(`已恢复 ${selectedRecyclePictureIds.value.length} 张图片`)
          confirmModal.show = false
          selectedRecyclePictureIds.value = []
          recycleMultiSelectMode.value = false
          await fetchAll()
        } else showToast(res.data.message || '恢复失败', 'error')
      } catch (e) { showToast('恢复失败', 'error') }
    }
  })
}

const confirmBatchPermanentDelete = () => {
  openConfirm({
    title: `批量永久删除 ${selectedRecyclePictureIds.value.length} 张图片？`,
    message: '此操作将永久删除选中的图片，无法恢复。请谨慎操作！',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.post('/recycle/batch-delete', { pictureIds: selectedRecyclePictureIds.value })
        if (res.data.success) {
          showToast(`已永久删除 ${selectedRecyclePictureIds.value.length} 张图片`)
          confirmModal.show = false
          selectedRecyclePictureIds.value = []
          recycleMultiSelectMode.value = false
          await fetchRecycleBin()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

const confirmClearRecycleBin = () => {
  openConfirm({
    title: '清空回收站?',
    message: '此操作将永久删除回收站中的所有图片，无法恢复。请谨慎操作！',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.delete('/recycle/clear')
        if (res.data.success) {
          showToast('回收站已清空')
          confirmModal.show = false
          selectedRecyclePictureIds.value = []
          recycleMultiSelectMode.value = false
          await fetchRecycleBin()
        } else showToast(res.data.message || '操作失败', 'error')
      } catch (e) { showToast('操作失败', 'error') }
    }
  })
}

// Global search
const doGlobalSearch = async () => {
  const kw = globalSearch.value.trim()
  if (!kw) { filterAlbumId.value = null; filterTagId.value = null; activeTab.value = 'pictures'; fetchPictures(); return }
  activeTab.value = 'pictures'
  filterAlbumId.value = null; filterTagId.value = null
  loading.value = true
  try {
    const res = await api.get(`/list?keyword=${encodeURIComponent(kw)}`)
    if (res.data.success) pictures.value = res.data.data || []
  } catch (e) {} finally { loading.value = false }
}

// Fetchers
const fetchPictures = async () => {
  loading.value = true
  try {
    let url = '/list'
    const q = []
    if (filterAlbumId.value) q.push(`albumId=${filterAlbumId.value}`)
    if (filterTagId.value) q.push(`tagId=${filterTagId.value}`)
    if (q.length > 0) url += '?' + q.join('&')
    const res = await api.get(url)
    if (res.data.success) pictures.value = res.data.data || []
  } catch (e) { console.error(e); showToast('加载失败', 'error') }
  finally { loading.value = false }
}
const fetchAlbums = async () => {
  try {
    const res = await api.get('/albums')
    if (res.data.success) allAlbums.value = res.data.data || []
  } catch (e) {}
}
const fetchTags = async () => {
  try {
    const res = await api.get('/tags')
    if (res.data.success) allTags.value = res.data.data || []
  } catch (e) {}
}
const fetchStats = async () => {
  try {
    const res = await api.get('/stats')
    if (res.data.success) globalStats.value = res.data.data || {}
  } catch (e) {}
}
const fetchRecycleBin = async () => {
  try {
    const res = await api.get('/recycle')
    if (res.data.success) recyclePictures.value = res.data.data || []
  } catch (e) {}
}

// Collaboration & Notifications methods
const fetchNotifications = async () => {
  try {
    const res = await api.get('/notifications')
    if (res.data.success) notifications.value = res.data.data || []
  } catch (e) {}
}

const fetchUnreadCount = async () => {
  try {
    const res = await api.get('/notifications/unread/count')
    if (res.data.success) unreadCount.value = res.data.data || 0
  } catch (e) {}
}

const startUnreadCountTimer = () => {
  if (unreadCountTimer) clearInterval(unreadCountTimer)
  unreadCountTimer = setInterval(() => {
    if (isLoggedIn.value) fetchUnreadCount()
  }, 30000)
}

const stopUnreadCountTimer = () => {
  if (unreadCountTimer) {
    clearInterval(unreadCountTimer)
    unreadCountTimer = null
  }
}

const markNotificationRead = async (id) => {
  try {
    await api.post(`/notifications/${id}/read`)
    const n = notifications.value.find(x => x.id === id)
    if (n) n.read = true
    if (unreadCount.value > 0) unreadCount.value--
  } catch (e) {}
}

const markAllNotificationsRead = async () => {
  try {
    await api.post('/notifications/read-all')
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
    showToast('已全部标记为已读')
  } catch (e) { showToast('操作失败', 'error') }
}

const acceptInvite = async (inviteId, notificationId) => {
  try {
    const res = await api.post(`/collaboration/invites/${inviteId}/accept`)
    if (res.data.success) {
      showToast('已接受邀请，您已成为协作者')
      if (notificationId) await markNotificationRead(notificationId)
      await fetchNotifications()
      await fetchUnreadCount()
      await fetchAlbumsByCategory()
      await fetchAlbums()
    } else {
      showToast(res.data.message || '操作失败', 'error')
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const rejectInvite = async (inviteId, notificationId) => {
  try {
    const res = await api.post(`/collaboration/invites/${inviteId}/reject`)
    if (res.data.success) {
      showToast('已拒绝邀请')
      if (notificationId) await markNotificationRead(notificationId)
      await fetchNotifications()
      await fetchUnreadCount()
    } else {
      showToast(res.data.message || '操作失败', 'error')
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const fetchAlbumsByCategory = async () => {
  try {
    const res = await api.get('/albums/category')
    if (res.data.success) {
      createdAlbums.value = res.data.data.created || []
      collaborationAlbums.value = res.data.data.collaboration || []
    }
  } catch (e) {}
}

const fetchCollaborators = async (albumId) => {
  try {
    const res = await api.get(`/collaboration/albums/${albumId}/collaborators`)
    if (res.data.success) collaborators.value = res.data.data || []
  } catch (e) {}
}

const fetchAlbumPermissions = async (albumId) => {
  try {
    const res = await api.get(`/collaboration/albums/${albumId}/permissions`)
    if (res.data.success) currentAlbumPermissions.value = res.data.data || {}
  } catch (e) {}
}

const openCollaborationManagement = async (album) => {
  currentCollaborationAlbum.value = album
  inviteUsername.value = ''
  currentAlbumPermissions.value = {
    canEdit: album.userRole === 'OWNER' || album.userRole === 'COLLABORATOR',
    canDelete: album.userRole === 'OWNER',
    canManageCollaborators: album.userRole === 'OWNER'
  }
  showCollaborationModal.value = true
  await fetchCollaborators(album.id)
  await fetchAlbumPermissions(album.id)
}

const inviteCollaborator = async () => {
  if (!inviteUsername.value.trim() || !currentCollaborationAlbum.value) return
  try {
    const res = await api.post(`/collaboration/albums/${currentCollaborationAlbum.value.id}/invite`, {
      username: inviteUsername.value.trim()
    })
    if (res.data.success) {
      showToast('邀请已发送')
      inviteUsername.value = ''
      await fetchCollaborators(currentCollaborationAlbum.value.id)
    } else {
      showToast(res.data.message || '邀请失败', 'error')
    }
  } catch (e) { showToast('邀请失败', 'error') }
}

const removeCollaborator = async (userId) => {
  if (!currentCollaborationAlbum.value) return
  openConfirm({
    title: '移除协作者？',
    message: '该用户将不再能编辑此专辑。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/collaboration/albums/${currentCollaborationAlbum.value.id}/collaborators/${userId}`)
        if (res.data.success) {
          showToast('已移除协作者')
          confirmModal.show = false
          await fetchCollaborators(currentCollaborationAlbum.value.id)
        } else {
          showToast(res.data.message || '操作失败', 'error')
        }
      } catch (e) { showToast('操作失败', 'error') }
    }
  })
}

const leaveCollaboration = async () => {
  if (!currentCollaborationAlbum.value) return
  openConfirm({
    title: '退出协作？',
    message: '退出后您将不再能编辑此专辑。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post(`/collaboration/albums/${currentCollaborationAlbum.value.id}/leave`)
        if (res.data.success) {
          showToast('已退出协作')
          confirmModal.show = false
          showCollaborationModal.value = false
          viewingAlbum.value = false
          await fetchAlbumsByCategory()
          await fetchAlbums()
        } else {
          showToast(res.data.message || '操作失败', 'error')
        }
      } catch (e) { showToast('操作失败', 'error') }
    }
  })
}

const toggleNotificationPanel = async () => {
  showNotificationPanel.value = !showNotificationPanel.value
  if (showNotificationPanel.value) {
    await fetchNotifications()
  }
}

const handleNotificationClick = (notification) => {
  if (!notification.isRead) {
    markNotificationRead(notification.id)
  }
}

const getRoleText = (role) => {
  if (role === 'OWNER') return '所有者'
  if (role === 'COLLABORATOR') return '协作者'
  return role || '成员'
}

const getRoleBadgeClass = (role) => {
  if (role === 'OWNER') return 'bg-gradient-to-r from-amber-500 to-orange-600 text-white'
  if (role === 'COLLABORATOR') return 'bg-gradient-to-r from-blue-500 to-purple-600 text-white'
  return 'bg-gray-100 text-gray-600'
}

const displayedAlbums = computed(() => {
  return albumCategory.value === 'created' ? createdAlbums.value : collaborationAlbums.value
})

const watermarkPositions = [
  { value: 'top_left', label: '左上角' },
  { value: 'top_right', label: '右上角' },
  { value: 'bottom_left', label: '左下角' },
  { value: 'bottom_right', label: '右下角' },
  { value: 'center', label: '居中' },
  { value: 'tile', label: '平铺' }
]

const watermarkPositionLabel = (pos) => {
  const found = watermarkPositions.find(p => p.value === pos)
  return found ? found.label : pos || ''
}

const fetchWatermarkConfig = async () => {
  try {
    const res = await api.get('/watermark/config')
    if (res.data.success && res.data.data) {
      const data = res.data.data
      Object.keys(watermarkConfig).forEach(key => {
        if (data[key] !== undefined && data[key] !== null) {
          watermarkConfig[key] = data[key]
        }
      })
    }
  } catch (e) {}
}

const saveWatermarkConfig = async () => {
  try {
    const res = await api.post('/watermark/config', { ...watermarkConfig })
    if (res.data.success) {
      showToast('水印配置已保存')
      const data = res.data.data
      Object.keys(watermarkConfig).forEach(key => {
        if (data[key] !== undefined && data[key] !== null) {
          watermarkConfig[key] = data[key]
        }
      })
    } else {
      showToast(res.data.message || '保存失败', 'error')
    }
  } catch (e) { showToast('保存失败', 'error') }
}

const fetchWatermarkTemplates = async () => {
  try {
    const [allRes, myRes] = await Promise.all([
      api.get('/watermark/templates'),
      api.get('/watermark/templates/mine')
    ])
    if (allRes.data.success) watermarkTemplates.value = allRes.data.data || []
    if (myRes.data.success) watermarkMyTemplates.value = myRes.data.data || []
  } catch (e) {}
}

const applyWatermarkTemplate = (template) => {
  watermarkConfig.type = template.type || 'text'
  watermarkConfig.textContent = template.textContent || ''
  watermarkConfig.fontFamily = template.fontFamily || 'SansSerif'
  watermarkConfig.fontSize = template.fontSize || 20
  watermarkConfig.fontColor = template.fontColor || '#FFFFFF'
  watermarkConfig.opacity = template.opacity || 0.6
  watermarkConfig.imageUrl = template.imageUrl || ''
  watermarkConfig.imageWidth = template.imageWidth
  watermarkConfig.imageHeight = template.imageHeight
  watermarkConfig.position = template.position || 'bottom_right'
  watermarkConfig.marginX = template.marginX || 20
  watermarkConfig.marginY = template.marginY || 20
  watermarkConfig.tileGap = template.tileGap || 150
  watermarkConfig.smartMode = template.smartMode !== false
  showToast(`已应用模板「${template.name}」`)
}

const openCreateWatermarkTemplate = () => {
  editingWatermarkTemplate.value = null
  watermarkTemplateForm.name = ''
  watermarkTemplateForm.type = watermarkConfig.type || 'text'
  watermarkTemplateForm.textContent = watermarkConfig.textContent || ''
  watermarkTemplateForm.fontFamily = watermarkConfig.fontFamily || 'SansSerif'
  watermarkTemplateForm.fontSize = watermarkConfig.fontSize || 20
  watermarkTemplateForm.fontColor = watermarkConfig.fontColor || '#FFFFFF'
  watermarkTemplateForm.opacity = watermarkConfig.opacity || 0.6
  watermarkTemplateForm.imageUrl = watermarkConfig.imageUrl || ''
  watermarkTemplateForm.imageWidth = watermarkConfig.imageWidth
  watermarkTemplateForm.imageHeight = watermarkConfig.imageHeight
  watermarkTemplateForm.position = watermarkConfig.position || 'bottom_right'
  watermarkTemplateForm.marginX = watermarkConfig.marginX || 20
  watermarkTemplateForm.marginY = watermarkConfig.marginY || 20
  watermarkTemplateForm.tileGap = watermarkConfig.tileGap || 150
  watermarkTemplateForm.smartMode = watermarkConfig.smartMode !== false
  showWatermarkTemplateModal.value = true
}

const openEditWatermarkTemplate = (template) => {
  editingWatermarkTemplate.value = template
  watermarkTemplateForm.name = template.name || ''
  watermarkTemplateForm.type = template.type || 'text'
  watermarkTemplateForm.textContent = template.textContent || ''
  watermarkTemplateForm.fontFamily = template.fontFamily || 'SansSerif'
  watermarkTemplateForm.fontSize = template.fontSize || 20
  watermarkTemplateForm.fontColor = template.fontColor || '#FFFFFF'
  watermarkTemplateForm.opacity = template.opacity || 0.6
  watermarkTemplateForm.imageUrl = template.imageUrl || ''
  watermarkTemplateForm.imageWidth = template.imageWidth
  watermarkTemplateForm.imageHeight = template.imageHeight
  watermarkTemplateForm.position = template.position || 'bottom_right'
  watermarkTemplateForm.marginX = template.marginX || 20
  watermarkTemplateForm.marginY = template.marginY || 20
  watermarkTemplateForm.tileGap = template.tileGap || 150
  watermarkTemplateForm.smartMode = template.smartMode !== false
  showWatermarkTemplateModal.value = true
}

const saveWatermarkTemplate = async () => {
  if (!watermarkTemplateForm.name.trim()) return
  try {
    let res
    if (editingWatermarkTemplate.value) {
      res = await api.put(`/watermark/templates/${editingWatermarkTemplate.value.id}`, { ...watermarkTemplateForm })
    } else {
      res = await api.post('/watermark/templates', { ...watermarkTemplateForm })
    }
    if (res.data.success) {
      showToast(editingWatermarkTemplate.value ? '模板已更新' : '模板已创建')
      showWatermarkTemplateModal.value = false
      await fetchWatermarkTemplates()
    } else {
      showToast(res.data.message || '操作失败', 'error')
    }
  } catch (e) { showToast('操作失败', 'error') }
}

const confirmDeleteWatermarkTemplate = (template) => {
  openConfirm({
    title: `删除模板"${template.name}"?`,
    message: '删除后无法恢复。',
    type: 'danger',
    onConfirm: async () => {
      try {
        const res = await api.delete(`/watermark/templates/${template.id}`)
        if (res.data.success) {
          showToast('模板已删除')
          confirmModal.show = false
          await fetchWatermarkTemplates()
        } else showToast(res.data.message || '删除失败', 'error')
      } catch (e) { showToast('删除失败', 'error') }
    }
  })
}

const uploadWatermarkImage = async (e) => {
  const f = e.target.files[0]
  if (!f) return
  try {
    const fd = new FormData()
    fd.append('file', f)
    const res = await api.post('/watermark/upload-watermark-image', fd)
    if (res.data.success) {
      watermarkConfig.imageUrl = res.data.data
      showToast('水印图片已上传')
    } else {
      showToast(res.data.message || '上传失败', 'error')
    }
  } catch (e) { showToast('上传失败', 'error') }
}

const previewWatermark = async () => {
  watermarkPreviewLoading.value = true
  try {
    const sampleCanvas = document.createElement('canvas')
    sampleCanvas.width = 800
    sampleCanvas.height = 600
    const ctx = sampleCanvas.getContext('2d')
    const gradient = ctx.createLinearGradient(0, 0, 800, 600)
    gradient.addColorStop(0, '#667eea')
    gradient.addColorStop(1, '#764ba2')
    ctx.fillStyle = gradient
    ctx.fillRect(0, 0, 800, 600)
    ctx.fillStyle = '#fff'
    ctx.font = '20px sans-serif'
    ctx.textAlign = 'center'
    ctx.fillText('示例图片', 400, 300)
    const blob = await new Promise(resolve => sampleCanvas.toBlob(resolve, 'image/png'))
    const fd = new FormData()
    fd.append('file', blob, 'preview_sample.png')
    const res = await api.post('/watermark/preview', fd)
    if (res.data.success) {
      watermarkPreviewUrl.value = res.data.data
    } else {
      showToast(res.data.message || '预览生成失败', 'error')
    }
  } catch (e) { showToast('预览生成失败', 'error') }
  finally { watermarkPreviewLoading.value = false }
}

const addWatermarkToCurrentPicture = async () => {
  try {
    const res = await api.post(`/pictures/${currentPicture.value.id}/add-watermark`)
    if (res.data.success) {
      showToast('水印已添加')
      currentPicture.value = res.data.data
      await fetchAll()
    } else {
      showToast(res.data.message || '添加失败', 'error')
    }
  } catch (e) { showToast('添加失败', 'error') }
}

const removeWatermarkFromCurrentPicture = async () => {
  openConfirm({
    title: '移除水印？',
    message: '将恢复原图，移除已添加的水印。',
    type: 'warning',
    onConfirm: async () => {
      try {
        const res = await api.post(`/pictures/${currentPicture.value.id}/remove-watermark`)
        if (res.data.success) {
          showToast('水印已移除')
          confirmModal.show = false
          currentPicture.value = res.data.data
          await fetchAll()
        } else {
          showToast(res.data.message || '移除失败', 'error')
        }
      } catch (e) { showToast('移除失败', 'error') }
    }
  })
}

const doBatchWatermark = async () => {
  if (selectedPictureIds.value.length === 0) return
  batchWatermarkProgress.show = true
  batchWatermarkProgress.current = 0
  batchWatermarkProgress.total = selectedPictureIds.value.length
  let successCount = 0
  for (const pid of selectedPictureIds.value) {
    try {
      const params = {}
      if (batchWatermarkTemplateId.value) params.templateId = batchWatermarkTemplateId.value
      await api.post(`/pictures/${pid}/add-watermark`, null, { params })
      successCount++
    } catch (e) {}
    batchWatermarkProgress.current++
  }
  showToast(`已完成 ${successCount}/${selectedPictureIds.value.length} 张图片的水印添加`)
  showBatchWatermarkModal.value = false
  batchWatermarkProgress.show = false
  batchWatermarkProgress.current = 0
  batchWatermarkProgress.total = 0
  selectedPictureIds.value = []
  multiSelectMode.value = false
  await fetchAll()
}

const fetchAll = async () => {
  await Promise.all([fetchPictures(), fetchRecycleBin(), fetchAlbums(), fetchAlbumsByCategory(), fetchTags(), fetchStats(), fetchShares(), fetchDiscoverPictures(), fetchMyFavorites(), fetchUnreadCount()])
}

// Watch tab changes
watch(activeTab, async (newVal) => {
  if (!isLoggedIn.value) return
  if (newVal === 'pictures' && pictures.value.length === 0) await fetchPictures()
  if (newVal === 'recycle') await fetchRecycleBin()
  if (newVal === 'albums') {
    await fetchAlbums()
    await fetchAlbumsByCategory()
  }
  if (newVal === 'tags') await fetchTags()
  if (newVal === 'watermark') { await fetchWatermarkConfig(); await fetchWatermarkTemplates() }
  if (newVal === 'stats') { await fetchAlbums(); await fetchTags(); await fetchStats() }
  if (newVal === 'shares') await fetchShares()
  if (newVal === 'discover') await fetchDiscoverPictures()
  if (newVal === 'favorites') await fetchMyFavorites()
})

watch(isLoggedIn, async (newVal) => {
  if (newVal) {
    startUnreadCountTimer()
    fetchUnreadCount()
  } else {
    stopUnreadCountTimer()
    unreadCount.value = 0
    notifications.value = []
    showNotificationPanel.value = false
  }
})

const handleClickOutside = (e) => {
  if (notificationPanelRef.value && !notificationPanelRef.value.contains(e.target)) return
  const bellBtn = document.getElementById('notification-bell-btn')
  if (bellBtn && bellBtn.contains(e.target)) return
  showNotificationPanel.value = false
}

onMounted(async () => {
  if (!parseHashRoute()) {
    const loggedIn = await checkAuth()
    if (loggedIn) {
      await fetchAll()
      startUnreadCountTimer()
    }
  }
  if (typeof window !== 'undefined') {
    document.addEventListener('click', handleClickOutside)
  }
})

onUnmounted(() => {
  stopUnreadCountTimer()
  if (typeof window !== 'undefined') {
    document.removeEventListener('click', handleClickOutside)
  }
})
</script>

<style>
::-webkit-scrollbar { width: 8px; height: 8px; }
::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 4px; }
::-webkit-scrollbar-thumb { background: #c1c1c1; border-radius: 4px; }
::-webkit-scrollbar-thumb:hover { background: #a8a8a8; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
</style>
