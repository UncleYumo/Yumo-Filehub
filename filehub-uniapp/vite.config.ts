import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  build: {
    rollupOptions: {
      // 在这里添加其他构建选项
    },
  },
  optimizeDeps: {
    include: ['@dcloudio/uni-ui'] // 添加需要转译的依赖
  }
});
