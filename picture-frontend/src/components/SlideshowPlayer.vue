<template>
  <div class="fixed inset-0 z-50 bg-black select-none" tabindex="0" ref="playerContainer"
    @keydown.space.prevent="togglePause"
    @keydown.arrow-left.prevent="prevSlide"
    @keydown.arrow-right.prevent="nextSlide"
    @keydown.escape.prevent="$emit('close')">
    <div v-if="loading" class="flex items-center justify-center h-full">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-white"></div>
    </div>

    <template v-else>
      <div v-if="showTitlePage && slideshow.titlePageText" class="absolute inset-0 flex items-center justify-center z-10">
        <div class="text-center" :class="titlePageAnimClass">
          <h1 class="text-5xl font-bold text-white mb-4 drop-shadow-2xl">{{ slideshow.titlePageText }}</h1>
          <p v-if="slideshow.description" class="text-xl text-white/70 drop-shadow-lg">{{ slideshow.description }}</p>
        </div>
      </div>

      <div v-else class="absolute inset-0">
        <div v-for="(item, idx) in items" :key="item.id || idx"
          v-show="idx === currentIndex"
          class="absolute inset-0 flex items-center justify-center overflow-hidden">
          <div class="absolute inset-0" :style="getKenBurnsStyle(idx)">
            <img :src="item.pictureUrl"
              class="w-full h-full object-cover"
              :style="getImageEffectStyle(item)" />
          </div>
          <div v-if="item.effect === 'filter'" class="absolute inset-0" :style="getFilterOverlayStyle(item)"></div>

          <div v-if="item.caption" class="absolute inset-x-0" :class="[getTextPositionClass(item.textPosition || slideshow.textPosition), getTextAnimClass(idx)]">
            <div class="px-8 py-4">
              <div class="inline-block bg-black/50 backdrop-blur-md rounded-2xl px-6 py-3 max-w-2xl">
                <p class="text-white text-lg font-medium drop-shadow-lg">{{ item.caption }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="paused" class="absolute inset-0 flex items-center justify-center z-20 pointer-events-none">
        <div class="bg-black/40 backdrop-blur-sm rounded-full p-6">
          <svg class="w-16 h-16 text-white" fill="currentColor" viewBox="0 0 24 24">
            <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z"/>
          </svg>
        </div>
      </div>

      <div class="absolute top-4 right-4 z-30 flex items-center space-x-2">
        <button @click="togglePause"
          class="p-2.5 bg-black/40 backdrop-blur-sm rounded-full text-white hover:bg-black/60 transition">
          <svg v-if="paused" class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
          <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24"><path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z"/></svg>
        </button>
        <button @click="$emit('close')"
          class="p-2.5 bg-black/40 backdrop-blur-sm rounded-full text-white hover:bg-black/60 transition">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
        </button>
      </div>

      <div class="absolute bottom-0 left-0 right-0 z-30">
        <div class="px-6 pb-2">
          <div class="flex items-center justify-between text-white/60 text-xs mb-2">
            <span>{{ currentIndex + 1 }} / {{ items.length }}</span>
            <span>{{ formatSlideTime }}</span>
          </div>
          <div class="relative w-full h-1.5 bg-white/20 rounded-full overflow-hidden cursor-pointer" @click="seekProgress">
            <div class="h-full bg-gradient-to-r from-blue-400 to-purple-500 rounded-full transition-all duration-300"
              :style="{ width: progressPercent + '%' }"></div>
          </div>
        </div>
        <div class="flex items-center justify-between px-6 pb-4 pt-2">
          <div class="flex items-center space-x-3">
            <button @click="prevSlide"
              class="p-2 bg-black/30 backdrop-blur-sm rounded-full text-white hover:bg-black/50 transition">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/></svg>
            </button>
            <button @click="nextSlide"
              class="p-2 bg-black/30 backdrop-blur-sm rounded-full text-white hover:bg-black/50 transition">
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
            </button>
          </div>
          <div v-if="slideshow.title" class="text-white/50 text-sm truncate max-w-xs">
            {{ slideshow.title }}
          </div>
          <div class="flex items-center space-x-2">
            <div v-if="slideshow.bgMusicType !== 'none'" class="flex items-center space-x-2">
              <svg class="w-4 h-4 text-white/50" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z"/></svg>
              <button @click="toggleMute"
                class="p-1.5 bg-black/30 backdrop-blur-sm rounded-full text-white hover:bg-black/50 transition">
                <svg v-if="muted" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2"/></svg>
                <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z"/></svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <audio v-if="slideshow.bgMusicType !== 'none' && slideshow.bgMusicUrl" ref="bgAudio"
      :src="slideshow.bgMusicUrl" :loop="slideshow.bgMusicLoop" preload="auto"></audio>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SlideshowPlayer',
  props: {
    slideshowData: { type: Object, default: null },
    shareToken: { type: String, default: null }
  },
  emits: ['close'],
  data() {
    return {
      slideshow: {},
      items: [],
      currentIndex: 0,
      paused: false,
      muted: false,
      loading: true,
      showTitlePage: false,
      timer: null,
      slideStartTime: 0,
      elapsed: 0,
      transitionClass: '',
      kenBurnsActive: false,
      kenBurnsPhase: 0
    }
  },
  computed: {
    currentInterval() {
      if (this.items.length === 0) return 5
      const item = this.items[this.currentIndex]
      return (item && item.customInterval) || this.slideshow.interval || 5
    },
    progressPercent() {
      if (this.items.length === 0) return 0
      return ((this.currentIndex + this.elapsed / (this.currentInterval * 1000)) / this.items.length) * 100
    },
    formatSlideTime() {
      const sec = Math.floor(this.elapsed / 1000)
      return sec + 's / ' + this.currentInterval + 's'
    },
    titlePageAnimClass() {
      return 'animate-fade-in'
    }
  },
  async mounted() {
    await this.loadSlideshow()
    this.$refs.playerContainer?.focus()
  },
  beforeUnmount() {
    this.stopTimer()
    this.stopBgMusic()
  },
  methods: {
    async loadSlideshow() {
      try {
        let res
        if (this.shareToken) {
          res = await axios.get('/api/slideshows/share/' + this.shareToken)
        } else if (this.slideshowData && this.slideshowData.id) {
          res = await axios.get('/api/slideshows/' + this.slideshowData.id)
        } else {
          this.loading = false
          return
        }

        if (res.data.success) {
          this.slideshow = res.data.data
          this.items = res.data.data.items || []
          this.loading = false

          if (this.slideshow.titlePageText) {
            this.showTitlePage = true
            setTimeout(() => {
              this.showTitlePage = false
              this.startPlayback()
            }, 3000)
          } else if (this.slideshow.autoStart !== false) {
            this.startPlayback()
          }
        }
      } catch (e) {
        console.error('Failed to load slideshow', e)
        this.loading = false
      }
    },
    startPlayback() {
      this.startBgMusic()
      this.startTimer()
    },
    startTimer() {
      this.stopTimer()
      this.slideStartTime = Date.now()
      this.elapsed = 0

      this.timer = setInterval(() => {
        if (this.paused) return
        this.elapsed = Date.now() - this.slideStartTime

        if (this.kenBurnsActive) {
          this.kenBurnsPhase = this.elapsed / (this.currentInterval * 1000)
        }

        if (this.elapsed >= this.currentInterval * 1000) {
          this.nextSlide()
        }
      }, 50)
    },
    stopTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    nextSlide() {
      if (this.items.length === 0) return
      if (this.currentIndex >= this.items.length - 1) {
        if (this.slideshow.loop !== false) {
          this.currentIndex = 0
        } else {
          this.paused = true
          return
        }
      } else {
        this.currentIndex++
      }
      this.slideStartTime = Date.now()
      this.elapsed = 0
    },
    prevSlide() {
      if (this.items.length === 0) return
      if (this.currentIndex <= 0) {
        if (this.slideshow.loop !== false) {
          this.currentIndex = this.items.length - 1
        }
      } else {
        this.currentIndex--
      }
      this.slideStartTime = Date.now()
      this.elapsed = 0
    },
    togglePause() {
      this.paused = !this.paused
      if (!this.paused) {
        this.slideStartTime = Date.now() - this.elapsed
      }
      if (this.paused) {
        this.pauseBgMusic()
      } else {
        this.resumeBgMusic()
      }
    },
    seekProgress(e) {
      const rect = e.currentTarget.getBoundingClientRect()
      const pct = (e.clientX - rect.left) / rect.width
      const targetIndex = Math.floor(pct * this.items.length)
      this.currentIndex = Math.min(Math.max(targetIndex, 0), this.items.length - 1)
      this.slideStartTime = Date.now()
      this.elapsed = 0
    },
    startBgMusic() {
      const audio = this.$refs.bgAudio
      if (audio && this.slideshow.bgMusicType !== 'none') {
        audio.volume = this.muted ? 0 : (this.slideshow.bgMusicVolume || 0.5)
        audio.play().catch(() => {})
      }
    },
    stopBgMusic() {
      const audio = this.$refs.bgAudio
      if (audio) {
        audio.pause()
        audio.currentTime = 0
      }
    },
    pauseBgMusic() {
      const audio = this.$refs.bgAudio
      if (audio) audio.pause()
    },
    resumeBgMusic() {
      const audio = this.$refs.bgAudio
      if (audio && !this.muted) audio.play().catch(() => {})
    },
    toggleMute() {
      this.muted = !this.muted
      const audio = this.$refs.bgAudio
      if (audio) {
        audio.volume = this.muted ? 0 : (this.slideshow.bgMusicVolume || 0.5)
      }
    },
    getKenBurnsStyle(idx) {
      const effect = this.items[idx]?.effect || this.slideshow.effect || 'none'
      if (effect === 'kenburns' && idx === this.currentIndex) {
        const intensity = (this.items[idx]?.effectIntensity || this.slideshow.effectIntensity || 0.5)
        const scale = 1 + intensity * 0.3 * this.kenBurnsPhase
        const tx = intensity * 20 * this.kenBurnsPhase
        const ty = intensity * 10 * this.kenBurnsPhase
        return {
          transform: `scale(${scale}) translate(${tx}px, ${ty}px)`,
          transition: 'transform 0.1s linear'
        }
      }
      return {}
    },
    getImageEffectStyle(item) {
      const effect = item?.effect || this.slideshow.effect || 'none'
      if (effect === 'parallax') {
        return {
          transition: 'transform 0.8s ease-out'
        }
      }
      return {}
    },
    getFilterOverlayStyle(item) {
      const intensity = (item?.effectIntensity || this.slideshow.effectIntensity || 0.5) * 0.5
      return {
        background: `linear-gradient(135deg, rgba(59,130,246,${intensity}), rgba(168,85,247,${intensity}))`,
        mixBlendMode: 'overlay'
      }
    },
    getTextPositionClass(position) {
      switch (position) {
        case 'top': return 'top-0 pt-8'
        case 'center': return 'top-1/2 -translate-y-1/2'
        case 'bottom': default: return 'bottom-24'
      }
    },
    getTextAnimClass(idx) {
      const anim = this.items[idx]?.textAnimation || this.slideshow.textAnimation || 'fade'
      if (idx === this.currentIndex) {
        switch (anim) {
          case 'typewriter': return 'animate-typewriter'
          case 'slide': return 'animate-slide-in'
          case 'fade': default: return 'animate-fade-in'
        }
      }
      return ''
    }
  }
}
</script>

<style scoped>
@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes slide-in {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}
@keyframes typewriter {
  from { opacity: 0; max-width: 0; overflow: hidden; }
  to { opacity: 1; max-width: 600px; }
}
.animate-fade-in {
  animation: fade-in 0.8s ease-out forwards;
}
.animate-slide-in {
  animation: slide-in 0.6s ease-out forwards;
}
.animate-typewriter {
  animation: typewriter 1.5s ease-out forwards;
  overflow: hidden;
  white-space: nowrap;
}
</style>
