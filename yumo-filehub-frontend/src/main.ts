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

// Markdown
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import '@kangc/v-md-editor/lib/theme/style/vuepress.css';
import Prism from "prismjs";

VMdPreview.use(vuepressTheme, {
    Prism,
});

const pinia = createPinia()
pinia.use(createPersistedState())

const app = createApp(App);

app.use(ElementPlus);
app.use(VMdPreview);
app.use(naive);
app.use(router);
app.use(pinia);
app.mount('#app');