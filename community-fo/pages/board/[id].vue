<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h1 class="text-2xl font-bold text-slate-800">{{ menuName }}</h1>
        <p class="text-slate-500 mt-1">{{ menuDescription }}</p>
      </div>
      <NuxtLink v-if="isLoggedIn" :to="`/write?menuId=${menuId}`" class="btn btn-primary flex items-center space-x-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>글쓰기</span>
      </NuxtLink>
    </div>

    <!-- Post List -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block w-8 h-8 border-4 border-primary-200 border-t-primary-500 rounded-full animate-spin"></div>
    </div>

    <div v-else-if="posts.length === 0" class="card p-12 text-center">
      <svg class="w-16 h-16 mx-auto text-slate-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      </svg>
      <p class="text-slate-500">이 게시판에 아직 게시물이 없습니다.</p>
    </div>

    <div v-else class="space-y-4">
      <NuxtLink
        v-for="post in posts"
        :key="post.id"
        :to="`/post/${post.id}`"
        class="card card-hover p-4 block group"
      >
        <div class="flex flex-col gap-3">
          <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
            <div class="flex-1 min-w-0">
              <h3 class="text-lg font-semibold text-slate-800 group-hover:text-primary-600 transition-colors line-clamp-2">
                {{ post.title }}
              </h3>
              <div class="flex items-center space-x-3 text-sm text-slate-500 mt-2">
                <span>{{ post.authorNickname }}</span>
                <span>•</span>
                <span>{{ formatDate(post.createdAt) }}</span>
              </div>
            </div>

            <div v-if="post.thumbnailUrl" class="w-full sm:w-60 h-48 sm:h-40 flex-shrink-0 rounded-lg overflow-hidden bg-slate-100 border border-slate-200">
              <img :src="post.thumbnailUrl" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" alt="Thumbnail" />
            </div>
          </div>
          <div class="flex items-center space-x-4 text-sm text-slate-500">
            <div class="flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <span>{{ post.viewCount }}</span>
            </div>
            <div class="flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <span>{{ post.likeCount }}</span>
            </div>
            <div class="flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <span>{{ post.commentCount }}</span>
            </div>
          </div>
        </div>
      </NuxtLink>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center mt-8 space-x-2">
      <button
        v-for="p in totalPages"
        :key="p"
        @click="loadPosts(p - 1)"
        :class="[
          'w-10 h-10 rounded-lg font-medium transition-all',
          currentPage === p - 1
            ? 'bg-primary-500 text-white'
            : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
        ]"
      >
        {{ p }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Post {
  id: number
  title: string
  authorNickname: string
  viewCount: number
  likeCount: number
  commentCount: number
  createdAt: string
  thumbnailUrl?: string
}

const extractText = (html: string) => {
  return html.replace(/<[^>]+>/g, '').slice(0, 100) + (html.length > 100 ? '...' : '')
}

interface Menu {
  id: number
  name: string
  description: string
}

interface PageResponse {
  content: Post[]
  totalPages: number
  number: number
}

const route = useRoute()
const { isLoggedIn } = useAuth()
const api = useApi()

const menuId = computed(() => route.params.id as string)
const menuName = ref('')
const menuDescription = ref('')
const posts = ref<Post[]>([])
const loading = ref(true)
const currentPage = ref(0)
const totalPages = ref(0)

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric' })
}

const loadPosts = async (page = 0) => {
  loading.value = true
  const result = await api.get<PageResponse>(`/api/posts?menuId=${menuId.value}&page=${page}&size=10`)
  if (result.success && result.data) {
    posts.value = result.data.content
    totalPages.value = result.data.totalPages
    currentPage.value = result.data.number
  }
  loading.value = false
}

onMounted(async () => {
  const menuResult = await api.get<Menu[]>('/api/menus')
  if (menuResult.success && menuResult.data) {
    const menu = menuResult.data.find(m => m.id === parseInt(menuId.value))
    if (menu) {
      menuName.value = menu.name
      menuDescription.value = menu.description || ''
    }
  }
  await loadPosts()
})

watch(menuId, () => loadPosts())
</script>
