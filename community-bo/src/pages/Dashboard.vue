<template>
  <div>
    <h1 class="text-2xl font-bold text-slate-800 mb-8">대시보드</h1>
    
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div class="card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-slate-500">전체 메뉴</p>
            <p class="text-3xl font-bold text-slate-800 mt-1">{{ stats.menuCount }}</p>
          </div>
          <div class="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
            <svg class="w-6 h-6 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-slate-500">전체 게시물</p>
            <p class="text-3xl font-bold text-slate-800 mt-1">{{ stats.postCount }}</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>
      
      <div class="card p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-slate-500">활성 메뉴</p>
            <p class="text-3xl font-bold text-slate-800 mt-1">{{ stats.activeMenuCount }}</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="card p-6">
        <h2 class="text-lg font-semibold text-slate-800 mb-4">빠른 링크</h2>
        <div class="space-y-3">
          <router-link to="/menus" class="flex items-center p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
            <svg class="w-5 h-5 text-primary-500 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span class="text-slate-700">새 메뉴 추가</span>
          </router-link>
          <router-link to="/posts" class="flex items-center p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
            <svg class="w-5 h-5 text-primary-500 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            <span class="text-slate-700">게시물 관리</span>
          </router-link>
        </div>
      </div>
      
      <div class="card p-6">
        <h2 class="text-lg font-semibold text-slate-800 mb-4">시스템 정보</h2>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between py-2 border-b border-slate-100">
            <span class="text-slate-500">버전</span>
            <span class="text-slate-700">1.0.0</span>
          </div>
          <div class="flex justify-between py-2 border-b border-slate-100">
            <span class="text-slate-500">FO API</span>
            <span class="text-slate-700">http://localhost:8080</span>
          </div>
          <div class="flex justify-between py-2">
            <span class="text-slate-500">BO API</span>
            <span class="text-slate-700">http://localhost:8081</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  menuCount: 0,
  postCount: 0,
  activeMenuCount: 0
})

const fetchStats = async () => {
  try {
    const menuRes = await fetch('/api/admin/menus')
    const menuData = await menuRes.json()
    if (menuData.success && menuData.data) {
      stats.value.menuCount = menuData.data.length
      stats.value.activeMenuCount = menuData.data.filter(m => m.active).length
    }

    const postRes = await fetch('/api/admin/posts?size=1')
    const postData = await postRes.json()
    if (postData.success && postData.data) {
      stats.value.postCount = postData.data.totalElements || 0
    }
  } catch (e) {
    console.error('Failed to fetch stats', e)
  }
}

onMounted(fetchStats)
</script>
