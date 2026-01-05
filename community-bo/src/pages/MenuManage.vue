<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-slate-800">메뉴 관리</h1>
      <button @click="openModal()" class="btn btn-primary flex items-center space-x-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>새 메뉴 추가</span>
      </button>
    </div>

    <div class="card overflow-hidden">
      <table class="w-full">
        <thead class="bg-slate-50 border-b border-slate-200">
          <tr>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">순서</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">메뉴명</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">설명</th>
            <th class="px-6 py-4 text-left text-sm font-semibold text-slate-600">상태</th>
            <th class="px-6 py-4 text-right text-sm font-semibold text-slate-600">관리</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="menu in menus" :key="menu.id" class="hover:bg-slate-50">
            <td class="px-6 py-4 text-slate-600">{{ menu.displayOrder }}</td>
            <td class="px-6 py-4 font-medium text-slate-800">{{ menu.name }}</td>
            <td class="px-6 py-4 text-slate-500">{{ menu.description || '-' }}</td>
            <td class="px-6 py-4">
              <span :class="[
                'px-2 py-1 text-xs font-medium rounded-full',
                menu.active ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-500'
              ]">
                {{ menu.active ? '활성' : '비활성' }}
              </span>
            </td>
            <td class="px-6 py-4 text-right space-x-2">
              <button @click="openModal(menu)" class="text-primary-600 hover:text-primary-700 text-sm font-medium">
                수정
              </button>
              <button @click="deleteMenu(menu.id)" class="text-red-500 hover:text-red-600 text-sm font-medium">
                삭제
              </button>
            </td>
          </tr>
          <tr v-if="menus.length === 0">
            <td colspan="5" class="px-6 py-12 text-center text-slate-400">
              등록된 메뉴가 없습니다.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="absolute inset-0 bg-black/50" @click="showModal = false"></div>
      <div class="relative bg-white rounded-xl shadow-xl w-full max-w-md mx-4 p-6">
        <h2 class="text-xl font-bold text-slate-800 mb-6">
          {{ editingMenu ? '메뉴 수정' : '새 메뉴 추가' }}
        </h2>
        
        <form @submit.prevent="saveMenu" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">메뉴명</label>
            <input v-model="form.name" type="text" class="input" placeholder="게시판 이름" required />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">설명</label>
            <input v-model="form.description" type="text" class="input" placeholder="메뉴 설명 (선택)" />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-2">표시 순서</label>
            <input v-model.number="form.displayOrder" type="number" class="input" placeholder="1" required />
          </div>
          
          <div class="flex items-center">
            <input v-model="form.active" type="checkbox" id="active" class="w-4 h-4 rounded border-slate-300 text-primary-600 focus:ring-primary-500" />
            <label for="active" class="ml-2 text-sm text-slate-700">활성화</label>
          </div>
          
          <div class="flex justify-end space-x-3 pt-4">
            <button type="button" @click="showModal = false" class="btn btn-secondary">취소</button>
            <button type="submit" class="btn btn-primary">저장</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const menus = ref([])
const showModal = ref(false)
const editingMenu = ref(null)
const form = ref({
  name: '',
  description: '',
  displayOrder: 1,
  active: true
})

const fetchMenus = async () => {
  const res = await fetch('/api/admin/menus')
  const data = await res.json()
  if (data.success) {
    menus.value = data.data
  }
}

const openModal = (menu = null) => {
  editingMenu.value = menu
  if (menu) {
    form.value = { ...menu }
  } else {
    form.value = { name: '', description: '', displayOrder: menus.value.length + 1, active: true }
  }
  showModal.value = true
}

const saveMenu = async () => {
  const url = editingMenu.value
    ? `/api/admin/menus/${editingMenu.value.id}`
    : '/api/admin/menus'
  const method = editingMenu.value ? 'PUT' : 'POST'

  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })
  const data = await res.json()
  if (data.success) {
    showModal.value = false
    fetchMenus()
  } else {
    alert(data.message || '오류가 발생했습니다')
  }
}

const deleteMenu = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  const res = await fetch(`/api/admin/menus/${id}`, { method: 'DELETE' })
  const data = await res.json()
  if (data.success) {
    fetchMenus()
  } else {
    alert(data.message || '오류가 발생했습니다')
  }
}

onMounted(fetchMenus)
</script>
