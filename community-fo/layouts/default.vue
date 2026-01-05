<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center">
            <!-- Mobile Menu Button -->
            <button @click="mobileMenuOpen = !mobileMenuOpen" class="md:hidden p-2 -ml-2 mr-2 rounded-lg hover:bg-slate-100 text-slate-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="!mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>

            <!-- Logo -->
            <NuxtLink to="/" class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-600 rounded-lg flex items-center justify-center">
                <span class="text-white font-bold text-lg">C</span>
              </div>
              <span class="text-xl font-bold bg-gradient-to-r from-primary-600 to-primary-500 bg-clip-text text-transparent">Community</span>
            </NuxtLink>
          </div>

          <!-- Desktop Navigation -->
          <nav class="hidden md:flex items-center space-x-1">
            <NuxtLink
              v-for="menu in menus"
              :key="menu.id"
              :to="`/board/${menu.id}`"
              class="px-4 py-2 rounded-lg text-slate-600 hover:text-primary-600 hover:bg-primary-50 transition-all duration-200 font-medium"
            >
              {{ menu.name }}
            </NuxtLink>
          </nav>

          <!-- Auth Buttons / User Menu -->
          <div class="flex items-center space-x-3">
            <template v-if="isLoggedIn">
              <NuxtLink to="/mypage" class="hidden sm:inline text-sm font-medium text-slate-700 hover:text-primary-600 transition-colors">
                {{ user?.nickname }}
              </NuxtLink>
              <button @click="logout" class="btn btn-secondary text-xs sm:text-sm px-2 py-1.5 sm:px-4 sm:py-2">로그아웃</button>
            </template>
            <template v-else>
              <NuxtLink to="/login" class="btn btn-secondary text-xs sm:text-sm px-2 py-1.5 sm:px-4 sm:py-2">로그인</NuxtLink>
              <NuxtLink to="/register" class="btn btn-primary text-xs sm:text-sm px-2 py-1.5 sm:px-4 sm:py-2">회원가입</NuxtLink>
            </template>


          </div>
        </div>

        <!-- Mobile Menu -->
        <div v-if="mobileMenuOpen" class="md:hidden py-4 border-t border-slate-100">
          <nav class="flex flex-col space-y-1">
            <NuxtLink
              v-if="isLoggedIn"
              to="/mypage"
              class="px-4 py-3 rounded-lg bg-primary-50 text-primary-700 font-bold mb-2 hover:bg-primary-100 transition-colors flex items-center"
              @click="mobileMenuOpen = false"
            >
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              마이페이지
            </NuxtLink>
            <NuxtLink
              v-for="menu in menus"
              :key="menu.id"
              :to="`/board/${menu.id}`"
              class="px-4 py-2 rounded-lg text-slate-600 hover:text-primary-600 hover:bg-primary-50 transition-all"
              @click="mobileMenuOpen = false"
            >
              {{ menu.name }}
            </NuxtLink>
          </nav>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1">
      <slot />
    </main>

    <!-- Footer -->
    <footer class="bg-slate-900 text-slate-400 py-8 mt-auto">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <p>&copy; 2026 Community. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
interface Menu {
  id: number
  name: string
}

const { user, isLoggedIn, logout, checkAuth } = useAuth()
const api = useApi()

const menus = ref<Menu[]>([])
const mobileMenuOpen = ref(false)

onMounted(async () => {
  await checkAuth()
  const result = await api.get<Menu[]>('/api/menus')
  if (result.success && result.data) {
    menus.value = result.data
  }
})
</script>
