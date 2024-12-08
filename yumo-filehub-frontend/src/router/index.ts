import {createRouter, createWebHistory} from 'vue-router'

import FileUploadPage from "@/views/File-Upload-Page.vue";
import FileRetrievePage from "@/views/File-Retrieve-Page.vue";
import FoundNothingPage from "@/views/Found-Nothing-Page.vue";
import TokenAuthorizationPage from "@/views/Token-Authorization-Page.vue";

import {useTokenStore} from "@/stores/tokenStore";
import MarkdownInstructionPage from "@/views/Markdown-Instruction-Page.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: FileUploadPage
        },
        {
            path: '/retrieve',
            name: 'file-retrieve',
            component: FileRetrievePage
        },
        {
            path: '/authorize',
            name: 'token-authorization',
            component: TokenAuthorizationPage
        },
        {
            path: '/instruction',
            name: 'markdown-instruction',
            component: MarkdownInstructionPage
        },
        {
            path: '/:catchAll(.*)',  // catch all route to handle 404 errors
            name: 'not-found',
            component: FoundNothingPage
        },
    ],
})

// 全局路由前置守卫
router.beforeEach((to, from, next) => {
    // 判断本地是否有token
    const tokenStore = useTokenStore()
    if (tokenStore.token === null || tokenStore.token === '') {
        // 如果本地没有 token，且目标路径不是鉴权页面，则跳转到鉴权页面
        if (to.name !== 'token-authorization') {
            next({ name: 'token-authorization' })
        } else {
            next() // 如果是鉴权页面，继续
        }
    } else {
        next() // 如果有 token，继续
    }
})

export default router
