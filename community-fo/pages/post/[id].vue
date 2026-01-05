<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block w-8 h-8 border-4 border-primary-200 border-t-primary-500 rounded-full animate-spin"></div>
    </div>

    <template v-else-if="post">
      <!-- Post Header -->
      <div class="card p-6 mb-6">
        <div class="mb-4">
          <span class="inline-block px-2 py-1 text-xs font-medium bg-primary-100 text-primary-700 rounded-full">
            {{ post.menuName }}
          </span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-bold text-slate-800 mb-4">{{ post.title }}</h1>
        <div class="flex flex-wrap items-center justify-between gap-4 text-sm text-slate-500 pb-4 border-b border-slate-100">
          <div class="flex items-center space-x-3">
            <span class="font-medium text-slate-700">{{ post.authorNickname }}</span>
            <span>•</span>
            <span>{{ formatDate(post.createdAt) }}</span>
          </div>
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <span>{{ post.viewCount }}</span>
            </div>
          </div>
        </div>

        <!-- Post Content -->
        <div class="py-6 prose prose-slate max-w-none whitespace-pre-wrap tiptap" v-html="post.content"></div>

        <!-- Action Buttons -->
        <div class="flex flex-wrap items-center justify-between gap-4 pt-4 border-t border-slate-100">
          <button
            @click="toggleLike"
            :disabled="!isLoggedIn"
            :class="[
              'flex items-center space-x-2 px-4 py-2 rounded-lg transition-all',
              post.liked
                ? 'bg-red-50 text-red-500 hover:bg-red-100'
                : 'bg-slate-50 text-slate-600 hover:bg-slate-100'
            ]"
          >
            <svg class="w-5 h-5" :fill="post.liked ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span>추천 {{ post.likeCount }}</span>
          </button>

          <div v-if="isAuthor" class="flex items-center space-x-2">
            <NuxtLink :to="`/write?id=${post.id}`" class="btn btn-secondary text-sm">수정</NuxtLink>
            <button @click="deletePost" class="btn btn-danger text-sm">삭제</button>
          </div>
        </div>
      </div>

      <!-- Comments Section -->
      <div class="card p-6">
        <h2 class="text-lg font-bold text-slate-800 mb-6 flex items-center">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          댓글 {{ comments.length }}개
        </h2>

        <!-- Comment Form -->
        <form v-if="isLoggedIn" @submit.prevent="submitComment" class="mb-6">
          <textarea
            v-model="newComment"
            class="input resize-none"
            rows="3"
            placeholder="댓글을 작성하세요..."
            required
          ></textarea>
          <div class="flex justify-end mt-2">
            <button type="submit" :disabled="!newComment.trim()" class="btn btn-primary text-sm">
              댓글 작성
            </button>
          </div>
        </form>
        <div v-else class="mb-6 p-4 bg-slate-50 rounded-lg text-center text-slate-500">
          <NuxtLink to="/login" class="text-primary-600 hover:underline">로그인</NuxtLink> 후 댓글을 작성할 수 있습니다.
        </div>

        <!-- Comments List -->
        <div v-if="comments.length === 0" class="text-center py-8 text-slate-400">
          아직 댓글이 없습니다. 첫 댓글을 작성해보세요!
        </div>
        <div v-else class="space-y-4">
          <div v-for="comment in comments" :key="comment.id" class="p-4 bg-slate-50 rounded-lg">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center space-x-2">
                <span class="font-medium text-slate-700">{{ comment.authorNickname }}</span>
                <span class="text-sm text-slate-400">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <button
                v-if="user?.id === comment.authorId"
                @click="deleteComment(comment.id)"
                class="text-sm text-red-500 hover:text-red-600"
              >
                삭제
              </button>
            </div>
            <p class="text-slate-600 whitespace-pre-wrap">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
interface Post {
  id: number
  title: string
  content: string
  authorNickname: string
  authorId: number
  menuName: string
  viewCount: number
  likeCount: number
  liked: boolean
  createdAt: string
}

interface Comment {
  id: number
  content: string
  authorNickname: string
  authorId: number
  createdAt: string
}

const route = useRoute()
const { user, isLoggedIn } = useAuth()
const api = useApi()

const postId = computed(() => route.params.id as string)
const post = ref<Post | null>(null)
const comments = ref<Comment[]>([])
const loading = ref(true)
const newComment = ref('')

const isAuthor = computed(() => user.value?.id === post.value?.authorId)

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const loadPost = async () => {
  loading.value = true
  const result = await api.get<Post>(`/api/posts/${postId.value}`)
  if (result.success && result.data) {
    post.value = result.data
  }
  loading.value = false
}

const loadComments = async () => {
  const result = await api.get<Comment[]>(`/api/comments/post/${postId.value}`)
  if (result.success && result.data) {
    comments.value = result.data
  }
}

const toggleLike = async () => {
  if (!isLoggedIn.value || !post.value) return
  const result = await api.post<boolean>(`/api/posts/${postId.value}/like`, {})
  if (result.success) {
    post.value.liked = result.data ?? false
    post.value.likeCount += post.value.liked ? 1 : -1
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  const result = await api.post<Comment>('/api/comments', {
    postId: parseInt(postId.value),
    content: newComment.value
  })
  if (result.success && result.data) {
    comments.value.push(result.data)
    newComment.value = ''
  }
}

const deleteComment = async (id: number) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return
  const result = await api.delete(`/api/comments/${id}`)
  if (result.success) {
    comments.value = comments.value.filter(c => c.id !== id)
  }
}

const deletePost = async () => {
  if (!confirm('게시물을 삭제하시겠습니까?')) return
  const result = await api.delete(`/api/posts/${postId.value}`)
  if (result.success) {
    navigateTo('/')
  }
}

onMounted(async () => {
  await loadPost()
  await loadComments()
})
</script>
