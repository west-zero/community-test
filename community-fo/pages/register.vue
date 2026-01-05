<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4">
    <div class="w-full max-w-md">
      <div class="card p-8">
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-slate-800">회원가입</h1>
          <p class="text-slate-500 mt-2">커뮤니티에 가입하세요</p>
        </div>

        <form @submit.prevent="handleSignup" class="space-y-5">
          <div v-if="error" class="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            {{ error }}
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">아이디</label>
            <input
              v-model="username"
              type="text"
              class="input"
              placeholder="4자 이상의 아이디"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">이메일</label>
            <input
              v-model="email"
              type="email"
              class="input"
              placeholder="example@email.com"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">닉네임</label>
            <input
              v-model="nickname"
              type="text"
              class="input"
              placeholder="커뮤니티에서 사용할 닉네임"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">비밀번호</label>
            <input
              v-model="password"
              type="password"
              class="input"
              placeholder="6자 이상의 비밀번호"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">비밀번호 확인</label>
            <input
              v-model="confirmPassword"
              type="password"
              class="input"
              placeholder="비밀번호를 다시 입력하세요"
              required
            />
          </div>

          <button type="submit" :disabled="loading" class="btn btn-primary w-full py-3">
            <span v-if="loading" class="inline-block w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
            <span v-else>가입하기</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-slate-500">
            이미 계정이 있으신가요?
            <NuxtLink to="/login" class="text-primary-600 hover:text-primary-700 font-medium">로그인</NuxtLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { signup } = useAuth()

const username = ref('')
const email = ref('')
const nickname = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

const handleSignup = async () => {
  error.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = '비밀번호가 일치하지 않습니다'
    return
  }

  if (password.value.length < 6) {
    error.value = '비밀번호는 6자 이상이어야 합니다'
    return
  }

  loading.value = true

  const result = await signup(
    username.value,
    password.value,
    email.value,
    nickname.value || username.value
  )
  
  if (result.success) {
    navigateTo('/login')
  } else {
    error.value = result.message || '회원가입에 실패했습니다'
  }
  
  loading.value = false
}
</script>
