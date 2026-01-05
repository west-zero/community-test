import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

import Dashboard from './pages/Dashboard.vue'
import MenuManage from './pages/MenuManage.vue'
import PostManage from './pages/PostManage.vue'

const routes = [
    { path: '/', component: Dashboard },
    { path: '/menus', component: MenuManage },
    { path: '/posts', component: PostManage }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

createApp(App)
    .use(router)
    .mount('#app')
