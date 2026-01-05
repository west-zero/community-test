<template>
  <div class="border border-slate-200 rounded-lg overflow-hidden bg-white">
    <div v-if="editor" class="border-b border-slate-200 p-2 flex flex-wrap gap-2 bg-slate-50">
      <button 
        @click="editor.chain().focus().toggleBold().run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('bold') }]">
        Bold
      </button>
      <button 
        @click="editor.chain().focus().toggleItalic().run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('italic') }]">
        Italic
      </button>
      <button 
        @click="editor.chain().focus().toggleStrike().run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('strike') }]">
        Strike
      </button>
      <div class="w-px h-6 bg-slate-300 mx-1"></div>
      <button 
        @click="editor.chain().focus().toggleHeading({ level: 2 }).run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('heading', { level: 2 }) }]">
        H2
      </button>
      <button 
        @click="editor.chain().focus().toggleHeading({ level: 3 }).run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('heading', { level: 3 }) }]">
        H3
      </button>
      <div class="w-px h-6 bg-slate-300 mx-1"></div>
      <button 
        @click="editor.chain().focus().toggleBulletList().run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('bulletList') }]">
        Bullet
      </button>
      <button 
        @click="editor.chain().focus().toggleOrderedList().run()" 
        :class="['editor-btn', { 'is-active': editor.isActive('orderedList') }]">
        Ordered
      </button>
      <div class="w-px h-6 bg-slate-300 mx-1"></div>
      <label class="editor-btn cursor-pointer flex items-center">
        <input type="file" class="hidden" accept="image/*" @change="addImage" />
        <span>Image</span>
      </label>
    </div>
    <editor-content :editor="editor" class="p-4 min-h-[300px] prose max-w-none focus:outline-none" />
  </div>
</template>

<script setup>
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue'])

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit,
    Image,
  ],
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML())
  },
})

// Update editor content if modelValue changes externally
watch(() => props.modelValue, (newValue) => {
  if (editor.value && newValue !== editor.value.getHTML()) {
    editor.value.commands.setContent(newValue, false)
  }
})

const api = useApi()

const addImage = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    // useApi automatically handles base URL and auth headers if implemented correctly
    // Since useApi might expect JSON, we need to check how it handles FormData.
    // Assuming useApi wrapper might set Content-Type to application/json automatically,
    // we might need to use raw fetch with token or check useApi implementation.
    // Let's stick to fetch with credentials for now to be safe, but fix the URL.
    
    // Actually, looking at other components, useApi seems standard.
    // But to be absolutely sure about FormData support without seeing useApi code,
    // let's use the explicit fetch with credentials and proper config.
    
    // Better approach: mimic the pattern used in other parts of the app but adding credentials.
    // However, since I can't read useApi, I'll use fetch with credentials include.
    
    const res = await fetch('http://localhost:8080/api/files/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include' // This is key for session based auth
    })
    
    if (!res.ok) {
        throw new Error('Upload failed')
    }
    
    const data = await res.json()
    
    if (data.success) {
      editor.value.chain().focus().setImage({ src: 'http://localhost:8080' + data.data }).run()
    } else {
      alert('Image upload failed: ' + data.message)
    }
  } catch (error) {
    console.error('Error uploading image:', error)
    alert('Error uploading image')
  }
}
</script>

<style scoped>
.editor-btn {
  @apply px-3 py-1.5 rounded text-sm font-medium text-slate-600 hover:bg-slate-200 transition-colors;
}
.editor-btn.is-active {
  @apply bg-slate-800 text-white hover:bg-slate-700;
}
:deep(.ProseMirror) {
  @apply outline-none min-h-[300px];
}
:deep(.ProseMirror img) {
  @apply max-w-full rounded-lg my-4 border border-slate-100 shadow-sm;
}
</style>
