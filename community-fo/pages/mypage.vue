<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <h1 class="text-2xl font-bold text-slate-800 mb-6">마이페이지</h1>

    <!-- Tabs -->
    <div class="flex gap-4 border-b border-slate-200 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="currentTab = tab.id"
        :class="[
          'px-6 py-3 font-medium text-sm transition-colors relative',
          currentTab === tab.id
            ? 'text-primary-600'
            : 'text-slate-500 hover:text-slate-700'
        ]"
      >
        {{ tab.label }}
        <div
          v-if="currentTab === tab.id"
          class="absolute bottom-0 left-0 w-full h-0.5 bg-primary-600"
        ></div>
      </button>
    </div>

    <!-- Profile Tab -->
    <div v-if="currentTab === 'profile'" class="card p-8">
      <h2 class="text-lg font-bold text-slate-800 mb-4">내 정보</h2>
      <div v-if="user" class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="mb-2">
            <label class="block text-sm font-medium text-slate-500 mb-1">아이디</label>
            <div class="text-slate-800">{{ user.username }}</div>
          </div>
          <div class="mb-2">
            <label class="block text-sm font-medium text-slate-500 mb-1">닉네임</label>
            <div class="text-slate-800">{{ user.nickname }}</div>
          </div>
          <div class="mb-2">
            <label class="block text-sm font-medium text-slate-500 mb-1">이메일</label>
            <div class="text-slate-800">{{ user.email }}</div>
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-500 mb-1">가입일</label>
            <div class="text-slate-800">{{ formatDate(user.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- My Posts Tab -->
    <div v-else-if="currentTab === 'posts'">
      <div v-if="postsLoading" class="text-center py-12">
        <div class="inline-block w-8 h-8 border-4 border-primary-200 border-t-primary-500 rounded-full animate-spin"></div>
      </div>
      <div v-else-if="posts.length === 0" class="card p-12 text-center text-slate-500">
        작성한 게시물이 없습니다.
      </div>
      <div v-else class="space-y-4">
        <NuxtLink
          v-for="post in posts"
          :key="post.id"
          :to="`/post/${post.id}`"
          class="card p-5 block hover:shadow-md transition-shadow"
        >
          <div class="flex items-center space-x-2 text-sm text-slate-500 mb-1">
            <span class="px-2 py-0.5 bg-slate-100 rounded text-xs">{{ post.menuName }}</span>
            <span>{{ formatDate(post.createdAt) }}</span>
          </div>
          <h3 class="text-lg font-semibold text-slate-800 truncate mb-2">
            {{ post.title }}
          </h3>
          <div class="flex items-center space-x-4 text-sm text-slate-500">
            <span>조회 {{ post.viewCount }}</span>
            <span>추천 {{ post.likeCount }}</span>
            <span>댓글 {{ post.commentCount }}</span>
          </div>
        </NuxtLink>

        <!-- Pagination -->
        <div v-if="postsTotalPages > 1" class="flex justify-center mt-6 space-x-2">
          <button
            v-for="p in postsTotalPages"
            :key="p"
            @click="loadPosts(p - 1)"
            :class="[
              'w-8 h-8 rounded font-medium transition-all text-sm',
              postsPage === p - 1 ? 'bg-primary-500 text-white' : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
            ]"
          >
            {{ p }}
          </button>
        </div>
      </div>
    </div>

    <!-- My Comments Tab -->
    <div v-else-if="currentTab === 'comments'">
      <div v-if="commentsLoading" class="text-center py-12">
        <div class="inline-block w-8 h-8 border-4 border-primary-200 border-t-primary-500 rounded-full animate-spin"></div>
      </div>
      <div v-else-if="comments.length === 0" class="card p-12 text-center text-slate-500">
        작성한 댓글이 없습니다.
      </div>
      <div v-else class="space-y-4">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="card p-5"
        >
          <div class="flex items-center justify-between mb-2">
            <NuxtLink :to="`/post/${comment.postId}`" class="text-sm text-primary-600 hover:underline">
              게시물 보기
            </NuxtLink>
            <span class="text-sm text-slate-400">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <p class="text-slate-700 whitespace-pre-wrap">{{ comment.content }}</p>
        </div>

        <!-- Pagination -->
        <div v-if="commentsTotalPages > 1" class="flex justify-center mt-6 space-x-2">
          <button
            v-for="p in commentsTotalPages"
            :key="p"
            @click="loadComments(p - 1)"
            :class="[
              'w-8 h-8 rounded font-medium transition-all text-sm',
              commentsPage === p - 1 ? 'bg-primary-500 text-white' : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
            ]"
          >
            {{ p }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Post {
  id: number
  title: string
  menuName: string
  viewCount: number
  likeCount: number
  commentCount: number
  createdAt: string
}

interface Comment {
  id: number
  content: string
  postId: number
  createdAt: string
}

const { user, isLoggedIn, checkAuth } = useAuth()
const api = useApi()

const tabs = [
  { id: 'profile', label: '내 정보' },
  { id: 'posts', label: '작성 글' },
  { id: 'comments', label: '작성 댓글' }
]

const currentTab = ref('profile')

// Posts State
const posts = ref<Post[]>([])
const postsLoading = ref(false)
const postsPage = ref(0)
const postsTotalPages = ref(0)

// Comments State
const comments = ref<Comment[]>([])
const commentsLoading = ref(false)
const commentsPage = ref(0)
const commentsTotalPages = ref(0)

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const loadPosts = async (page = 0) => {
  postsLoading.value = true
  const result = await api.get<any>(`/api/posts/my?page=${page}&size=10`)
  if (result.success && result.data) {
    posts.value = result.data.content
    postsTotalPages.value = result.data.totalPages
    postsPage.value = result.data.number
  }
  postsLoading.value = false
}

const loadComments = async (page = 0) => {
  commentsLoading.value = true
  const result = await api.get<any>(`/api/comments/my?page=${page}&size=10`)
  if (result.success && result.data) {
    comments.value = result.data.content
    commentsTotalPages.value = result.data.totalPages
    commentsPage.value = result.data.number
  }
  commentsLoading.value = false
}

onMounted(async () => {
  if (!isLoggedIn.value) {
    navigateTo('/login')
    return
  }
  // Refresh user info to get createdAt if missing
  if (!user.value?.createdAt) {
    await checkAuth()
  }
})

watch(currentTab, (newTab) => {
  if (newTab === 'posts' && posts.value.length === 0) {
    loadPosts()
  } else if (newTab === 'comments' && comments.value.length === 0) {
    loadComments()
  }
})
</script>
