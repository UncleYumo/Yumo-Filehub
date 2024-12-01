// This file is used to store the token in the local storage
import { defineStore } from "pinia";
import { ref } from "vue";

// 两个参数，第一个参数是模块名称，第二个参数是存储数据的函数
// 这里我们定义了一个名为 token 的模块，并使用 defineStore 函数创建了这个模块的存储器
export const useTokenStore = defineStore('token', () => {
    // 定义状态的内容

    const token = ref('')
    const setToken = (newToken: string) => {
        token.value = newToken
    }
    const removeToken = () => {
        token.value = ''
    }

    return {
        token,
        setToken,
        removeToken
    }
}, {
    persist: true, // 持久化存储
})