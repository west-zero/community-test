<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="card p-6">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">
        {{ isEditMode ? '게시물 수정' : '새 게시물 작성' }}
      </h1>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
          {{ error }}
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">게시판</label>
          <select v-model="menuId" class="input" required>
            <option value="">게시판 선택</option>
            <option v-for="menu in menus" :key="menu.id" :value="menu.id">
              {{ menu.name }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">제목</label>
          <input
            v-model="title"
            type="text"
            class="input"
            placeholder="제목을 입력하세요"
            required
          />
        </div>

        <div>
           <label class="block text-sm font-medium text-slate-700 mb-2">내용</label>
           <tiptap-editor v-model="content" />
        </div>

        <div class="flex items-center justify-end space-x-3">
          <button type="button" @click="navigateTo(-1)" class="btn btn-secondary">
            취소
          </button>
          <button type="submit" :disabled="loading" class="btn btn-primary">
            <span v-if="loading" class="inline-block w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            <span v-else>{{ isEditMode ? '수정' : '작성' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import TiptapEditor from '~/components/TiptapEditor.vue'

interface Menu {
  id: number
  name: string
}

interface Post {
  id: number
  title: string
  content: string
  menuId: number
}

const route = useRoute()
const { isLoggedIn } = useAuth()
const api = useApi()

const postIdQuery = computed(() => route.query.id as string | undefined)
const menuIdQuery = computed(() => route.query.menuId as string | undefined)
const isEditMode = computed(() => !!postIdQuery.value)

const menus = ref<Menu[]>([])
const menuId = ref<number | ''>('')
const title = ref('')
const content = ref('')
const error = ref('')
const loading = ref(false)

const extractThumbnailUrl = (html: string): string | null => {
  const match = html.match(/<img[^>]+src="([^">]+)"/)
  return match ? match[1] : null
}

const handleSubmit = async () => {
  if (!menuId.value) {
    error.value = '게시판을 선택해주세요'
    return
  }

  error.value = ''
  loading.value = true

  const thumbnailUrl = extractThumbnailUrl(content.value)
  
  const payload = {
    title: title.value,
    content: content.value,
    thumbnailUrl: thumbnailUrl,
    menuId: menuId.value
  }

  let result
  if (isEditMode.value) {
    result = await api.put<Post>(`/api/posts/${postIdQuery.value}`, payload)
  } else {
    result = await api.post<Post>('/api/posts', payload)
  }

  if (result.success && result.data) {
    navigateTo(`/post/${result.data.id}`)
  } else {
    error.value = result.message || '오류가 발생했습니다'
  }

  loading.value = false
}

onMounted(async () => {
  if (!isLoggedIn.value) {
    navigateTo('/login')
    return
  }

  const menuResult = await api.get<Menu[]>('/api/menus')
  if (menuResult.success && menuResult.data) {
    menus.value = menuResult.data
  }

  if (menuIdQuery.value) {
    menuId.value = parseInt(menuIdQuery.value)
  }

  if (postIdQuery.value) {
    const postResult = await api.get<Post>(`/api/posts/${postIdQuery.value}`)
    if (postResult.success && postResult.data) {
      title.value = postResult.data.title
      content.value = postResult.data.content
      menuId.value = postResult.data.menuId
    }
  }
})
</script>
