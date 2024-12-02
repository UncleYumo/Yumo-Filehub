import {createRouter, createWebHistory} from 'vue-router'

import FileUploadPage from "@/views/File-Upload-Page.vue";
import FileRetrievePage from "@/views/File-Retrieve-Page.vue";
import FoundNothingPage from "@/views/Found-Nothing-Page.vue";
import TokenAuthorizationPage from "@/views/Token-Authorization-Page.vue";

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
            path: '/:catchAll(.*)',  // catch all route to handle 404 errors
            name: 'not-found',
            component: FoundNothingPage
        },
    ],
})

export default router
