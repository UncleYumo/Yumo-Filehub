import { createApp } from 'vue';
import { createPinia  } from 'pinia'

import App from './App.vue';
import router from './router';

// @ts-ignore
import { createPersistedState } from 'pinia-persistedstate-plugin'

// ElementPlus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';

// Naive UI
import naive from 'naive-ui';

const pinia = createPinia()
pinia.use(createPersistedState())

const app = createApp(App);

app.use(ElementPlus);
app.use(naive);
app.use(router);
app.use(pinia); // 使用 Pinia 实例
app.mount('#app');