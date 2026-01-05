<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-slate-800">게시물 관리</h1>
    </div>

    <div class="card overflow-hidden">
      <table class="w-full">
        <thead class="bg-slate-50 border-b border-slate-200">
          <tr>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">ID</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">제목</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">게시판</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">작성자</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">조회</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">추천</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">작성일</th>
            <th class="px-6 py-4 text-right text-sm font-semibold text-slate-600">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="post in posts" :key="post.id" class="hover:bg-slate-50">
            <td class="px-6 py-4 text-slate-500">{{ post.id }}</td>
            <td class="px-6 py-4 font-medium text-slate-800 max-w-xs truncate">{{ post.title }}</td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 text-xs font-medium bg-primary-100 text-primary-700 rounded-full">
                {{ post.menuName }}
              </span>
            </td>
            <td class="px-6 py-4 text-slate-600">{{ post.authorNickname }}</td>
            <td class="px-6 py-4 text-slate-500">{{ post.viewCount }}</td>
            <td class="px-6 py-4 text-slate-500">{{ post.likeCount }}</td>
            <td class="px-6 py-4 text-slate-500">{{ formatDate(post.createdAt) }}</td>
            <td class="px-6 py-4 text-right">
              <button @click="deletePost(post.id)" class="text-red-500 hover:text-red-600 text-sm font-medium">
                삭제
              </button>
            </td>
          </tr>
          <tr v-if="posts.length === 0">
            <td colspan="8" class="px-6 py-12 text-center text-slate-400">
              등록된 게시물이 없습니다.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center mt-6 space-x-2">
      <button
        v-for="p in totalPages"
        :key="p"
        @click="fetchPosts(p - 1)"
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

<script setup>
import { ref, onMounted } from 'vue'

const posts = ref([])
const currentPage = ref(0)
const totalPages = ref(0)

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric' })
}

const fetchPosts = async (page = 0) => {
  const res = await fetch(`/api/admin/posts?page=${page}&size=10`)
  const data = await res.json()
  if (data.success && data.data) {
    posts.value = data.data.content
    totalPages.value = data.data.totalPages
    currentPage.value = data.data.number
  }
}

const deletePost = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  const res = await fetch(`/api/admin/posts/${id}`, { method: 'DELETE' })
  const data = await res.json()
  if (data.success) {
    fetchPosts(currentPage.value)
  } else {
    alert(data.message || '오류가 발생했습니다')
  }
}

onMounted(() => fetchPosts())
</script>
