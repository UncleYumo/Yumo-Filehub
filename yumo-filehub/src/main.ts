import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// ElementPlus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

// Naive UI
import naive from 'naive-ui'

const app = createApp(App)

app.use(ElementPlus)
app.use(naive)
app.use(createPinia())
app.use(router)

app.mount('#app')
