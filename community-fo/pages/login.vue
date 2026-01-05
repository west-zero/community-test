<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="w-full max-w-md">
      <div class="card p-8">
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-slate-800">로그인</h1>
          <p class="text-slate-500 mt-2">커뮤니티에 오신 것을 환영합니다</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            {{ error }}
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">아이디</label>
            <input
              v-model="username"
              type="text"
              class="input"
              placeholder="아이디를 입력하세요"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">비밀번호</label>
            <input
              v-model="password"
              type="password"
              class="input"
              placeholder="비밀번호를 입력하세요"
              required
            />
          </div>

          <button type="submit" :disabled="loading" class="btn btn-primary w-full py-3">
            <span v-if="loading" class="inline-block w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            <span v-else>로그인</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-slate-500">
            계정이 없으신가요?
            <NuxtLink to="/register" class="text-primary-600 hover:text-primary-700 font-medium">회원가입</NuxtLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { login } = useAuth()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  const result = await login(username.value, password.value)
  
  if (result.success) {
    navigateTo('/')
  } else {
    error.value = result.message || '로그인에 실패했습니다'
  }
  
  loading.value = false
}
</script>
